package com.karikeo.cashless.bt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import java.io.IOException;

public class BTControl implements OnBTActions{
    public static final int REQUEST_ENABLE_BT = 0x922625;

    private Activity mActivity;
    private static String id;
    private static BTControl control;
    private BluetoothDevice device;
    private BluetoothAdapter adapter;
    private BTSerialSocket socket;
    private Communication com;

    private BTControl(Activity activity, String btID){
        mActivity = activity;
        id = btID;
    }

    public static synchronized BTControl getInstance(Activity activity, String btID){
        if (control == null){
            control = new BTControl(activity, btID);
            return control;
        }

        //Add code to check activity and btID if not equal
        //it means that something changed and we must close connection and re run.
        if (id != btID){
            //Ok we need new connection with new device, we need close correctly prev and create new one
            control.close();
            control = new BTControl(activity, id);
        }

        return control;
    }

    public void openConnection() {
        adapter = BluetoothAdapter.getDefaultAdapter();

        if (!adapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(intent, REQUEST_ENABLE_BT);
            return;
        }

        onDeviceEnabled();
    }

    @Override
    public void onDeviceEnabled() {
        device = adapter.getRemoteDevice(id);
        if (device.getBondState() != BluetoothDevice.BOND_BONDED){
            receiver.register();
            adapter.startDiscovery();
            return;
        }
        openSockets(device);
    }

    final BlueToothBroadcastReceiver receiver = new BlueToothBroadcastReceiver(mActivity, new IBTBroadcastReceiverListener(){

        @Override
        public void OnDiscoveryDeviceFound(BluetoothDevice device) {
            if (device.getAddress() == id){
                if (adapter.isDiscovering())
                    adapter.cancelDiscovery();

                openSockets(device);
            }
        }

        @Override
        public void OnDiscoveryFinished() {
            receiver.unregister();
        }
    });

    private void openSockets(BluetoothDevice device) {
        socket = new BTSerialSocket(device);
        socket.openSocketsAsync(new IOnBTOpenPort() {
            @Override
            public void onBTOpenPortDone() {
                com = new Communication(socket);
            }

            @Override
            public void onBTOpenPortError() {

            }
        });
    }

    public void close(){
        if (com != null){
            com.setStop(true);
        }
        socket.closeSockets();
    }

    public void sendBalance(int balance) throws IOException{
        socket.write(("Balance:" + Integer.toString(balance)).getBytes());
    }
}

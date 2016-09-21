package com.karikeo.cashless.bt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

public class BTControl implements OnBTActions{
    public static final int REQUEST_ENABLE_BT = 0x922625;

    private Activity mActivity;
    private String id;
    private static BTControl control;
    private BluetoothDevice device;
    private BluetoothAdapter adapter;
    private BTSerialSocket socket;

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
        (socket.openSockets()) ? /*all start working*/ :

    }
}

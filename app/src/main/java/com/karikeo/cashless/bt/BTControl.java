package com.karikeo.cashless.bt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BTControl implements OnBTActions{
    public static final int REQUEST_ENABLE_BT = 0x922625;

    private Activity mActivity;
    private static String id;
    private static BTControl control;

    private BluetoothAdapter adapter;
    private BTSerialSocket socket;
    private Communication com;

    private IOnBTOpenPort actions;

    private BlueToothBroadcastReceiver receiver;

    private BTControl(Activity activity, String btID){
        mActivity = activity;
        id = btID;

        receiver = new BlueToothBroadcastReceiver(mActivity, new IBTBroadcastReceiverListener(){

            @Override
            public void OnDiscoveryDeviceFound(BluetoothDevice device) {
                Log.d("BTDevice: ", device.getAddress());
                if (device.getAddress().equals(id)){
                    if (adapter.isDiscovering())
                        adapter.cancelDiscovery();

                    pairDevice(device);
                }
            }

            @Override
            public void OnDiscoveryFinished() {
                /*receiver.unregister();*/
            }

            @Override
            public void OnPairDone(BluetoothDevice device) {
                Log.d("PAIRDONE", device.getAddress());
                openSockets(device);
                receiver.unregister();
            }

            @Override
            public void OnPairError() {
                receiver.unregister();
                //Oops something wrong
            }
        });
    }

    public static synchronized BTControl getInstance(Activity activity, String btID){
        if (control == null){
            control = new BTControl(activity, btID);
            return control;
        }

        //Add code to check activity and btID if not equal
        //it means that something changed and we must close connection and re run.
        if (!id.equals(btID)){
            //Ok we need new connection with new device, we need close correctly prev and create new one
            control.close();
            control = new BTControl(activity, id);
        }

        return control;
    }

    public void openConnection(IOnBTOpenPort actions) {
        adapter = BluetoothAdapter.getDefaultAdapter();
        this.actions = actions;

        if (!adapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(intent, REQUEST_ENABLE_BT);
            return;
        }

        onDeviceEnabled();
    }

    @Override
    public void onDeviceEnabled() {
        BluetoothDevice device = adapter.getRemoteDevice(id);
        if (device.getBondState() != BluetoothDevice.BOND_BONDED){
            receiver.register();
            adapter.startDiscovery();
            return;
        }

        pairDevice(device);
    }

    private void pairDevice(BluetoothDevice device){
        if (device.getBondState() != BluetoothDevice.BOND_BONDED){
            try{
                Method method = device.getClass().getMethod("createBond", (Class[]) null);
                method.invoke(device, (Object[]) null);
                Log.d("CreateBOND", device.getAddress());
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
/*
            try {
                Method method = device.getClass().getMethod("removeBond", (Class[]) null);
                method.invoke(device, (Object[]) null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
*/
            Log.d("OPEN SOCKETS", device.getAddress());
            openSockets(device);
        }
    }


    private void openSockets(BluetoothDevice device) {

        socket = new BTSerialSocket(device);
        socket.openSocketsAsync(new IOnBTOpenPort() {
            @Override
            public void onBTOpenPortDone() {
                com = new Communication(socket);
                if(actions!=null){
                    actions.onBTOpenPortDone();
                }
            }

            @Override
            public void onBTOpenPortError() {
                if (actions!=null){
                    actions.onBTOpenPortError();
                }
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
        socket.write(("BALANCE=" + Integer.toString(balance)).getBytes());
        Log.d("BALANCE", Integer.toString(balance));
    }
}

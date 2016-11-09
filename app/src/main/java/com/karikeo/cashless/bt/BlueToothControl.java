package com.karikeo.cashless.bt;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;

import java.io.*;
import java.lang.reflect.Method;

public class BlueToothControl implements CommInterface, OutputStream {
    private static final String TAG = "BlueToothControl";
    public static final int REQUEST_ENABLE_BT = 0x922625;

    private String id;
    private BlueToothControl control;
    private boolean isConnected = false;

    private BluetoothAdapter adapter;
    private BlueToothSerialSocket socket;
    private Communication com;

    private BTOpenPortStatus openPortStatus;

    private BlueToothBroadcastReceiver receiver;

    public BlueToothControl(Application app){

        receiver = new BlueToothBroadcastReceiver(app, new IBTBroadcastReceiverListener(){
            @Override
            public void OnDiscoveryDeviceFound(BluetoothDevice device) {
                Log.d(TAG, "BTDevice: " + device.getAddress());
                if (device.getAddress().equals(id)){
                    if (adapter.isDiscovering())
                        adapter.cancelDiscovery();

                    pairDevice(device);
                }
            }

            @Override
            public void OnDiscoveryFinished() {
            }

            @Override
            public void OnPairDone(BluetoothDevice device) {
                Log.d(TAG, "PAIRDONE " + device.getAddress());
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

    @Override
    public void openConnection(Activity activity, String id) {
        if (!checkId(id)){
            //Generate error;
            return;
        }

        this.id = id;

        adapter = BluetoothAdapter.getDefaultAdapter();

        if (!adapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(intent, REQUEST_ENABLE_BT);
            return;
        }

        onDeviceEnabled();
    }

    @Override
    public void closeConnection() {
        if (com != null){
            com.setStop(true);
        }
        socket.closeSockets();
        isConnected = false;
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean checkId(String id) {
        return adapter.checkBluetoothAddress(id);
    }

    private Communication.DataCallback callback;
    @Override
    public void registerOnRawData(Communication.DataCallback callback) {
        this.callback = callback;
    }


    @Override
    public void addAsyncResponseListener(BTOpenPortStatus actions) {
        this.openPortStatus = actions;
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
                Log.d(TAG, "Create bond " + device.getAddress());
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Log.d(TAG, "Open sockets: " + device.getAddress());
            openSockets(device);
        }
    }


    private void openSockets(BluetoothDevice device) {

        if (socket!=null) {
            socket.closeSockets();
            socket = null;
        }

        socket = new BlueToothSerialSocket(device);
        socket.openSocketsAsync(new BTOpenPortStatus() {
            @Override
            public void onBTOpenPortDone() {
                com = new Communication(socket);
                if(openPortStatus !=null){
                    isConnected = true;
                    com.registerReceiver(callback);
                    openPortStatus.onBTOpenPortDone();
                }
            }

            @Override
            public void onBTOpenPortError() {
                if (openPortStatus !=null){
                    isConnected = false;
                    openPortStatus.onBTOpenPortError();
                }
            }
        });
    }

    @Override
    public void write(byte[] b) throws IOException {
        if (isConnected){
            socket.write(b);
        }
    }

}

package com.karikeo.cashless.bt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;


public class BTSerialSocket {
    private final static int CONNECTION_ATTEMPTS = 5;
    private final static UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothSocket mmSocket;
    private BufferedInputStream bufIn;
    private BufferedOutputStream bufOut;
    private String btMacAddr;

    private final BluetoothDevice mBTDevice;


    public BTSerialSocket(BluetoothDevice device) {
        mBTDevice = device;
    }


    public boolean openSockets(){

        if (mBTDevice.getBondState() != BluetoothDevice.BOND_BONDED){
            return false;
        }

        mmSocket = getBTSocket();

        int idx = 0;
        do {
            try{
                idx++;
                mmSocket.connect();

                bufIn = new BufferedInputStream(mmSocket.getInputStream());
                bufOut = new BufferedOutputStream(mmSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();

                //Try to do several attempts to connect device.
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }

        }while(bufIn==null && bufOut==null && idx<=CONNECTION_ATTEMPTS);

        if (bufIn==null || bufOut==null){
            return false;
        }

        return true;
    }

    public void closeSockets(){
        try {
            if (bufIn != null && mmSocket!= null) {
                mmSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(byte[] bytes) throws IOException{
        bufOut.write(bytes);
        bufOut.flush();
    }

    public void write(byte b) throws IOException{
        bufOut.write(b);
        bufOut.flush();
    }

    public int read(byte[] buf, int count) throws IOException{
        return bufIn.read(buf, 0, count);
    }

    public int available()throws IOException{
        if (bufIn != null)
            return bufIn.available();
        return 0;
    }

    // Get a BluetoothSocket to connect with the given BluetoothDevice
    private BluetoothSocket getBTSocket(){
        BluetoothSocket btSocket = null;

        try {
            int currentApiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentApiVersion > 10) {
                btSocket = mBTDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } else {
                BluetoothDevice hxm = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mBTDevice.getAddress());
                Method m = hxm.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                btSocket = (BluetoothSocket) m.invoke(hxm, 1);
            }

        } catch (IOException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return btSocket;
    }

    public void openSocketsAsync(final IOnBTOpenPort callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!openSockets()) {
                    callback.onBTOpenPortError();
                }else{
                    callback.onBTOpenPortDone();
                }
            }
        }).run();
    }
}
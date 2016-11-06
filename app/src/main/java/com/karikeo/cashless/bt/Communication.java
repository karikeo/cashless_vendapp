package com.karikeo.cashless.bt;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.CircularArray;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;

public class Communication {
    private final static String TAG = "com.karikeo.cashless.bt.Communication";

    private final static byte MESSAGE_DELIMITER = 0x10;
    private final static int B_SIZE = 1024;

    private BlueToothSerialSocket btSocket;
    protected final CircularArray<Byte> buf = new CircularArray<>();

    private boolean mStop = false;


    public Communication(BlueToothSerialSocket port) {
        btSocket = port;
        backgroundThread.start();
    }


    //Thread safe method to send data between threads.
    Thread backgroundThread = new Thread(new Runnable() {
        @Override
        public void run() {

            byte data[] = new byte[B_SIZE];

            while (!mStop) {
                try {
                    if (btSocket.available() != 0) {
                        final int count = btSocket.read(data, B_SIZE);
                        sendMessage(data, count);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    mStop = true;
                }
                Thread.yield();
            }
        }

        private void sendMessage(byte[] data, int count) {
            if (count != 0 && data != null) {
                android.os.Message msg = mHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putByteArray("Data", Arrays.copyOf(data, count));
                b.putInt("Length", count);
                msg.setData(b);
                mHandler.sendMessage(msg);
            }
        }
    });

    public void setStop(boolean flag) {
        mStop = flag;
    }

    public boolean isStop() {
        return mStop;
    }

    private final Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            byte[] d = msg.getData().getByteArray("Data");
            int length = msg.getData().getInt("Length");

            Log.d(TAG,"Income-Size:"+Integer.valueOf(length).toString()+" data:"+new String(d));

            if (d != null && length != 0) {
                synchronized (buf) {
                    for (int i = 0; i < length; i++) {
                        buf.addLast(d[i]);
                    }
                }
                onInputData();
            }
        }
    };

    public void onInputData(){
        for (int i = 0; i<buf.size(); i++){
            if (buf.get(i) == MESSAGE_DELIMITER){
                byte b[] = new byte[i];
                for (int k = 0; k<i; k++){
                    b[k] = buf.popFirst();
                }
                if (callback != null){
                    callback.onRawData(b);
                }
            }
        }
    }

    public void write(byte[] data) throws IOException{
        //pack outgoing message
        btSocket.write(data);
    }


    public interface DataCallback{
        void onRawData(byte[] b);
    }

    private DataCallback callback;
    public void registerReceiver(DataCallback callback){
        this.callback = callback;
    }
}

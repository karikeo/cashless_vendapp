package com.karikeo.cashless.bt;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.CircularArray;

import java.io.IOException;
import java.util.Arrays;

public class Communication {
    private final static int B_SIZE = 1024;

    private BTSerialSocket btSocket;
    protected final CircularArray<Byte> buf = new CircularArray<>();

    private boolean mStop = false;


    public Communication(BTSerialSocket port) {
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
        //Here should be parser of the protocol
    }

    public void write(byte[] data) throws IOException{
        //pack outgoing message
        btSocket.write(data);
    }
}
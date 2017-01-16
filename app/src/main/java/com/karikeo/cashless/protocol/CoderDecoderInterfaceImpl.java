package com.karikeo.cashless.protocol;


import android.util.Log;

import com.karikeo.cashless.bt.Communication;
import com.karikeo.cashless.bt.OutputStream;
import com.karikeo.cashless.db.Transaction;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


//This class is one more layer in case we need to add transport information like CRC ID and so one.
public class CoderDecoderInterfaceImpl implements CoderDecoderInterface, Communication.DataCallback,
OutputStream{
    private static final String TAG = CoderDecoderInterfaceImpl.class.getSimpleName();
/*
    @Override
    public void write(byte[] b) throws IOException {
        code(new String(b));
    }
*/
    @Override
    public void onRawData(byte[] b) {
/*
        if(receiver!= null){
            receiver.OnPacket(new String(b));
        }
*/
        decode(new String(b));
    }

    @Override
    public void write(byte[] b) throws IOException {
        //if (stream!= null){
        //    stream.write(b);
        //}
        code(new String(b));
    }

    public interface OnPacket{
        void OnPacket(String message);
    }

    OnPacket receiver;

    private static final String MESSAGE_DIVIDER = "\r";

    private OutputStream stream;

    @Override
    public void code(String src) throws IOException{
        if (stream!= null) {
            final String cmd = new String(src + MESSAGE_DIVIDER);
            stream.write(cmd.getBytes());

            Log.d(TAG, "CMD: " + cmd);
        }
    }

    @Override
    public void decode(String src) {
        if (receiver!= null){
            receiver.OnPacket(src);
        }
    }

    @Override
    public void addOutputStream(OutputStream oStream) {
        stream = oStream;
    }

    @Override
    public void registerOnPacketListener(OnPacket msg) {
        receiver = msg;
    }

}

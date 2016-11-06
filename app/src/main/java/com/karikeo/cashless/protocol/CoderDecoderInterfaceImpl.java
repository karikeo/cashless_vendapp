package com.karikeo.cashless.protocol;


import android.util.Log;

import com.karikeo.cashless.bt.Communication;
import com.karikeo.cashless.bt.OutputStream;
import com.karikeo.cashless.db.Transaction;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


//This class is one more layer in case we need to add transport information like CRC ID and so one.
public class CoderDecoderInterfaceImpl implements CoderDecoderInterface, Communication.DataCallback{
    private static final String TAG = "com.karikeo.cashless.protocol.CoderDecoderInterfaceImpl";
/*
    @Override
    public void write(byte[] b) throws IOException {
        code(new String(b));
    }
*/
    @Override
    public void onRawData(byte[] b) {
        if(receiver!= null){
            receiver.OnPacket(new String(b));
        }
    }

    public interface OnPacket{
        void OnPacket(String message);
    }

    OnPacket receiver;

    private static final String MESSAGE_DIVIDER = "\n";
    private static final String FIELDS_DIVIDER = ",";

    private static final String TYPE_STATUS_OK = "OK";

    private OutputStream stream;

    @Override
    public void code(String src) throws IOException{
        if (stream!= null) {
            stream.write(new String(src + MESSAGE_DIVIDER).getBytes());
        }
    }

    @Override
    public void decode(String src) {
/*
        Transaction t = new Transaction();

        t.setStatus(false);

        String parts[] = src.split(FIELDS_DIVIDER);

        if (parts.length < 3){
            Log.d(TAG, "Unknown message:" + src);
            sendMessage(t);
            return;
        }

        t.setType(parts[0]);
        if (parts[1].equalsIgnoreCase(TYPE_STATUS_OK)){
            t.setStatus(true);
        }

        t.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Calendar.getInstance().getTime()));

        t.setBalanceDelta(parts[3]);

        sendMessage(t);
*/
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

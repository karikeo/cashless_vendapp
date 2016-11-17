package com.karikeo.cashless.protocol;


import android.util.Log;

import com.karikeo.cashless.bt.CommInterface;
import com.karikeo.cashless.bt.OutputStream;
import com.karikeo.cashless.db.Transaction;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CommandInterfaceImpl implements CommandInterface, CoderDecoderInterfaceImpl.OnPacket,
        OutputStream{
    private static final String TAG = "CommandInterfaceImpl";
    private static final String COMMAND_BALANCE = "BALANCE=";
    private static final String COMMAND_CANCEL = "CANCEL";
/*
    private static final String MESSAGE_DIVIDER = ";";

    private static final String TYPE_VEND = "VEND";
    private static final String TYPE_STATUS_OK = "OK";
*/
    private OnMessage listener;


    @Override
    public void sendBalance(float i) {
        write(COMMAND_BALANCE+Float.toString(i));
    }

    @Override
    public void sendCancel() {
        write(COMMAND_CANCEL);
    }

    private void write(String msg){
        if(stream != null){
            try {
                stream.write(msg.getBytes());
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void registerOnMessageListener(OnMessage listener) {
        this.listener = listener;
    }

    private OutputStream stream;
    @Override
    public void addOutputStream(OutputStream stream) {
        this.stream = stream;
    }

    private static final String FIELDS_DIVIDER = ",";
    private static final String TYPE_STATUS_OK = "OK";

    @Override
    public void OnPacket(String message) {
        Transaction t = new Transaction();

        t.setStatus(false);

        String parts[] = message.split(FIELDS_DIVIDER);

        if (parts.length < 3){
            Log.d(TAG, "Unknown message:" + message);
            //sendMessage(t);
            return;
        }

        t.setType(parts[0]);
        if (parts[1].equalsIgnoreCase(TYPE_STATUS_OK)){
            t.setStatus(true);
        }

        t.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Calendar.getInstance().getTime()));

        t.setBalanceDelta(parts[2]);

        if (listener != null) {
            listener.onMessage(t);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        if (stream!= null){
            stream.write(b);
        }
    }

    public interface OnMessage{
        void onMessage(Transaction t);
    }
}

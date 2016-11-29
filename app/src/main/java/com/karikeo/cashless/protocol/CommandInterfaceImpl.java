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

        if (parseMessage(message, t)) return;

        if (listener != null) {
            listener.onMessage(t);
        }
    }

    private boolean parseMessage(String message, Transaction t) {
        message = message.trim();

        if (message.equalsIgnoreCase("SESSION,COMPLETE")){
            t.setType(Transaction.TYPE.COMPLETE);
            return false;
        }

        if (message.equalsIgnoreCase("VEND,TIMEOUT")){
            t.setType(Transaction.TYPE.TIMEOUT);
            return false;
        }

        if (message.equalsIgnoreCase("VEND,FAIL")){
            t.setType(Transaction.TYPE.FAIL);
            return false;
        }

        String parts[] = message.split(FIELDS_DIVIDER);

        if (parts.length < 3){
            Log.d(TAG, "Unknown message:" + message);
            return true;
        }

        t.setType(Transaction.TYPE.BALANCE);

        t.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Calendar.getInstance().getTime()));

        t.setBalanceDelta(parts[2]);
        return false;
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

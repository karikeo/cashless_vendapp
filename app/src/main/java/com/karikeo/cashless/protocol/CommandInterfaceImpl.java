package com.karikeo.cashless.protocol;


import com.karikeo.cashless.bt.OutputStream;
import com.karikeo.cashless.db.Transaction;

import java.io.IOException;

public class CommandInterfaceImpl implements CommandInterface, CoderDecoderInterfaceImpl.OnPacket {
    private static final String COMMAND_BALANCE = "BALANCE=";
    private static final String COMMAND_CANCEL = "CANCEL";

    private static final String MESSAGE_DIVIDER = ";";

    private static final String TYPE_VEND = "VEND";
    private static final String TYPE_STATUS_OK = "OK";

    private OnMessage listener;


    @Override
    public void sendBalance(int i) {
        write(COMMAND_BALANCE+Integer.toString(i));
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

    @Override
    public void OnPacket(String message) {

    }

    public interface OnMessage{
        void onMessage(Transaction t);
    }
}

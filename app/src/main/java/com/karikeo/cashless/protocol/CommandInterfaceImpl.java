package com.karikeo.cashless.protocol;


import com.karikeo.cashless.bt.OutputStream;

public class CommandInterfaceImpl implements CommandInterface {
    private static final String COMMAND_BALANCE = "BALANCE=";
    private static final String COMMAND_CANCEL = "CANCEL";

    private static final String MESSAGE_DIVIDER = ";";

    private static final String TYPE_VEND = "VEND";
    private static final String TYPE_STATUS_OK = "OK";

    private OnMessage listener;


    @Override
    public void sendBalance(int i) {

    }

    @Override
    public void sendCancel() {

    }

    @Override
    public void registerListener(OnMessage listener) {
        this.listener = listener;
    }

    @Override
    public void addOutputStream(OutputStream stream) {

    }
}

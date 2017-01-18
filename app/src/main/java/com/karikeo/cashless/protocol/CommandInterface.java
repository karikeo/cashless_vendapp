package com.karikeo.cashless.protocol;


import com.karikeo.cashless.bt.OutputStream;


public interface CommandInterface {

    void sendBalance(float i);
    void sendCancel();

    void registerOnMessageListener(CommandInterfaceImpl.OnMessage listener);
    void addOutputStream(OutputStream stream);
}

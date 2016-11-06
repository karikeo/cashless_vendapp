package com.karikeo.cashless.protocol;


import com.karikeo.cashless.bt.OutputStream;
import com.karikeo.cashless.db.Transaction;


public interface CommandInterface {

    void sendBalance(int i);
    void sendCancel();

    void registerOnMessageListener(CommandInterfaceImpl.OnMessage listener);
    void addOutputStream(OutputStream stream);
}

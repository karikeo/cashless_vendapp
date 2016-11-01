package com.karikeo.cashless.ctrl;


public interface IMessageListener<T extends IMessage> {
    Class<T> getPacketClass();
    void onMessage(T message);
}

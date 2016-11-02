package com.karikeo.cashless.ctrl;

public interface IMessageListenerRegistry {
    void registerMessageListener(IMessageListener messageListener);
    void unregisterMessageListener(IMessageListener messageListener);
}

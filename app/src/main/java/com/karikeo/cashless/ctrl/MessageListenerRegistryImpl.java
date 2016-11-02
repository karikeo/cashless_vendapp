package com.karikeo.cashless.ctrl;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MessageListenerRegistryImpl implements IMessageListenerRegistry, IMessageListener{
    private static MessageListenerRegistryImpl instance;

    public synchronized static MessageListenerRegistryImpl getInstance(){
        if (instance == null){
            instance = new MessageListenerRegistryImpl();
        }

        return instance;
    }

    private MessageListenerRegistryImpl(){}


    private Set<IMessageListener> listeners = new HashSet<>();

    @Override
    public void registerMessageListener(IMessageListener messageListener) {
        listeners.add(messageListener);
    }

    @Override
    public void unregisterMessageListener(IMessageListener messageListener) {
        listeners.remove(messageListener);
    }

    @Override
    public void onMessage(String message) {
        for (IMessageListener l : listeners){
            l.onMessage(message);
        }
    }
}

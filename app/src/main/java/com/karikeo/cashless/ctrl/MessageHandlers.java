package com.karikeo.cashless.ctrl;


public class MessageHandlers <T extends IMessage>{
    final IMessageParser<T> parser;
    IMessageListener<T> listener;

    MessageHandlers(IMessageParser<T> parser){
        this.parser = parser;
    }

    void setListener(IMessageListener<T> listener){
        this.listener = listener;
    }
}

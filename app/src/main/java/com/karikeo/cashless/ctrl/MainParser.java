package com.karikeo.cashless.ctrl;


import java.util.HashMap;

public class MainParser implements IMessageParsersRegistry {

    private final HashMap<Class<?>, MessageHandlers<?>> handlers = new HashMap<>();
    private final HashMap<Integer, IMessageParser> parsers = new HashMap<>();

    @Override
    public <T extends IMessage> void registerMessageParser(IMessageParser messageParser) {
        parsers.put(messageParser.getId(), messageParser);

        Class<T> messageClass = messageParser.getMessageClass();
        handlers.put(messageClass, new MessageHandlers<IMessage>(messageParser));
    }

    @Override
    public <T extends IMessage> void registerMessageListener(IMessageListener messageListener) {

    }
}

package com.karikeo.cashless.ctrl;

public interface IMessageParsersRegistry {
    <T extends IMessage> void registerMessageParser(IMessageParser messageParser);
    <T extends IMessage> void registerMessageListener(IMessageListener messageListener);
}

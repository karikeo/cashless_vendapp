package com.karikeo.cashless.ctrl;



public interface IMessageParser<T extends IMessage> {
    Class<T> getMessageClass();
    int getPacketLength();
    int getId();
    IMessage parse(byte[] raw);
}

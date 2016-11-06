package com.karikeo.cashless.protocol;


import com.karikeo.cashless.bt.OutputStream;

import java.io.IOException;


public interface CoderDecoderInterface {
    void code(String src) throws IOException;
    void decode(String src);

    void addOutputStream(OutputStream oStream);
    void registerOnPacketListener(CoderDecoderInterfaceImpl.OnPacket msg);
}

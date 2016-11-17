package com.karikeo.cashless.serverrequests;


public interface OnAsyncServerRequest {
    void OnOk(String param);
    void OnError(String msg);
}

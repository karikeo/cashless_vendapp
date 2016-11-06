package com.karikeo.cashless.bt;

import android.app.Activity;

public interface CommInterface extends IOnBTActions {
    void openConnection(Activity activity, String id);
    void closeConnection();
    boolean isConnected();
    String getId();

    boolean checkId(String id);

    void registerOnRawData(Communication.DataCallback callback);
    void addAsyncResponseListener(BTOpenPortStatus actions);
}

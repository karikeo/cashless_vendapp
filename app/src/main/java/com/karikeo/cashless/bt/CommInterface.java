package com.karikeo.cashless.bt;

import android.app.Activity;

public interface CommInterface extends IOnBTActions{
    void openConnection(Activity activity, String id);
    void closeConnection();
    boolean isConnected();

    boolean checkId(String id);

    void addDataListener();
    void addAsyncResponseListener(BTOpenPortStatus actions);
}

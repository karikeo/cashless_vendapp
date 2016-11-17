package com.karikeo.cashless.serverrequests;


import android.os.Bundle;
import android.support.annotation.Nullable;

public interface OnAsyncServerRequest {
    void OnOk(@Nullable Bundle bundle);
    void OnError(String msg);
}

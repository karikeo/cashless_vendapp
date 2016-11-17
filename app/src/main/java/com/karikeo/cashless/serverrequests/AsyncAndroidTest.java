package com.karikeo.cashless.serverrequests;


import android.os.Bundle;
import android.util.Log;

import java.util.Map;

public class AsyncAndroidTest extends AsyncRequest{
    private final static String METHOD_NAME = "AndroidTest";

    public AsyncAndroidTest(OnAsyncServerRequest onRequest){
        super(METHOD_NAME);
        listener = onRequest;
    }

    @Override
    protected void onExecute(Map<String, String> key_value) {

    }

    @Override
    protected void onPostExecute(Bundle bundle) {
        if (bundle == null){
            sendError("Error during request");
            return;
        }
    }

    private void sendError(String msg) {
        Log.d(getClass().getSimpleName(), "onPostExecute: " + msg);
        if (listener != null){
            listener.OnError("Some Error Occur");
        }
    }
}

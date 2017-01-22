package com.karikeo.cashless.serverrequests;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Map;

public class AsyncSerialToMac extends AsyncRequest {

    private static final String TAG = AsyncSerialToMac.class.getSimpleName();
    private static final String METHOD_NAME = "GetVendicontMAC";

    final String serial;
    final OnAsyncServerRequest listener;

    public AsyncSerialToMac(@NonNull final String serial,  OnAsyncServerRequest request){
        super(METHOD_NAME);
        this.serial = serial;
        listener = request;
    }


    @Override
    protected void onExecute(Map<String, String> key_value) {
        key_value.put(PropertyFields.SERIAL, serial);
    }

    @Override
    protected void onPostExecute(Bundle bundle) {
        if (bundle == null){
            sendError("Error during request");
            return;
        }

        final String mac = bundle.getString(PropertyFields.SERIALMAC);
        if (mac == null){
            sendError("Can not find known parameter");
            return;
        }

        Log.d(TAG, "onPostExecute: MAC=" + mac);
        if (listener != null){
            listener.OnOk(bundle);
        }
    }

    private void sendError(String msg) {
        Log.d(TAG, "onPostExecute: " + msg);
        if (listener != null){
            listener.OnError("Some Error Occur");
        }
    }
}

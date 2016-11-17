package com.karikeo.cashless.serverrequests;

import android.os.Bundle;
import android.util.Log;

import java.util.Map;


public class AsyncGetBalance extends AsyncRequest{

    private static final String TAG = "AsyncGetBalance";
    private static final String METHOD_NAME = "GetSaldoActualizado";
/*
    private static final String LOGIN = "email";
    private static final String PWD = "password";
*/
    private String name;
    private String pwd;

    public AsyncGetBalance(String login, String pwd, OnAsyncServerRequest request){
        super(METHOD_NAME);
        this.name = login;
        this.pwd = pwd;
        listener = request;
    }

    @Override
    protected void onExecute(Map<String, String> key_value) {
        key_value.put(PropertyFields.EMAIL, name);
        key_value.put(PropertyFields.PWD, pwd);
    }

    @Override
    protected void onPostExecute(Bundle bundle) {
        if (bundle == null){
            sendError("Error during request");
            return;
        }

        final String serverBalance = bundle.getString(PropertyFields.BALANCE);
        if (serverBalance == null){
            sendError("Can not find known parameter");
            return;
        }

        Log.d(TAG, "onPostExecute: Balance=" + serverBalance);
        if (listener != null){
            listener.OnOk(serverBalance);
        }
    }

    private void sendError(String msg) {
        Log.d(TAG, "onPostExecute: " + msg);
        if (listener != null){
            listener.OnError("Some Error Occur");
        }
    }
}

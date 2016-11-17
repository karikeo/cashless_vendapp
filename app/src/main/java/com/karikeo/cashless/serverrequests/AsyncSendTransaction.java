package com.karikeo.cashless.serverrequests;

import android.os.Bundle;
import android.util.Log;

import com.karikeo.cashless.db.Transaction;

import java.util.Map;


public class AsyncSendTransaction extends AsyncRequest {

    private static final String TAG = "AsyncSendTransaction";
    private static final String METHOD_NAME = "NewTransaction";

    private Transaction transaction;

    public final static String TRANSACTION = "transaction";


    public AsyncSendTransaction(Transaction t, OnAsyncServerRequest request) {
        super(METHOD_NAME);
        listener = request;
        transaction = t;
    }


    @Override
    protected void onExecute(Map<String, String> key_value) {
        key_value.put(PropertyFields.EMAIL, transaction.getEmail());
        key_value.put(PropertyFields.TRANSACTION_TYPE, /*transaction.getType()*/ "1");
        key_value.put(PropertyFields.TRANSACTION_BALANCE, transaction.getBalanceDelta());
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
            listener.OnOk(null);
        }
    }

    private void sendError(String msg) {
        Log.d(TAG, "onPostExecute: " + msg);
        if (listener != null){
            listener.OnError("Some Error Occur");
        }
    }
}

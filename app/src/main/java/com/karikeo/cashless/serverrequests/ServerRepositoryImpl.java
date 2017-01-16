package com.karikeo.cashless.serverrequests;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.karikeo.cashless.db.Transaction;
import com.karikeo.cashless.model.InternetStatus;
import com.karikeo.cashless.model.localstorage.LocalStorage;

public class ServerRepositoryImpl implements ServerRepository{

    private final static String TAG = ServerRepositoryImpl.class.getSimpleName();

    LocalStorage localStorage;
    Context context;
    ServerRepository.Response listener;


    @Override
    public void login(@NonNull String email, @NonNull String pwd) {
        if (!InternetStatus.isOnline(context)){
            onError("No Internet Connection");
            return;
        }

        //Send Request in case of ok
        //1. Store email
        //2. Store hash
    }

    @Override
    public void isLogged() {
        //check hash is available
        //check hash is valid
    }

    @Override
    public void logout() {
        //clear hash
    }

    @Override
    public void getBalance() {
        isLogged();
        //use balance updater
    }

    @Override
    public void addTransaction(@NonNull Transaction t) {

    }

    @Override
    public void getLastTransactions(int num) {

    }

    @Override
    public void setOnResponse(@NonNull Response response) {

    }

    @Override
    public void clearOnResponse() {

    }

    private void onError(String msg){
        if (listener!= null)
            listener.Error(msg);
    }

    private void onOk(Bundle b){
        if (listener != null){
            listener.Ok(b);
        }
    }
}

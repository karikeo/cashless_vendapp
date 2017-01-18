package com.karikeo.cashless.serverrequests;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.karikeo.cashless.db.DatabaseRepository;
import com.karikeo.cashless.model.Transaction;
import com.karikeo.cashless.model.InternetStatus;
import com.karikeo.cashless.model.localstorage.LocalStorage;

import javax.inject.Inject;

public class ServerRepositoryImpl implements ServerRepository{

    private final static String TAG = ServerRepositoryImpl.class.getSimpleName();

    @Inject
    LocalStorage localStorage;

    @Inject
    Context context;

    @Inject
    DatabaseRepository databaseRepo;


    ServerRepository.Response listener;


    @Override
    public void login(@NonNull String email, @NonNull String pwd) {
        if (!InternetStatus.isOnline()){
            onError("No Internet Connection");
            return;
        }

        //Send Request in case of ok
        //1. Store email
        //2. Store hash
    }

    @Override
    public boolean isLogged() {
        //check hash is available
        //check hash is valid
        return false;
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
        //add transaction to the DB
    }

    @Override
    public void getPendingTransactions() {
        //return all pending transactions from the DB
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

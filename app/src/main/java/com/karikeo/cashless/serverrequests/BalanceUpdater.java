package com.karikeo.cashless.serverrequests;


import android.os.Bundle;
import android.util.Log;

import com.karikeo.cashless.CashlessApplication;
import com.karikeo.cashless.db.Transaction;
import com.karikeo.cashless.db.TransactionDataSource;

public class BalanceUpdater {
    private static final String TAG = "BalanceUpdater";

    private String login, password;

    private float currentBalance, serverBalance;
    private TransactionDataSource db;

    interface OnBalanceUpdateListener{
        void onUpdate(int balance);
    }

    OnBalanceUpdateListener listener;

    public BalanceUpdater(String login, String pwd, TransactionDataSource db){
        this.login = login;
        password = pwd;
        this.db = db;
    }

    public int getBalance(){
        updateBalance();
        return (int) currentBalance;
    }

    public void registerListener(OnBalanceUpdateListener update){
        listener = update;
    }

    public void updateBalance(){
        //try to upload everything to the server and get server balance after this

        b.execute();
    }

    public void updateBalance(Transaction t){

    }

    private AsyncGetBalance b = new AsyncGetBalance(login, password, new OnAsyncServerRequest() {
        @Override
        public void OnOk(Bundle bundle) {
            if(listener != null){
                int b =Float.valueOf(String.valueOf(bundle.getString(PropertyFields.BALANCE))).intValue();
                serverBalance = b;
                db.getBalanceDeltaFromAllTransactions();

                listener.onUpdate(b);
                Log.d(TAG, String.format("Updated from server with:%5d", b));
            }
        }

        @Override
        public void OnError(String msg) {
            Log.d(TAG, String.format("Can't connect to the server! BOOM! with msg %s", msg));
        }
    });


    private void uploadToServer(Transaction[] transactions){
        for (final Transaction tr : transactions){
            AsyncSendTransaction aT = new AsyncSendTransaction(tr, new OnAsyncServerRequest(){

                @Override
                public void OnOk(Bundle bundle) {
                    db.deleteTransaction(tr);
                }

                @Override
                public void OnError(String msg) {

                }
            });
            aT.execute();
        }
    }
}

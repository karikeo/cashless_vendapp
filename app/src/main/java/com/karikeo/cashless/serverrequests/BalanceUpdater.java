package com.karikeo.cashless.serverrequests;


import android.os.Bundle;
import android.util.Log;

import com.karikeo.cashless.db.Transaction;
import com.karikeo.cashless.db.TransactionDataSource;

public class BalanceUpdater {
    private static final String TAG = "BalanceUpdater";

    private String login, password;

    private float localBalance, serverBalance;
    private TransactionDataSource db;

    public interface OnBalanceUpdateListener{
        void onUpdate(int balance);
        void onError();
    }

    OnBalanceUpdateListener listener;


    public BalanceUpdater(TransactionDataSource db){
        this.db = db;
    }


    public int getBalance(){
        updateBalance();
        return getInternalBalance();
    }

    private int getInternalBalance(){
        final int lb = (int) (serverBalance - localBalance);
        return (lb >= 0) ? lb : 0;
    }

    public void registerListener(OnBalanceUpdateListener update){
        listener = update;
    }

    public void updateBalance(){
        //try to upload everything to the server and get server balance after this
        final Transaction t[] = db.getTransactions();
        if (t != null)
            uploadToServer(t);

    }

    //This functions used to get dirty balance it means that one transaction doesn't sore in the db.
    //Income transaction can be removed in case of error from VND.
    //So we need calculate balance without changing internal state.
    public int getDirtyBalance(Transaction t){
        final int lb = getInternalBalance();
        final int tb = lb - Float.valueOf(t.getBalanceDelta()).intValue();
        return tb >= 0 ? tb : 0;
    }

    public void getBalanceFromServer(String login, String pwd){
        this.login = login;
        password = pwd;

        final AsyncGetBalance b = new AsyncGetBalance(login, password, new OnAsyncServerRequest() {
            @Override
            public void OnOk(Bundle bundle) {
                if(listener != null){
                    int b =Float.valueOf(String.valueOf(bundle.getString(PropertyFields.BALANCE))).intValue();
                    serverBalance = b;
                    localBalance = db.getBalanceDeltaFromAllTransactions();

                    if (listener != null) {
                        listener.onUpdate(getInternalBalance());
                    }
                    Log.d(TAG, String.format("Updated from server with:%5d", b));
                }
            }

            @Override
            public void OnError(String msg) {
                if (listener != null){
                    listener.onError();
                }

                Log.d(TAG, String.format("Can't connect to the server! BOOM! with msg %s", msg));
            }
        });
        b.execute();
    }


    private void uploadToServer(Transaction[] transactions){
        for (final Transaction tr : transactions){
            db.updateSent(tr, 1);
            AsyncSendTransaction aT = new AsyncSendTransaction(tr, new OnAsyncServerRequest(){

                @Override
                public void OnOk(Bundle bundle) {
                    db.deleteTransaction(tr);
                    serverBalance -= Float.valueOf(tr.getBalanceDelta());
                    localBalance = db.getBalanceDeltaFromAllTransactions();

                    if (listener!= null){
                        listener.onUpdate(getInternalBalance());
                    }
                    Log.d(TAG, String.format("Removed transaction: %d",tr.getId()));
                }

                @Override
                public void OnError(String msg) {
                    db.updateSent(tr, 0);
                    Log.d(TAG, String.format("Can't upload transaction to the server with msg:%s", msg));
                }
            });
            aT.execute();
        }
    }
}

package com.karikeo.cashless.db;

import android.content.Context;

import java.lang.ref.WeakReference;

public class TransactionsAccess {
    private static TransactionsAccess instance;
    private static WeakReference<Context> c;


    public synchronized static TransactionsAccess getInstance(Context context){
        if (instance == null){
            c = new WeakReference<Context>(context);
            instance = new TransactionsAccess(context);
        }

        if (!c.equals(context)){
            instance = null;
            instance = new TransactionsAccess(context);
            c = new WeakReference<Context>(context);
        }

        return instance;
    }

    private TransactionDataSource d;

    private TransactionsAccess(Context c){
        d = new TransactionDataSource(c);
    }

    public TransactionDataSource getTransactionDataSource(){
        return d;
    }


}

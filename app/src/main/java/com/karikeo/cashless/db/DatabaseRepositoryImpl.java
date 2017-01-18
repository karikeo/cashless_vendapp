package com.karikeo.cashless.db;


import android.support.annotation.NonNull;

import com.karikeo.cashless.model.Transaction;

public class DatabaseRepositoryImpl implements DatabaseRepository{
    @Override
    public int getSumOfBalanceFromPendingTransactions() {
        return 0;
    }

    @Override
    public void addTransaction(@NonNull Transaction t) {

    }

    @Override
    public void deleteTransaction(@NonNull Transaction t) {

    }
}

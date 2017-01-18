package com.karikeo.cashless.db;


import android.support.annotation.NonNull;

import com.karikeo.cashless.model.Transaction;

public interface DatabaseRepository {
    int getSumOfBalanceFromPendingTransactions();

    void addTransaction(@NonNull Transaction t);
    void deleteTransaction(@NonNull Transaction t);
}

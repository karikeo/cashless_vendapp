package com.karikeo.cashless.serverrequests;


import android.os.Bundle;
import android.support.annotation.NonNull;

import com.karikeo.cashless.model.Transaction;

public interface ServerRepository {
    interface Response{
        void Ok(Bundle data);
        void Error(String msg);
    }

    void login(@NonNull String email, @NonNull String pwd);
    boolean isLogged();
    void logout();

    void getBalance();

    void addTransaction(@NonNull Transaction t);
    Transaction[] getPendingTransactions();

    void setOnResponse(@NonNull Response response);
    void clearOnResponse();
}

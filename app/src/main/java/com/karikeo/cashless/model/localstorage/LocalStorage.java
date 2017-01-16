package com.karikeo.cashless.model.localstorage;


import android.support.annotation.NonNull;

public interface LocalStorage {

    void setEmail(final String email);
    String getEmail();

    void setHashKey(final String hash);
    String getHashKey();

    void setLocalBalance(final String localBalance);
    String getLocalBalance();
}

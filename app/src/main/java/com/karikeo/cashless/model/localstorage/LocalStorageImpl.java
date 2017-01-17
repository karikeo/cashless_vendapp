package com.karikeo.cashless.model.localstorage;


import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public class LocalStorageImpl implements LocalStorage{
    private static final String SHARED_PREFS_BALANCE = "stored_balance";
    private static final String SHARED_PREFS_EMAIL = "stored_email";
    private static final String SHARED_PREFS_SERVER_KEY = "server_key";

    private SharedPreferences prefs;

    public LocalStorageImpl(SharedPreferences prefs){
        this.prefs = prefs;
    }


    @Override
    public void setEmail(@NonNull String email) {
        storeString(SHARED_PREFS_EMAIL, email);
    }

    @Override
    public String getEmail() {
        return readString(SHARED_PREFS_EMAIL, null);
    }

    @Override
    public void setHashKey(@NonNull String hash) {
        storeString(SHARED_PREFS_SERVER_KEY, hash);
    }

    @Override
    public String getHashKey() {
        return readString(SHARED_PREFS_SERVER_KEY, null);
    }

    @Override
    public void setLocalBalance(String localBalance) {
        storeString(SHARED_PREFS_BALANCE, localBalance);
    }

    @Override
    public String getLocalBalance() {
        return readString(SHARED_PREFS_BALANCE, null);
    }

    private void storeString(@NonNull final String key, @NonNull String value){

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @NonNull
    private String readString(@NonNull String key){
        return readString(key, "");
    }

    @NonNull
    private String readString(@NonNull String key, @NonNull String defValue){
        return prefs.getString(key, defValue);
    }
}

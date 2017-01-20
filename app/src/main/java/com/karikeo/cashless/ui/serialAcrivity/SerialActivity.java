package com.karikeo.cashless.ui.serialAcrivity;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SerialActivity extends AppCompatActivity implements SerialContract.View{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void hideProgressDialog() {

    }

    @Override
    public void showErrorHint(@NonNull String errMsg) {

    }

    @Override
    public void finishWithResult(int result, String data) {

    }
}

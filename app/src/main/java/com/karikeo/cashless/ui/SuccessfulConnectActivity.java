package com.karikeo.cashless.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.karikeo.cashless.R;

public class SuccessfulConnectActivity extends AppCompatActivity {

    private TextView balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful_connect);

        balance = (TextView) findViewById(R.id.balance);

        setDataFromModel();
    }

    private void setDataFromModel() {
        balance.setText("1000$");
    }
}

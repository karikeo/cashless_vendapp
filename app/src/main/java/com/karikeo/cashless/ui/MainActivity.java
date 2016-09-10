package com.karikeo.cashless.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.karikeo.cashless.R;

public class MainActivity extends AppCompatActivity {

    private TextView balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        balance = (TextView) findViewById(R.id.balance);
        getSetDataFromModel();
    }

    private void getSetDataFromModel() {
        balance.setText("1000$");
    }
}

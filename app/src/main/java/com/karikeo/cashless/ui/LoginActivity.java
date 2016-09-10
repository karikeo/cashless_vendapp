package com.karikeo.cashless.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.karikeo.cashless.R;

public class LoginActivity extends ProgressBarActivity {

    private TextView login;
    private TextView password;
    private Button loginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        login = (TextView) findViewById(R.id.login);
        password = (TextView) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(login.getText().toString(), password.getText().toString());
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    private void login(String login, String password) {
        showProgress(R.string.logging_in);
        loginButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                onLogin();
            }
        }, 2000);
    }

    private void updateBalance() {
        showProgress(R.string.update_balance);
        loginButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                onBalanceUpdated();
            }
        }, 2000);
    }

    public void onLogin() {
        updateBalance();
    }

    public void onBalanceUpdated() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

}

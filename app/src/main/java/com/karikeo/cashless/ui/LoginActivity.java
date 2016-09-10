package com.karikeo.cashless.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.karikeo.cashless.R;

public class LoginActivity extends AppCompatActivity {

    private TextView login;
    private TextView password;
    private Button loginButton;
    private ProgressBar progressBar;
    private View content;
    private View progress;
    private TextView progressText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressText = (TextView) findViewById(R.id.progress_text);
        content = findViewById(R.id.content);
        progress = findViewById(R.id.progress);

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

    private void login(String login, String password) {
        content.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        progressText.setText(R.string.logging_in);

        content.postDelayed(new Runnable() {
            @Override
            public void run() {
                onLogin();
            }
        }, 2000);
    }

    private void updateBalance() {
        content.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        progressText.setText(R.string.update_balance);

        content.postDelayed(new Runnable() {
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

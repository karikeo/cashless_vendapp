package com.karikeo.cashless.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.karikeo.cashless.Constants;
import com.karikeo.cashless.R;
import com.karikeo.cashless.serverrequests.AsyncGetBalance;
import com.karikeo.cashless.serverrequests.OnAsyncServerRequest;
import com.karikeo.cashless.serverrequests.PropertyFields;

public class LoginActivity extends ProgressBarActivity {

    private TextView login;
    private TextView password;
    private Button loginButton;

    private String balance = "0";
    private String email;

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

        if (Constants.DEBUG != 0) {
            //onBalanceUpdated();
            login("spb@gmail.com", "1111");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    private void login(final String login, String password) {
        UIUtil.hideKeyboard(loginButton);
        showProgress(R.string.logging_in);

        AsyncGetBalance b = new AsyncGetBalance(login, password, new OnAsyncServerRequest() {
            @Override
            public void OnOk(Bundle bundle) {
                balance = bundle.getString(PropertyFields.BALANCE);
                email = login;
                onLogin();
            }

            @Override
            public void OnError(String msg) {
                //Ooops something wrong
            }
        });
        b.execute();
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
        Bundle b = new Bundle();
        b.putString(PropertyFields.BALANCE,  balance);
        b.putString(PropertyFields.EMAIL, email);

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtras(b);

        startActivity(intent);
    }

}

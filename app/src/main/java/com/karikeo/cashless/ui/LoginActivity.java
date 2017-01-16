package com.karikeo.cashless.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.karikeo.cashless.CashlessApplication;
import com.karikeo.cashless.Constants;
import com.karikeo.cashless.model.InternetStatus;
import com.karikeo.cashless.R;
import com.karikeo.cashless.model.NfcTagValidator;
import com.karikeo.cashless.serverrequests.BalanceUpdater;
import com.karikeo.cashless.serverrequests.PropertyFields;

public class LoginActivity extends ProgressBarActivity {
    private final static String TAG = "LoginActivity";

    private TextView login;
    private TextView password;
    private Button loginButton;

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

/*
        if (Constants.DEBUG != 0) {
            //onBalanceUpdated();
            login("spb@gmail.com", "1111");
        }
*/
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String bt = NfcTagValidator.getBlueToothAddress(getIntent());
    }

    @Override
    protected void onResume() {
        super.onStart();

        login.setText("");
        password.setText("");

        final String storedEmail = ((CashlessApplication)getApplication()).getLocalStorage().getEmail();
        if (storedEmail != null) {
            login.setText(storedEmail);
            password.requestFocus();
        }

        String bt = NfcTagValidator.getBlueToothAddress(getIntent());
/*
        if (InternetStatus.isOnline(this)){
            login()
        }
*/
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    private void login(final String login, String password) {
        UIUtil.hideKeyboard(loginButton);
        showProgress(R.string.logging_in);

        final BalanceUpdater b = ((CashlessApplication)getApplication()).getBalanceUpdater();
        b.registerListener(new BalanceUpdater.OnBalanceUpdateListener() {
            @Override
            public void onUpdate(int balance) {
                email = login;
                onLogin();

                b.registerListener(null);
            }

            @Override
            public void onError() {
                Log.d(TAG, String.format("Can't login to the server"));
            }
        });
        b.getBalanceFromServer(login, password);
    }


    public void onLogin() {
        //Store used email to restore nex time.
        ((CashlessApplication)getApplication()).getLocalStorage().setEmail(email);
        updateBalance();
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


    public void onBalanceUpdated() {
        ((CashlessApplication)getApplication()).getLocalStorage().setHashKey(password.getText().toString());


        Bundle b = new Bundle();
        b.putString(PropertyFields.EMAIL, email);

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtras(b);

        startActivity(intent);
    }

}

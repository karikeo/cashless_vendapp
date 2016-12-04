package com.karikeo.cashless.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.karikeo.cashless.CashlessApplication;
import com.karikeo.cashless.Constants;
import com.karikeo.cashless.R;
import com.karikeo.cashless.db.TransactionDataSource;
import com.karikeo.cashless.serverrequests.BalanceUpdater;
import com.karikeo.cashless.serverrequests.PropertyFields;

import static com.karikeo.cashless.CashlessApplication.SHARED_PREFS;
import static com.karikeo.cashless.CashlessApplication.SHARED_PREFS_EMAIL;

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

        SharedPreferences settings = getSharedPreferences(SHARED_PREFS, 0);
        final String storedEmail = settings.getString(SHARED_PREFS_EMAIL, null);
        if (storedEmail != null) {
            login.setText(storedEmail);
            password.requestFocus();
        }

        TransactionDataSource db = ((CashlessApplication)getApplication()).getDbAccess();
        if (db == null){
            db = new TransactionDataSource(getApplication().getApplicationContext());
            ((CashlessApplication)getApplication()).setTransactionAccess(db);
        }

        BalanceUpdater b = new BalanceUpdater(db);
        ((CashlessApplication)getApplication()).setBalanceUpdater(b);


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
        SharedPreferences settings = getSharedPreferences(SHARED_PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SHARED_PREFS_EMAIL, email);
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
        Bundle b = new Bundle();
        b.putString(PropertyFields.EMAIL, email);

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtras(b);

        startActivity(intent);
    }

}

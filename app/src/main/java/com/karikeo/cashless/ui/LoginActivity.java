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
import com.karikeo.cashless.model.localstorage.LocalStorage;
import com.karikeo.cashless.serverrequests.BalanceUpdater;

public class LoginActivity extends ProgressBarActivity {
    private final static String TAG = LoginActivity.class.getSimpleName();

    private TextView login;
    private TextView password;
    private Button loginButton;

    private String email;

    private String macAddr;

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
            login("spb@gmail.com", "1111");
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        macAddr = NfcTagValidator.getBlueToothAddress(getIntent());
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

        macAddr = NfcTagValidator.getBlueToothAddress(getIntent());

        checkLoginAndRun();
    }

    private void checkLoginAndRun() {
        final LocalStorage s = ((CashlessApplication)getApplication()).getLocalStorage();

        if ((InternetStatus.isOnline(this)) && (s.getEmail() != null && s.getHashKey () !=null)){
                login(s.getEmail(), s.getHashKey());
        }else{
            //how we check that we works offline???
            if (s.getHashKey()!=null)
                onBalanceUpdated();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    private void login(final String login, final String password) {
        UIUtil.hideKeyboard(loginButton);
        showProgress(R.string.logging_in);

        final BalanceUpdater b = ((CashlessApplication)getApplication()).getBalanceUpdater();
        b.registerListener(new BalanceUpdater.OnBalanceUpdateListener() {
            @Override
            public void onUpdate(int balance) {
                ((CashlessApplication)getApplication()).getLocalStorage().setHashKey(password);
                email = login;
                onLogin();

                b.registerListener(null);
            }

            @Override
            public void onError() {
                Log.d(TAG, String.format("Can't login to the server"));
                showContent();
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

        Bundle b = new Bundle();
        b.putString(MainActivity.EMAIL_KEY, email);
        if (macAddr != null){
            b.putString(MainActivity.MACADDR_KEY, macAddr);
        }

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtras(b);

        startActivity(intent);
        finish();
    }

}

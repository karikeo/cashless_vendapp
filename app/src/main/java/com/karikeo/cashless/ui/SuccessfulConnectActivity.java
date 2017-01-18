package com.karikeo.cashless.ui;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.karikeo.cashless.CashlessApplication;
import com.karikeo.cashless.R;
import com.karikeo.cashless.model.Transaction;
import com.karikeo.cashless.db.TransactionDataSource;
import com.karikeo.cashless.protocol.CommandInterface;
import com.karikeo.cashless.protocol.CommandInterfaceImpl;
import com.karikeo.cashless.serverrequests.BalanceUpdater;
import com.karikeo.cashless.serverrequests.PropertyFields;

public class SuccessfulConnectActivity extends AppCompatActivity {

    final static String TAG = "SuccessfulConnectA";

    private TextView balance;
    private Transaction transaction;

    private String email;

    BalanceUpdater updater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful_connect);

        balance = (TextView) findViewById(R.id.balance);

        Bundle b = getIntent().getExtras();
        if (b != null){
            email = b.getString(PropertyFields.EMAIL);
        }

        updater = ((CashlessApplication)getApplication()).getBalanceUpdater();

        setDataFromModel(null);

        final CommandInterface comm  = ((CashlessApplication)getApplication()).getCommandInterface();

        comm.registerOnMessageListener(new CommandInterfaceImpl.OnMessage() {
            @Override
            public void onMessage(Transaction t) {
                if (t.getType() == Transaction.TYPE.BALANCE){
                    //dirty transaction
                    transaction = t;
                    setDataFromModel(updater.getDirtyBalance(t));

                    Log.d(TAG, String.format("BalanceDelta=%5d", (int)Float.parseFloat(t.getBalanceDelta())));
                } else if (t.getType() == Transaction.TYPE.COMPLETE){

                    //store dirty transaction
                    if (transaction != null){
                        TransactionDataSource db = ((CashlessApplication)getApplication()).getDbAccess();

                        db.createTransaction(transaction.getType().toString(),
                                transaction.getBalanceDelta(),
                                ((CashlessApplication)getApplication()).getCommInterface().getId(), email);

                        setDataFromModel(null);
                        transaction = null;
                    }
                    closeActivity();
                } else if (t.getType() == Transaction.TYPE.TIMEOUT){
                    transaction = null;
                    closeActivity();
                } else if (t.getType() == Transaction.TYPE.FAIL){
                    transaction = null;
                    closeActivity();
                }
            }
        });

        (findViewById(R.id.disconnect)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CashlessApplication)getApplication()).getCommandInterface().sendCancel();
            }
        });

        updateBalanceOnTarget();
    }

    private void setDataFromModel(@Nullable final Integer dirty) {
        int currentBalance = (dirty == null) ? updater.getBalance() : dirty;
        balance.setText(String.format("%5d", (int)currentBalance));
        Log.d(TAG, String.format("balance on screen:%5d", (int)currentBalance));
    }


    private void closeActivity(){
        Log.d(TAG, String.format("closeActivity:%5d", updater.getBalance()));
        ((CashlessApplication)getApplication()).getCommInterface().closeConnection();
        finish();
    }

    private void updateBalanceOnTarget(){
        CommandInterface comm  = ((CashlessApplication)getApplication()).getCommandInterface();
        comm.sendCancel();
        try {
            Thread.sleep(500);
        }catch (Exception e){
            e.printStackTrace();
        }
        comm.sendBalance(updater.getBalance());
        Log.d(TAG, String.format("sent to vnd:%5d", updater.getBalance()));
    }
}

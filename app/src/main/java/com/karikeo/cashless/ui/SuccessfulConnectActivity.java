package com.karikeo.cashless.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.karikeo.cashless.CashlessApplication;
import com.karikeo.cashless.R;
import com.karikeo.cashless.bt.BlueToothControl;
import com.karikeo.cashless.db.Transaction;
import com.karikeo.cashless.db.TransactionDataSource;
import com.karikeo.cashless.protocol.CommandInterface;
import com.karikeo.cashless.protocol.CommandInterfaceImpl;
import com.karikeo.cashless.serverrequests.PropertyFields;

public class SuccessfulConnectActivity extends AppCompatActivity {

    final static String TAG = "SuccessfulConnectA";

    private TextView balance;
    private Transaction transaction;

    private float currentBalance = 0;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful_connect);

        balance = (TextView) findViewById(R.id.balance);

        Bundle b = getIntent().getExtras();
        if (b != null){
            currentBalance = Float.valueOf(b.getString(PropertyFields.BALANCE, "0"));
            email = b.getString(PropertyFields.EMAIL);
        }

        Log.d(TAG, String.format("balance:%5d", (int)currentBalance));

        //setDataFromModel();
        final CommandInterface comm  = ((CashlessApplication)getApplication()).getCommandInterface();


        comm.registerOnMessageListener(new CommandInterfaceImpl.OnMessage() {
            @Override
            public void onMessage(Transaction t) {
                if (t.getType() == Transaction.TYPE.BALANCE){
                    //dirty transaction
                    transaction = t;
                    currentBalance -= Float.parseFloat(t.getBalanceDelta());
                    if (currentBalance<0){
                        currentBalance = 0;
                    }
                    setDataFromModel();

                    Log.d(TAG, String.format("BalanceDelta=%5d", (int)Float.parseFloat(t.getBalanceDelta())));
                } else if (t.getType() == Transaction.TYPE.COMPLETE){
                    //store dirty transaction
                    if (transaction != null){
                        TransactionDataSource db = ((CashlessApplication)getApplication()).getDbAccess();

                        Transaction tr = db.createTransaction(transaction.getType().toString(),
                                transaction.getBalanceDelta(),
                                ((CashlessApplication)getApplication()).getCommInterface().getId(), email);

                        currentBalance -= Float.parseFloat(tr.getBalanceDelta());
                        if (currentBalance<0){
                            currentBalance = 0;
                        }
                        setDataFromModel();
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

        ((Button)findViewById(R.id.disconnect)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CashlessApplication)getApplication()).getCommandInterface().sendCancel();
            }
        });

        updateBalanceOnTarget(currentBalance);
    }

    private void setDataFromModel() {
        balance.setText(String.format("%5d", (int)currentBalance));
        Log.d(TAG, String.format("balance on screen:%5d", (int)currentBalance));
    }


    private void closeActivity(){
        Log.d(TAG, String.format("closeActivity:%5d", (int)currentBalance));
        ((CashlessApplication)getApplication()).getCommInterface().closeConnection();
        finish();
    }

    private void updateBalanceOnTarget(float i){
        CommandInterface comm  = ((CashlessApplication)getApplication()).getCommandInterface();
        comm.sendCancel();
        try {
            Thread.sleep(500);
        }catch (Exception e){

        }
        comm.sendBalance(currentBalance);
        Log.d(TAG, String.format("sent to vnd:%5d", (int)currentBalance));
    }
}

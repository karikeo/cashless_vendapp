package com.karikeo.cashless.ui.serialAcrivity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.karikeo.cashless.R;
import com.karikeo.cashless.serverrequests.PropertyFields;
import com.karikeo.cashless.ui.ProgressBarActivity;

public class SerialActivity extends ProgressBarActivity implements SerialContract.View{

    EditText serialNumber;
    SerialContract.UserAction userAction;
    SerialContract.ViewLifeCycle lifeCycle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        serialNumber = (EditText)findViewById(R.id.serialnumber);

        findViewById(R.id.request_serial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAction.enterSerialValue(serialNumber.getText().toString());
            }
        });

        SerialPresenter p = new SerialPresenter(this);
        userAction = p;
        lifeCycle = p;

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_serial;
    }

    @Override
    public void showProgressDialog() {
        showProgress(R.string.serial_request);
    }

    @Override
    public void hideProgressDialog() {
        showContent();
    }

    @Override
    public void showErrorHint(@NonNull int errMsgId) {
        showToolTip(errMsgId);
    }


    @Override
    public void finishWithResult(int result, @NonNull String data) {
        setResult(result, new Intent().putExtra(PropertyFields.SERIAL, data));
        finish();
    }
}

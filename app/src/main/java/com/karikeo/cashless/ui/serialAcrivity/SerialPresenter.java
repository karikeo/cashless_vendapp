package com.karikeo.cashless.ui.serialAcrivity;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.karikeo.cashless.R;
import com.karikeo.cashless.model.InternetStatus;
import com.karikeo.cashless.serverrequests.AsyncSerialToMac;
import com.karikeo.cashless.serverrequests.OnAsyncServerRequest;
import com.karikeo.cashless.serverrequests.PropertyFields;

import static android.app.Activity.RESULT_OK;

public class SerialPresenter implements SerialContract.UserAction, SerialContract.ViewLifeCycle {
    SerialContract.View listener;


    public SerialPresenter(@NonNull final SerialContract.View listener){
        this.listener = listener;
    }


    @Override
    public void enterSerialValue(@NonNull String serial) {
        listener.showProgressDialog();

        new AsyncSerialToMac(serial, new OnAsyncServerRequest() {
            @Override
            public void OnOk(@Nullable Bundle bundle) {
                listener.finishWithResult(RESULT_OK, bundle.getString(PropertyFields.SERIAL));
            }

            @Override
            public void OnError(String msg) {
                //If troubles with network?
                listener.showErrorHint(R.string.serial_error);
            }
        }).execute();

        listener.hideProgressDialog();
    }
}

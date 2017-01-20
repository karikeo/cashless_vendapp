package com.karikeo.cashless.ui.serialAcrivity;


import android.support.annotation.NonNull;

public interface SerialContract {
    interface View{
        void showProgressDialog();
        void hideProgressDialog();

        void showErrorHint(@NonNull final int errMsgId);

        void finishWithResult(int result, String data);
    }

    interface UserAction {
        void enterSerialValue(@NonNull final String serial);
    }

    interface ViewLifeCycle{

    }

}

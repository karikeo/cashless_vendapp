package com.karikeo.cashless.ui.serialAcrivity;


import android.support.annotation.NonNull;

interface SerialContract {
    interface View{
        void showProgressDialog();
        void hideProgressDialog();

        void showErrorHint(final int errMsgId);

        void finishWithResult(int result, String data);
    }

    interface UserAction {
        void enterSerialValue(@NonNull final String serial);
    }

    interface ViewLifeCycle{

    }

}

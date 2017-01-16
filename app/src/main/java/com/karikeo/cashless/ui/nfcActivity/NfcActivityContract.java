package com.karikeo.cashless.ui.nfcActivity;


import android.content.Intent;
import android.support.annotation.NonNull;

public class NfcActivityContract {

    interface View{
        void enableNfcAdapter(int code);
        void enableNfcForeground();
        void disableNfcForeground();
        void showErrorPopUp();

        void finishWithNoResults();
        void finishWithResult(int code, String data);
    }

    interface Action{
        void onActivityResult(int requestCode, int resultCode, Intent data);
        void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
        void onNewIntent(Intent intent);
    }


    interface LifeCycle{
        void onResume();
        void onPause();
    }
}

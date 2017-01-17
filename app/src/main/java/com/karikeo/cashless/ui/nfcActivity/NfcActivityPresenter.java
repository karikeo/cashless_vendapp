package com.karikeo.cashless.ui.nfcActivity;


import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.support.annotation.NonNull;

import com.karikeo.cashless.model.NfcTagValidator;


public class NfcActivityPresenter implements NfcActivityContract.Action, NfcActivityContract.LifeCycle{
    private final static String TAG =  NfcActivityPresenter.class.getSimpleName();

    private final static int ENABLE_NFC_CODE = 0x2231;

    private NfcActivityContract.View listener;
    private NfcAdapter nfcAdapter;

    public NfcActivityPresenter(@NonNull NfcActivityContract.View listener, @NonNull NfcAdapter adapter){
        this.listener = listener;
        nfcAdapter = adapter;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENABLE_NFC_CODE){
            if (resultCode == Activity.RESULT_CANCELED){
                listener.finishWithResult(requestCode, null);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    @Override
    public void onNewIntent(Intent intent) {
        //Verify NFC tag.
        if (!NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))
            return;

        String mac = NfcTagValidator.getBlueToothAddress(intent);
        if (mac == null){
            listener.showErrorPopUp();
        }else {
            listener.finishWithResult(Activity.RESULT_OK, mac);
        }
    }

    @Override
    public void onResume() {
        if (nfcAdapter == null){
            listener.finishWithNoResults();
            return;
        }

        if (!nfcAdapter.isEnabled()){
            listener.enableNfcAdapter(ENABLE_NFC_CODE);
            return;
        }

        listener.enableNfcForeground();
    }

    @Override
    public void onPause() {
        listener.disableNfcForeground();
    }
}

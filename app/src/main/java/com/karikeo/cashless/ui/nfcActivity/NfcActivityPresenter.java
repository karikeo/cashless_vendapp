package com.karikeo.cashless.ui.nfcActivity;


import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import java.nio.charset.StandardCharsets;

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
                //TODO:Oops can't work here without finish.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    @Override
    public void onNewIntent(Intent intent) {
        final String a = intent.getAction();
        Log.d(TAG, String.format("onNewIntent: %s", a));
        if (intent != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
            Parcelable[] rawMessage =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (rawMessage != null){
                NdefMessage[] messages = new NdefMessage[rawMessage.length];
                for (int i = 0; i<rawMessage.length; i++){
                    messages[i] = (NdefMessage) rawMessage[i];
                    NdefRecord[] records = messages[i].getRecords();
                        for (NdefRecord rec : records){
                            byte[] payloadData = rec.getPayload();
                            Log.d(TAG, String.format("Payload income: %s", new String(payloadData)));
                            byte[] typeData = rec.getType();
                            Log.d(TAG, String.format("Type income: %s", new String(typeData)));
                            byte[] typeID = rec.getId();
                            Log.d(TAG, String.format("ID income: %s", new String(typeID)));
                        }
                        // do something with the payload (data passed through your NDEF record)
                        // or process remaining NDEF message
                }
            }
        }
    }

    @Override
    public void onResume() {
        if (nfcAdapter == null){
            //can't work without adapter.
            //Maybe add this check to the MainWindow and disable button in case of.
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

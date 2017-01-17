package com.karikeo.cashless.model;


import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

/*
    Valid format is:
    Message with 2 records.
    1st - text/com.karikeo.cashless-BT MAC.
    2nd - AAR message with com.karikeo.cashless
 */


public class NfcTagValidator {
    private static final String TAG = NfcTagValidator.class.getSimpleName();

    private static final String pack = "com.karikeo.cashless";

    private NfcTagValidator(){}

    @Nullable
    public static String getBlueToothAddress(Intent intent){
        final String a = intent.getAction();
        if (intent != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
            Parcelable[] rawMessage =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (rawMessage != null){
                NdefMessage[] messages = new NdefMessage[rawMessage.length];
                for (int i = 0; i<rawMessage.length; i++){
                    messages[i] = (NdefMessage) rawMessage[i];
                    NdefRecord[] records = messages[i].getRecords();

                    if (records.length != 2) return null;

                    for (NdefRecord rec : records){
                        byte[] payloadData = rec.getPayload();
                        final String payload = new String(payloadData);
                        Log.d(TAG, String.format("Payload income: %s", payload));
                        if (payload.contains(pack)){
                            return payload.substring(payload.indexOf("-")+1);
                        }
                    }
                }
            }
        }
        return null;
    }
}

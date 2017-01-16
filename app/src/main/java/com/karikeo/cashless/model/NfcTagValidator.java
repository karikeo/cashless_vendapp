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
    1st - text/com.karikeo.cashless:BT MAC.
    2nd - AAR message with com.karikeo.cashless
 */
        //01-16 01:10:19.450 18458-18458/com.karikeo.cashless D/NfcTagValidator: Payload income: encom.karikeo.cashless-10:20:30:40:50:60
        //01-16 01:10:20.830 18458-18458/com.karikeo.cashless D/NfcTagValidator: Type income: T
        //01-16 01:10:22.309 18458-18458/com.karikeo.cashless D/NfcTagValidator: ID income:
        //01-16 01:10:22.309 18458-18458/com.karikeo.cashless D/NfcTagValidator: Payload income: com.karikeo.cashless
        //01-16 01:10:22.309 18458-18458/com.karikeo.cashless D/NfcTagValidator: Type income: android.com:pkg
        //01-16 01:10:22.309 18458-18458/com.karikeo.cashless D/NfcTagValidator: ID income:

public class NfcTagValidator {
    private static final String TAG = NfcTagValidator.class.getSimpleName();

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
        return null;
    }
}

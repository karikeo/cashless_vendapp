package com.karikeo.cashless.ui.nfcActivity;


import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.karikeo.cashless.R;

public class NfcActivity extends AppCompatActivity implements NfcActivityContract.View{
    private final static String TAG = NfcActivity.class.getSimpleName();

    public final static String MAC_ADDR = "macAddr";

    private NfcActivityPresenter presenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, String.format("onCreate: Intent : Action : %s", getIntent().getAction()));

        presenter = new NfcActivityPresenter(this, NfcAdapter.getDefaultAdapter(this));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        presenter.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        presenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void enableNfcAdapter(int code) {
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            startActivityForResult(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS), code);
        } else {
            startActivityForResult(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS), code);
        }
    }

    @Override
    public void finishWithNoResults(){
        finish();
    }

    @Override
    public void finishWithResult(int code, String data) {
        if (data != null){
            setResult(code, new Intent().putExtra(MAC_ADDR, data));
        }else{
            setResult(code);
        }

        finishWithNoResults();
    }

    @Override
    public void enableNfcForeground() {
        Log.d(TAG, "enableNfcForeground");
        Intent nfcIntent = new Intent(this, getClass());
        nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent nfcPendingIntent =
                PendingIntent.getActivity(this, 0, nfcIntent, 0);

        IntentFilter tagIntentFilter =
                new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            tagIntentFilter.addDataType("text/plain");
        }
        catch (Throwable t) {
            t.printStackTrace();
        }

        IntentFilter[] intentFiltersArray = new IntentFilter[]{tagIntentFilter};
        NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, nfcPendingIntent, intentFiltersArray, null);


        presenter.onNewIntent(getIntent());
    }

    @Override
    public void disableNfcForeground() {
        Log.d(TAG, "disableNfcForeground");
        NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
    }

    @Override
    public void showErrorPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.nfc_tag_error);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.create().show();
    }
}

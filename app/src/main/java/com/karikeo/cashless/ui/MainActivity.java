package com.karikeo.cashless.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.karikeo.cashless.R;
import com.karikeo.cashless.bt.BTControl;
import com.karikeo.cashless.ui.barcode.BarcodeCaptureActivity;

import java.util.Set;

public class MainActivity extends ProgressBarActivity {
    private static final int RC_BARCODE_CAPTURE = 9001;
    private TextView balance;

    private BTControl btControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        balance = (TextView) findViewById(R.id.balance);
        findViewById(R.id.qr_code_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BarcodeCaptureActivity.class);
                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        });
        setDataFromModel();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    connect(barcode.displayValue);
                } else {
                    Toast.makeText(this, R.string.barcode_failure, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == BTControl.REQUEST_ENABLE_BT){
                btControl
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    private void connect(String id) {
/*
        showProgress(R.string.connecting);
        balance.postDelayed(new Runnable() {
            @Override
            public void run() {
                onConnect();
            }
        }, 2000);
*/
        if (!BluetoothAdapter.checkBluetoothAddress(id)){
            //Oops wrong address.
            return;
        }

        btControl = new BTControl(this, id);
        btControl.openConnection();

    }

    public void onConnect() {
        Intent intent = new Intent(MainActivity.this, SuccessfulConnectActivity.class);
        startActivity(intent);
    }

    private void setDataFromModel() {
        balance.setText("1000$");
    }
}

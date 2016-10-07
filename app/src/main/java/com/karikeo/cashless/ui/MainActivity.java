package com.karikeo.cashless.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.karikeo.cashless.Constants;
import com.karikeo.cashless.R;
import com.karikeo.cashless.bt.BTControl;
import com.karikeo.cashless.bt.IOnBTOpenPort;
import com.karikeo.cashless.ui.barcode.BarcodeCaptureActivity;

import java.io.IOException;

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

        if (Constants.DEBUG != 0){
            connect("03:F6:07:01:91:FE");
        }

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
            if (requestCode == Activity.RESULT_CANCELED){
                //TODO Show error. we can't work without BT.
            } else {
                btControl.onDeviceEnabled();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    private void connect(String id) {

        if (!BluetoothAdapter.checkBluetoothAddress(id)){
            //Oops wrong address.
            return;
        }

        btControl = BTControl.getInstance(this, id);
        btControl.openConnection(new IOnBTOpenPort() {
            @Override
            public void onBTOpenPortDone() {
                try {
                    btControl.sendBalance(1000);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onBTOpenPortError() {
                //TODO: add actions if something wrong with BT
                btControl.close();
            }
        });

    }

    public void onConnect() {
        Intent intent = new Intent(MainActivity.this, SuccessfulConnectActivity.class);
        startActivity(intent);
    }

    private void setDataFromModel() {
        balance.setText("1000$");
    }
}

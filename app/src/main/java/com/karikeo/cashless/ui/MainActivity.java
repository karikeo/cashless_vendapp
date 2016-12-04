package com.karikeo.cashless.ui;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.karikeo.cashless.CashlessApplication;
import com.karikeo.cashless.Constants;
import com.karikeo.cashless.R;
import com.karikeo.cashless.bt.BlueToothControl;
import com.karikeo.cashless.bt.BTOpenPortStatus;
import com.karikeo.cashless.bt.CommInterface;
import com.karikeo.cashless.bt.Communication;
import com.karikeo.cashless.bt.OutputStream;
import com.karikeo.cashless.db.TransactionDataSource;
import com.karikeo.cashless.protocol.CoderDecoderInterface;
import com.karikeo.cashless.protocol.CoderDecoderInterfaceImpl;
import com.karikeo.cashless.protocol.CommandInterface;
import com.karikeo.cashless.protocol.CommandInterfaceImpl;
import com.karikeo.cashless.serverrequests.PropertyFields;
import com.karikeo.cashless.ui.barcode.BarcodeCaptureActivity;

import static com.karikeo.cashless.CashlessApplication.SHARED_PREFS;
import static com.karikeo.cashless.CashlessApplication.SHARED_PREFS_EMAIL;


public class MainActivity extends ProgressBarActivity {
    private final static String TAG = "MainActivity";

    private static final int RC_BARCODE_CAPTURE = 0x9001;
    private static final int PM_CAMERA_PERMISSION_REQUEST = 0x9002;
    private static final int PM_BT_PERMISSION_REQUEST = 0x9003;

    private static String[] PERMISSIONS_BT = {Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN};

    private TextView balance;

    private CommInterface blueToothControl;
    private String qrCode;

    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        balance = (TextView) findViewById(R.id.balance);
        findViewById(R.id.qr_code_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    cameraPermissionRequest();
                    Log.d(TAG, "onClick: Request camera permission");
                }else {
                    startCapturingCode();
                }
            }
        });

        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(SHARED_PREFS, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(SHARED_PREFS_EMAIL, null);
                editor.commit();

                finish();
            }
        });

        Bundle b = getIntent().getExtras();
        if (b != null){
            email = b.getString(PropertyFields.EMAIL);
        }

/*TODO REMOVE*//*
        Transaction[] t = new Transaction[1];
        t[0] = new Transaction();
        t[0].setEmail(email);
        t[0].setBalanceDelta("150");
        t[0].setType("1");
        uploadToServer(t);
/*REMOVE*/

        setupCommunication();

        if (Constants.DEBUG != 0){
            //currentBalance = 10002;
            connect("10:14:07:10:29:10");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDataFromModel();
    }


    /*Setup communication chain*/
    private void setupCommunication() {
        blueToothControl = ((CashlessApplication)getApplication()).getCommInterface();
        if (blueToothControl == null){
            blueToothControl = new BlueToothControl(getApplication());
            ((CashlessApplication)getApplication()).setCommInterface(blueToothControl);
        }

        CommandInterface comm  = ((CashlessApplication)getApplication()).getCommandInterface();
        if (comm == null){
            comm = new CommandInterfaceImpl();
            ((CashlessApplication)getApplication()).setCommandInterface(comm);
        }

        CoderDecoderInterface cd = ((CashlessApplication)getApplication()).getCoderDecoderInterface();
        if (cd == null){
            cd = new CoderDecoderInterfaceImpl();
            ((CashlessApplication)getApplication()).setCoderDecoderInterface(cd);
        }

        TransactionDataSource db = ((CashlessApplication)getApplication()).getDbAccess();
        if (db == null){
            db = new TransactionDataSource(getApplication().getApplicationContext());
            ((CashlessApplication)getApplication()).setTransactionAccess(db);
        }

        //Setup Download chain
        cd.addOutputStream((OutputStream)blueToothControl);
        comm.addOutputStream((OutputStream)(cd));

        //Setup Upload chain
        blueToothControl.registerOnRawData((Communication.DataCallback) cd);
        cd.registerOnPacketListener((CommandInterfaceImpl)comm);

    }

    @Override
    protected void onStop() {
        super.onStop();

        ((CashlessApplication)getApplication()).getDbAccess().close();
    }

    private void cameraPermissionRequest() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}
                , PM_CAMERA_PERMISSION_REQUEST);
    }

    private void requestBTPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_BT, PM_BT_PERMISSION_REQUEST);
    }

    private void startCapturingCode() {
        Intent intent = new Intent(MainActivity.this, BarcodeCaptureActivity.class);
        startActivityForResult(intent, RC_BARCODE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    qrCode = barcode.displayValue;

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
                            != PackageManager.PERMISSION_GRANTED){
                        requestBTPermissions();
                    }else {
                        connect(qrCode);
                    }

                } else {
                    Toast.makeText(this, R.string.barcode_failure, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == BlueToothControl.REQUEST_ENABLE_BT){
            if (requestCode == Activity.RESULT_CANCELED){
                Log.d(TAG, "onActivityResult: Can't work without BlueTooth");
            } else {
                blueToothControl.onDeviceEnabled();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PM_CAMERA_PERMISSION_REQUEST){
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: Camera granted");
                startCapturingCode();
            }
        }else if(requestCode == PM_BT_PERMISSION_REQUEST){
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "onRequestPermissionsResult: BlueTooth granted");
                connect(qrCode);
            }
        }else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    private void connect(String id) {
        Log.d(TAG, "connect:<<"+id+">> attempt to connect");

        if (!BluetoothAdapter.checkBluetoothAddress(id)){
            Log.d(TAG, "connect:<<"+id+">> Wrong Address");
            return;
        }

        if (!blueToothControl.isConnected()) {
            blueToothControl.addAsyncResponseListener(new BTOpenPortStatus() {
                @Override
                public void onBTOpenPortDone() {

                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(400);

                    onConnect();
                }

                @Override
                public void onBTOpenPortError() {
                    //TODO: add actions if something wrong with BT
                    Log.d(TAG, "onBTOpenPortError: Can't open BlueTooth port");
                    blueToothControl.closeConnection();
                }
            });

            blueToothControl.openConnection(MainActivity.this, id);
        }
    }

    public void onConnect() {
        Bundle b = new Bundle();
        b.putString(PropertyFields.EMAIL, email);

        Intent intent = new Intent(MainActivity.this, SuccessfulConnectActivity.class);
        intent.putExtras(b);

        startActivity(intent);
    }

    private void setDataFromModel() {
        final int b = ((CashlessApplication)getApplication()).getBalanceUpdater().getBalance();
        balance.setText(String.format("%5d", b));
        Log.d(TAG, String.format("Balance on Screen = %5d", b));
    }
}

package com.karikeo.cashless.bt;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;

public class BTControl {
    public static final int REQUEST_ENABLE_BT = 0x922625;

    private Activity mActivity;
    private String mBTId;

    public BTControl(Activity activity, String btID){
        mActivity = activity;
        mBTId = btID;
    }

    public void openConnection(){

    }


}

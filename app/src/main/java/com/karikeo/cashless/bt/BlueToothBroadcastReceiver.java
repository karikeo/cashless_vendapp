package com.karikeo.cashless.bt;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BlueToothBroadcastReceiver extends android.content.BroadcastReceiver{
    BluetoothDevice device;

    IBTBroadcastReceiverListener listener;
    Context tContext;

    public BlueToothBroadcastReceiver(Activity a, IBTBroadcastReceiverListener l) {
        tContext = a.getApplicationContext();
        listener = l;
    }

    public void register(){

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        tContext.registerReceiver(this, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        tContext.registerReceiver(this, filter);
    }

    public void unregister(){
        tContext.getApplicationContext().unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Get the BluetoothDevice object from the Intent
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            listener.OnDiscoveryDeviceFound(device);
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
            listener.OnDiscoveryFinished();
        }
    }

}

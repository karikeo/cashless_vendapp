package com.karikeo.cashless.bt;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

public class BlueToothBroadcastReceiver extends BroadcastReceiver {
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

        filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        tContext.registerReceiver(this, filter);

        filter = new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST");
        tContext.registerReceiver(this, filter);

        Log.d("BROADCAST", "register");
    }

    public void unregister(){
        tContext.getApplicationContext().unregisterReceiver(this);
        Log.d("BROADCAST", "unregister");
    }

    @Override
    public void onReceive(Context context, final Intent intent) {
        final String action = intent.getAction();
        Log.d("ACTION: ", action);

        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Get the BluetoothDevice object from the Intent
            listener.OnDiscoveryDeviceFound(device);
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
            listener.OnDiscoveryFinished();
        }

        if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
            final int state        = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
            final int prevState    = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

            if ((state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED)) {
                try {
                    Method method = device.getClass().getMethod("createBond", (Class[]) null);
                    method.invoke(device, (Object[]) null);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if((state == BluetoothDevice.BOND_BONDED)){
                listener.OnPairDone(device);
            }else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDING){
                listener.OnPairError();
            }
        }

        if ("android.bluetooth.device.action.PAIRING_REQUEST".equals(action)){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            final Method m2 = device.getClass().getMethod("setPin", new Class[]{Array.newInstance(byte.class, 4).getClass()});
                            m2.invoke(device, "1234".getBytes());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).run();
        }

    }

}

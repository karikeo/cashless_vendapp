package com.karikeo.cashless.bt;

import android.bluetooth.BluetoothDevice;

interface IBTBroadcastReceiverListener {

    void OnDiscoveryDeviceFound(BluetoothDevice device);

    void OnDiscoveryFinished();

    void OnPairDone(BluetoothDevice device);
    void OnPairError();
}

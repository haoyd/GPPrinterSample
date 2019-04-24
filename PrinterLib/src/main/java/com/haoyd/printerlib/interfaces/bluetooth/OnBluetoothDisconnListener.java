package com.haoyd.printerlib.interfaces.bluetooth;


import com.haoyd.printerlib.entities.BluetoothDeviceInfo;

public interface OnBluetoothDisconnListener {

    void onDisconnected(BluetoothDeviceInfo info);

}

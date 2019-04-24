package com.haoyd.printerlib.interfaces.bluetooth;


import com.haoyd.printerlib.entities.BluetoothDeviceInfo;

public interface OnBluetoothPairdChangedListener {
    void onPared(BluetoothDeviceInfo info);
    void onDispared(BluetoothDeviceInfo info);
}

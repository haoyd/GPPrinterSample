package com.haoyd.printerlib.interfaces.bluetooth;


import com.haoyd.printerlib.entities.BluetoothDeviceInfo;

public interface OnFindNewBluetoothListener {

    void onFindNew(BluetoothDeviceInfo info);

    void onFindPaird(BluetoothDeviceInfo info);

}

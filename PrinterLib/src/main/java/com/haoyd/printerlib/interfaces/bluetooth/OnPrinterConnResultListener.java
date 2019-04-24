package com.haoyd.printerlib.interfaces.bluetooth;


import com.haoyd.printerlib.entities.BluetoothDeviceInfo;

public interface OnPrinterConnResultListener {

    void onResult(BluetoothDeviceInfo info, boolean isSucc);

}

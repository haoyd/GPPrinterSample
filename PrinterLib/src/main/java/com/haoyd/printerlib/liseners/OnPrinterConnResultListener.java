package com.haoyd.printerlib.liseners;

import com.haoyd.printerlib.entities.BluetoothDeviceInfo;

public interface OnPrinterConnResultListener {

    void onResult(BluetoothDeviceInfo info, boolean isSucc);

}

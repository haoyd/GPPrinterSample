package com.haoyd.printerlib.manager;

import com.haoyd.printerlib.entities.BluetoothDeviceInfo;

public class GPPrinterConnectingManager {

    private BluetoothDeviceInfo connectingDeviceInfo;

    private static volatile GPPrinterConnectingManager singleton;
    private GPPrinterConnectingManager() {
    }

    public static GPPrinterConnectingManager getInstance() {
        if (singleton == null) {
            synchronized (GPPrinterConnectingManager.class) {
                if (singleton == null) {
                    singleton = new GPPrinterConnectingManager();
                }
            }
        }
        return singleton;
    }

    public void setConnectingDeviceInfo(BluetoothDeviceInfo connectingDeviceInfo) {
        this.connectingDeviceInfo = connectingDeviceInfo;
    }

    public BluetoothDeviceInfo getConnectingDeviceInfo() {
        return connectingDeviceInfo;
    }
}
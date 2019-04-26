package com.haoyd.printerlib.manager;

import com.haoyd.printerlib.entities.BluetoothDeviceInfo;

public class PrinterConnectingManager {

    private BluetoothDeviceInfo connectingDeviceInfo;

    private static volatile PrinterConnectingManager singleton;
    private PrinterConnectingManager() {
    }

    public static PrinterConnectingManager getInstance() {
        if (singleton == null) {
            synchronized (PrinterConnectingManager.class) {
                if (singleton == null) {
                    singleton = new PrinterConnectingManager();
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
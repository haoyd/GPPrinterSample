package com.haoyd.printerlib.entities;

import android.text.TextUtils;

import java.io.Serializable;

public class BluetoothDeviceInfo implements Serializable {

    public static final String GROUP_PAIRED = "我的设备";
    public static final String GROUP_OTHERS = "可用设备";
    private static final String PRINTER_NAME_PREFIX = "Printer_";

    /**
     * business data
     */
    public String name = "";
    public String address = "";

    /**
     * custom data
     */
    public boolean isShowGroup = false;
    public String groupName = "";
    public boolean isConnected = false;

    public BluetoothDeviceInfo(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public BluetoothDeviceInfo() {
    }

    /**
     * 根据名称来判断是不是打印机设备
     * @return
     */
    public boolean isPrinterDevice() {
        if (!TextUtils.isEmpty(name) && name.startsWith(PRINTER_NAME_PREFIX)) {
            return true;
        }

        return false;
    }

    /**
     * 设备名称是否为空
     * @return
     */
    public boolean isNameEmpty() {
        if (TextUtils.isEmpty(name)) {
            return true;
        }

        return false;
    }

}

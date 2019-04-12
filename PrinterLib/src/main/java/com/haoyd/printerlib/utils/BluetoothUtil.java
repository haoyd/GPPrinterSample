package com.haoyd.printerlib.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

import java.util.Set;

public class BluetoothUtil {

    /**
     * 是否支持蓝牙
     * @return
     */
    public static boolean isSupportBluetooth() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            return false;
        }

        return true;
    }

    /**
     * 是否开启蓝牙
     * @return
     */
    public static boolean isOpening() {
        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 蓝牙是否处于关闭状态
     * @return
     */
    public static boolean isClosed() {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 强制开启蓝牙
     */
    public static void forceOpenBluetooth() {
        try {
            BluetoothAdapter.getDefaultAdapter().enable();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * 强制关闭蓝牙
     */
    public static void forceCloseBluetooth() {
        try {
            BluetoothAdapter.getDefaultAdapter().disable();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * 已经配对设备中是否包含某一设备
     * @param name
     * @return
     */
    public static boolean isContainsDevice(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }

        boolean isContains = false;

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (!TextUtils.isEmpty(device.getName()) && device.getName().equals(name)) {
                    isContains = true;
                }
            }
        }

        return isContains;
    }
}

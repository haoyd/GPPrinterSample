package com.haoyd.printerlib.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.haoyd.printerlib.entities.BluetoothDeviceInfo;
import com.zhimadj.utils.Jackson;

public class GPPrinterDao {

    private static final String KEY_PRINT_LAST_CONNCT_DEVICE = "PrintLastConnDevice";                   // 最后一次连接打印设备信息

    private static volatile GPPrinterDao singleton;
    private SharedPreferences preferences = null;
    private SharedPreferences.Editor editor = null;

    private BluetoothDeviceInfo bluetoothDeviceInfo = null;

    private GPPrinterDao(Context context) {
        preferences = context.getSharedPreferences("GPPrinterDAO", 0);
        editor = preferences.edit();
    }

    public static GPPrinterDao getInstance(Context context) {
        if (singleton == null) {
            synchronized (GPPrinterDao.class) {
                if (singleton == null) {
                    singleton = new GPPrinterDao(context);
                }
            }
        }
        return singleton;
    }

    /**
     * 获取最近连接小票打印机信息
     * @return
     */
    public BluetoothDeviceInfo getBluetoothDeviceInfo() {
        if (bluetoothDeviceInfo == null) {
            String deviceJson = preferences.getString(KEY_PRINT_LAST_CONNCT_DEVICE, null);
            if (!TextUtils.isEmpty(deviceJson)) {
                bluetoothDeviceInfo = Jackson.toObject(deviceJson, BluetoothDeviceInfo.class);
            }
        }
        return bluetoothDeviceInfo;
    }

    /**
     * 设置最近连接小票机
     * @param bluetoothDeviceInfo
     */
    public void setBluetoothDeviceInfo(BluetoothDeviceInfo bluetoothDeviceInfo) {
        this.bluetoothDeviceInfo = bluetoothDeviceInfo;
        if (bluetoothDeviceInfo != null) {
            editor.putString(KEY_PRINT_LAST_CONNCT_DEVICE, Jackson.toJson(bluetoothDeviceInfo)).apply();
        } else {
            editor.putString(KEY_PRINT_LAST_CONNCT_DEVICE, "").apply();
        }
    }

    /**
     * 是否有历史记录
     * @return
     */
    public boolean hasHistoryPrinter() {
        if (getBluetoothDeviceInfo() == null) {
            return false;
        }

        return true;
    }

    /**
     * 清除历史记录
     */
    public void clearHistory() {
        setBluetoothDeviceInfo(null);
    }


}
package com.haoyd.printerlib.manager;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;

public class GPBluetoothDeviceManager {

    private Activity mActivity;
    private BluetoothAdapter mBluetoothAdapter;

    public GPBluetoothDeviceManager(Activity mActivity) {
        this.mActivity = mActivity;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * 扫描蓝牙设备
     */
    public void scanDevice() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
    }

    /**
     * 停止扫描
     */
    public void cancelScan() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    /**
     * 是否正在扫描
     */
    public boolean isScaning() {
        return mBluetoothAdapter.isDiscovering();
    }
}

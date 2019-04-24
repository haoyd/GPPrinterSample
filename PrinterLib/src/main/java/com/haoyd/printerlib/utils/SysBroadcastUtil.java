package com.haoyd.printerlib.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.haoyd.printerlib.interfaces.bluetooth.OnBluetoothConnectedListener;
import com.haoyd.printerlib.interfaces.bluetooth.OnBluetoothDisconnListener;
import com.haoyd.printerlib.interfaces.bluetooth.OnBluetoothPairdChangedListener;
import com.haoyd.printerlib.interfaces.bluetooth.OnBluetoothStateChangeListener;
import com.haoyd.printerlib.interfaces.bluetooth.OnFindNewBluetoothListener;
import com.haoyd.printerlib.interfaces.bluetooth.OnFinishDiscoveryBluetoothListener;

public class SysBroadcastUtil extends BaseSysBroadcastUtil{


    public SysBroadcastUtil(Activity mActivity) {
        super(mActivity);
    }

    /**
     * 注册发现新设备
     * @param listener
     */
    public void setOnDiscoveryNewBluetoothListener(OnFindNewBluetoothListener listener) {
        mOnFindListener = listener;
        registReceiver(BluetoothDevice.ACTION_FOUND);
    }

    /**
     * 完成蓝牙扫描
     * @param listener
     */
    public void setOnFinishDiscoveryBluetoothListener(OnFinishDiscoveryBluetoothListener listener) {
        mFinishDiscoveryBluetoothListener = listener;
        registReceiver(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
    }

    /**
     * 设备配对监听
     * @param listener
     */
    public void setOnSysPairedListener(OnBluetoothPairdChangedListener listener) {
        mPairdChangedListener = listener;
        registReceiver(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
    }

    /**
     * 监听蓝牙设备的打开与关闭
     * @param listener
     */
    public void setOnBluetoothStateChangeListener(OnBluetoothStateChangeListener listener) {
        mBluetoothStateChangeListener = listener;
        registReceiver(BluetoothAdapter.ACTION_STATE_CHANGED);
    }

    /**
     * 蓝牙设备连接成功
     * @param listener
     */
    public void setOnBluetoothConnectListener(OnBluetoothConnectedListener listener) {
        mOnBluetoothConnListener = listener;
        registReceiver(BluetoothDevice.ACTION_ACL_CONNECTED);
    }

    /**
     * 蓝牙设备连接失败
     * @param listener
     */
    public void setOnBluetoothDisconnListener(OnBluetoothDisconnListener listener) {
        mOnBluetoothDisconnListener = listener;
        registReceiver(BluetoothDevice.ACTION_ACL_DISCONNECTED);
    }


}

package com.haoyd.printerlib.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.haoyd.printerlib.entities.BluetoothDeviceInfo;
import com.haoyd.printerlib.interfaces.bluetooth.OnBluetoothConnectedListener;
import com.haoyd.printerlib.interfaces.bluetooth.OnBluetoothDisconnListener;
import com.haoyd.printerlib.interfaces.bluetooth.OnBluetoothPairdChangedListener;
import com.haoyd.printerlib.interfaces.bluetooth.OnBluetoothStateChangeListener;
import com.haoyd.printerlib.interfaces.bluetooth.OnFindNewBluetoothListener;
import com.haoyd.printerlib.interfaces.bluetooth.OnFinishDiscoveryBluetoothListener;

public class BaseSysBroadcastUtil {

    protected Activity mActivity;

    protected OnFindNewBluetoothListener mOnFindListener;
    protected OnBluetoothPairdChangedListener mPairdChangedListener;
    protected OnFinishDiscoveryBluetoothListener mFinishDiscoveryBluetoothListener;
    protected OnBluetoothStateChangeListener mBluetoothStateChangeListener;
    protected OnBluetoothConnectedListener mOnBluetoothConnListener;
    protected OnBluetoothDisconnListener mOnBluetoothDisconnListener;

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothDevice.ACTION_FOUND:
                    processFindNewBluetooth(intent);
                    Log.d("bluetooth", ">>>>>>> 找到新蓝牙设备");
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    processFinishDiscoveryBluetooth(intent);
                    Log.d("bluetooth",">>>>>>> 完成蓝牙扫描");
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    processBluetoothPairdChange(intent);
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    processBluetoothOpenState(intent);
                    break;
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    processBluetoothConnSuccess(intent);
                    Log.d("bluetooth",">>>>>>> 蓝牙设备连接");
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    processBluetoothDisconnected(intent);
                    Log.d("bluetooth",">>>>>>> 蓝牙设备断开");
                    break;
            }
        }
    };

    public BaseSysBroadcastUtil(Activity mActivity) {
        this.mActivity = mActivity;
    }

    protected void registReceiver(String action) {
        if (mActivity == null || mActivity.isFinishing() || TextUtils.isEmpty(action)) {
            return;
        }

        mActivity.registerReceiver(mReceiver, new IntentFilter(action));
    }

    public void unregistReceiver() {
        mActivity.unregisterReceiver(mReceiver);
    }

    /**
     * 找到新的蓝牙设备
     * @param intent
     */
    private void processFindNewBluetooth(Intent intent) {
        if (intent == null || mOnFindListener == null) {
            return;
        }

        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
            mOnFindListener.onFindNew(new BluetoothDeviceInfo(device.getName(), device.getAddress()));
        } else {
            mOnFindListener.onFindPaird(new BluetoothDeviceInfo(device.getName(), device.getAddress()));
        }
    }

    /**
     * 完成蓝牙扫描
     * @param intent
     */
    private void processFinishDiscoveryBluetooth(Intent intent) {
        if (intent == null || mFinishDiscoveryBluetoothListener == null) {
            return;
        }

        mFinishDiscoveryBluetoothListener.onFinish();
    }

    /**
     * 蓝牙配对成功与否
     * @param intent
     */
    private void processBluetoothPairdChange(Intent intent) {
        if (intent == null || mPairdChangedListener == null) {
            return;
        }

        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
        switch (state) {
            case BluetoothDevice.BOND_NONE:
                // 删除配对
                mPairdChangedListener.onDispared(new BluetoothDeviceInfo(device.getName(), device.getAddress()));
                Log.d("bluetooth",">>>>>>> 删除蓝牙配对");
                break;
            case BluetoothDevice.BOND_BONDING:
                // 正在配对

                break;
            case BluetoothDevice.BOND_BONDED:
                // 配对成功
                mPairdChangedListener.onPared(new BluetoothDeviceInfo(device.getName(), device.getAddress()));
                Log.d("bluetooth",">>>>>>> 添加蓝牙配对");
                break;
        }
    }

    /**
     * 蓝牙打开状态
     * @param intent
     */
    private void processBluetoothOpenState(Intent intent) {
        if (intent == null || mBluetoothStateChangeListener == null) {
            return;
        }

        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

        switch (state) {
            case BluetoothAdapter.STATE_OFF:
                // 关闭
                mBluetoothStateChangeListener.stateOff();
                Log.d("bluetooth",">>>>>>> 蓝牙关闭");
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                // 正在关闭
                break;
            case BluetoothAdapter.STATE_ON:
                // 打开
                mBluetoothStateChangeListener.stateOn();
                Log.d("bluetooth",">>>>>>> 蓝牙打开");
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                // 正在打开
                break;
        }
    }

    /**
     * 蓝牙设备连接成功
     * @param intent
     */
    private void processBluetoothConnSuccess(Intent intent) {
        if (intent == null || mOnBluetoothConnListener == null) {
            return;
        }

        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        mOnBluetoothConnListener.onConnected(new BluetoothDeviceInfo(device.getName(), device.getAddress()));
    }

    /**
     * 蓝牙设备连接断开
     * @param intent
     */
    private void processBluetoothDisconnected(Intent intent) {
        if (intent == null || mOnBluetoothDisconnListener == null) {
            return;
        }

        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        mOnBluetoothDisconnListener.onDisconnected(new BluetoothDeviceInfo(device.getName(), device.getAddress()));
    }

}

package com.haoyd.printerlib.manager;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.gprinter.aidl.GpService;
import com.gprinter.command.GpCom;
import com.gprinter.io.GpDevice;
import com.gprinter.io.PortParameters;
import com.gprinter.service.GpPrintService;
import com.haoyd.printerlib.dao.GPPrinterDao;
import com.haoyd.printerlib.entities.BluetoothDeviceInfo;
import com.haoyd.printerlib.liseners.OnPrintResultListener;
import com.haoyd.printerlib.receivers.PrinterBroadcastReceiver;
import com.haoyd.printerlib.utils.BluetoothUtil;

import java.util.Timer;
import java.util.TimerTask;

import static com.haoyd.printerlib.GPPrinterConstant.DEFAULT_PRINTER_ID;
import static com.haoyd.printerlib.GPPrinterConstant.MAIN_QUERY_PRINTER_STATUS;

/**
 * 该类集成了打印机操作的一些基本功能
 * 1、连接、断开打印机
 * 2、判断是否已经连接
 * 3、获取打印状态
 * 4、设置打印结果监听
 * 5、打印小票
 */
public class BaseGPPrinterManager {

    private Activity mActivity;
    private PrinterServiceConnection conn = null;
    private GpService mGpService = null;
    private PrinterBroadcastReceiver printerBroadcastReceiver = null;
    private boolean needListenPrinterStatus = false;
    private Timer timer = null;

    public BaseGPPrinterManager(Activity mActivity) {
        this.mActivity = mActivity;
        printerBroadcastReceiver = new PrinterBroadcastReceiver();
    }

    /**
     * 绑定打印服务
     */
    public void bindService() {
        conn = new PrinterServiceConnection();
        Intent intent = new Intent(mActivity, GpPrintService.class);
        mActivity.bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService

        // 注册实时状态查询广播
        mActivity.registerReceiver(printerBroadcastReceiver, new IntentFilter(GpCom.ACTION_DEVICE_REAL_STATUS));
        // 票据模式下，可注册该广播，在需要打印内容的最后加入addQueryPrinterStatus()，在打印完成后会接收到
        mActivity.registerReceiver(printerBroadcastReceiver, new IntentFilter(GpCom.ACTION_RECEIPT_RESPONSE));
    }

    /**
     * 解绑打印服务
     */
    public void unbindService() {
        if (conn != null) {
            mActivity.unbindService(conn);
        }
        mActivity.unregisterReceiver(printerBroadcastReceiver);
        timer.cancel();
        timer.purge();
        timer = null;
    }

    /**
     * 是否连接到打印机
     * @return
     */
    public boolean isConnecting() {
        try {
            if (mGpService != null && mGpService.getPrinterConnectStatus(0) == GpDevice.STATE_CONNECTED) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 打印打印机当前状态
     */
    public void queryPrinterStatus() {
        try {
            mGpService.queryPrinterStatus(DEFAULT_PRINTER_ID, 1500, MAIN_QUERY_PRINTER_STATUS);
        } catch (RemoteException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    /**
     * 设置打印状态监听
     * @param listener
     */
    public void setOnPrinterConnResultListener(final OnPrintResultListener listener) {
        if (printerBroadcastReceiver != null && listener != null) {
            printerBroadcastReceiver.setOnPrintResultListener(new OnPrintResultListener() {
                @Override
                public void onPrintSucc() {
                    listener.onPrintSucc();
                    if (timer != null) {
                        timer.cancel();
                        timer.purge();
                        timer = null;
                    }
                }

                @Override
                public void onPrintError(String error) {
                    listener.onPrintError(error);
                    if (timer != null) {
                        timer.cancel();
                        timer.purge();
                        timer = null;
                    }
                }
            });
        }
    }

    /**
     * 指定要连接的打印机
     * @param info
     * @return true：连接成功   false：连接失败
     */
    public void connectToPrinter(BluetoothDeviceInfo info) {
        if (info == null) {
            toast("数据错误");
            return;
        }

        if (!BluetoothUtil.isOpening()) {
            toast("请先开启蓝牙");
            return;
        }

        int connStatus = 0;

        try {
            // 如果打印机已经连接了，就不要再重复连接了
            if (mGpService != null && mGpService.getPrinterConnectStatus(0) == GpDevice.STATE_CONNECTED) {
                return;
            }

            connStatus = mGpService.openPort(DEFAULT_PRINTER_ID, PortParameters.BLUETOOTH, info.address, 0);
        } catch (Exception e) {
            toast("连接失败");
            return;
        }

        GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[connStatus];

        if (r != GpCom.ERROR_CODE.SUCCESS) {
            toast(GpCom.getErrorText(r));
            return;
        }

        info.isConnected = true;
        GPPrinterDao.getInstance(mActivity).setBluetoothDeviceInfo(info);
        GPPrinterConnectingManager.getInstance().setConnectingDeviceInfo(info);
    }

    /**
     * 断开与打印机的连接
     */
    public void disConnectToPrinter() {
        try {
            if (mGpService != null && mGpService.getPrinterConnectStatus(0) == GpDevice.STATE_CONNECTED) {
                mGpService.closePort(DEFAULT_PRINTER_ID);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 打印小票信息
     * @param data
     * @return
     */
    public boolean printTicket(String data) {
        int rs;
        try {
            rs = mGpService.sendEscCommand(DEFAULT_PRINTER_ID, data);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rs];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Toast.makeText(mActivity.getApplicationContext(), GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }

        if (needListenPrinterStatus) {
            timer = new Timer();
            timer.schedule(new ListenPrinterTask(), 0, 1000);
        }

        return true;
    }

    /**
     * 连接到历史打印机
     */
    public void connToHistoryDevice() {
        BluetoothDeviceInfo bluetoothDeviceInfo = GPPrinterDao.getInstance(mActivity).getBluetoothDeviceInfo();

        if (bluetoothDeviceInfo != null) {
            connectToPrinter(bluetoothDeviceInfo);
        }
    }

    /**
     * 打印过程中会监听打印机状态异常
     */
    public void setListenPrinterStatus(boolean needListenPrinterStatus) {
        this.needListenPrinterStatus = needListenPrinterStatus;
    }

    private void toast(String msg) {
        if (mActivity == null || TextUtils.isEmpty(msg)) {
            return;
        }

        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    class PrinterServiceConnection implements ServiceConnection {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mGpService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mGpService = GpService.Stub.asInterface(service);
        }
    }

    class ListenPrinterTask extends TimerTask {
        @Override
        public void run() {
            Log.d("printer", "query status");
            queryPrinterStatus();
        }
    }
}

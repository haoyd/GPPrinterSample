package com.haoyd.printerlib;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import com.gprinter.aidl.GpService;
import com.gprinter.command.GpCom;
import com.gprinter.io.GpDevice;
import com.gprinter.service.GpPrintService;
import com.haoyd.printerlib.entities.BluetoothDeviceInfo;
import com.haoyd.printerlib.liseners.OnPrintResultListener;
import com.haoyd.printerlib.liseners.OnPrinterConnResultListener;
import com.haoyd.printerlib.utils.BluetoothUtil;

import static com.haoyd.printerlib.PrinterConstant.DEFAULT_PRINTER_ID;
import static com.haoyd.printerlib.PrinterConstant.MAIN_QUERY_PRINTER_STATUS;

public class BasePrinterManager {

    private Activity mActivity;
    private PrinterServiceConnection conn = null;
    private GpService mGpService = null;
    private PrinterBroadcastReceiver printerBroadcastReceiver = null;
    private OnPrintResultListener printResultListener;
    private OnPrinterConnResultListener connResultListener;

    public BasePrinterManager(Activity mActivity) {
        this.mActivity = mActivity;
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
    public void getPrinterStatus() {
        try {
            mGpService.queryPrinterStatus(DEFAULT_PRINTER_ID, 500, MAIN_QUERY_PRINTER_STATUS);
        } catch (RemoteException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    /**
     * 设置打印状态监听
     * @param listener
     */
    public void setOnPrinterConnResultListener(OnPrintResultListener listener) {
        printResultListener = listener;
        if (printerBroadcastReceiver != null) {
            printerBroadcastReceiver.setOnPrintResultListener(listener);
        }
    }

    public boolean connectToHistoryPrinter() {

        return false;
    }

    /**
     * 指定要连接的打印机
     * @param info
     * @return true：连接成功   false：连接失败
     */
    public void connectToPrinter(BluetoothDeviceInfo info) {
        if (info == null) {
            callbackConnectResult(info,false);
            return;
        }

        if (!BluetoothUtil.isOpening()) {
            callbackConnectResult(info,false);
            return;
        }

        int rel = 0;

        try {
            // 如果打印机已经连接了，就不要再重复连接了
            if (mGpService != null && mGpService.getPrinterConnectStatus(0) == GpDevice.STATE_CONNECTED) {
                callbackConnectResult(info,true);
                return;
            }

            rel = mGpService.openPort(DEFAULT_PRINTER_ID, 4, info.address, 0);

        } catch (Exception e) {
            callbackConnectResult(info,false);
            return;
        }

        GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];

        if (r != GpCom.ERROR_CODE.SUCCESS) {
            Toast.makeText(mActivity, GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            callbackConnectResult(info,true);
            return;
        }

        callbackConnectResult(info,false);
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

    public void setConnResultListener(OnPrinterConnResultListener connResultListener) {
        this.connResultListener = connResultListener;
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
                Toast.makeText(mActivity.getApplicationContext(), "打印成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mActivity.getApplicationContext(), GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 回调打印机连接结果
     * @param result
     */
    private void callbackConnectResult(BluetoothDeviceInfo info, boolean result) {
        if (connResultListener != null) {
            connResultListener.onResult(info, result);
        }
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
}

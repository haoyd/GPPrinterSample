// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.service;

import android.os.IBinder;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.Context;
import com.gprinter.util.LogInfo;
import android.util.Log;
import android.os.PowerManager;
import com.gprinter.model.PrinterStatus;
import com.gprinter.printer.DeviceInfoManager;
import com.gprinter.printer.PrinterManager;
import android.app.Service;

public class AllService extends Service
{
    private PrinterManager mPrinterManager;
    private DeviceInfoManager mDeviceInfoManager;
    private PrinterStatusBroadcastReceiver printerStatusBroadcastReceiver;
    private UpDeviceStatusThread mUpDeviceStatusThread;
    private SendDeviceInfoThread mSendDeviceInfoThread;
    private PrinterStatus mPrinterStatus;
    public static final String TAG = "--ALLService--";
    PowerManager.WakeLock wakeLock;
    
    public AllService() {
        this.printerStatusBroadcastReceiver = null;
        this.mPrinterStatus = new PrinterStatus();
        this.wakeLock = null;
    }
    
    public void onCreate() {
        super.onCreate();
        Log.d("--ALLService--", "onCreate()");
        this.acquireWakeLock();
        LogInfo.setContext((Context)this);
        this.startPrinterConnect();
        this.mDeviceInfoManager = DeviceInfoManager.getDeviceInfoManager((Context)this);
        (this.mUpDeviceStatusThread = new UpDeviceStatusThread(this, this.mPrinterStatus)).start();
        final SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences((Context)this);
        final boolean ischeck = sharedPreference.getBoolean("key_ischecked", true);
        SendDeviceInfoThread.isChecked(ischeck);
        (this.mSendDeviceInfoThread = new SendDeviceInfoThread(this)).start();
    }
    
    public void startPrinterConnect() {
        if (this.printerStatusBroadcastReceiver != null) {
            this.unregisterReceiver((BroadcastReceiver)this.printerStatusBroadcastReceiver);
            this.printerStatusBroadcastReceiver = null;
        }
        this.printerStatusBroadcastReceiver = new PrinterStatusBroadcastReceiver();
        final IntentFilter filter = new IntentFilter();
        filter.addAction("action.connect.status");
        this.registerReceiver((BroadcastReceiver)this.printerStatusBroadcastReceiver, filter);
        (this.mPrinterManager = PrinterManager.getPrinterManager((Context)this)).start();
    }
    
    public IBinder onBind(final Intent intent) {
        return null;
    }
    
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        LogInfo.out("-Service onStartCommand-");
        return 1;
    }
    
    public void onDestroy() {
        this.mUpDeviceStatusThread.setStop(true);
        this.mSendDeviceInfoThread.setStop(true);
        if (this.mPrinterManager != null) {
            this.mPrinterManager.stop();
        }
        if (this.printerStatusBroadcastReceiver != null) {
            this.unregisterReceiver((BroadcastReceiver)this.printerStatusBroadcastReceiver);
            this.printerStatusBroadcastReceiver = null;
        }
        if (this.mUpDeviceStatusThread.mBroadcastReceiver != null) {
            this.unregisterReceiver(this.mUpDeviceStatusThread.mBroadcastReceiver);
        }
        final Intent localIntent = new Intent();
        localIntent.setClass((Context)this, (Class)AllService.class);
        this.startService(localIntent);
        this.releaseWakeLock();
        super.onDestroy();
    }
    
    public PrinterManager getmPrinterManager() {
        return this.mPrinterManager;
    }
    
    public void setmPrinterManager(final PrinterManager mPrinterManager) {
        this.mPrinterManager = mPrinterManager;
    }
    
    public DeviceInfoManager getDeviceInfoManager() {
        return this.mDeviceInfoManager;
    }
    
    public void setDeviceInfoManager(final DeviceInfoManager deviceInfoManager) {
        this.mDeviceInfoManager = deviceInfoManager;
    }
    
    private void acquireWakeLock() {
        if (null == this.wakeLock) {
            final PowerManager pm = (PowerManager)this.getSystemService("power");
            this.wakeLock = pm.newWakeLock(536870913, "PostLocationService");
            if (null != this.wakeLock) {
                this.wakeLock.acquire();
            }
        }
    }
    
    private void releaseWakeLock() {
        if (null != this.wakeLock) {
            this.wakeLock.release();
            this.wakeLock = null;
        }
    }
}

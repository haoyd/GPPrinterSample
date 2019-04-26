// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.service;

import com.lidroid.xutils.exception.DbException;
import java.io.File;
import com.gprinter.model.DataInfoModel;
import com.gprinter.protocol.DeviceStatus;
import java.io.IOException;
import java.util.Properties;
import com.gprinter.util.DBUtil;
import android.content.IntentFilter;
import com.gprinter.util.LogInfo;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;
import com.lidroid.xutils.DbUtils;
import com.gprinter.printer.DeviceInfoManager;
import com.gprinter.model.PrinterStatus;

public class UpDeviceStatusThread extends Thread
{
    private int time;
    private boolean isStop;
    private boolean error;
    private AllService mAllService;
    private PrinterStatus mPrinterStatus;
    private DeviceInfoManager mDeviceInfoManager;
    private DbUtils db;
    private int lastDeviceStatus;
    public static final int QUERY_PRINTER_STATUS = 255;
    public BroadcastReceiver mBroadcastReceiver;
    
    public UpDeviceStatusThread(final AllService allService, final PrinterStatus printerStatus) {
        this.time = 1700000;
        this.mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                final String action = intent.getAction();
                if (action.equals("action.device.real.status")) {
                    int deviceStatus = intent.getIntExtra("action.printer.real.status", 16);
                    final int requestCode = intent.getIntExtra("printer.request_code", -1);
                    if (requestCode == 255) {
                        final Intent statusIntent = new Intent("com.pointercn.smartprinter.status.RECEIVER");
                        deviceStatus = UpDeviceStatusThread.this.mAllService.getmPrinterManager().getDeviceStatus(deviceStatus);
                        if (UpDeviceStatusThread.this.db != null) {
                            if (deviceStatus == 1) {
                                UpDeviceStatusThread.this.saveDateInfo(deviceStatus);
                                UpDeviceStatusThread.this.setError(false);
                            }
                            else if (!UpDeviceStatusThread.this.error) {
                                UpDeviceStatusThread.this.saveDateInfo(deviceStatus);
                                UpDeviceStatusThread.this.setError(true);
                            }
                        }
                        UpDeviceStatusThread.this.sendStatus(statusIntent, deviceStatus);
                        if (deviceStatus >= 0 && (deviceStatus == 4 || deviceStatus == 5)) {
                            LogInfo.out(" --> 4 \u65e0\u6cd5\u68c0\u6d4b\u5230\u6253\u5370\u673a \u6216\u8005 5 \u672a\u77e5\u9519\u8bef ");
                        }
                    }
                }
            }
        };
        (this.mAllService = allService).registerReceiver(this.mBroadcastReceiver, new IntentFilter("action.device.real.status"));
        this.mPrinterStatus = printerStatus;
        this.mDeviceInfoManager = allService.getDeviceInfoManager();
        this.db = DBUtil.getDB((Context)allService);
        this.setDaemon(true);
        this.setName("SmartPrinter-UpDeviceStatusThread");
        final Properties properties = new Properties();
        try {
            properties.load(allService.getAssets().open("interval.properties"));
            this.time = Integer.parseInt(properties.getProperty("up"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        LogInfo.out("up time ->" + this.time);
    }
    
    @Override
    public void run() {
        final Intent statusIntent = new Intent("com.pointercn.smartprinter.status.RECEIVER");
        int status = -1;
        while (!this.isStop) {
            try {
                Thread.sleep(this.time);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (this.mAllService == null) {
                continue;
            }
            if (this.mAllService.getmPrinterManager() != null && this.mAllService.getmPrinterManager().isStop()) {
                continue;
            }
            if (this.mAllService.getmPrinterManager() == null) {
                continue;
            }
            status = this.mAllService.getmPrinterManager().getPrinterConnectStatus();
            if (status == 3) {
                this.mAllService.getmPrinterManager().getPrinterStatus();
                try {
                    Thread.sleep(1000000L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                if (status != 0) {
                    continue;
                }
                this.sendStatus(statusIntent, DeviceStatus.NO_PRINTER.toInt());
                if (this.error) {
                    continue;
                }
                this.saveDateInfo(DeviceStatus.NO_PRINTER.toInt());
                this.setError(true);
            }
        }
    }
    
    private void sendStatus(final Intent intent, final int printStatus) {
        intent.putExtra("status", printStatus);
        this.mAllService.sendBroadcast(intent);
    }
    
    private void saveDateInfo(final int printStatus) {
        if (this.db != null) {
            try {
                DataInfoModel dm = new DataInfoModel();
                if (this.mDeviceInfoManager != null) {
                    dm = this.mDeviceInfoManager.getDataInfo();
                }
                else {
                    LogInfo.out("mDeviceInfoManager\u65e0\u5b9e\u4f8b");
                }
                dm.setStatus(printStatus);
                this.db.save((Object)dm);
            }
            catch (DbException e) {
                e.printStackTrace();
                LogInfo.out("Db\u5f02\u5e38");
                LogInfo.out(e.getCause().getMessage());
                final File f = new File(GpPrintService.DB_DIR + "/" + "smartprint.db");
                LogInfo.out(f.toString());
                if (!f.exists()) {
                    this.sendStatus(new Intent("com.gprinter.status.RECEIVER"), 404);
                }
            }
        }
        else {
            LogInfo.out("\u65e0db\u5b9e\u4f8b");
        }
    }
    
    public void setError(final boolean error) {
        this.error = error;
    }
    
    public void setTime(final int time) {
        this.time = time;
    }
    
    public void setStop(final boolean isStop) {
        this.isStop = isStop;
    }
}

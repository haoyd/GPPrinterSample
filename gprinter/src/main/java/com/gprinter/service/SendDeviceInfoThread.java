// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.service;

import com.gprinter.model.DeviceInfoModel;
import org.json.JSONException;
import com.gprinter.util.WebServiceUtil;
import java.util.Iterator;
import com.gprinter.util.ReflectUtils;
import com.gprinter.model.DataInfoLog;
import java.util.ArrayList;
import com.lidroid.xutils.exception.DbException;
import com.gprinter.interfaces.CallBackInterface;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import com.lidroid.xutils.db.sqlite.Selector;
import java.util.Date;
import com.gprinter.util.LogInfo;
import java.io.IOException;
import java.util.Properties;
import android.content.Context;
import com.gprinter.util.DBUtil;
import com.gprinter.model.DataInfoModel;
import java.util.List;
import com.lidroid.xutils.DbUtils;
import com.gprinter.printer.DeviceInfoManager;

public class SendDeviceInfoThread extends Thread
{
    private int time;
    private boolean isStop;
    private AllService mAllService;
    private DeviceInfoManager mDeviceInfoManager;
    private static boolean isChecked;
    private DbUtils db;
    private List<DataInfoModel> dataInfoModelList;
    
    public SendDeviceInfoThread(final AllService allService) {
        this.time = 1800000;
        this.mAllService = allService;
        this.mDeviceInfoManager = allService.getDeviceInfoManager();
        this.db = DBUtil.getDB((Context)this.mAllService);
        this.setDaemon(true);
        this.setName("SmartPrinter-SendDeviceInfoThread");
        final Properties properties = new Properties();
        try {
            properties.load(allService.getAssets().open("interval.properties"));
            this.time = Integer.parseInt(properties.getProperty("send"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        LogInfo.out("send time ->" + this.time);
    }
    
    @Override
    public void run() {
        while (!this.isStop) {
            try {
                Thread.sleep(this.time);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if (this.mAllService == null) {
                continue;
            }
            this.upDataImmediately(SendDeviceInfoThread.isChecked);
        }
    }
    
    private void upDataImmediately(final boolean isFilter) {
        final DeviceInfoModel deviceInfoModel = this.mDeviceInfoManager.getDeviceInfo(SendDeviceInfoThread.isChecked);
        try {
            final long ago = new Date().getTime() - this.time;
            this.dataInfoModelList = (List<DataInfoModel>)this.db.findAll(Selector.from((Class)DataInfoModel.class).where("dateTime", ">", (Object)ago));
            if (isFilter && this.dataInfoModelList.size() == 0 && !SendDeviceInfoThread.isChecked) {
                LogInfo.out("===\u6570\u636e\u5e93\u6570\u636e\u4e3a\u7a7a\uff0c\u8df3\u8fc7\u672c\u6b21\u64cd\u4f5c===");
                return;
            }
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final JSONObject deviceData = new JSONObject();
            final JSONArray jsonArray = new JSONArray();
            for (final DataInfoModel dm : this.dataInfoModelList) {
                final JSONObject jsonObj = new JSONObject();
                jsonObj.put("datetime", (Object)sdf.format(dm.getDateTime()));
                jsonObj.put("processcpurate", dm.getProcessCpuRate());
                jsonObj.put("appmem", dm.getAppMem());
                jsonObj.put("systemavailablemem", dm.getSystemAvailableMem());
                jsonObj.put("memrate", dm.getMemRate());
                jsonObj.put("status", dm.getStatus());
                jsonArray.put((Object)jsonObj);
            }
            deviceData.put("printername", (Object)deviceInfoModel.getPrinter());
            deviceData.put("brand", (Object)deviceInfoModel.getBrand());
            deviceData.put("mobilename", (Object)deviceInfoModel.getMobileName());
            deviceData.put("osversion", (Object)deviceInfoModel.getOsVersion());
            deviceData.put("androidid", (Object)deviceInfoModel.getAndroidId());
            deviceData.put("deviceid", (Object)deviceInfoModel.getDeviceId());
            deviceData.put("uuid", (Object)deviceInfoModel.getUuid());
            deviceData.put("installedappnum", deviceInfoModel.getInstalledAppNum());
            deviceData.put("iccid", (Object)deviceInfoModel.getIccid());
            deviceData.put("macaddress", (Object)deviceInfoModel.getMacAddress());
            deviceData.put("ipaddress", (Object)deviceInfoModel.getIpAddress());
            deviceData.put("uptime", (Object)deviceInfoModel.getUpTime());
            deviceData.put("installedapp", (Object)deviceInfoModel.getInstalledApp());
            deviceData.put("datetime", (Object)sdf.format(deviceInfoModel.getDateTime()));
            deviceData.put("data", (Object)jsonArray);
            final String upData = deviceData.toString();
            LogInfo.out(upData);
            WebServiceUtil.callWebService(upData, new CallBackInterface() {
                @Override
                public void onCallBack(final boolean isSuccess) {
                    if (isSuccess) {
                        LogInfo.out("\u8bbe\u5907\u76d1\u63a7\u4fe1\u606f\u6267\u884c\u6210\u529f");
                        try {
                            if (SendDeviceInfoThread.this.db == null) {
                                SendDeviceInfoThread.this.db = DBUtil.getDB((Context)SendDeviceInfoThread.this.mAllService);
                            }
                            SendDeviceInfoThread.this.db.deleteAll((Class)DataInfoModel.class);
                            LogInfo.out("\u8bbe\u5907\u76d1\u63a7\u4fe1\u606f\u5df2\u6e05\u9664");
                        }
                        catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        LogInfo.out("\u8bbe\u5907\u76d1\u63a7\u4fe1\u606f\u6267\u884c\u5931\u8d25");
                        final List<DataInfoLog> logList = new ArrayList<DataInfoLog>();
                        for (final DataInfoModel dm : SendDeviceInfoThread.this.dataInfoModelList) {
                            DataInfoLog dl = new DataInfoLog();
                            dl = ReflectUtils.mappingFieldByField(dm, dl);
                            logList.add(dl);
                        }
                        if (SendDeviceInfoThread.this.db == null) {
                            SendDeviceInfoThread.this.db = DBUtil.getDB((Context)SendDeviceInfoThread.this.mAllService);
                        }
                        try {
                            SendDeviceInfoThread.this.db.saveAll((List)logList);
                        }
                        catch (DbException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            });
        }
        catch (DbException | JSONException ex2) {
            final Exception ex;
            final Exception e = ex;
            e.printStackTrace();
        }
    }
    
    public void setTime(final int time) {
        this.time = time;
    }
    
    public void setStop(final boolean isStop) {
        this.isStop = isStop;
    }
    
    public static void isChecked(final boolean isChecked) {
        SendDeviceInfoThread.isChecked = isChecked;
    }
}

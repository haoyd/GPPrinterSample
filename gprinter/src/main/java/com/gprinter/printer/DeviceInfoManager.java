// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.printer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import android.location.Location;
import android.content.SharedPreferences;
import android.text.TextUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import android.os.Debug;
import android.os.Process;
import android.os.SystemClock;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.bluetooth.BluetoothAdapter;
import java.util.Iterator;
import android.content.pm.ApplicationInfo;
import java.util.List;
import android.content.pm.PackageManager;
import com.gprinter.model.DataInfoModel;
import java.util.Date;
import java.util.UUID;
import com.gprinter.service.GpPrintService;
import com.gprinter.save.PortParamDataBase;
import android.provider.Settings;
import android.os.Build;
import com.gprinter.model.DeviceInfoModel;
import android.location.LocationManager;
import android.app.ActivityManager;
import android.telephony.TelephonyManager;
import android.content.Context;

public class DeviceInfoManager
{
    private static Context context;
    private static DeviceInfoManager deviceInfoManager;
    private static TelephonyManager telephonyManager;
    private static ActivityManager activityManager;
    private static LocationManager locationManager;
    private static final String UNKNOWN = "n";
    private static final String KEY_DEVICE_ID = "device_id";
    
    public static DeviceInfoManager getDeviceInfoManager(final Context context) {
        if (DeviceInfoManager.deviceInfoManager == null) {
            DeviceInfoManager.context = context;
            DeviceInfoManager.telephonyManager = (TelephonyManager)context.getSystemService("phone");
            DeviceInfoManager.activityManager = (ActivityManager)context.getSystemService("activity");
            DeviceInfoManager.locationManager = (LocationManager)context.getSystemService("location");
            DeviceInfoManager.deviceInfoManager = new DeviceInfoManager();
        }
        return DeviceInfoManager.deviceInfoManager;
    }
    
    public DeviceInfoModel getDeviceInfo(final Boolean isChecked) {
        final DeviceInfoModel deviceInfoModel = new DeviceInfoModel();
        final String mobileName = Build.MODEL;
        final String mobileBrand = Build.BRAND;
        final String ANDROID_ID = Settings.Secure.getString(DeviceInfoManager.context.getContentResolver(), "android_id");
        final String osVersion = Build.VERSION.RELEASE;
        final String deviceID = this.getDeviceId(DeviceInfoManager.telephonyManager.getDeviceId());
        final String printerName = new PortParamDataBase(DeviceInfoManager.context).readPrinterName(GpPrintService.PrinterId);
        final String uuid = UUID.randomUUID().toString();
        deviceInfoModel.setBrand(mobileBrand);
        deviceInfoModel.setMobileName(mobileName);
        deviceInfoModel.setAndroidId(ANDROID_ID);
        deviceInfoModel.setOsVersion(osVersion);
        deviceInfoModel.setDeviceId(deviceID);
        deviceInfoModel.setUuid(uuid);
        deviceInfoModel.setPrinter(printerName);
        if (isChecked) {
            final String ICCID = DeviceInfoManager.telephonyManager.getSimSerialNumber();
            final String macAddress = this.getMacAddress();
            final String ipAddress = this.getIpAddress();
            final String upTime = this.getTimes();
            final int allAppNum = this.getNumberOfApp();
            final int installedAppNum = this.getNumberOfAppWithoutSystemApp();
            final String installedApp = this.getNameOfInstalledApp();
            final Date dateTime = new Date();
            deviceInfoModel.setIccid(ICCID);
            deviceInfoModel.setMacAddress(macAddress);
            deviceInfoModel.setIpAddress(ipAddress);
            deviceInfoModel.setUpTime(upTime);
            deviceInfoModel.setInstalledAppNum(installedAppNum);
            deviceInfoModel.setInstalledApp(installedApp);
            deviceInfoModel.setDateTime(dateTime);
        }
        else {
            final Date dateTime2 = new Date();
            final String deviceId = DeviceInfoManager.telephonyManager.getDeviceId();
            deviceInfoModel.setDeviceId(deviceId);
            deviceInfoModel.setDateTime(dateTime2);
        }
        return deviceInfoModel;
    }
    
    public DataInfoModel getDataInfo() {
        final DataInfoModel dataInfoModel = new DataInfoModel();
        final Date dateTime = new Date();
        final int systemAvaialbeMem = this.getSystemAvaialbeMemorySize();
        final int memRate = this.getMemRate();
        final int appMem = this.getAppMem();
        final double processCpuRate = this.getProcessCpuRate();
        dataInfoModel.setDateTime(dateTime);
        dataInfoModel.setSystemAvailableMem(systemAvaialbeMem);
        dataInfoModel.setMemRate(memRate);
        dataInfoModel.setAppMem(appMem);
        dataInfoModel.setProcessCpuRate(processCpuRate);
        return dataInfoModel;
    }
    
    private int getNumberOfApp() {
        final PackageManager pm = DeviceInfoManager.context.getPackageManager();
        final List<ApplicationInfo> applicationInfos = (List<ApplicationInfo>)pm.getInstalledApplications(8192);
        return applicationInfos.size();
    }
    
    private int getNumberOfAppWithoutSystemApp() {
        int numberOfApp = 0;
        final PackageManager pm = DeviceInfoManager.context.getPackageManager();
        final List<ApplicationInfo> applicationInfos = (List<ApplicationInfo>)pm.getInstalledApplications(8192);
        for (final ApplicationInfo appInfo : applicationInfos) {
            if ((appInfo.flags & 0x1) == 0x0) {
                ++numberOfApp;
            }
        }
        return numberOfApp;
    }
    
    private String getNameOfInstalledApp() {
        final StringBuilder builder = new StringBuilder();
        final PackageManager pm = DeviceInfoManager.context.getPackageManager();
        final List<ApplicationInfo> applicationInfos = (List<ApplicationInfo>)pm.getInstalledApplications(8192);
        for (final ApplicationInfo appInfo : applicationInfos) {
            if ((appInfo.flags & 0x1) == 0x0) {
                final String str = appInfo.loadLabel(pm).toString();
                builder.append(str + ",");
            }
        }
        if (builder.length() > 1) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }
    
    private String getBtMacAddress() {
        String macAddress = null;
        macAddress = BluetoothAdapter.getDefaultAdapter().getAddress();
        return macAddress;
    }
    
    private String getMacAddress() {
        final WifiManager wifiManager = (WifiManager)DeviceInfoManager.context.getSystemService("wifi");
        final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String macAddress;
        if (wifiInfo.getMacAddress() != null) {
            macAddress = wifiInfo.getMacAddress();
        }
        else {
            macAddress = "Fail";
        }
        return macAddress;
    }
    
    private String getIpAddress() {
        final WifiManager wifiManager = (WifiManager)DeviceInfoManager.context.getSystemService("wifi");
        if (!wifiManager.isWifiEnabled()) {
            return "\u672a\u5f00\u542fwifi";
        }
        final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        final int ipAddress = wifiInfo.getIpAddress();
        final String ip = this.intToIp(ipAddress);
        return ip;
    }
    
    private String intToIp(final int i) {
        return (i & 0xFF) + "." + (i >> 8 & 0xFF) + "." + (i >> 16 & 0xFF) + "." + (i >> 24 & 0xFF);
    }
    
    private String getTimes() {
        long ut = SystemClock.elapsedRealtime() / 1000L;
        if (ut == 0L) {
            ut = 1L;
        }
        final int m = (int)(ut / 60L % 60L);
        final int h = (int)(ut / 3600L);
        return h + "h" + m + "m";
    }
    
    private int getAppMem() {
        final int uid = Process.myUid();
        int memSize = 0;
        for (final ActivityManager.RunningAppProcessInfo appProcess : DeviceInfoManager.activityManager.getRunningAppProcesses()) {
            if (appProcess.uid == uid) {
                final int[] pid = { appProcess.pid };
                final Debug.MemoryInfo[] memoryInfo = DeviceInfoManager.activityManager.getProcessMemoryInfo(pid);
                memSize += memoryInfo[0].dalvikPrivateDirty;
            }
        }
        return memSize;
    }
    
    private int getSystemAvaialbeMemorySize() {
        final ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        DeviceInfoManager.activityManager.getMemoryInfo(memoryInfo);
        final long memSize = memoryInfo.availMem;
        final int i = (int)memSize / 1048576;
        return i;
    }
    
    private int getMemRate() {
        try {
            final File file = new File("/proc/meminfo");
            final FileInputStream fis = new FileInputStream(file);
            final BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            final String totalRam = br.readLine();
            br.close();
            final StringBuffer sb = new StringBuffer();
            final char[] charArray;
            final char[] cs = charArray = totalRam.toCharArray();
            for (final char c : charArray) {
                if (c >= '0' && c <= '9') {
                    sb.append(c);
                }
            }
            int totalMem = 0;
            try {
                totalMem = (int)(Long.parseLong(sb.toString()) * 1024L) / 1048576;
            }
            catch (Exception ex) {}
            final int availableMem = this.getSystemAvaialbeMemorySize();
            final int rate = 100 * (totalMem - availableMem) / totalMem;
            return rate;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    private double getProcessCpuRate() {
        final float totalCpuTime1 = this.getTotalCpuTime();
        final float processCpuTime1 = this.getAppCpuTime();
        try {
            Thread.sleep(360L);
        }
        catch (Exception ex) {}
        final float totalCpuTime2 = this.getTotalCpuTime();
        final float processCpuTime2 = this.getAppCpuTime();
        final float cpuRate = 100.0f * (processCpuTime2 - processCpuTime1) / (totalCpuTime2 - totalCpuTime1);
        double f;
        try {
            final BigDecimal b = new BigDecimal(cpuRate);
            f = b.setScale(2, 4).doubleValue();
        }
        catch (Exception e) {
            f = 0.0;
        }
        return f;
    }
    
    private long getTotalCpuTime() {
        String[] cpuInfos = null;
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/stat")), 1000);
            final String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        long totalCpu = 0L;
        try {
            totalCpu = Long.parseLong(cpuInfos[2]) + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4]) + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5]) + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
        }
        catch (Exception e) {
            totalCpu = 0L;
        }
        return totalCpu;
    }
    
    private long getAppCpuTime() {
        String[] cpuInfos = null;
        try {
            final int pid = Process.myPid();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/" + pid + "/stat")), 1000);
            final String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        long appCpuTime = 0L;
        try {
            appCpuTime = Long.parseLong(cpuInfos[13]) + Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15]) + Long.parseLong(cpuInfos[16]);
        }
        catch (Exception ex2) {}
        return appCpuTime;
    }
    
    private String getDeviceId(String deviceId) {
        if (deviceId == null) {
            String macAddr = this.getMacAddress().replaceAll(":", "");
            if (macAddr.equals("Fail")) {
                macAddr = this.getBtMacAddress();
                if (TextUtils.isEmpty((CharSequence)macAddr)) {
                    final SharedPreferences sharedPreference = DeviceInfoManager.context.getSharedPreferences("device_id", 0);
                    deviceId = sharedPreference.getString("device_id", deviceId);
                    if (TextUtils.isEmpty((CharSequence)deviceId)) {
                        final long randomNum = (long)(Math.random() * 1.0E15 + 1.0);
                        deviceId = String.valueOf(randomNum);
                        sharedPreference.edit().putString("device_id", deviceId).commit();
                    }
                    return deviceId;
                }
            }
            final String androidId = Settings.Secure.getString(DeviceInfoManager.context.getContentResolver(), "android_id");
            deviceId = macAddr + androidId.substring(androidId.length() - 3, androidId.length());
        }
        return deviceId;
    }
    
    private String getLocation() {
        final Location location = DeviceInfoManager.locationManager.getLastKnownLocation("gps");
        final double longtitude = location.getLongitude();
        final double latitude = location.getLatitude();
        return longtitude + "," + latitude;
    }
}

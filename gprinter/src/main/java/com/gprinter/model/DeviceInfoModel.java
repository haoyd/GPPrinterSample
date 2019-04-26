// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.model;

import java.util.Date;

public class DeviceInfoModel
{
    private int id;
    private String brand;
    private String mobileName;
    private String androidId;
    private String osVersion;
    private String deviceId;
    private String iccid;
    private String macAddress;
    private String ipAddress;
    private String upTime;
    private int allAppNum;
    private String installedApp;
    private int installedAppNum;
    private String uuid;
    private Date dateTime;
    public String printer;
    
    public String getPrinter() {
        return this.printer;
    }
    
    public void setPrinter(final String printer) {
        this.printer = printer;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public String getMobileName() {
        return this.mobileName;
    }
    
    public void setMobileName(final String mobileName) {
        this.mobileName = mobileName;
    }
    
    public String getAndroidId() {
        return this.androidId;
    }
    
    public void setAndroidId(final String androidId) {
        this.androidId = androidId;
    }
    
    public String getOsVersion() {
        return this.osVersion;
    }
    
    public void setOsVersion(final String osVersion) {
        this.osVersion = osVersion;
    }
    
    public String getDeviceId() {
        return this.deviceId;
    }
    
    public void setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getIccid() {
        return this.iccid;
    }
    
    public void setIccid(final String iccid) {
        this.iccid = iccid;
    }
    
    public String getMacAddress() {
        return this.macAddress;
    }
    
    public void setMacAddress(final String macAddress) {
        this.macAddress = macAddress;
    }
    
    public String getIpAddress() {
        return this.ipAddress;
    }
    
    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUpTime() {
        return this.upTime;
    }
    
    public void setUpTime(final String upTime) {
        this.upTime = upTime;
    }
    
    public int getAllAppNum() {
        return this.allAppNum;
    }
    
    public void setAllAppNum(final int allAppNum) {
        this.allAppNum = allAppNum;
    }
    
    public String getInstalledApp() {
        return this.installedApp;
    }
    
    public void setInstalledApp(final String installedApp) {
        this.installedApp = installedApp;
    }
    
    public String getUuid() {
        return this.uuid;
    }
    
    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }
    
    public Date getDateTime() {
        return this.dateTime;
    }
    
    public void setDateTime(final Date dateTime) {
        this.dateTime = dateTime;
    }
    
    public String getBrand() {
        return this.brand;
    }
    
    public void setBrand(final String brand) {
        this.brand = brand;
    }
    
    public int getInstalledAppNum() {
        return this.installedAppNum;
    }
    
    public void setInstalledAppNum(final int installedAppNum) {
        this.installedAppNum = installedAppNum;
    }
}

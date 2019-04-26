// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.io;

public class PortParameters
{
    public static final int SERIAL = 0;
    public static final int PARALLEL = 1;
    public static final int USB = 2;
    public static final int ETHERNET = 3;
    public static final int BLUETOOTH = 4;
    public static final int UNDEFINE = 5;
    private boolean mbPortOpen;
    private String mBluetoothAddr;
    private String mUsbDeviceName;
    private String mIpAddr;
    private int mPortType;
    private int mPortNumber;
    
    public PortParameters() {
        this.mbPortOpen = false;
        this.mBluetoothAddr = null;
        this.mUsbDeviceName = null;
        this.mIpAddr = null;
        this.mPortType = 0;
        this.mPortNumber = 0;
        this.mBluetoothAddr = "";
        this.mUsbDeviceName = "";
        this.mIpAddr = "192.168.123.100";
        this.mPortNumber = 9100;
        this.mPortType = 5;
    }
    
    public void setBluetoothAddr(final String adr) {
        this.mBluetoothAddr = adr;
    }
    
    public String getBluetoothAddr() {
        return this.mBluetoothAddr;
    }
    
    public void setUsbDeviceName(final String name) {
        this.mUsbDeviceName = name;
    }
    
    public String getUsbDeviceName() {
        return this.mUsbDeviceName;
    }
    
    public void setIpAddr(final String adr) {
        this.mIpAddr = adr;
    }
    
    public String getIpAddr() {
        return this.mIpAddr;
    }
    
    public void setPortType(final int PortType) {
        this.mPortType = PortType;
    }
    
    public int getPortType() {
        return this.mPortType;
    }
    
    public void setPortNumber(final int number) {
        this.mPortNumber = number;
    }
    
    public int getPortNumber() {
        return this.mPortNumber;
    }
    
    public void setPortOpenState(final boolean state) {
        this.mbPortOpen = state;
    }
    
    public boolean getPortOpenState() {
        return this.mbPortOpen;
    }
}

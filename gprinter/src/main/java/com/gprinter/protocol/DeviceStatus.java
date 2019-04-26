// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.protocol;

public enum DeviceStatus
{
    NORMAL(1), 
    LACK_PAGER(2), 
    ERROR(3), 
    NO_PRINTER(4), 
    COVER_OPEN(5);
    
    private int status;
    
    private DeviceStatus(final int _status) {
        this.status = _status;
    }
    
    public static DeviceStatus getDeviceStatus(final int _status) {
        switch (_status) {
            case 1: {
                return DeviceStatus.NORMAL;
            }
            case 2: {
                return DeviceStatus.LACK_PAGER;
            }
            case 3: {
                return DeviceStatus.ERROR;
            }
            case 4: {
                return DeviceStatus.NO_PRINTER;
            }
            case 5: {
                return DeviceStatus.COVER_OPEN;
            }
            default: {
                return DeviceStatus.ERROR;
            }
        }
    }
    
    public int toInt() {
        return this.status;
    }
}

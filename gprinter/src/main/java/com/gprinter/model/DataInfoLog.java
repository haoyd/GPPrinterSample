// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.model;

import java.util.Date;

public class DataInfoLog
{
    private int id;
    private Date dateTime;
    private double processCpuRate;
    private int appMem;
    private int systemAvailableMem;
    private int memRate;
    private int status;
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public Date getDateTime() {
        return this.dateTime;
    }
    
    public void setDateTime(final Date dateTime) {
        this.dateTime = dateTime;
    }
    
    public double getProcessCpuRate() {
        return this.processCpuRate;
    }
    
    public void setProcessCpuRate(final double processCpuRate) {
        this.processCpuRate = processCpuRate;
    }
    
    public int getAppMem() {
        return this.appMem;
    }
    
    public void setAppMem(final int appMem) {
        this.appMem = appMem;
    }
    
    public int getSystemAvailableMem() {
        return this.systemAvailableMem;
    }
    
    public void setSystemAvailableMem(final int systemAvailableMem) {
        this.systemAvailableMem = systemAvailableMem;
    }
    
    public int getMemRate() {
        return this.memRate;
    }
    
    public void setMemRate(final int memRate) {
        this.memRate = memRate;
    }
    
    public int getStatus() {
        return this.status;
    }
    
    public void setStatus(final int status) {
        this.status = status;
    }
}

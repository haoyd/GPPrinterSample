// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.model;

import java.util.Date;

public class LogModel
{
    private int id;
    private Date logTime;
    private int logType;
    private String logMsg;
    
    public LogModel() {
        this.logType = 0;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public Date getLogTime() {
        return this.logTime;
    }
    
    public void setLogTime(final Date logTime) {
        this.logTime = logTime;
    }
    
    public int getLogType() {
        return this.logType;
    }
    
    public void setLogType(final int logType) {
        this.logType = logType;
    }
    
    public String getLogMsg() {
        return this.logMsg;
    }
    
    public void setLogMsg(final String logMsg) {
        this.logMsg = logMsg;
    }
}

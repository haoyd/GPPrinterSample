// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.model;

import java.util.Date;

public class PrintModel
{
    private int id;
    private Date printTime;
    private int deviceStatus;
    private int printResult;
    private String printResultMsg;
    private int eventNum;
    private int partNum;
    private int partIndex;
    private long orderId;
    private int controller;
    private String printMsg;
    
    public PrintModel() {
        this.printResult = -1;
        this.printResultMsg = "\u672a\u6253\u5370";
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public Date getPrintTime() {
        return this.printTime;
    }
    
    public void setPrintTime(final Date printTime) {
        this.printTime = printTime;
    }
    
    public int getDeviceStatus() {
        return this.deviceStatus;
    }
    
    public void setDeviceStatus(final int deviceStatus) {
        this.deviceStatus = deviceStatus;
    }
    
    public int getPrintResult() {
        return this.printResult;
    }
    
    public void setPrintResult(final int printResult) {
        this.printResult = printResult;
    }
    
    public long getOrderId() {
        return this.orderId;
    }
    
    public void setOrderId(final long orderId) {
        this.orderId = orderId;
    }
    
    public int getController() {
        return this.controller;
    }
    
    public void setController(final int controller) {
        this.controller = controller;
    }
    
    public String getPrintMsg() {
        return this.printMsg;
    }
    
    public void setPrintMsg(final String printMsg) {
        this.printMsg = printMsg;
    }
    
    public String getPrintResultMsg() {
        return this.printResultMsg;
    }
    
    public void setPrintResultMsg(final String printResultMsg) {
        this.printResultMsg = printResultMsg;
    }
    
    public int getEventNum() {
        return this.eventNum;
    }
    
    public void setEventNum(final int eventNum) {
        this.eventNum = eventNum;
    }
    
    public int getPartNum() {
        return this.partNum;
    }
    
    public void setPartNum(final int partNum) {
        this.partNum = partNum;
    }
    
    public int getPartIndex() {
        return this.partIndex;
    }
    
    public void setPartIndex(final int partIndex) {
        this.partIndex = partIndex;
    }
}

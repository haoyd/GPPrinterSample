// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.model;

public class PrinterStatus
{
    private int status;
    
    public synchronized void setStatus(final int status) {
        this.status = status;
    }
    
    public synchronized int getStatus() {
        return this.status;
    }
}

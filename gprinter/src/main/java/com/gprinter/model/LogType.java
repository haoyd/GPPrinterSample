// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.model;

public enum LogType
{
    NO_KNOW(0), 
    NORMAL(1), 
    CONNECT_SERVER_ERR(2), 
    CONNECT_PRINTER_ERR(3), 
    DATA_ERR(4), 
    PRINT_ERR(5), 
    APP_ERR(6);
    
    private int type;
    
    private LogType(final int _type) {
        this.type = _type;
    }
    
    public int toInt() {
        return this.type;
    }
}

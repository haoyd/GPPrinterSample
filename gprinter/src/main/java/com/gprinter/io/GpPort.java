// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.io;

import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import com.gprinter.command.GpCom;
import java.util.Vector;
import android.os.Handler;

public abstract class GpPort
{
    private static final String DEBUG_TAG = "GpPort";
    protected boolean mClosePort;
    protected int mState;
    protected Handler mHandler;
    protected int mmBytesAvailable;
    protected int mPrinterId;
    
    public GpPort() {
        this.mHandler = null;
    }
    
    abstract void connect();
    
    abstract void stop();
    
    abstract GpCom.ERROR_CODE writeDataImmediately(final Vector<Byte> p0);
    
    protected synchronized void setState(final int state) {
        if (this.mState != state) {
            Log.d("GpPort", "setState() " + this.mState + " -> " + state);
            Log.d("GpPort", "PrinterId() " + this.mPrinterId + " -> " + this.mPrinterId);
            this.mState = state;
            final Message msg = this.mHandler.obtainMessage(1);
            final Bundle bundle = new Bundle();
            bundle.putInt("printer.id", this.mPrinterId);
            bundle.putInt("device_status", state);
            msg.setData(bundle);
            this.mHandler.sendMessage(msg);
        }
        else {
            Log.d("GpPort", "STATE NOT CHANGE");
        }
    }
    
    protected int getState() {
        return this.mState;
    }
    
    protected void connectionFailed() {
        this.setState(0);
    }
    
    protected void closePortFailed() {
        this.setState(0);
        Log.d("GpPort", "closePortFailed ");
    }
    
    protected void connectionLost() {
        this.setState(0);
        Log.d("GpPort", "connectionLost ");
    }
    
    protected void invalidPrinter() {
        this.setState(0);
        final Message msg = this.mHandler.obtainMessage(5);
        final Bundle bundle = new Bundle();
        bundle.putInt("printer.id", this.mPrinterId);
        bundle.putString("toast", "Please use Gprinter");
        msg.setData(bundle);
        this.mHandler.sendMessage(msg);
    }
    
    protected void connectionToPrinterFailed() {
        this.setState(0);
        Log.d("GpPort", "Close port failed ");
    }
}

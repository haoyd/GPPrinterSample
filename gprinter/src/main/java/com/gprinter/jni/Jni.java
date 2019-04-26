// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.jni;

public class Jni
{
    private static Jni mJni;
    
    public static Jni getInstance() {
        if (Jni.mJni == null) {
            Jni.mJni = new Jni();
        }
        return Jni.mJni;
    }
    
    public native void clear();
    
    public native void reset();
    
    public native void setBacklight(final boolean p0);
    
    public native void setCursorPosition(final int p0, final int p1);
    
    public native void setBacklightTimeout(final int p0);
    
    public native void setInputInCurrentCursor(final byte[] p0, final int p1);
    
    public native void setInputBebindCursor(final byte[] p0, final int p1);
    
    public native void getBacklight();
    
    public native void getBacklightTimeout();
    
    public native void getCursorPosition();
    
    public native void getDisplayRowAndColumn();
    
    public native void swichMode(final byte p0);
    
    public native void displayBitmap(final byte[] p0, final long p1);
    
    public native void setContrast(final byte p0);
    
    public native void setBrightness(final byte p0);
    
    public native void setCursorVisible(final boolean p0);
    
    public native boolean isPortOpen();
}

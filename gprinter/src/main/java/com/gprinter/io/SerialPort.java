// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.io;

import java.io.InputStream;
import java.io.IOException;
import android.util.Log;
import android.app.Activity;
import android.content.DialogInterface;
import android.app.AlertDialog;
import java.io.File;
import android.content.Context;
import java.io.FileInputStream;
import java.io.FileDescriptor;

public class SerialPort
{
    private static final String TAG = "SPort";
    protected FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private Context mContext;
    
    protected SerialPort(final Context context) {
        this.mContext = context;
    }
    
    protected void openSerialPort(final File device, final int baudrate, final int flags) throws IOException {
        Label_0141: {
            if (device.canRead()) {
                if (device.canWrite()) {
                    break Label_0141;
                }
            }
            try {
                final Process su = Runtime.getRuntime().exec("/system/xbin/su");
                final String cmd = "chmod 666 " + device.getAbsolutePath() + "\nexit\n";
                su.getOutputStream().write(cmd.getBytes());
                if (su.waitFor() != 0 || !device.canRead() || !device.canWrite()) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
                    builder.setMessage((CharSequence)"\u6ca1\u6709\u6743\u9650");
                    builder.setPositiveButton((CharSequence)"\u5173\u95ed", (DialogInterface.OnClickListener)new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int which) {
                            ((Activity)SerialPort.this.mContext).finish();
                        }
                    });
                    builder.show();
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.mFd = open(device.getAbsolutePath(), baudrate, flags);
        if (this.mFd == null) {
            Log.e("SPort", "native open returns null");
            throw new IOException();
        }
        this.mFileInputStream = new FileInputStream(this.mFd);
    }
    
    protected InputStream getInputStream() {
        return this.mFileInputStream;
    }
    
    private static native FileDescriptor open(final String p0, final int p1, final int p2);
    
    protected native void close();
    
    protected native void is();
    
    protected native boolean check(final byte[] p0, final int p1);
    
    protected native void update();
    
    protected native void updateCheck(final byte[] p0, final int p1);
    
    protected native void requestVersionInfo();
    
    protected native void requestUpdate(final int p0, final int p1, final int p2, final int p3);
    
    protected native void download(final int p0, final int p1, final int p2, final int p3, final byte[] p4, final int p5);
    
    static {
        System.loadLibrary("gpequipment");
    }
}

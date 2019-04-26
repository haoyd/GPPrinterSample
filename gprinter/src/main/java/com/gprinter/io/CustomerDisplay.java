// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.io;

import android.widget.Toast;
import com.gprinter.io.utils.GpUtils;
import android.graphics.Bitmap;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.File;
import android.os.Build;
import android.content.Context;
import com.gprinter.jni.Jni;

public class CustomerDisplay extends GpEquipmentPort
{
    private Jni mJni;
    private Context mContext;
    private static CustomerDisplay mCustomerDisplay;
    
    private CustomerDisplay(final Context context) {
        super(context);
        this.mJni = Jni.getInstance();
        this.mContext = context;
    }
    
    public static CustomerDisplay getInstance(final Context context) {
        if (CustomerDisplay.mCustomerDisplay == null) {
            CustomerDisplay.mCustomerDisplay = new CustomerDisplay(context);
        }
        return CustomerDisplay.mCustomerDisplay;
    }
    
    public void openPort() throws IOException {
        String path;
        if (Build.VERSION.SDK_INT >= 21) {
            path = "/dev/ttyS2";
        }
        else {
            path = "/dev/ttyS3";
        }
        final File file = new File(path);
        if (!file.exists()) {
            throw new IOException("Not found serial port");
        }
        this.getSerialPort(file, 115200, 0);
    }
    
    public boolean isPortOpen() {
        return this.mJni.isPortOpen();
    }
    
    public void setReceivedListener(final OnDataReceived onDataReceived) {
        this.setDataReceived(onDataReceived);
    }
    
    public void clear() {
        this.mJni.clear();
    }
    
    public void reset() {
        this.mJni.reset();
        this.closeSerialPort();
    }
    
    public void setBacklight(final boolean isOn) {
        this.mJni.setBacklight(isOn);
    }
    
    public void setCursorPosition(final int x, final int y) {
        this.mJni.setCursorPosition(x, y);
    }
    
    public void setBacklightTimeout(final int timeout) {
        this.mJni.setBacklightTimeout(timeout);
    }
    
    public void setTextCurrentCursor(final String text) {
        try {
            final byte[] bytes = text.getBytes("GB2312");
            if (bytes.length > 255 && bytes.length <= 0) {
                throw new IllegalArgumentException("the content must be greater than 0 and less than 255");
            }
            this.mJni.setInputInCurrentCursor(bytes, bytes.length);
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
    public void setTextBebindCursor(final String text) {
        try {
            final byte[] bytes = text.getBytes("GB2312");
            if (bytes.length > 255 && bytes.length <= 0) {
                throw new IllegalArgumentException("the content must be greater than 0 and less than 255");
            }
            this.mJni.setInputBebindCursor(bytes, bytes.length);
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
    public void getBacklight() {
        this.mJni.getBacklight();
    }
    
    public void getBacklightTimeout() {
        this.mJni.getBacklightTimeout();
    }
    
    public void getCursorPosition() {
        this.mJni.getCursorPosition();
    }
    
    public void getDisplayRowAndColumn() {
        this.mJni.getDisplayRowAndColumn();
    }
    
    public void swichMode(final byte mode) {
        this.mJni.swichMode(mode);
    }
    
    public void displayBitmap(final Bitmap bitmap, final int nWidth) {
        if (bitmap != null) {
            final int width = (nWidth + 7) / 8 * 8;
            int height = bitmap.getHeight() * width / bitmap.getWidth();
            final Bitmap grayBitmap = GpUtils.toGrayscale(bitmap);
            final Bitmap rszBitmap = GpUtils.resizeImage(grayBitmap, width, height);
            final byte[] src = GpUtils.bitmapToBWPix(rszBitmap);
            final byte[] command = new byte[4];
            height = src.length / width;
            command[0] = (byte)(width / 8 % 256);
            command[1] = (byte)(width / 8 / 256);
            command[2] = (byte)(height % 256);
            command[3] = (byte)(height / 256);
            final byte[] codecontent = GpUtils.pixToEscRastBitImageCmd(src);
            final byte[] bytes = new byte[command.length + codecontent.length];
            System.arraycopy(command, 0, bytes, 0, command.length);
            System.arraycopy(codecontent, 0, bytes, command.length, codecontent.length);
            this.mJni.displayBitmap(bytes, bytes.length);
        }
    }
    
    public void setContrast(final byte contrast) {
        if (contrast < 0 || contrast > 21) {
            Toast.makeText(this.mContext, (CharSequence)"contrast param error", 0).show();
            return;
        }
        this.mJni.setContrast(contrast);
    }
    
    public void setBrightness(final byte brightness) {
        if (brightness < 0 || brightness > 5) {
            Toast.makeText(this.mContext, (CharSequence)"brightness param error", 0).show();
            return;
        }
        this.mJni.setBrightness(brightness);
    }
    
    public void setCursorVisible(final boolean visible) {
        this.mJni.setCursorVisible(visible);
    }
}

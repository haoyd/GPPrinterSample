// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.io;

import java.util.Date;
import java.nio.channels.FileChannel;
import android.util.Log;
import java.io.FileInputStream;
import android.text.TextUtils;
import java.security.InvalidParameterException;
import java.io.IOException;
import java.io.File;
import android.os.Looper;
import android.os.Handler;
import android.content.Context;
import java.io.InputStream;

public abstract class GpEquipmentPort extends SerialPort
{
    private SerialPort mSerialPort;
    private ReadThread mReadThread;
    private InputStream mInputStream;
    private Context mContext;
    private String mPath;
    private Handler mHandler;
    private boolean mRequestVersion;
    private boolean mPrepareUpdate;
    private boolean mUpdating;
    private int currentPackage;
    private int mPackOffsetL;
    private int mPackOffsetH;
    private int mSignlePackageSize;
    private byte[] data;
    int index;
    private OnDataReceived mOnDataReceived;
    
    protected GpEquipmentPort(final Context context) {
        super(context);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mRequestVersion = false;
        this.mPrepareUpdate = false;
        this.mUpdating = false;
        this.currentPackage = 0;
        this.mPackOffsetL = 0;
        this.mPackOffsetH = 0;
        this.mSignlePackageSize = 64;
        this.index = 0;
        this.mContext = context;
    }
    
    protected SerialPort getSerialPort(final File file, final int baudrate, final int flag) throws SecurityException, IOException, InvalidParameterException {
        if (this.mSerialPort == null) {
            (this.mSerialPort = new SerialPort(this.mContext)).openSerialPort(file, baudrate, flag);
            this.mInputStream = this.mSerialPort.getInputStream();
            (this.mReadThread = new ReadThread()).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(200L);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    GpEquipmentPort.this.is();
                }
            }).start();
        }
        return this.mSerialPort;
    }
    
    private void resetFlag() {
        this.mUpdating = false;
        this.mRequestVersion = false;
        this.mPrepareUpdate = false;
    }
    
    public void closeSerialPort() {
        this.resetFlag();
        if (this.mSerialPort != null) {
            this.mSerialPort.close();
            this.mSerialPort = null;
            if (this.mReadThread != null) {
                this.mReadThread.interrupt();
                this.mReadThread = null;
            }
        }
    }
    
    protected void setPath(final String path) {
        this.mPath = path;
    }
    
    private int getFileSize() throws IOException {
        int fileSize = 0;
        FileInputStream inputStream = null;
        int len = -1;
        if (TextUtils.isEmpty((CharSequence)this.mPath)) {
            throw new IOException("path is invalid");
        }
        try {
            inputStream = new FileInputStream(this.mPath);
            final FileChannel fc = inputStream.getChannel();
            while (inputStream.read() != -1) {
                ++fileSize;
            }
            this.data = new byte[fileSize];
            fc.position(0L);
            len = inputStream.read(this.data);
            Log.d("fileSize =", len + "");
        }
        catch (IOException e) {
            e.printStackTrace();
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        if (len == -1) {
            throw new IOException("read error");
        }
        return fileSize;
    }
    
    private void prepareUpdate() throws IOException {
        final int fileSize = this.getFileSize();
        final boolean addPack = fileSize % 64 == 0;
        final int packSize = addPack ? (fileSize / 64) : (fileSize / 64 + 1);
        final int packL = packSize % 256;
        final int packH = packSize / 256;
        final int sizeL = 64;
        final int sizeH = 0;
        this.requestUpdate(packL, packH, sizeL, sizeH);
    }
    
    public byte[] getUpdateData() {
        final int currentPackageIndex = this.mSignlePackageSize * (this.mPackOffsetL + this.mPackOffsetH * 256);
        int calcPackageSize = this.data.length - currentPackageIndex;
        calcPackageSize = ((calcPackageSize > 64) ? this.mSignlePackageSize : calcPackageSize);
        final byte[] d1 = new byte[calcPackageSize];
        System.arraycopy(this.data, currentPackageIndex, d1, 0, calcPackageSize);
        return d1;
    }
    
    private void callbackIsPortOpen(final boolean isOpen) {
        this.mHandler.post((Runnable)new Runnable() {
            @Override
            public void run() {
                GpEquipmentPort.this.mOnDataReceived.onPortOpen(isOpen);
            }
        });
    }
    
    private void callbackBacklightTimeout(final int t1, final int t2) {
        this.mHandler.post((Runnable)new Runnable() {
            @Override
            public void run() {
                final int timeout = t1 + t2 * 256;
                GpEquipmentPort.this.mOnDataReceived.onBacklightTimeout(timeout);
            }
        });
    }
    
    private void callbackBacklightStatus(final byte buffer) {
        this.mHandler.post((Runnable)new Runnable() {
            @Override
            public void run() {
                boolean isOn;
                if (buffer == -86) {
                    isOn = true;
                }
                else {
                    if (buffer != -35) {
                        return;
                    }
                    isOn = false;
                }
                GpEquipmentPort.this.mOnDataReceived.onBacklightStatus(isOn);
            }
        });
    }
    
    private void callbackCursorPosition(final int x, final int y) {
        this.mHandler.post((Runnable)new Runnable() {
            @Override
            public void run() {
                GpEquipmentPort.this.mOnDataReceived.onCursorPosition(x, y);
            }
        });
    }
    
    private void callbackDisplayRowAndColumn(final int x, final int y) {
        this.mHandler.post((Runnable)new Runnable() {
            @Override
            public void run() {
                GpEquipmentPort.this.mOnDataReceived.onDisplayRowAndColumn(x, y);
            }
        });
    }
    
    private void callbackDisplayUpdateSuccess() {
        this.mHandler.post((Runnable)new Runnable() {
            @Override
            public void run() {
                GpEquipmentPort.this.mOnDataReceived.onUpdateSuccess();
            }
        });
    }
    
    private void callbackDisplayUpdateFail(final String error) {
        this.mHandler.post((Runnable)new Runnable() {
            @Override
            public void run() {
                GpEquipmentPort.this.mOnDataReceived.onUpdateFail(error);
            }
        });
    }
    
    protected void setDataReceived(final OnDataReceived onDataReceived) {
        this.mOnDataReceived = onDataReceived;
    }
    
    private class ReadThread extends Thread
    {
        @Override
        public void run() {
            Log.d("=======", "Read start");
            final byte[] version = new byte[36];
            int versionCnt = 0;
            while (!this.isInterrupted()) {
                try {
                    final byte[] buffer = new byte[64];
                    if (GpEquipmentPort.this.mInputStream == null) {
                        return;
                    }
                    final int size = GpEquipmentPort.this.mInputStream.read(buffer);
                    if (GpEquipmentPort.this.mOnDataReceived == null) {
                        continue;
                    }
                    if (GpEquipmentPort.this.mRequestVersion) {
                        for (int i = 0; i < size && versionCnt < 36; version[versionCnt++] = buffer[i], ++i) {}
                        if (versionCnt != 36) {
                            continue;
                        }
                        GpEquipmentPort.this.mRequestVersion = false;
                        GpEquipmentPort.this.mPrepareUpdate = true;
                        versionCnt = 0;
                        if (this.checkVersion(new String(version, 0, version.length), new File(GpEquipmentPort.this.mPath).getName())) {
                            Log.d("====", "prepareUpdate");
                            GpEquipmentPort.this.prepareUpdate();
                        }
                        else {
                            GpEquipmentPort.this.resetFlag();
                            GpEquipmentPort.this.callbackDisplayUpdateFail("\u7248\u672c\u9519\u8bef");
                        }
                    }
                    else if (size == 2) {
                        switch (buffer[0]) {
                            case 2: {
                                GpEquipmentPort.this.callbackBacklightStatus(buffer[1]);
                                continue;
                            }
                        }
                    }
                    else if (size == 3) {
                        switch (buffer[0]) {
                            case 1: {
                                GpEquipmentPort.this.callbackBacklightTimeout(buffer[1] & 0xFF, buffer[2] & 0xFF);
                                continue;
                            }
                            case 3: {
                                GpEquipmentPort.this.callbackCursorPosition(buffer[1], buffer[2]);
                                continue;
                            }
                            case 4: {
                                GpEquipmentPort.this.callbackDisplayRowAndColumn(buffer[1], buffer[2] & 0xFF);
                                continue;
                            }
                        }
                    }
                    else if (size == 4) {
                        Log.d("OK", "-----_OK_------");
                        final String ok = new String(buffer, 0, size);
                        if (GpEquipmentPort.this.mPrepareUpdate) {
                            if (!"_OK_".equals(ok)) {
                                continue;
                            }
                            GpEquipmentPort.this.mPrepareUpdate = false;
                            GpEquipmentPort.this.mUpdating = true;
                            Log.d("---ok----", "\u53ef\u4ee5\u5f00\u59cb\u4e0b\u8f7d\u7a0b\u5e8f\u4e86");
                            this.update();
                        }
                        else if (GpEquipmentPort.this.mUpdating) {
                            Log.d("====updateing====", GpEquipmentPort.this.index + " -> OK");
                            this.update();
                        }
                        else {
                            if (!"_OK_".equals(ok)) {
                                continue;
                            }
                            GpEquipmentPort.this.mRequestVersion = true;
                            GpEquipmentPort.this.requestVersionInfo();
                        }
                    }
                    else if (size == 5) {
                        if (buffer[0] != 95 || buffer[1] != 66 || buffer[2] != 85 || buffer[3] != 83 || buffer[4] != 89 || buffer[5] != 95) {
                            continue;
                        }
                        GpEquipmentPort.this.mPrepareUpdate = false;
                        Log.d("===BUSY===", "\u4e0b\u4f4d\u673a\u5fd9");
                        GpEquipmentPort.this.resetFlag();
                        GpEquipmentPort.this.callbackDisplayUpdateFail("\u4e0b\u4f4d\u673a\u5fd9");
                    }
                    else if (size == 7) {
                        if ("_ERROR_".equals(new String(buffer, 0, size))) {
                            if (GpEquipmentPort.this.mPrepareUpdate) {
                                Log.d("===ERROR===", "\u56fa\u4ef6\u9519\u8bef");
                                GpEquipmentPort.this.mPrepareUpdate = false;
                            }
                            GpEquipmentPort.this.resetFlag();
                            GpEquipmentPort.this.callbackDisplayUpdateFail("\u56fa\u4ef6\u9519\u8bef");
                        }
                        else {
                            final boolean isPortOpen = GpEquipmentPort.this.check(buffer, size);
                            GpEquipmentPort.this.callbackIsPortOpen(isPortOpen);
                        }
                    }
                    else {
                        if (size != 8) {
                            continue;
                        }
                        final boolean isFinish = new String(buffer, 0, size).equals("_FINISH_");
                        if (isFinish) {
                            GpEquipmentPort.this.callbackDisplayUpdateSuccess();
                        }
                        else {
                            GpEquipmentPort.this.updateCheck(buffer, size);
                        }
                    }
                    continue;
                }
                catch (IOException e) {
                    e.printStackTrace();
                    GpEquipmentPort.this.closeSerialPort();
                    Log.d("GpEquipment", "\u7aef\u53e3\u5173\u95ed");
                    return;
                }
                break;
            }
        }
        
        private boolean checkVersion(final String originVersion, final String updateVersion) {
            final String[] origin = originVersion.split(",");
            final String[] update = updateVersion.split(",");
            if (!origin[0].equals(update[0])) {
                return false;
            }
            final String originHardwareVer = origin[1].substring(0, 5);
            final float originSoftwareVer = Float.parseFloat(origin[1].substring(5));
            final String updateHardwareVer = update[1].substring(0, 5);
            final float updateSoftwareVer = Float.parseFloat(update[1].substring(5));
            if (!originHardwareVer.equals(updateHardwareVer)) {
                return false;
            }
            if (originSoftwareVer < updateSoftwareVer) {
                return true;
            }
            final String originDateStr = origin[2].replace("_", "/").replace("E", "");
            final String updateDateStr = origin[2].replace("_", "/").replace("E", "");
            final Date originDate = new Date(originDateStr);
            final Date updateDate = new Date(updateDateStr);
            return originDate.getTime() < updateDate.getTime();
        }
        
        private void update() {
            final byte[] d = GpEquipmentPort.this.getUpdateData();
            final int length = d.length;
            final int sizeL = length % 256;
            final int sizeH = length / 256;
            GpEquipmentPort.this.download(GpEquipmentPort.this.mPackOffsetL, GpEquipmentPort.this.mPackOffsetH, sizeL, sizeH, d, d.length);
            GpEquipmentPort.this.mPackOffsetL++;
            if (GpEquipmentPort.this.mPackOffsetL > 255) {
                GpEquipmentPort.this.mPackOffsetL = 0;
                GpEquipmentPort.this.mPackOffsetH++;
            }
        }
    }
    
    public interface OnDataReceived
    {
        void onPortOpen(final boolean p0);
        
        void onBacklightStatus(final boolean p0);
        
        void onCursorPosition(final int p0, final int p1);
        
        void onDisplayRowAndColumn(final int p0, final int p1);
        
        void onBacklightTimeout(final int p0);
        
        void onUpdateSuccess();
        
        void onUpdateFail(final String p0);
    }
}

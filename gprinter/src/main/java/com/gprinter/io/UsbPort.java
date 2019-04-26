// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.io;

import android.hardware.usb.UsbEndpoint;
import java.util.Iterator;
import java.util.HashMap;
import android.app.PendingIntent;
import android.content.IntentFilter;
import com.gprinter.command.GpCom;
import java.util.Vector;
import android.os.Message;
import android.os.Bundle;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbDeviceConnection;
import android.util.Log;
import android.hardware.usb.UsbDevice;
import android.content.Intent;
import android.os.Handler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.hardware.usb.UsbManager;

public class UsbPort extends GpPort
{
    private static final String DEBUG_TAG = "UsbPortService";
    public static final String ACTION_USB_DEVICE_ATTACHED = "com.example.ACTION_USB_DEVICE_ATTACHED";
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static final String USB_PRINTER_NAME = "Gprinter";
    private String mUsbDeviceName;
    private UsbManager mUsbManager;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private Context mContext;
    private final BroadcastReceiver mUsbPermissionReceiver;
    
    public UsbPort(final Context context, final int id, final String name, final Handler handler) {
        this.mConnectThread = null;
        this.mConnectedThread = null;
        this.mContext = null;
        this.mUsbPermissionReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                final String action = intent.getAction();
                if ("com.android.example.USB_PERMISSION".equals(action)) {
                    synchronized (this) {
                        final UsbDevice device = (UsbDevice)intent.getParcelableExtra("device");
                        if (intent.getBooleanExtra("permission", false)) {
                            if (device != null) {
                                Log.d("UsbPortService", "permission ok for device " + device);
                                UsbPort.this.connect();
                            }
                        }
                        else {
                            Log.d("UsbPortService", "permission denied for device " + device);
                            UsbPort.this.stop();
                        }
                        UsbPort.this.mContext.unregisterReceiver((BroadcastReceiver)this);
                    }
                }
            }
        };
        this.mPrinterId = id;
        this.mState = 0;
        this.mHandler = handler;
        this.mUsbDeviceName = name;
        this.mContext = context;
        this.mUsbManager = (UsbManager)context.getSystemService("usb");
    }
    
    public synchronized void connect() {
        Log.d("UsbPortService", "connect to usb device ");
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        (this.mConnectThread = new ConnectThread(this.mUsbDeviceName)).start();
        this.setState(2);
    }
    
    public synchronized void connected(final UsbDeviceConnection connection, final UsbInterface intf) {
        Log.d("UsbPortService", "connected");
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        (this.mConnectedThread = new ConnectedThread(connection, intf)).start();
        final Message msg = this.mHandler.obtainMessage(4);
        final Bundle bundle = new Bundle();
        bundle.putInt("printer.id", this.mPrinterId);
        bundle.putString("device_name", "Gprinter");
        msg.setData(bundle);
        this.mHandler.sendMessage(msg);
        this.setState(3);
    }
    
    public synchronized void stop() {
        Log.d("UsbPortService", "stop");
        this.setState(0);
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
    }
    
    public GpCom.ERROR_CODE writeDataImmediately(final Vector<Byte> data) {
        GpCom.ERROR_CODE retval = GpCom.ERROR_CODE.SUCCESS;
        final ConnectedThread r;
        synchronized (this) {
            if (this.mState != 3) {
                return GpCom.ERROR_CODE.PORT_IS_NOT_OPEN;
            }
            r = this.mConnectedThread;
        }
        retval = r.writeDataImmediately(data);
        return retval;
    }
    
    boolean checkUsbDevicePidVid(final UsbDevice dev) {
        final int pid = dev.getProductId();
        final int vid = dev.getVendorId();
        boolean rel = false;
        if ((vid == 34918 && pid == 256) || (vid == 1137 && pid == 85) || (vid == 6790 && pid == 30084) || (vid == 26728 && pid == 256) || (vid == 26728 && pid == 512) || (vid == 26728 && pid == 256) || (vid == 26728 && pid == 768) || (vid == 26728 && pid == 1024) || (vid == 26728 && pid == 1280) || (vid == 26728 && pid == 1536) || (vid == 7358 && pid == 2)) {
            rel = true;
        }
        return rel;
    }
    
    private class ConnectThread extends Thread
    {
        private UsbDevice mmUSBDevice;
        private String mmDeviceName;
        private UsbDeviceConnection mmConnection;
        private UsbInterface mmIntf;
        
        public ConnectThread(final String devicename) {
            this.mmUSBDevice = null;
            this.mmDeviceName = null;
            this.mmConnection = null;
            this.mmDeviceName = devicename;
            this.mmUSBDevice = null;
            this.mmConnection = null;
            this.mmIntf = null;
        }
        
        @Override
        public void run() {
            Log.i("UsbPortService", "BEGIN mConnectThread");
            this.setName("ConnectThread");
            this.mmUSBDevice = null;
            final HashMap<String, UsbDevice> usbDeviceList = (HashMap<String, UsbDevice>)UsbPort.this.mUsbManager.getDeviceList();
            if (!this.mmDeviceName.equals("")) {
                Log.d("UsbPortService", "UsbDeviceName not empty. Trying to open it...");
                this.mmUSBDevice = usbDeviceList.get(this.mmDeviceName);
            }
            else {
                Log.d("UsbPortService", "PortName is empty. Trying to find Gp device...");
                for (final String deviceName : usbDeviceList.keySet()) {
                    final UsbDevice device = usbDeviceList.get(deviceName);
                    if (UsbPort.this.checkUsbDevicePidVid(device)) {
                        this.mmUSBDevice = device;
                        break;
                    }
                }
            }
            if (this.mmUSBDevice != null) {
                if (!UsbPort.this.mUsbManager.hasPermission(this.mmUSBDevice)) {
                    final IntentFilter UsbPermissionIntentFilter = new IntentFilter("com.android.example.USB_PERMISSION");
                    UsbPort.this.mContext.registerReceiver(UsbPort.this.mUsbPermissionReceiver, UsbPermissionIntentFilter);
                    final UsbDevice dev = this.mmUSBDevice;
                    this.mmUSBDevice = null;
                    final PendingIntent pi = PendingIntent.getBroadcast(UsbPort.this.mContext, 0, new Intent("com.android.example.USB_PERMISSION"), 0);
                    if (UsbPort.this.checkUsbDevicePidVid(dev)) {
                        UsbPort.this.mUsbManager.requestPermission(dev, pi);
                    }
                }
                else {
                    final int count = this.mmUSBDevice.getInterfaceCount();
                    UsbInterface intf = null;
                    for (int i = 0; i < count; ++i) {
                        intf = this.mmUSBDevice.getInterface(i);
                        if (intf.getInterfaceClass() == 7) {
                            break;
                        }
                    }
                    if (intf != null) {
                        this.mmIntf = intf;
                        this.mmConnection = null;
                        this.mmConnection = UsbPort.this.mUsbManager.openDevice(this.mmUSBDevice);
                        if (this.mmConnection != null) {
                            synchronized (UsbPort.this) {
                                UsbPort.this.mConnectThread = null;
                            }
                            UsbPort.this.connected(this.mmConnection, this.mmIntf);
                        }
                        else {
                            UsbPort.this.connectionToPrinterFailed();
                            UsbPort.this.stop();
                        }
                    }
                    else {
                        UsbPort.this.connectionFailed();
                        UsbPort.this.stop();
                    }
                }
            }
            else {
                Log.e("UsbPortService", "Cannot find usb device");
                UsbPort.this.stop();
            }
        }
        
        public void cancel() {
            if (this.mmConnection != null) {
                this.mmConnection.releaseInterface(this.mmIntf);
                this.mmConnection.close();
            }
            this.mmConnection = null;
        }
    }
    
    private class ConnectedThread extends Thread
    {
        UsbDeviceConnection mmConnection;
        UsbInterface mmIntf;
        private UsbEndpoint mmEndIn;
        private UsbEndpoint mmEndOut;
        
        public ConnectedThread(final UsbDeviceConnection Connection, final UsbInterface Intf) {
            this.mmEndIn = null;
            this.mmEndOut = null;
            Log.d("UsbPortService", "create ConnectedThread");
            this.mmConnection = Connection;
            this.mmIntf = Intf;
            Log.i("UsbPortService", "BEGIN mConnectedThread");
            if (this.mmConnection.claimInterface(this.mmIntf, true)) {
                for (int i = 0; i < this.mmIntf.getEndpointCount(); ++i) {
                    final UsbEndpoint ep = this.mmIntf.getEndpoint(i);
                    if (ep.getType() == 2) {
                        if (ep.getDirection() == 0) {
                            this.mmEndOut = ep;
                        }
                        else {
                            this.mmEndIn = ep;
                        }
                    }
                }
            }
        }
        
        @Override
        public void run() {
            if (this.mmEndOut != null && this.mmEndIn != null) {
                UsbPort.this.mClosePort = false;
                while (!UsbPort.this.mClosePort) {
                    try {
                        final byte[] ReceiveData = new byte[100];
                        final int bytes = this.mmConnection.bulkTransfer(this.mmEndIn, ReceiveData, ReceiveData.length, 200);
                        if (bytes > 0) {
                            final Message msg = UsbPort.this.mHandler.obtainMessage(2);
                            final Bundle bundle = new Bundle();
                            bundle.putInt("printer.id", UsbPort.this.mPrinterId);
                            bundle.putInt("device.readcnt", bytes);
                            bundle.putByteArray("device.read", ReceiveData);
                            msg.setData(bundle);
                            UsbPort.this.mHandler.sendMessage(msg);
                        }
                        Thread.sleep(30L);
                        continue;
                    }
                    catch (InterruptedException e) {
                        UsbPort.this.connectionLost();
                    }
                    break;
                }
                Log.d("UsbPortService", "Closing Usb work");
            }
            else {
                UsbPort.this.stop();
                UsbPort.this.connectionLost();
            }
        }
        
        public void cancel() {
            UsbPort.this.mClosePort = true;
            this.mmConnection.releaseInterface(this.mmIntf);
            this.mmConnection.close();
            this.mmConnection = null;
        }
        
        public GpCom.ERROR_CODE writeDataImmediately(final Vector<Byte> data) {
            GpCom.ERROR_CODE retval = GpCom.ERROR_CODE.SUCCESS;
            if (data != null && data.size() > 0) {
                final byte[] sendData = new byte[data.size()];
                for (int i = 0; i < data.size(); ++i) {
                    sendData[i] = data.get(i);
                }
                try {
                    final int result = this.mmConnection.bulkTransfer(this.mmEndOut, sendData, sendData.length, 500);
                    if (result >= 0) {
                        Log.d("UsbPortService", "send success");
                    }
                }
                catch (Exception e) {
                    Log.d("UsbPortService", "Exception occured while sending data immediately: " + e.getMessage());
                    retval = GpCom.ERROR_CODE.FAILED;
                }
            }
            return retval;
        }
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.io;

import java.util.LinkedList;
import java.util.Vector;
import android.content.Context;
import android.bluetooth.BluetoothDevice;
import java.net.InetAddress;
import com.gprinter.command.GpCom;
import android.os.Handler;
import android.util.Log;
import java.util.Queue;
import android.bluetooth.BluetoothAdapter;

public class GpDevice
{
    private static final String DEBUG_TAG = "GpDevice";
    public static final String CONNECT_ERROR = "connect error";
    private BluetoothAdapter mBluetoothAdapter;
    private GpPort mPort;
    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_INVALID_PRINTER = 4;
    public static final int STATE_VALID_PRINTER = 5;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_OFFLINE_STATUS = 6;
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public static final String PRINTER_ID = "printer.id";
    public static final String DEVICE_STATUS = "device_status";
    public static final String DEVICE_READ = "device.read";
    public static final String DEVICE_READ_CNT = "device.readcnt";
    private PortParameters mPortParam;
    private int mCommandType;
    public static Queue<Integer> mReceiveQueue;
    private boolean mReceiveDataEnable;
    
    public GpDevice() {
        this.mBluetoothAdapter = null;
        this.mPort = null;
        this.mPortParam = null;
        this.mCommandType = -1;
        this.mReceiveDataEnable = false;
        this.mBluetoothAdapter = null;
        this.mPort = null;
        this.mPortParam = new PortParameters();
        this.mCommandType = 0;
        this.mReceiveDataEnable = false;
    }
    
    public void setCommandType(final int command) {
        this.mCommandType = command;
    }
    
    public int getCommandType() {
        return this.mCommandType;
    }
    
    public PortParameters getPortParameters() {
        return this.mPortParam;
    }
    
    public void setReceiveDataEnable(final boolean b) {
        this.mReceiveDataEnable = b;
    }
    
    public boolean getReceiveDataEnable() {
        return this.mReceiveDataEnable;
    }
    
    public int getConnectState() {
        int state = 0;
        if (this.mPort != null) {
            Log.d("GpDevice", "getConnectState ");
            state = this.mPort.getState();
        }
        return state;
    }
    
    public GpCom.ERROR_CODE openEthernetPort(final int id, final String ip, final int port, final Handler mHandler) {
        GpCom.ERROR_CODE retval = GpCom.ERROR_CODE.SUCCESS;
        this.mPortParam.setPortType(3);
        this.mPortParam.setIpAddr(ip);
        this.mPortParam.setPortNumber(port);
        if (mHandler == null) {
            Log.e("GpDevice", "Parameters is invalid");
            retval = GpCom.ERROR_CODE.INVALID_DEVICE_PARAMETERS;
        }
        else if (port <= 0) {
            Log.e("GpDevice", "PortNumber is invalid");
            retval = GpCom.ERROR_CODE.INVALID_PORT_NUMBER;
        }
        else if (ip.length() != 0) {
            try {
                InetAddress.getByName(ip);
                if (this.mPort != null) {
                    if (this.mPort.getState() == 3) {
                        return GpCom.ERROR_CODE.DEVICE_ALREADY_OPEN;
                    }
                    Log.e("GpDevice", "UsbPort is open already, try to closing port");
                    this.mPort.stop();
                    this.mPort = null;
                }
                (this.mPort = new EthernetPort(id, ip, port, mHandler)).connect();
            }
            catch (Exception e) {
                Log.e("GpDevice", "IpAddress is invalid");
                retval = GpCom.ERROR_CODE.INVALID_IP_ADDRESS;
            }
        }
        else {
            Log.e("GpDevice", "IpAddress is invalid");
            retval = GpCom.ERROR_CODE.INVALID_IP_ADDRESS;
        }
        return retval;
    }
    
    public GpCom.ERROR_CODE openBluetoothPort(final int id, final String addr, final Handler mHandler) {
        GpCom.ERROR_CODE retval = GpCom.ERROR_CODE.SUCCESS;
        this.mPortParam.setPortType(4);
        this.mPortParam.setBluetoothAddr(addr);
        if (mHandler == null) {
            Log.e("GpDevice", "Parameters is invalid");
            retval = GpCom.ERROR_CODE.INVALID_DEVICE_PARAMETERS;
        }
        else {
            this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (this.mBluetoothAdapter == null) {
                retval = GpCom.ERROR_CODE.BLUETOOTH_IS_NOT_SUPPORT;
                Log.e("GpDevice", "Bluetooth is not support");
            }
            else if (!this.mBluetoothAdapter.isEnabled()) {
                retval = GpCom.ERROR_CODE.OPEN_BLUETOOTH;
                Log.e("GpDevice", "Bluetooth is not open");
            }
            else if (BluetoothAdapter.checkBluetoothAddress(addr)) {
                final BluetoothDevice device = this.mBluetoothAdapter.getRemoteDevice(addr);
                if (this.mPort != null) {
                    if (this.mPort.getState() == 3) {
                        return GpCom.ERROR_CODE.DEVICE_ALREADY_OPEN;
                    }
                    Log.e("GpDevice", "Bluetooth is open already, try to closing port");
                    this.mPort.stop();
                    this.mPort = null;
                }
                (this.mPort = new BluetoothPort(id, device, mHandler)).connect();
            }
            else {
                Log.e("GpDevice", "Bluetooth address is invalid");
                retval = GpCom.ERROR_CODE.INVALID_BLUETOOTH_ADDRESS;
            }
        }
        return retval;
    }
    
    public GpCom.ERROR_CODE openUSBPort(final Context context, final int id, final String deviceName, final Handler handler) {
        GpCom.ERROR_CODE retval = GpCom.ERROR_CODE.SUCCESS;
        this.mPortParam.setPortType(2);
        this.mPortParam.setUsbDeviceName(deviceName);
        if (handler == null || context == null) {
            retval = GpCom.ERROR_CODE.INVALID_DEVICE_PARAMETERS;
            Log.e("GpDevice", "Parameters is invalid");
        }
        else {
            if (this.mPort != null) {
                if (this.mPort.getState() == 3) {
                    return GpCom.ERROR_CODE.DEVICE_ALREADY_OPEN;
                }
                Log.e("GpDevice", "UsbPort is open already, try to closing port");
                this.mPort.stop();
                this.mPort = null;
            }
            Log.e("GpDevice", "openUSBPort id " + id);
            (this.mPort = new UsbPort(context, id, deviceName, handler)).connect();
        }
        return retval;
    }
    
    public void closePort() {
        if (this.mPort != null) {
            this.mPort.stop();
            this.mPort = null;
        }
    }
    
    public GpCom.ERROR_CODE sendDataImmediately(final Vector<Byte> Command) {
        final Vector<Byte> data = new Vector<Byte>(Command.size());
        GpCom.ERROR_CODE retval;
        if (this.mPort != null) {
            if (this.mPort.getState() == 3) {
                for (int k = 0; k < Command.size(); ++k) {
                    if (data.size() >= 1024) {
                        retval = this.mPort.writeDataImmediately(data);
                        data.clear();
                        if (retval != GpCom.ERROR_CODE.SUCCESS) {
                            return retval;
                        }
                    }
                    data.add(Command.get(k));
                }
                retval = this.mPort.writeDataImmediately(data);
                Log.i("GpDevice", "retval = " + retval);
            }
            else {
                retval = GpCom.ERROR_CODE.PORT_IS_DISCONNECT;
                Log.e("GpDevice", "Port is disconnect");
            }
        }
        else {
            retval = GpCom.ERROR_CODE.PORT_IS_NOT_OPEN;
            Log.e("GpDevice", "Port is not open");
        }
        return retval;
    }
    
    static {
        GpDevice.mReceiveQueue = new LinkedList<Integer>();
    }
}

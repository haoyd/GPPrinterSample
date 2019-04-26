// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.service;

import java.util.Iterator;
import android.app.ActivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import java.util.Arrays;
import java.io.Serializable;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import com.gprinter.io.PortParameters;
import android.os.IBinder;
import android.content.IntentFilter;
import com.gprinter.util.LogInfo;
import java.util.List;
import android.widget.Toast;
import java.util.ArrayList;
import com.gprinter.save.PortParamDataBase;
import android.hardware.usb.UsbDevice;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Message;
import android.os.Bundle;
import java.util.TimerTask;
import java.util.Timer;
import java.util.Vector;
import android.util.Base64;
import android.os.RemoteException;
import android.content.Context;
import android.util.Log;
import android.os.Handler;
import android.content.BroadcastReceiver;
import com.gprinter.aidl.GpService;
import com.gprinter.command.GpCom;
import android.os.PowerManager;
import com.lidroid.xutils.DbUtils;
import android.annotation.SuppressLint;
import com.gprinter.io.GpDevice;
import android.app.Service;

public class GpPrintService extends Service
{
    private static final String DEBUG_TAG = "GpPrintService";
    public static final String ACTION_PORT_OPEN = "action.port.open";
    public static final String ACTION_PORT_CLOSE = "action.port.close";
    public static final String ACTION_PRINT_TESTPAGE = "action.print.testpage";
    public static final String PRINTER_ID = "printer.id";
    public static final String PORT_TYPE = "port.type";
    public static final String USB_DEVICE_NAME = "usb.devicename";
    public static final String BLUETOOT_ADDR = "bluetooth.addr";
    public static final String IP_ADDR = "port.addr";
    public static final String PORT_NUMBER = "port.number";
    public static final String CONNECT_STATUS = "connect.status";
    public static final String PRINTER_CALLBACK = "printer.callback";
    public static final int MAX_PRINTER_CNT = 20;
    public static final String PRINTER_STATUS = "printer.status";
    public static final String ACTION_PRINTER_STATUS = "action.printer.status";
    private static GpDevice[] mDevice;
    private boolean[] mIsAuth;
    public static final String KEY_USER_EXPERIENCE = "UserExperience";
    public static final String BrocastAction = "com.gprinter.service.ReadBrocastReceiver";
    public static final String ACTION_CONNECT_STATUS = "action.connect.status";
    public static final String PRINTER_SERVICE = "com.gprinter.aidl.GpPrintService";
    public static final String ALLSERVICE = "com.gprinter.service.ALLSERVICE";
    public static final String SMPRINT_SERVICE = "com.gprinter.service.SmPrintService";
    public static String IMSI;
    public static int PrinterId;
    @SuppressLint({ "SdCardPath" })
    public static String DB_DIR;
    public static final String DB_NAME = "smartprint.db";
    public static DbUtils db;
    public static String CLIENTNUM;
    private PowerManager.WakeLock wakeLock;
    private int mTimeout;
    private int mServiceTimeout;
    public static final Object object;
    private boolean mIsReceivedStatus;
    public static final byte FLAG = 16;
    private GpCom.ERROR_CODE retval;
    GpService.Stub aidls;
    private BroadcastReceiver PortOperateBroadcastReceiver;
    private boolean isServiceStart;
    private boolean mUseUsb;
    private final Handler mHandler;
    
    public GpPrintService() {
        this.mIsAuth = new boolean[20];
        this.wakeLock = null;
        this.mTimeout = 1000;
        this.mServiceTimeout = 4000;
        this.aidls = new GpService.Stub() {
            public int openPort(final int PrinterId, final int PortType, final String DeviceName, final int PortNumber) throws RemoteException {
                GpCom.ERROR_CODE retval = GpCom.ERROR_CODE.SUCCESS;
                Log.d("GpPrintService", "port type " + PortType + "PrinterId " + PrinterId);
                switch (PortType) {
                    case 2: {
                        Log.d("GpPrintService", "port addr " + DeviceName);
                        retval = GpPrintService.mDevice[PrinterId].openUSBPort((Context)GpPrintService.this, PrinterId, DeviceName, GpPrintService.this.mHandler);
                        break;
                    }
                    case 4: {
                        retval = GpPrintService.mDevice[PrinterId].openBluetoothPort(PrinterId, DeviceName, GpPrintService.this.mHandler);
                        break;
                    }
                    case 3: {
                        retval = GpPrintService.mDevice[PrinterId].openEthernetPort(PrinterId, DeviceName, PortNumber, GpPrintService.this.mHandler);
                        break;
                    }
                }
                return retval.ordinal();
            }
            
            public void closePort(final int PrinterId) throws RemoteException {
                GpPrintService.mDevice[PrinterId].closePort();
                GpPrintService.this.mIsAuth[PrinterId] = false;
            }
            
            public int printeTestPage(final int PrinterId) throws RemoteException {
                Log.d("GpPrintService", "printeTestPage ");
                final int rel = GpPrintService.this.printTestPage(PrinterId);
                return rel;
            }
            
            public int sendEscCommand(final int PrinterId, final String b64) throws RemoteException {
                Log.d("GpPrintService", "sendEscCommand");
                GpPrintService.this.retval = GpCom.ERROR_CODE.SUCCESS;
                final Thread thread = new Thread() {
                    @Override
                    public void run() {
                        if (GpPrintService.mDevice[PrinterId].getCommandType() == 0) {
                            final byte[] datas = Base64.decode(b64, 0);
                            final Vector<Byte> vector = new Vector<Byte>();
                            for (final byte b : datas) {
                                vector.add(b);
                            }
                            GpPrintService.this.retval = GpPrintService.mDevice[PrinterId].sendDataImmediately(vector);
                        }
                        else if (GpPrintService.mDevice[PrinterId].getCommandType() == -1) {
                            GpPrintService.this.retval = GpCom.ERROR_CODE.PORT_IS_NOT_OPEN;
                        }
                        else {
                            GpPrintService.this.retval = GpCom.ERROR_CODE.FAILED;
                        }
                    }
                };
                thread.start();
                try {
                    thread.join();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return GpPrintService.this.retval.ordinal();
            }
            
            public int sendLabelCommand(final int PrinterId, final String b64) throws RemoteException {
                GpPrintService.this.retval = GpCom.ERROR_CODE.SUCCESS;
                final Thread thread = new Thread() {
                    @Override
                    public void run() {
                        if (GpPrintService.mDevice[PrinterId].getCommandType() == 1) {
                            final byte[] datas = Base64.decode(b64, 0);
                            final Vector<Byte> vector = new Vector<Byte>();
                            for (final byte b : datas) {
                                vector.add(b);
                            }
                            GpPrintService.this.retval = GpPrintService.mDevice[PrinterId].sendDataImmediately(vector);
                        }
                        else if (GpPrintService.mDevice[PrinterId].getCommandType() == -1) {
                            GpPrintService.this.retval = GpCom.ERROR_CODE.PORT_IS_NOT_OPEN;
                        }
                        else {
                            GpPrintService.this.retval = GpCom.ERROR_CODE.FAILED;
                        }
                    }
                };
                thread.start();
                try {
                    thread.join();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return GpPrintService.this.retval.ordinal();
            }
            
            public synchronized void queryPrinterStatus(final int PrinterId, final int Timesout, final int requestCode) throws RemoteException {
                GpPrintService.this.mIsReceivedStatus = false;
                final byte[] esc = { 16, 4, 2 };
                final byte[] tsc = { 27, 33, 63 };
                Log.i("GpPrintService", "queryPrintStatus ");
                if (GpPrintService.mDevice[PrinterId].getConnectState() == 3) {
                    new Thread() {
                        @Override
                        public void run() {
                            Vector<Byte> data = null;
                            if (GpPrintService.mDevice[PrinterId].getCommandType() == 0) {
                                data = new Vector<Byte>(esc.length);
                                for (int i = 0; i < esc.length; ++i) {
                                    data.add(esc[i]);
                                }
                            }
                            else {
                                data = new Vector<Byte>(tsc.length);
                                for (int i = 0; i < tsc.length; ++i) {
                                    data.add(tsc[i]);
                                }
                            }
                            GpDevice.mReceiveQueue.offer(requestCode);
                            GpPrintService.mDevice[PrinterId].sendDataImmediately(data);
                            final Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if (!GpPrintService.this.mIsReceivedStatus) {
                                        final byte[] statusBytes = { 16 };
                                        final Message msg = GpPrintService.this.mHandler.obtainMessage(2);
                                        final Bundle bundle = new Bundle();
                                        bundle.putInt("printer.id", PrinterId);
                                        bundle.putInt("device.readcnt", 1);
                                        bundle.putByteArray("device.read", statusBytes);
                                        msg.setData(bundle);
                                        GpPrintService.this.mHandler.sendMessage(msg);
                                    }
                                }
                            }, Timesout);
                        }
                    }.start();
                }
                else {
                    GpDevice.mReceiveQueue.add(requestCode);
                    final byte[] statusBytes = { 1 };
                    final Message msg = GpPrintService.this.mHandler.obtainMessage(6);
                    final Bundle bundle = new Bundle();
                    bundle.putInt("printer.id", PrinterId);
                    bundle.putInt("device.readcnt", 1);
                    bundle.putByteArray("device.read", statusBytes);
                    msg.setData(bundle);
                    GpPrintService.this.mHandler.sendMessage(msg);
                }
            }
            
            public int getPrinterCommandType(final int PrinterId) throws RemoteException {
                final int type = GpPrintService.mDevice[PrinterId].getCommandType();
                return type;
            }
            
            public int getPrinterConnectStatus(final int PrinterId) throws RemoteException {
                final int status = GpPrintService.mDevice[PrinterId].getConnectState();
                return status;
            }
            
            public void isUserExperience(final boolean userExperience) throws RemoteException {
                final SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences((Context)GpPrintService.this);
                sharedPreference.edit().putBoolean("key_ischecked", userExperience);
                SendDeviceInfoThread.isChecked(userExperience);
            }
            
            public String getClientID() throws RemoteException {
                return "";
            }
            
            public int setServerIP(final String ip, final int port) throws RemoteException {
                return 0;
            }
            
            public void setCommandType(final int printerId, final int commandType, final boolean response) throws RemoteException {
                GpPrintService.this.mUseUsb = response;
                GpPrintService.mDevice[printerId].setCommandType(commandType);
            }
        };
        this.PortOperateBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if ("action.port.open".equals(intent.getAction())) {
                    Log.d("GpPrintService", "PortOperateBroadcastReceiver action.port.open");
                    GpCom.ERROR_CODE retval = GpCom.ERROR_CODE.SUCCESS;
                    final int type = intent.getIntExtra("port.type", 0);
                    final int id = intent.getIntExtra("printer.id", 0);
                    Log.d("GpPrintService", "port type " + type + "PrinterId " + id);
                    switch (type) {
                        case 2: {
                            final String name = intent.getStringExtra("usb.devicename");
                            Log.d("GpPrintService", "port addr " + name);
                            retval = GpPrintService.mDevice[id].openUSBPort((Context)GpPrintService.this, id, name, GpPrintService.this.mHandler);
                            break;
                        }
                        case 4: {
                            final String addr = intent.getStringExtra("bluetooth.addr");
                            retval = GpPrintService.mDevice[id].openBluetoothPort(id, addr, GpPrintService.this.mHandler);
                            break;
                        }
                        case 3: {
                            final int port = intent.getIntExtra("port.number", 9100);
                            final String addr = intent.getStringExtra("port.addr");
                            retval = GpPrintService.mDevice[id].openEthernetPort(id, addr, port, GpPrintService.this.mHandler);
                            break;
                        }
                    }
                    if (retval != GpCom.ERROR_CODE.SUCCESS) {
                        GpPrintService.this.showError(retval);
                    }
                }
                else if ("action.port.close".equals(intent.getAction())) {
                    final int id2 = intent.getIntExtra("printer.id", 0);
                    Log.d("GpPrintService", "PrinterId " + id2);
                    GpPrintService.mDevice[id2].closePort();
                }
                else if ("action.print.testpage".equals(intent.getAction())) {
                    final int id2 = intent.getIntExtra("printer.id", 0);
                    GpPrintService.this.printTestPage(id2);
                }
                else if ("android.bluetooth.device.action.ACL_DISCONNECTED".equals(intent.getAction())) {
                    final BluetoothDevice device = (BluetoothDevice)intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                    GpPrintService.this.disconnectBluetoothDevice(device.getAddress());
                }
                else if ("android.hardware.usb.action.USB_DEVICE_DETACHED".equals(intent.getAction())) {
                    final UsbDevice device2 = (UsbDevice)intent.getParcelableExtra("device");
                    GpPrintService.this.disconnectUsbDevice(device2.getDeviceName());
                }
            }
        };
        this.mUseUsb = false;
        this.mHandler = new Handler((Handler.Callback)new Handler.Callback() {
            public boolean handleMessage(final Message msg) {
                switch (msg.what) {
                    case 1: {
                        Log.i("GpPrintService", "MESSAGE_STATE_CHANGE: " + msg.arg1);
                        final int type = msg.getData().getInt("device_status");
                        final int id = msg.getData().getInt("printer.id");
                        switch (type) {
                            case 3: {
                                if (GpPrintService.mDevice[id].getPortParameters().getPortType() == 2 && GpPrintService.this.mUseUsb) {
                                    GpPrintService.this.mIsAuth[id] = true;
                                    if (GpPrintService.mDevice[id].getCommandType() == 1) {
                                        new PortParamDataBase((Context)GpPrintService.this).insertPrinterName(id, "MODEL:GP-2120");
                                    }
                                    else {
                                        new PortParamDataBase((Context)GpPrintService.this).insertPrinterName(id, "_GP5890XIII");
                                    }
                                    GpPrintService.this.sendConnectionStatusBroadcastToFront(id);
                                    break;
                                }
                                new Thread() {
                                    @Override
                                    public void run() {
                                        Log.i("GpPrintService", "STATE_CONNECTED");
                                        final Vector<Byte> vector = new Vector<Byte>();
                                        vector.add((byte)29);
                                        vector.add((byte)73);
                                        vector.add((byte)67);
                                        GpPrintService.mDevice[id].sendDataImmediately(vector);
                                        Log.d("GpPrintService", "send auth 1");
                                        final Timer timer = new Timer();
                                        timer.schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                if (!GpPrintService.this.mIsAuth[id]) {
                                                    Log.d("GpPrintUsb", "send auth label 3");
                                                    final Vector<Byte> tscAuth = new Vector<Byte>();
                                                    tscAuth.add((byte)126);
                                                    tscAuth.add((byte)33);
                                                    tscAuth.add((byte)84);
                                                    Log.d("GpPrintService", "send ~!T");
                                                    GpPrintService.mDevice[id].sendDataImmediately(tscAuth);
                                                    final Timer timer1 = new Timer();
                                                    timer1.schedule(new TimerTask() {
                                                        @Override
                                                        public void run() {
                                                            if (!GpPrintService.this.mIsAuth[id]) {
                                                                GpPrintService.mDevice[id].closePort();
                                                            }
                                                        }
                                                    }, 4000L);
                                                }
                                            }
                                        }, 4000L);
                                    }
                                }.start();
                                break;
                            }
                            case 2: {
                                Log.i("GpPrintService", "STATE_CONNECTING");
                                break;
                            }
                            case 0:
                            case 1: {
                                Log.i("GpPrintService", "STATE_NONE");
                                GpPrintService.this.mIsAuth[id] = false;
                                break;
                            }
                        }
                        final Intent intent = new Intent("action.connect.status");
                        intent.putExtra("connect.status", type);
                        intent.putExtra("printer.id", id);
                        GpPrintService.this.sendBroadcast(intent);
                    }
                    case 2: {
                        final int printerId = msg.getData().getInt("printer.id");
                        final int cnt = msg.getData().getInt("device.readcnt");
                        final byte[] readBuf = msg.getData().getByteArray("device.read");
                        final List<Byte> data = new ArrayList<Byte>();
                        for (int i = 0; i < cnt; ++i) {
                            if (readBuf[i] != 19 && readBuf[i] != 17) {
                                data.add(readBuf[i]);
                            }
                        }
                        if (!GpPrintService.this.mIsAuth[printerId]) {
                            final byte[] device = new byte[cnt];
                            final int size = data.size();
                            for (int j = 0; j < size; ++j) {
                                device[j] = data.get(j);
                            }
                            final String name = new String(device, 0, size);
                            new PortParamDataBase((Context)GpPrintService.this).insertPrinterName(printerId, name);
                            GpPrintService.this.mIsAuth[printerId] = GpPrintService.this.IsGprinter(printerId, device);
                            Log.d("GpPrintService", "RESULT AUTH->" + GpPrintService.this.mIsAuth[printerId]);
                            Log.d("GpPrintService", "size->" + size);
                            if (GpPrintService.this.mIsAuth[printerId]) {
                                GpPrintService.this.sendConnectionStatusBroadcastToFront(printerId);
                            }
                        }
                        else {
                            GpPrintService.this.mIsReceivedStatus = true;
                            Log.i("GpPrintService", "readMessage cnt" + cnt);
                            if (GpPrintService.mDevice[printerId].getCommandType() == 0) {
                                if (cnt <= 1) {
                                    final int result = GpPrintService.this.judgeResponseType(readBuf[0]);
                                    if (result == 0) {
                                        if (readBuf[0] == 0) {
                                            GpPrintService.this.sendFinishBroadcastToFront(printerId);
                                        }
                                    }
                                    else if (result == 1) {
                                        GpPrintService.this.sendStatusBroadcastToFront(printerId, readBuf[0]);
                                    }
                                }
                            }
                            else if (GpPrintService.mDevice[printerId].getCommandType() == 1) {
                                if (cnt == 1) {
                                    GpPrintService.this.sendStatusBroadcastToFront(printerId, readBuf[0]);
                                }
                                else {
                                    GpPrintService.this.sendResponseBroadcastToFront(printerId, readBuf, cnt);
                                }
                            }
                        }
                        break;
                    }
                    case 4: {
                        Log.i("GpPrintService", "DeviceName: " + msg.getData().getString("device_name"));
                        break;
                    }
                    case 5: {
                        Log.i("GpPrintService", "MessageToast: " + msg.getData().getString("toast"));
                        Toast.makeText((Context)GpPrintService.this, (CharSequence)msg.getData().getString("toast"), 0).show();
                        break;
                    }
                    case 6: {
                        GpPrintService.this.mIsReceivedStatus = true;
                        final int pid = msg.getData().getInt("printer.id");
                        final int cnt = msg.getData().getInt("device.readcnt");
                        final byte[] readBuf = msg.getData().getByteArray("device.read");
                        Log.i("GpPrintService", "readMessage byte " + readBuf[0]);
                        Log.i("GpPrintService", "readMessage cnt" + cnt);
                        GpPrintService.this.sendStatusBroadcastToFront(pid, readBuf[0]);
                        break;
                    }
                }
                return false;
            }
        });
    }
    
    public void onCreate() {
        super.onCreate();
        Log.d("GpPrintService", "-Service onCreate-");
        this.acquireWakeLock();
        this.registerUserPortActionBroadcast();
        this.initUpService();
        for (int i = 0; i < 20; ++i) {
            GpPrintService.mDevice[i] = new GpDevice();
        }
    }
    
    private void initUpService() {
        GpPrintService.DB_DIR = this.getExternalFilesDir((String)null).toString();
        final String macAddress = this.getWIFIMacAddress();
        GpPrintService.IMSI = this.getIMSI(macAddress);
        LogInfo.out("IMSI:" + GpPrintService.IMSI);
        GpPrintService.CLIENTNUM = getClientNUM(GpPrintService.IMSI);
    }
    
    public void registerUserPortActionBroadcast() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction("action.port.open");
        filter.addAction("action.port.close");
        filter.addAction("action.print.testpage");
        filter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        this.registerReceiver(this.PortOperateBroadcastReceiver, filter);
    }
    
    public void onStart(final Intent intent, final int startId) {
        Log.d("GpPrintService", "-Service onStart-");
    }
    
    public void onDestroy() {
        Log.d("GpPrintService", "-Service onDestory-");
        this.unregisterReceiver(this.PortOperateBroadcastReceiver);
        this.releaseWakeLock();
        super.onDestroy();
    }
    
    public boolean onUnbind(final Intent intent) {
        Log.d("GpPrintService", "-Service onUnbind-");
        return super.onUnbind(intent);
    }
    
    public void onRebind(final Intent intent) {
        super.onRebind(intent);
        Log.d("GpPrintService", "-Service onRebind-");
    }
    
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.d("GpPrintService", "-Service onStartCommand-");
        return 1;
    }
    
    public IBinder onBind(final Intent intent) {
        System.out.println("Service onBind");
        return (IBinder)this.aidls;
    }
    
    public boolean[] getConnectState() {
        final boolean[] state = new boolean[20];
        for (int i = 0; i < 20; ++i) {
            state[i] = false;
        }
        for (int i = 0; i < 20; ++i) {
            if (GpPrintService.mDevice[i] != null) {
                Log.d("GpPrintService", "getConnectState " + i);
                if (GpPrintService.mDevice[i].getConnectState() == 3) {
                    state[i] = true;
                }
            }
        }
        return state;
    }
    
    private void showError(final GpCom.ERROR_CODE retval) {
        GpCom.getErrorText(retval);
    }
    
    private void disconnectBluetoothDevice(final String addr) {
        for (int i = 0; i < 20; ++i) {
            final PortParameters p = GpPrintService.mDevice[i].getPortParameters();
            if (p.getPortType() == 4 && p.getBluetoothAddr().equals(addr)) {
                GpPrintService.mDevice[i].closePort();
                break;
            }
        }
    }
    
    private void disconnectUsbDevice(final String name) {
        for (int i = 0; i < 20; ++i) {
            final PortParameters p = GpPrintService.mDevice[i].getPortParameters();
            if (p.getPortType() == 2 && p.getUsbDeviceName().equals(name)) {
                GpPrintService.mDevice[i].closePort();
                break;
            }
        }
    }
    
    int printTestPage(final int id) {
        this.retval = GpCom.ERROR_CODE.SUCCESS;
        final Thread thread = new Thread() {
            @Override
            public void run() {
                Vector<Byte> TestPageData = null;
                if (GpPrintService.mDevice[id].getConnectState() == 3) {
                    if (GpPrintService.mDevice[id].getCommandType() == 0) {
                        if (GpPrintService.this.getResources().getConfiguration().locale.getCountry().equals("CN")) {
                            TestPageData = GpPrintService.this.getTestPageData("/esc_CN.txt");
                            Log.d("GpPrintService", "Send  ESC data ");
                        }
                        else {
                            TestPageData = GpPrintService.this.getTestPageData("/esc.txt");
                        }
                        GpPrintService.this.retval = GpPrintService.mDevice[id].sendDataImmediately(TestPageData);
                    }
                    else if (GpPrintService.mDevice[id].getCommandType() == 1) {
                        Log.d("GpPrintService", "Send Label data ");
                        if (GpPrintService.this.getResources().getConfiguration().locale.getCountry().equals("CN")) {
                            TestPageData = GpPrintService.this.getTestPageData("/tsc_CN.txt");
                        }
                        else {
                            TestPageData = GpPrintService.this.getTestPageData("/tsc.txt");
                        }
                        GpPrintService.this.retval = GpPrintService.mDevice[id].sendDataImmediately(TestPageData);
                    }
                    else {
                        GpPrintService.this.retval = GpCom.ERROR_CODE.INVALID_DEVICE_PARAMETERS;
                    }
                }
                else {
                    Log.d("GpPrintService", "Port is not connect ");
                    GpPrintService.this.retval = GpCom.ERROR_CODE.PORT_IS_NOT_OPEN;
                }
            }
        };
        thread.start();
        try {
            thread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this.retval.ordinal();
    }
    
    Vector<Byte> getTestPageData(final String root) {
        byte[] data = null;
        Log.d("GpPrintService", "PrintTestPageButtonOnClickListener" + root);
        final InputStream in = this.getClass().getResourceAsStream(root);
        final byte[] bs = new byte[8192];
        int len = 0;
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            while ((len = in.read(bs)) != -1) {
                out.write(bs, 0, len);
            }
            data = out.toByteArray();
        }
        catch (IOException e) {
            e.printStackTrace();
            try {
                in.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        finally {
            try {
                in.close();
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        final Vector<Byte> TestPageData = new Vector<Byte>(data.length);
        for (int i = 0; i < data.length; ++i) {
            TestPageData.add(bs[i]);
        }
        return TestPageData;
    }
    
    private void sendConnectionStatusBroadcastToFront(final int id) {
        this.isServiceStart = false;
        Log.d("GpPrintService", "Current state ->[" + id + "]" + GpPrintService.mDevice[id].getConnectState());
        if (GpPrintService.mDevice[id].getConnectState() == 3) {
            final int type = GpPrintService.mDevice[id].getCommandType();
            final Intent allServiceIntent = new Intent((Context)this, (Class)AllService.class);
            allServiceIntent.putExtra("mode", type);
            allServiceIntent.putExtra("printId", id);
            this.startService(allServiceIntent);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long time = System.currentTimeMillis();
                    final long timeout = time + GpPrintService.this.mServiceTimeout;
                    while (time < timeout && !GpPrintService.this.isServiceStart) {
                        try {
                            Thread.sleep(100L);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        time = System.currentTimeMillis();
                        GpPrintService.this.isServiceStart = GpPrintService.this.isServiceRunning(AllService.class);
                    }
                    if (GpPrintService.this.isServiceStart) {
                        Log.e("GpPrintService", "STATE_VALID_PRINTER");
                        GpPrintService.mDevice[id].setCommandType(type);
                        final Intent intent = new Intent("action.connect.status");
                        intent.putExtra("connect.status", 5);
                        intent.putExtra("printer.id", id);
                        GpPrintService.this.sendBroadcast(intent);
                    }
                    else {
                        GpPrintService.mDevice[id].closePort();
                        final Intent intent = new Intent("action.connect.status");
                        intent.putExtra("connect.status", 0);
                        intent.putExtra("printer.id", id);
                        GpPrintService.this.sendBroadcast(intent);
                        final Message msg = GpPrintService.this.mHandler.obtainMessage(5);
                        final Bundle bundle = new Bundle();
                        bundle.putInt("printer.id", id);
                        bundle.putString("toast", "Please start service");
                        msg.setData(bundle);
                        GpPrintService.this.mHandler.sendMessage(msg);
                    }
                }
            }).start();
        }
    }
    
    private void sendFinishBroadcastToFront(final int id) {
        final Intent statusBroadcast = new Intent("action.device.receipt.response");
        statusBroadcast.putExtra("printer.id", id);
        this.sendBroadcast(statusBroadcast);
    }
    
    private void sendStatusBroadcastToFront(final int id, final int r) {
        Log.i("GpPrintService", "printer disconnect " + r);
        int status;
        if (r == 16) {
            status = 16;
        }
        else {
            status = 0;
        }
        if (GpPrintService.mDevice[id].getConnectState() == 3) {
            if (GpPrintService.mDevice[id].getCommandType() == 0) {
                if ((r & 0x20) > 0) {
                    status |= 0x2;
                }
                if ((r & 0x4) > 0) {
                    status |= 0x4;
                }
                if ((r & 0x40) > 0) {
                    status |= 0x8;
                }
            }
            else {
                if ((r & 0x4) > 0) {
                    status |= 0x2;
                }
                if ((r & 0x40) > 0) {
                    status |= 0x4;
                }
                if ((r & 0x80) > 0) {
                    status |= 0x8;
                }
            }
        }
        else {
            status |= 0x1;
        }
        if (GpDevice.mReceiveQueue.isEmpty()) {
            return;
        }
        final Integer requestCode = GpDevice.mReceiveQueue.poll();
        final Intent statusBroadcast = new Intent("action.device.real.status");
        statusBroadcast.putExtra("action.printer.real.status", status);
        statusBroadcast.putExtra("printer.id", id);
        statusBroadcast.putExtra("printer.request_code", (Serializable)requestCode);
        this.sendBroadcast(statusBroadcast);
    }
    
    private void sendResponseBroadcastToFront(final int id, final byte[] response, final int cnt) {
        final Intent statusBroadcast = new Intent("action.device.label.response");
        statusBroadcast.putExtra("printer.id", id);
        statusBroadcast.putExtra("printer.label.response", response);
        statusBroadcast.putExtra("printer.label.response.cnt", cnt);
        this.sendBroadcast(statusBroadcast);
    }
    
    private int judgeResponseType(final byte r) {
        final byte result = (byte)((r & 0x10) >> 4);
        return result;
    }
    
    private boolean judgePrinter(final byte[] table, final byte[] readPrinter) {
        boolean result = false;
        final int readLength = readPrinter.length;
        if (readLength < 5) {
            return result;
        }
        final int tableLength = table.length;
        if (tableLength >= readLength) {
            for (int i = 0; i < readLength; ++i) {
                if (table[i] != readPrinter[i]) {
                    return result;
                }
            }
            result = true;
            return result;
        }
        return false;
    }
    
    private boolean IsGprinter(final int id, final byte[] readBuf) {
        boolean result = false;
        final byte[] PRINTER_NAME_TABLE1 = { 95, 71, 80, 55, 54, 32, 83, 101, 114, 105, 101, 115, 0 };
        final byte[] PRINTER_NAME_TABLE2 = { 95, 71, 80, 45, 50, 51, 51, 48, 73, 86, 67, 0 };
        final byte[] PRINTER_NAME_TABLE3 = { 95, 71, 80, 53, 56, 57, 48, 88, 73, 73, 73, 0 };
        final byte[] PRINTER_NAME_TABLE4 = { 95, 71, 80, 45, 76, 56, 48, 49, 54, 48, 0 };
        final byte[] PRINTER_NAME_TABLE5 = { 95, 71, 80, 45, 76, 56, 48, 51, 48, 48, 0 };
        final byte[] PRINTER_NAME_TABLE6 = { 95, 71, 80, 45, 56, 48, 49, 50, 48, 73, 0 };
        final byte[] PRINTER_NAME_TABLE7 = { 95, 71, 80, 45, 53, 56, 49, 51, 48, 0 };
        final byte[] PRINTER_NAME_TABLE8 = { 95, 71, 80, 53, 56, 49, 51, 48, 0 };
        final byte[] PRINTER_NAME_TABLE9 = { 95, 71, 80, 50, 49, 50, 48, 84, 0 };
        final byte[] PRINTER_NAME_TABLE10 = { 95, 71, 80, 45, 85, 52, 50, 48, 0 };
        final byte[] PRINTER_NAME_TABLE11 = { 95, 71, 80, 53, 56, 57, 48, 0 };
        final byte[] PRINTER_NAME_TABLE12 = { 95, 80, 84, 50, 56, 48, 0 };
        final byte[] PRINTER_NAME_TABLE13 = { 95, 80, 114, 111, 53, 0 };
        final byte[] PRINTER_NAME_TABLE14 = { 71, 80, 75, 83, 45 };
        final byte[] PRINTER_NAME_TABLE15 = { 95, 67, 76, 73, 67, 45, 53, 56, 66, 0 };
        final byte[] PRINTER_NAME_TABLE16 = { 95, 67, 76, 73, 67, 45, 53, 56, 67, 0 };
        final byte[] PRINTER_NAME_TABLE17 = { 95, 82, 80, 45, 49, 48, 48, 45, 51, 48, 48, 73, 73 };
        final byte[] PRINTER_NAME_TABLE18 = { 95, 82, 80, 45, 49, 48, 48, 45, 50, 53, 48, 73 };
        final byte[] PRINTER_NAME_TABLE19 = { 95, 82, 80, 45, 49, 48, 48, 75 };
        final byte[] PRINTER_TYPE1 = { 77, 79, 68, 69, 76, 58, 71, 80, 45, 51, 49, 50, 48, 84, 76, 13, 10 };
        final byte[] PRINTER_TYPE2 = { 77, 79, 68, 69, 76, 58, 71, 80, 45, 57, 48, 51, 52, 84, 13, 10 };
        final byte[] PRINTER_TYPE3 = { 77, 79, 68, 69, 76, 58, 71, 80, 45, 57, 48, 50, 53, 84, 13, 10 };
        final byte[] PRINTER_TYPE4 = { 77, 79, 68, 69, 76, 58, 71, 80, 45, 49, 49, 50, 52, 68, 13, 10 };
        final byte[] PRINTER_TYPE5 = { 77, 79, 68, 69, 76, 58, 71, 80, 45, 49, 49, 50, 52, 84, 13, 10 };
        final byte[] PRINTER_TYPE6 = { 77, 79, 68, 69, 76, 58, 71, 80, 45, 49, 49, 51, 52, 84, 13, 10 };
        final byte[] PRINTER_TYPE7 = { 77, 79, 68, 69, 76, 58, 71, 80, 45, 57, 48, 50, 53, 84, 13, 10 };
        final byte[] PRINTER_TYPE8 = { 77, 79, 68, 69, 76, 58, 71, 80, 45, 57, 49, 51, 52, 84, 13, 10 };
        final byte[] PRINTER_TYPE9 = { 77, 79, 68, 69, 76, 58, 71, 80, 45, 50, 49, 50, 48, 13, 10 };
        if (!result) {
            result = this.judgePrinter(PRINTER_NAME_TABLE1, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_NAME_TABLE2, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_NAME_TABLE3, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_NAME_TABLE4, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_NAME_TABLE5, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_NAME_TABLE6, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_NAME_TABLE7, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_NAME_TABLE8, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_NAME_TABLE9, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_NAME_TABLE10, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_NAME_TABLE11, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_NAME_TABLE12, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_NAME_TABLE13, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_NAME_TABLE14, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_NAME_TABLE15, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_NAME_TABLE16, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_NAME_TABLE17, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_NAME_TABLE18, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_NAME_TABLE19, readBuf);
        }
        if (result) {
            GpPrintService.mDevice[id].setCommandType(0);
            return result;
        }
        result = this.judgePrinter(PRINTER_TYPE1, readBuf);
        if (!result) {
            result = this.judgePrinter(PRINTER_TYPE2, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_TYPE3, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_TYPE4, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_TYPE5, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_TYPE6, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_TYPE7, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_TYPE8, readBuf);
        }
        if (!result) {
            result = this.judgePrinter(PRINTER_TYPE9, readBuf);
        }
        if (result) {
            GpPrintService.mDevice[id].setCommandType(1);
            return result;
        }
        return result;
    }
    
    public boolean useList(final byte[] arr, final Object targetValue) {
        return Arrays.asList(new byte[][] { arr }).contains(targetValue);
    }
    
    private void acquireWakeLock() {
        if (null == this.wakeLock) {
            final PowerManager pm = (PowerManager)this.getSystemService("power");
            this.wakeLock = pm.newWakeLock(536870913, this.getClass().getCanonicalName());
            if (null != this.wakeLock) {
                Log.i("-wakeLock-", "wakelock acquireWakeLock");
                this.wakeLock.acquire();
            }
        }
    }
    
    private void releaseWakeLock() {
        if (null != this.wakeLock && this.wakeLock.isHeld()) {
            Log.i("-releaseWakeLock-", "wakelock releaseWakeLock");
            this.wakeLock.release();
            this.wakeLock = null;
        }
    }
    
    public static String getClientNUM(final String IMSI) {
        if (IMSI.length() != 15) {
            return null;
        }
        final String[] map = { "3", "9", "6", "1", "5", "0", "8", "4", "2", "7" };
        int x = 0;
        for (int i = 0; i < IMSI.length(); ++i) {
            x = x + IMSI.charAt(i) - 48;
        }
        final int y = x + (IMSI.charAt(10) - '0') + (IMSI.charAt(12) - '0') + (IMSI.charAt(14) - '0');
        final int z = y % 10;
        final String n = map[z];
        final String clientNum = IMSI + n;
        return clientNum;
    }
    
    public String getIMSI(String macAddress) {
        if (TextUtils.isEmpty((CharSequence)macAddress)) {
            return "";
        }
        macAddress = macAddress.replaceAll(":", "");
        long long_IMSI = Long.parseLong(macAddress, 16);
        long_IMSI += 100000000000000L;
        final String str_IMSI = String.valueOf(long_IMSI);
        return str_IMSI;
    }
    
    private String getWIFIMacAddress() {
        String macAddress = null;
        final WifiManager wifiMgr = (WifiManager)this.getSystemService("wifi");
        final WifiInfo info = (null == wifiMgr) ? null : wifiMgr.getConnectionInfo();
        if (null != info) {
            macAddress = info.getMacAddress();
        }
        LogInfo.out("mac:" + macAddress);
        return macAddress;
    }
    
    private boolean isServiceRunning(final Class<?> serviceClass) {
        final ActivityManager manager = (ActivityManager)this.getSystemService("activity");
        for (final ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    
    static {
        GpPrintService.mDevice = new GpDevice[20];
        GpPrintService.PrinterId = 1;
        GpPrintService.db = null;
        GpPrintService.CLIENTNUM = null;
        object = new Object();
    }
}

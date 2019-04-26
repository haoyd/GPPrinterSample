// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.printer;

import com.gprinter.command.GpCom;
import com.gprinter.protocol.DeviceStatus;
import android.os.RemoteException;
import android.util.Base64;
import com.gprinter.io.utils.GpUtils;
import java.util.Vector;
import com.gprinter.command.LabelCommand;
import com.gprinter.save.SharedPreferencesUtil;
import com.gprinter.command.EscCommand;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import com.gprinter.model.LogType;
import java.util.Iterator;
import java.util.HashMap;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbDevice;
import android.os.IBinder;
import com.gprinter.util.LogInfo;
import android.content.ComponentName;
import com.gprinter.service.GpPrintService;
import android.content.ServiceConnection;
import com.gprinter.service.PrinterStatusBroadcastReceiver;
import android.content.Intent;
import com.gprinter.io.PortParameters;
import com.gprinter.aidl.GpService;
import android.content.Context;

public class PrinterManager
{
    private static final String ACTION_CONNECT_STATUS = "action.connect.status";
    private static PrinterManager printerManager;
    private Context context;
    private GpService mGpService;
    private int PrinterId;
    private PortParameters mPortParam;
    private String usbDeviceName;
    private int PrinterCommandType;
    private Intent intentPrinterService;
    private Intent intentConnectionPrinter;
    private PrinterStatusBroadcastReceiver printerStatusBroadcastReceiver;
    private boolean isStop;
    private ServiceConnection conn;
    
    private PrinterManager(final Context context) {
        this.mGpService = null;
        this.usbDeviceName = null;
        this.PrinterCommandType = -1;
        this.isStop = false;
        this.context = context;
        this.PrinterId = GpPrintService.PrinterId;
        this.printerStatusBroadcastReceiver = new PrinterStatusBroadcastReceiver();
        this.conn = (ServiceConnection)new ServiceConnection() {
            public void onServiceDisconnected(final ComponentName name) {
                LogInfo.out("\u6253\u5370\u673a-\u5df2\u65ad\u5f00");
                PrinterManager.this.mGpService = null;
                PrinterManager.this.stop();
            }
            
            public void onServiceConnected(final ComponentName name, final IBinder service) {
                LogInfo.out("\u6253\u5370\u673a-\u5df2\u8fde\u63a5");
                PrinterManager.this.mGpService = GpService.Stub.asInterface(service);
                PrinterManager.this.getPrinterCommandType();
            }
        };
    }
    
    private boolean checkUsbDevicePidVid(final UsbDevice dev) {
        final int pid = dev.getProductId();
        final int vid = dev.getVendorId();
        boolean rel = false;
        if ((vid == 34918 && pid == 256) || (vid == 1137 && pid == 85) || (vid == 6790 && pid == 30084) || (vid == 26728 && pid == 256) || (vid == 26728 && pid == 512) || (vid == 26728 && pid == 768) || (vid == 26728 && pid == 1024) || (vid == 26728 && pid == 1280) || (vid == 26728 && pid == 1536)) {
            rel = true;
        }
        return rel;
    }
    
    private void getUsbDeviceList() {
        final UsbManager manager = (UsbManager)this.context.getSystemService("usb");
        final HashMap<String, UsbDevice> devices = (HashMap<String, UsbDevice>)manager.getDeviceList();
        final Iterator<UsbDevice> deviceIterator = devices.values().iterator();
        final int count = devices.size();
        LogInfo.out("usb device count " + count);
        if (count > 0) {
            while (deviceIterator.hasNext()) {
                final UsbDevice device = deviceIterator.next();
                final String devicename = device.getDeviceName();
                LogInfo.out("devicename:" + devicename);
                if (this.checkUsbDevicePidVid(device)) {
                    this.usbDeviceName = devicename;
                    LogInfo.out("use devicename:" + devicename);
                    break;
                }
            }
        }
        else {
            LogInfo.out("no usb Devices ");
        }
    }
    
    public static PrinterManager getPrinterManager(final Context context) {
        if (PrinterManager.printerManager == null) {
            PrinterManager.printerManager = new PrinterManager(context);
        }
        return PrinterManager.printerManager;
    }
    
    public void start() {
        this.isStop = false;
        this.startPrinterService();
        this.registerBroadcast();
        this.connectionPrinter();
    }
    
    public void stop() {
        this.isStop = true;
        this.closePort();
        try {
            this.context.unbindService(this.conn);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
            if (e.getCause() != null) {
                LogInfo.err(LogType.APP_ERR, e.getCause().getMessage());
            }
            else {
                LogInfo.err(LogType.APP_ERR, e.getMessage());
            }
        }
    }
    
    private void startPrinterService() {
        if (this.intentPrinterService == null) {
            this.intentPrinterService = new Intent(this.context, (Class)GpPrintService.class);
        }
        else {
            this.context.stopService(this.intentPrinterService);
        }
        this.context.startService(this.intentPrinterService);
        try {
            Thread.sleep(2000L);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            if (e.getCause() != null) {
                LogInfo.err(LogType.APP_ERR, e.getCause().getMessage());
            }
            else {
                LogInfo.err(LogType.APP_ERR, e.getMessage());
            }
        }
    }
    
    private void connectionPrinter() {
        if (this.intentConnectionPrinter == null) {
            this.intentConnectionPrinter = new Intent(this.context, (Class)GpPrintService.class);
        }
        this.context.bindService(this.intentConnectionPrinter, this.conn, 1);
    }
    
    private void registerBroadcast() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction("action.connect.status");
        this.context.registerReceiver((BroadcastReceiver)this.printerStatusBroadcastReceiver, filter);
    }
    
    public String getCommand(final String printMsg, final int controller) {
        final int commandType = this.getPrinterCommandType();
        String r = null;
        if (commandType == 0) {
            final EscCommand esc = new EscCommand();
            esc.addPrintAndLineFeed();
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
            esc.addText(printMsg);
            esc.addPrintAndFeedLines((byte)8);
            if (controller == 1) {
                esc.addCutPaper();
            }
            r = this.getString(esc.getCommand());
        }
        else if (commandType == 1) {
            final String labelWidth = SharedPreferencesUtil.ReadSharedPerference(this.context, "labelWidth");
            final String labelHeight = SharedPreferencesUtil.ReadSharedPerference(this.context, "labelHeight");
            final String labelGap = SharedPreferencesUtil.ReadSharedPerference(this.context, "labelGap");
            final LabelCommand tsc = new LabelCommand();
            tsc.addSize(Integer.valueOf(labelWidth), Integer.valueOf(labelHeight));
            tsc.addGap(Integer.valueOf(labelGap));
            tsc.addDirection(LabelCommand.DIRECTION.BACKWARD, LabelCommand.MIRROR.NORMAL);
            tsc.addReference(0, 0);
            if (controller == 1) {
                tsc.addTear(EscCommand.ENABLE.ON);
            }
            tsc.addCls();
            tsc.addText(20, 20, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, printMsg);
            tsc.addPrint(1, 1);
            r = this.getString(tsc.getCommand());
        }
        LogInfo.out("print command:" + r);
        return r;
    }
    
    private String getString(final Vector<Byte> datas) {
        final Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
        final byte[] bytes = GpUtils.ByteTo_byte(Bytes);
        return Base64.encodeToString(bytes, 0);
    }
    
    public int sendLabelCommand(final String b64) {
        int rl = -1;
        if (this.mGpService != null && this.PrinterCommandType == 1) {
            try {
                rl = this.mGpService.sendLabelCommand(this.PrinterId, b64);
                return rl;
            }
            catch (RemoteException e) {
                e.printStackTrace();
                if (e.getCause() != null) {
                    LogInfo.err(LogType.CONNECT_PRINTER_ERR, e.getCause().getMessage());
                }
                else {
                    LogInfo.err(LogType.CONNECT_PRINTER_ERR, e.getMessage());
                }
                return rl;
            }
        }
        return rl;
    }
    
    public int sendEscCommand(final String b64) {
        int rl = -1;
        if (this.mGpService != null && this.PrinterCommandType == 0) {
            try {
                rl = this.mGpService.sendEscCommand(this.PrinterId, b64);
                return rl;
            }
            catch (RemoteException e) {
                e.printStackTrace();
                if (e.getCause() != null) {
                    LogInfo.err(LogType.CONNECT_PRINTER_ERR, e.getCause().getMessage());
                }
                else {
                    LogInfo.err(LogType.CONNECT_PRINTER_ERR, e.getMessage());
                }
                return rl;
            }
        }
        return rl;
    }
    
    public int printTestPage() {
        if (this.mGpService == null) {
            return -1;
        }
        final int status = this.getPrinterConnectStatus();
        if (status == 3) {
            try {
                return this.mGpService.printeTestPage(this.PrinterId);
            }
            catch (RemoteException e) {
                e.printStackTrace();
                if (e.getCause() != null) {
                    LogInfo.err(LogType.CONNECT_PRINTER_ERR, e.getCause().getMessage());
                }
                else {
                    LogInfo.err(LogType.CONNECT_PRINTER_ERR, e.getMessage());
                }
                return -1;
            }
        }
        if (status == 0) {
            LogInfo.out("\u6253\u5370\u673a\u8fde\u63a5\u65ad\u5f00");
        }
        return -1;
    }
    
    public int getPrinterCommandType() {
        int type = -1;
        if (this.mGpService == null) {
            return type;
        }
        try {
            this.PrinterCommandType = this.mGpService.getPrinterCommandType(this.PrinterId);
            type = this.PrinterCommandType;
            if (type == 0) {
                LogInfo.out("\u6253\u5370\u673a\u4f7f\u7528 ESC \u547d\u4ee4");
            }
            else {
                LogInfo.out("\u6253\u5370\u673a\u4f7f\u7528 TSC \u547d\u4ee4");
            }
            return type;
        }
        catch (RemoteException e) {
            e.printStackTrace();
            if (e.getCause() != null) {
                LogInfo.err(LogType.CONNECT_PRINTER_ERR, e.getCause().getMessage());
            }
            else {
                LogInfo.err(LogType.CONNECT_PRINTER_ERR, e.getMessage());
            }
            return type;
        }
    }
    
    public synchronized void getPrinterStatus() {
        try {
            this.mGpService.queryPrinterStatus(this.PrinterId, 1000, 255);
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    public int getDeviceStatus(final int status) {
        int deviceStatus = DeviceStatus.NO_PRINTER.toInt();
        if (this.mGpService == null) {
            return deviceStatus;
        }
        LogInfo.out("\u72b6\u6001\u503c\uff1a " + status);
        String str = "\u672a\u77e5";
        if (status == 0) {
            str = "\u6b63\u5e38";
            deviceStatus = DeviceStatus.NORMAL.toInt();
        }
        else if ((byte)(status & 0x1) > 0) {
            str = "\u8131\u673a";
            deviceStatus = DeviceStatus.NO_PRINTER.toInt();
        }
        else if ((byte)(status & 0x2) > 0) {
            str = "\u7f3a\u7eb8";
            deviceStatus = DeviceStatus.LACK_PAGER.toInt();
        }
        else if ((byte)(status & 0x4) > 0) {
            str = "\u5f00\u76d6";
            deviceStatus = DeviceStatus.COVER_OPEN.toInt();
        }
        else if ((byte)(status & 0x8) > 0) {
            str = "\u8fc7\u70ed\u6216\u51fa\u9519";
            deviceStatus = DeviceStatus.ERROR.toInt();
        }
        return deviceStatus;
    }
    
    public void openPort() {
        if (this.mGpService == null) {
            return;
        }
        try {
            final int rel = this.mGpService.openPort(this.PrinterId, this.mPortParam.getPortType(), this.mPortParam.getUsbDeviceName(), this.mPortParam.getPortNumber());
            final GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                if (r == GpCom.ERROR_CODE.DEVICE_ALREADY_OPEN) {
                    LogInfo.out("\u7aef\u53e3\u5df2\u7ecf\u6253\u5f00");
                }
                else {
                    LogInfo.out(GpCom.getErrorText(r));
                }
            }
            else {
                LogInfo.out("\u6b63\u5e38\u6253\u5f00");
            }
        }
        catch (RemoteException e) {
            e.printStackTrace();
            if (e.getCause() != null) {
                LogInfo.err(LogType.CONNECT_PRINTER_ERR, e.getCause().getMessage());
            }
            else {
                LogInfo.err(LogType.CONNECT_PRINTER_ERR, e.getMessage());
            }
        }
    }
    
    public int getPrinterConnectStatus() {
        int status = -1;
        if (this.mGpService == null) {
            return status;
        }
        try {
            status = this.mGpService.getPrinterConnectStatus(this.PrinterId);
            if (status != 0) {
                if (status != 1) {
                    if (status != 2) {
                        if (status == 3) {}
                    }
                }
            }
            return status;
        }
        catch (RemoteException e) {
            e.printStackTrace();
            if (e.getCause() != null) {
                LogInfo.err(LogType.CONNECT_PRINTER_ERR, e.getCause().getMessage());
            }
            else {
                LogInfo.err(LogType.CONNECT_PRINTER_ERR, e.getMessage());
            }
            return status;
        }
    }
    
    public void closePort() {
        if (this.mGpService != null) {
            try {
                this.mGpService.closePort(this.PrinterId);
            }
            catch (RemoteException e) {
                e.printStackTrace();
                if (e.getCause() != null) {
                    LogInfo.err(LogType.CONNECT_PRINTER_ERR, e.getCause().getMessage());
                }
                else {
                    LogInfo.err(LogType.CONNECT_PRINTER_ERR, e.getMessage());
                }
            }
        }
    }
    
    public GpService getGpService() {
        return this.mGpService;
    }
    
    public boolean isStop() {
        return this.isStop;
    }
    
    static {
        PrinterManager.printerManager = null;
    }
}

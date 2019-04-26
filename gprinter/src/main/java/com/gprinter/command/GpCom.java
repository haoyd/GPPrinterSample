// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.command;

public class GpCom
{
    public static final int STATE_NO_ERR = 0;
    public static final int STATE_OFFLINE = 1;
    public static final int STATE_PAPER_ERR = 2;
    public static final int STATE_COVER_OPEN = 4;
    public static final int STATE_ERR_OCCURS = 8;
    public static final int STATE_TIMES_OUT = 16;
    public static final String ACTION_CONNECT_STATUS = "action.connect.status";
    public static final String EXTRA_PRINTER_REAL_STATUS = "action.printer.real.status";
    public static final String ACTION_DEVICE_REAL_STATUS = "action.device.real.status";
    public static final String ACTION_RECEIPT_RESPONSE = "action.device.receipt.response";
    public static final String ACTION_LABEL_RESPONSE = "action.device.label.response";
    public static final String EXTRA_PRINTER_ID = "printer.id";
    public static final String EXTRA_PRINTER_REQUEST_CODE = "printer.request_code";
    public static final String EXTRA_PRINTER_LABEL_RESPONSE = "printer.label.response";
    public static final String EXTRA_PRINTER_LABEL_RESPONSE_CNT = "printer.label.response.cnt";
    public static final int ESC_COMMAND = 0;
    public static final int LABEL_COMMAND = 1;
    public static final int NORMAL = -1;
    public static final String KEY_ISCHECKED = "key_ischecked";
    
    public static String getErrorText(final ERROR_CODE errorcode) {
        String s = null;
        switch (errorcode) {
            case SUCCESS: {
                s = "success";
                break;
            }
            case TIMEOUT: {
                s = "timeout";
                break;
            }
            case INVALID_DEVICE_PARAMETERS: {
                s = "Invalid device paramters";
                break;
            }
            case DEVICE_ALREADY_OPEN: {
                s = "Device already open";
                break;
            }
            case INVALID_PORT_NUMBER: {
                s = "Invalid port number";
                break;
            }
            case INVALID_IP_ADDRESS: {
                s = "Invalid ip address";
                break;
            }
            case BLUETOOTH_IS_NOT_SUPPORT: {
                s = "Bluetooth is not support by the device";
                break;
            }
            case OPEN_BLUETOOTH: {
                s = "Please open bluetooth";
                break;
            }
            case PORT_IS_NOT_OPEN: {
                s = "Port is not open";
                break;
            }
            case INVALID_BLUETOOTH_ADDRESS: {
                s = "Invalid bluetooth address";
                break;
            }
            case PORT_IS_DISCONNECT: {
                s = "Port is disconnect";
                break;
            }
            case INVALID_CALLBACK_OBJECT: {
                s = "Invalid callback object";
                break;
            }
            case FAILED: {
                s = "Failed";
                break;
            }
            default: {
                s = "Unknown error code";
                break;
            }
        }
        return s;
    }
    
    public enum ERROR_CODE
    {
        SUCCESS, 
        FAILED, 
        TIMEOUT, 
        INVALID_DEVICE_PARAMETERS, 
        DEVICE_ALREADY_OPEN, 
        INVALID_PORT_NUMBER, 
        INVALID_IP_ADDRESS, 
        INVALID_CALLBACK_OBJECT, 
        BLUETOOTH_IS_NOT_SUPPORT, 
        OPEN_BLUETOOTH, 
        PORT_IS_NOT_OPEN, 
        INVALID_BLUETOOTH_ADDRESS, 
        PORT_IS_DISCONNECT;
    }
}

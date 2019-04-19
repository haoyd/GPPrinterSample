package com.haoyd.printerlib;

public class PrinterConstant {

    private static final String PACKAGE_NAME = "com.haoyd.printerlib.";

    public static final int MAIN_QUERY_PRINTER_STATUS = 0xfe;
    public static final int REQUEST_PRINT_RECEIPT = 0xfc;
    public static final int DEFAULT_PRINTER_ID = 0;

    public static final String DATA_KEY = "DATA_KEY";

    /**
     * 打印机连接
     */
    public static final String INTENT_ACTION_CONN_SUCCESS = PACKAGE_NAME + "ConnectSuccess";
    public static final String INTENT_ACTION_CONN_FAIL = PACKAGE_NAME + "ConnectFail";
    public static final String INTENT_ACTION_PRINTER_SELE_RESULT = PACKAGE_NAME + "PrinterSelectResult";

}

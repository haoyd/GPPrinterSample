package com.haoyd.printerlib;

public class GPPrinterConfig {

    public static boolean checkErrorWhenPrinting = false;               // 在打印过程中是否检测异常
    public static boolean alertLackOfPager = false;                     // 是否提示缺纸
    public static boolean showPrintStateDialog = false;                 // 是否显示打印状态弹窗
    public static boolean autoConnectHistoryPrinter = false;            // 进入页面时是否自动尝试连接历史记录打印机
    public static String printerListDialogThemeColor = "";              // 打印连接弹窗主题颜色

}

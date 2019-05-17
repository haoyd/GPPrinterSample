package com.haoyd.printerlib.manager;

public class HistoryConnRecManager {

    private static boolean historyConnct = false;

    public static boolean isHistoryConnct() {
        return historyConnct;
    }

    public static void setHistoryConnct(boolean historyConnct) {
        HistoryConnRecManager.historyConnct = historyConnct;
    }
}

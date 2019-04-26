package com.haoyd.printerlib.manager;

import android.app.Activity;

import com.haoyd.printerlib.databuilder.PrintCommand;


public class PrinterManager extends BasePrinterManager {

    private String curConnName = "";

    public PrinterManager(Activity mActivity) {
        super(mActivity);
    }

    public void printTestTicket() {
        String result = new PrintCommand()
                .addInitLine()
                .alignCenter()
                .addText("Hello World")
                .addMutiFeedLines((short) 3)
                .build();

        printTicket(result);
    }

    public void setCurConnName(String curConnName) {
        this.curConnName = curConnName;
    }
}

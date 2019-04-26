package com.haoyd.printerlib.manager;

import android.app.Activity;
import android.util.Base64;

import com.gprinter.command.EscCommand;
import com.gprinter.command.GpUtils;
import com.gprinter.command.EscCommand.JUSTIFICATION;

import java.util.Vector;


public class PrinterManager extends BasePrinterManager {

    private String curConnName = "";

    public PrinterManager(Activity mActivity) {
        super(mActivity);
    }

    public void printTestTicket() {
        EscCommand esc = new EscCommand();
        esc.addInitializePrinter();
        esc.addSelectJustification(JUSTIFICATION.CENTER);// 设置打印居中
        esc.addText("Hello World"); // 打印文字
        esc.addPrintAndFeedLines((byte) 3);

        Vector<Byte> datas = esc.getCommand(); // 发送数据
        byte[] bytes = GpUtils.ByteTo_byte(datas);
        String result = Base64.encodeToString(bytes, Base64.DEFAULT);
        printTicket(result);
    }

    public void setCurConnName(String curConnName) {
        this.curConnName = curConnName;
    }
}

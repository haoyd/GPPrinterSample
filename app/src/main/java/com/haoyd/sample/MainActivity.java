package com.haoyd.sample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.gprinter.command.EscCommand;
import com.gprinter.command.EscCommand.JUSTIFICATION;
import com.gprinter.command.GpUtils;
import com.haoyd.printerlib.views.BluetoothDeviceListActivity;
import com.haoyd.printerlib.views.PrinterServiceActivity;

import java.util.Vector;

public class MainActivity extends PrinterServiceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void selectPrinter(View view) {
        startActivity(new Intent(this, BluetoothDeviceListActivity.class));
    }

    public void printTicket(View view) {
        EscCommand esc = new EscCommand();
        esc.addInitializePrinter();
        esc.addPrintAndFeedLines((byte) 3);
        esc.addSelectJustification(JUSTIFICATION.CENTER);// 设置打印居中
        esc.addText("Hello World"); // 打印文字
        esc.addPrintAndLineFeed();
        esc.addPrintAndFeedLines((byte) 3);

        Vector<Byte> datas = esc.getCommand(); // 发送数据
        byte[] bytes = GpUtils.ByteTo_byte(datas);
        String result = Base64.encodeToString(bytes, Base64.DEFAULT);
        printerManager.printTicket(result);
    }

}

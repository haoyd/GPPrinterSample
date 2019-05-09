package com.haoyd.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.haoyd.printerlib.dao.GPPrinterDao;
import com.haoyd.printerlib.views.GPBluetoothDeviceListActivity;
import com.haoyd.printerlib.views.GPPrinterConnActivity;
import com.haoyd.printerlib.views.GPPrinterServiceActivity;

public class MainActivity extends GPPrinterServiceActivity {

    private Switch mSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwitch = findViewById(R.id.switch_main);

        printerManager.setListenPrinterStatus(true);
    }

    @Override
    public void onConnSuccess() {
        super.onConnSuccess();
        mSwitch.setChecked(true);
    }

    @Override
    public void onDisconnect() {
        super.onDisconnect();
        mSwitch.setChecked(false);
    }

    @Override
    public void onPrintError(String error) {
        super.onPrintError(error);
        toast(error);
    }

    public void selectPrinterOnNewPage(View view) {
        startActivity(new Intent(this, GPPrinterConnActivity.class));
    }

    public void selectPrinterOnDialog(View view) {
        startActivity(new Intent(this, GPBluetoothDeviceListActivity.class));
    }

    public void selectPrinterByHistory(View view) {
        if (GPPrinterDao.getInstance(this).hasHistoryPrinter()) {
            printerManager.connToHistoryDevice();
        } else {
            Toast.makeText(this, "暂无历史记录", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearHistory(View view) {
        GPPrinterDao.getInstance(this).clearHistory();
    }

    public void disconnectPrinter(View view) {
        printerManager.disConnectToPrinter();
    }

    public void printTicket(View view) {
        printerManager.printTestTicket();
    }

    public void queryPrinterStatus(View view) {
        printerManager.queryPrinterStatus();
    }

    public void lineSpacePrint(View view) {
        printerManager.printTestTicketByLineSpace(80);

    }
}

package com.haoyd.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.haoyd.printerlib.views.GPPrinterConnActivity;
import com.haoyd.printerlib.views.GPPrinterServiceActivity;

public class MainActivity extends GPPrinterServiceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void selectPrinter(View view) {
        startActivity(new Intent(this, GPPrinterConnActivity.class));
//        startActivity(new Intent(this, BluetoothDeviceListActivity.class));
    }

    public void printTicket(View view) {
        printerManager.printTestTicket();
    }

}

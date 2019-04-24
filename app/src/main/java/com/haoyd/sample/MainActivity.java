package com.haoyd.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.haoyd.printerlib.views.BluetoothDeviceListActivity;
import com.haoyd.printerlib.views.PrinterServiceActivity;

public class MainActivity extends PrinterServiceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void selectPrinter(View view) {
//        startActivity(new Intent(this, PrinterConnActivity.class));
        startActivity(new Intent(this, BluetoothDeviceListActivity.class));
    }

    public void printTicket(View view) {
        printerManager.printTestTicket();
    }

}

package com.haoyd.printerlib.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.haoyd.printerlib.PrinterConstant;
import com.haoyd.printerlib.entities.BluetoothDeviceInfo;
import com.haoyd.printerlib.entities.CastInfo;
import com.haoyd.printerlib.liseners.OnBroadcastResultListener;
import com.haoyd.printerlib.liseners.OnPrintResultListener;
import com.haoyd.printerlib.manager.PrinterManager;
import com.haoyd.printerlib.receivers.GPBroadcastManager;

public class PrinterServiceActivity extends AppCompatActivity implements OnBroadcastResultListener, OnPrintResultListener {

    private GPBroadcastManager castManager;
    protected PrinterManager printerManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        castManager = new GPBroadcastManager();
        printerManager = new PrinterManager(this);

        processServiceBindLogic(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        processServiceBindLogic(false);
    }

    @Override
    public void onBroadcastResult(CastInfo result) {
        switch (result.action) {
            case PrinterConstant.INTENT_ACTION_PRINTER_SELE_RESULT:
                printerManager.connectToPrinter((BluetoothDeviceInfo) result.obj);
                break;
        }
    }

    /**
     * 绑定或解绑打印服务
     * @param isRegist  true：绑定   false：解绑
     */
    private void processServiceBindLogic(boolean isRegist) {
        if (isRegist) {
            // 绑定服务

            printerManager.bindService();
            printerManager.setOnPrinterConnResultListener(this);

            castManager.registReceiver(this, PrinterConstant.INTENT_ACTION_PRINTER_SELE_RESULT);
            castManager.setResultListener(this);
        } else {
            // 解绑服务
            printerManager.disConnectToPrinter();
            printerManager.unbindService();
            castManager.unregistReceiver(this);
        }
    }


    @Override
    public void onPrintSucc() {

    }

    @Override
    public void onPrintError(String error) {

    }
}

package com.haoyd.printerlib.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.haoyd.printerlib.liseners.OnPrintResultListener;
import com.haoyd.printerlib.manager.PrinterManager;
import com.haoyd.printerlib.receivers.PrinterConnReceiverManager;

/**
 * 该类主要有以下几个作用：
 * 1、初始化打印管理类
 * 2、监听连接成功与否
 * 3、监听打印成功与否
 */
public class GPPrinterServiceActivity extends AppCompatActivity implements OnPrintResultListener, PrinterConnReceiverManager.OnConnResultListener {

    protected PrinterManager printerManager;
    private PrinterConnReceiverManager connReceiverManager;

    /**
     * Printer Config
     */
    protected boolean disconnectWhenFinish = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        printerManager = new PrinterManager(this);
        connReceiverManager = new PrinterConnReceiverManager(this);

        processServiceBindLogic(true);

        connReceiverManager.setResultListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        processServiceBindLogic(false);
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
        } else {
            // 解绑服务
            if (disconnectWhenFinish) {
                printerManager.disConnectToPrinter();
            }
            printerManager.unbindService();
            connReceiverManager.unregist();
            printerManager = null;
        }
    }


    /**
     * 打印成功
     */
    @Override
    public void onPrintSucc() {

    }

    /**
     * 打印失败
     * @param error 失败原因
     */
    @Override
    public void onPrintError(String error) {

    }

    /**
     * 连接成功
     */
    @Override
    public void onConnSuccess() {

    }

    /**
     * 连接失败
     * @param error 失败原因
     */
    @Override
    public void onConnFail(String error) {
        toast(error);
    }

    /**
     * 断开连接
     */
    @Override
    public void onDisconnect() {

    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}

package com.haoyd.printerlib.receivers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.gprinter.command.GpCom;
import com.gprinter.io.GpDevice;
import com.gprinter.service.GpPrintService;
import com.kaopiz.kprogresshud.KProgressHUD;

public class PrinterConnReceiverManager extends BaseReceiverManager {

    private OnConnResultListener resultListener;
    private KProgressHUD hud;

    public PrinterConnReceiverManager(Activity activity) {
        super(activity);

        hud = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("连接中")
                .setCancellable(false);

        regist(GpCom.ACTION_CONNECT_STATUS);
    }

    @Override
    protected void onBroadcastReceive(Context context, Intent intent) {
        super.onBroadcastReceive(context, intent);
        int type = intent.getIntExtra(GpPrintService.CONNECT_STATUS, 0);

        if (type == GpDevice.STATE_CONNECTING) {
            // 连接中
            hud.show();
        } else if (type == GpDevice.STATE_NONE) {
            // 连接失败
            hud.dismiss();
            if (resultListener != null) {
                resultListener.onConnFail("连接失败，请重试");
            }
        } else if (type == GpDevice.STATE_VALID_PRINTER) {
            // 连接成功
            hud.dismiss();
            if (resultListener != null) {
                resultListener.onConnSuccess();
            }
        } else if (type == GpDevice.STATE_INVALID_PRINTER) {
            // 不可用的打印机
            hud.dismiss();
            if (resultListener != null) {
                resultListener.onConnFail("非法打印机，请使用GPrinter");
            }
        }
    }

    public void setResultListener(OnConnResultListener resultListener) {
        this.resultListener = resultListener;
    }

    public interface OnConnResultListener {
        void onConnSuccess();

        void onConnFail(String error);
    }
}

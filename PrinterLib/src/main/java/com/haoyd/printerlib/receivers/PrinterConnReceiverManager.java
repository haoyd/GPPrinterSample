package com.haoyd.printerlib.receivers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.gprinter.command.GpCom;
import com.gprinter.io.GpDevice;
import com.gprinter.service.GpPrintService;
import com.haoyd.printerlib.manager.HistoryConnRecManager;
import com.kaopiz.kprogresshud.KProgressHUD;

public class PrinterConnReceiverManager extends BaseReceiverManager {

    private OnConnResultListener resultListener;
    private KProgressHUD hud;

    // 用来记录当前连接状态
    private int curState = GpDevice.STATE_NONE;

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
            showHud();
        } else if (type == GpDevice.STATE_NONE) {
            // 连接失败 或 断开连接
            hideHud();
            if (resultListener != null) {
                if (curState == GpDevice.STATE_CONNECTING) {
                    // 如果从正在连接状态转为断开，则为连接失败
                    resultListener.onConnFail("连接失败，请重试");
                } else if (curState == GpDevice.STATE_VALID_PRINTER) {
                    // 如果从成功状态转为断开，则为用户主动断开连接
                    resultListener.onDisconnect();
                }
            }
        } else if (type == GpDevice.STATE_VALID_PRINTER) {
            // 连接成功
            hideHud();
            if (resultListener != null) {
                resultListener.onConnSuccess();
            }
        } else if (type == GpDevice.STATE_INVALID_PRINTER) {
            // 不可用的打印机
            hideHud();
            if (resultListener != null) {
                resultListener.onConnFail("非法打印机，请使用GPrinter");
            }
        }

        curState = type;
    }

    public void setResultListener(OnConnResultListener resultListener) {
        this.resultListener = resultListener;
    }

    private void showHud() {
        if (hud == null || HistoryConnRecManager.isHistoryConnct()) {
            return;
        }
        hud.show();
    }

    private void hideHud() {
        if (hud == null || !hud.isShowing()) {
            return;
        }
        hud.dismiss();
    }

    public interface OnConnResultListener {
        void onConnSuccess();

        void onConnFail(String error);

        void onDisconnect();
    }
}

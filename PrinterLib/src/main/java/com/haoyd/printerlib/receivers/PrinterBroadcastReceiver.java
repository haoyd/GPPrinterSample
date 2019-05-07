package com.haoyd.printerlib.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.gprinter.command.GpCom;
import com.haoyd.printerlib.liseners.OnPrintResultListener;

import static com.haoyd.printerlib.GPPrinterConstant.MAIN_QUERY_PRINTER_STATUS;
import static com.haoyd.printerlib.GPPrinterConstant.REQUEST_PRINT_RECEIPT;

public class PrinterBroadcastReceiver extends BroadcastReceiver {


    private Context mContext;
    private OnPrintResultListener onPrintResultListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        processActionLogic(intent);
    }

    /**
     * 打印结果监听
     *
     * @param onPrintResultListener
     */
    public void setOnPrintResultListener(OnPrintResultListener onPrintResultListener) {
        this.onPrintResultListener = onPrintResultListener;
    }

    /**
     * 处理动作
     *
     * @param intent
     */
    private void processActionLogic(Intent intent) {
        String action = intent.getAction();

        switch (action) {
            case GpCom.ACTION_DEVICE_REAL_STATUS:
                processStatusLogic(intent);
                break;
            case GpCom.ACTION_RECEIPT_RESPONSE:
                printSuccess();
                break;
        }
    }

    /**
     * 处理状态码
     *
     * @param intent
     */
    private void processStatusLogic(Intent intent) {
        // 业务逻辑的请求码，对应查询做什么操作
        int requestCode = intent.getIntExtra(GpCom.EXTRA_PRINTER_REQUEST_CODE, -1);
        // 判断请求码，是则进行业务操作
        if (requestCode == MAIN_QUERY_PRINTER_STATUS) {

            int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
            String str = "打印机";

            if (status != GpCom.STATE_NO_ERR) {
                if ((byte) (status & GpCom.STATE_OFFLINE) > 0) str += "脱机";
                if ((byte) (status & GpCom.STATE_PAPER_ERR) > 0) str += "缺纸";
                if ((byte) (status & GpCom.STATE_COVER_OPEN) > 0) str += "开盖";
                if ((byte) (status & GpCom.STATE_ERR_OCCURS) > 0) str += "出错";
                if ((byte) (status & GpCom.STATE_TIMES_OUT) > 0) str += "查询超时";
            }

            if (!str.equals("打印机")) {
                printError(str);
            }
        } else if (requestCode == REQUEST_PRINT_RECEIPT) {
            int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
            if (status == GpCom.STATE_NO_ERR) {
                Toast.makeText(mContext, "无异常", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "查询失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 打印成功
     */
    private void printSuccess() {
        if (onPrintResultListener != null) {
            onPrintResultListener.onPrintSucc();
        }
    }

    /**
     * 打印异常
     *
     * @param error
     */
    private void printError(String error) {
        if (onPrintResultListener != null) {
            onPrintResultListener.onPrintError(error);
        }
    }
}

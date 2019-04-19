package com.haoyd.printerlib.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.haoyd.printerlib.entities.CastInfo;
import com.haoyd.printerlib.liseners.OnBroadcastResultListener;

import java.util.List;

import static com.haoyd.printerlib.PrinterConstant.DATA_KEY;

public class GPBroadcastManager {

    private OnBroadcastResultListener resultListener;

    private MyBroadcast myBroadcast;

    public GPBroadcastManager() {
        myBroadcast = new MyBroadcast();
    }

    public void setResultListener(OnBroadcastResultListener resultListener) {
        this.resultListener = resultListener;
    }

    private class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (resultListener == null) {
                return;
            }

            CastInfo data = new CastInfo(intent.getAction());
            data.obj = intent.getSerializableExtra(DATA_KEY);
            resultListener.onBroadcastResult(data);
        }
    }

    /**
     * 注册单个广播
     * @param activity
     * @param filter
     */
    public void registReceiver(Activity activity, String filter) {
        IntentFilter intentFilter = new IntentFilter(filter);
        activity.registerReceiver(myBroadcast, intentFilter);
    }

    /**
     * 注销广播
     * @param activity
     */
    public void unregistReceiver(Activity activity) {
        activity.unregisterReceiver(myBroadcast);
    }

    /**
     * 注册多个广播
     * @param activity
     * @param filters
     */
    public void registReceivers(Activity activity, List<String> filters) {
        if (filters == null) {
            return;
        }

        for (String filter : filters) {
            activity.registerReceiver(myBroadcast, new IntentFilter(filter));
        }
    }

}

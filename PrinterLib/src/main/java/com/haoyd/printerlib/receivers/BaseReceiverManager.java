package com.haoyd.printerlib.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseReceiverManager {

    protected Activity activity;
    private BroadcastReceiver receiver;
    private List<String> mActions;

    public BaseReceiverManager(Activity activity) {
        this.activity = activity;
        this.receiver = new MyReceiver();
        mActions = new ArrayList<>();
    }

    /**
     * 注册单个事件
     * @param action
     */
    protected void regist(String action) {
        if (TextUtils.isEmpty(action)) {
            return;
        }

        activity.registerReceiver(receiver, new IntentFilter(action));
    }

    /**
     * 注册多个事件
     * @param actions
     */
    protected void regist(List<String> actions) {
        if (actions == null || actions.size() == 0) {
            return;
        }

        mActions.addAll(actions);

        for (String action : actions) {
            activity.registerReceiver(receiver, new IntentFilter(action));
        }
    }

    /**
     * 解注册
     */
    public void unregist() {
        if (activity != null && receiver != null) {
            activity.unregisterReceiver(receiver);
        }

        activity = null;
        receiver = null;
        mActions = null;
    }

    protected void onBroadcastReceive(Context context, Intent intent) {
        if (!isContainsAction(intent.getAction())) {
            return;
        }
    }

    /**
     * 是否为注册过的action
     * @param action
     * @return
     */
    private boolean isContainsAction(String action) {
        boolean result = false;

        for (String act : mActions) {
            if (action.equals(act)) {
                result = true;
                break;
            }
        }

        return result;
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBroadcastReceive(context, intent);
        }
    }
}

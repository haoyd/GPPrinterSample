package com.haoyd.printerlib.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

public class ActivityUtil {

    /**
     * 判断Activity是否在栈顶
     * @param activity
     * @return
     */
    public static boolean isActivityTop(Activity activity){
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(activity.getClass().getName());
    }

}

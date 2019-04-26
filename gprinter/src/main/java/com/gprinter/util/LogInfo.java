// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.util;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import java.util.Date;
import com.gprinter.model.LogModel;
import android.util.Log;
import com.gprinter.model.LogType;
import android.content.Context;

public class LogInfo
{
    private static final String tag = "smartprinter";
    private static Context mContext;
    
    public static void setContext(final Context context) {
        LogInfo.mContext = context;
        out("set LogInfo mContext!");
    }
    
    public static void err(final LogType logType, final String mes) {
        if ("smartprinter" != null && mes != null) {
            Log.e("smartprinter", mes);
            if (LogInfo.mContext != null) {
                DbUtils db = DBUtil.getDB(LogInfo.mContext);
                if (db != null) {
                    LogModel logModel = new LogModel();
                    logModel.setLogTime(new Date());
                    logModel.setLogType(logType.toInt());
                    logModel.setLogMsg(mes);
                    try {
                        db.save((Object)logModel);
                    }
                    catch (DbException e) {
                        e.printStackTrace();
                        if (e.getCause() != null) {
                            Log.e("smartprinter", e.getCause().getMessage());
                        }
                        else {
                            Log.e("smartprinter", e.getMessage());
                        }
                    }
                    logModel = null;
                }
                db = null;
            }
            else {
                out("LogInfo mContext is null!");
            }
        }
    }
    
    public static void out(final String mes) {
        if ("smartprinter" == null || mes != null) {}
    }
    
    public static void debug(final String mes) {
        if ("smartprinter" == null || mes != null) {}
    }
    
    static {
        LogInfo.mContext = null;
    }
}

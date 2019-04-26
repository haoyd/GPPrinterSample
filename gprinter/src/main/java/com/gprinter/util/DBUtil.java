// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.util;

import com.lidroid.xutils.exception.DbException;
import com.gprinter.model.DataInfoLog;
import com.gprinter.model.DataInfoModel;
import com.gprinter.model.DeviceInfoModel;
import com.gprinter.model.PrintModel;
import com.gprinter.model.LogModel;
import com.gprinter.service.GpPrintService;
import com.lidroid.xutils.DbUtils;
import android.content.Context;

public class DBUtil
{
    public static DbUtils getDB(final Context context) {
        if (GpPrintService.db == null) {
            final DbUtils.DaoConfig config = new DbUtils.DaoConfig(context);
            config.setDbName("smartprint.db");
            config.setDbVersion(1);
            config.setDbDir(GpPrintService.DB_DIR);
            GpPrintService.db = DbUtils.create(config);
            try {
                if (GpPrintService.db != null) {
                    GpPrintService.db.createTableIfNotExist((Class)LogModel.class);
                    GpPrintService.db.createTableIfNotExist((Class)PrintModel.class);
                    GpPrintService.db.createTableIfNotExist((Class)DeviceInfoModel.class);
                    GpPrintService.db.createTableIfNotExist((Class)DataInfoModel.class);
                    GpPrintService.db.createTableIfNotExist((Class)DataInfoLog.class);
                }
            }
            catch (DbException e) {
                e.printStackTrace();
            }
        }
        return GpPrintService.db;
    }
}

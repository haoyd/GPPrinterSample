// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.service;

import com.gprinter.util.LogInfo;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;

public class PrinterStatusBroadcastReceiver extends BroadcastReceiver
{
    public void onReceive(final Context context, final Intent intent) {
        if ("action.connect.status".equals(intent.getAction())) {
            final int type = intent.getIntExtra("connect.status", 0);
            final int id = intent.getIntExtra("printer.id", 0);
            LogInfo.out("PRINTER_ID:" + id);
            LogInfo.out("CONNECT_STATUS:" + type);
            if (type == 0) {
                LogInfo.out("\u6253\u5370\u673a-\u8fde\u63a5\u65ad\u5f00");
            }
            else if (type == 1) {
                LogInfo.out("\u6253\u5370\u673a-\u76d1\u542c\u72b6\u6001");
            }
            else if (type == 2) {
                LogInfo.out("\u6253\u5370\u673a-\u6b63\u5728\u8fde\u63a5");
            }
            else if (type == 3) {
                LogInfo.out("\u6253\u5370\u673a-\u5df2\u8fde\u63a5");
            }
            else if (type == 4) {
                LogInfo.out("\u6253\u5370\u673a-\u65e0\u6548\u7684\u6253\u5370\u673a");
            }
            else if (type == 5) {
                LogInfo.out("\u6253\u5370\u673a-\u6709\u6548\u7684\u6253\u5370\u673a");
            }
        }
    }
}

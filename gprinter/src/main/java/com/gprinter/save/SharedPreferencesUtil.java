// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.save;

import android.content.SharedPreferences;
import android.content.Context;

public class SharedPreferencesUtil
{
    private static final String XMLNAME = "SmartPrinter";
    private static final String XMLNAME2 = "Smartprinter-DeviceInfoPres";
    public static final String INIT_KEY = "init";
    public static final String LABEL_WIDTH_KEY = "labelWidth";
    public static final String LABEL_HEIGHT_KEY = "labelHeight";
    public static final String LABEL_GAP_KEY = "labelGap";
    public static final String KEYS_KEY = "keys";
    
    public static String ReadSharedPerference(final Context context, final String key) {
        final SharedPreferences preferences = context.getSharedPreferences("SmartPrinter", 0);
        final String value = preferences.getString(key, (String)null);
        return value;
    }
    
    public static void SharedPerferencesCreat(final Context context, final String key, final String value) {
        final SharedPreferences preferences = context.getSharedPreferences("SmartPrinter", 0);
        final SharedPreferences.Editor edit = preferences.edit();
        edit.putString(key, value);
        edit.commit();
    }
    
    public static Boolean ReadDeviceInfoSharedPerference(final Context context, final String key) {
        final SharedPreferences preferences = context.getSharedPreferences("Smartprinter-DeviceInfoPres", 0);
        final Boolean value = preferences.getBoolean(key, false);
        return value;
    }
    
    public static void DeviceInfoSharedPerferencesCreat(final Context context, final String key, final Boolean value) {
        final SharedPreferences preferences = context.getSharedPreferences("Smartprinter-DeviceInfoPres", 0);
        final SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(key, (boolean)value);
        edit.commit();
    }
}

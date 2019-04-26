// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.save;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.content.ContentValues;
import com.gprinter.io.PortParameters;
import android.content.Context;

public class PortParamDataBase
{
    Context context;
    DatabaseHelper dbHelper;
    private static final String DEBUG_TAG = "LabelDataBase";
    private static final String PORT_PARAM_DATABASE = "GpLink_port_db1";
    private static final String TABLE_PORT_PARAM = "portparam";
    private static final String[] PORT_PARAM_QUERY;
    
    public PortParamDataBase(final Context context) {
        this.dbHelper = null;
        this.context = context;
        this.dbHelper = new DatabaseHelper(this.context, "GpLink_port_db1");
    }
    
    public void updataDatabase(final int version) {
        this.dbHelper = new DatabaseHelper(this.context, "GpLink_port_db1", version);
    }
    
    public void insertPortParam(final int id, final PortParameters param) {
        final ContentValues values = new ContentValues();
        Log.i("LabelDataBase", "insertPortParam");
        values.put("id", id);
        values.put("open", param.getPortOpenState());
        values.put("porttype", param.getPortType());
        values.put("btaddr", param.getBluetoothAddr());
        values.put("usbname", param.getUsbDeviceName());
        values.put("ip", param.getIpAddr());
        values.put("port", param.getPortNumber());
        final SQLiteDatabase sqliteDatabase = this.dbHelper.getWritableDatabase();
        sqliteDatabase.insert("portparam", (String)null, values);
    }
    
    public void modifyPortParam(final String id, final PortParameters param) {
        Log.i("LabelDataBase", "modifyPortParam");
        final ContentValues values = new ContentValues();
        values.put("open", param.getPortOpenState());
        final SQLiteDatabase sqliteDatabase = this.dbHelper.getWritableDatabase();
        sqliteDatabase.update("portparam", values, "id=?", new String[] { id });
    }
    
    public PortParameters queryPortParamDataBase(final String id) {
        final PortParameters p = new PortParameters();
        final SQLiteDatabase sqliteDatabase = this.dbHelper.getReadableDatabase();
        Log.i("LabelDataBase", "queryPortParam");
        final Cursor cursor = sqliteDatabase.query("portparam", PortParamDataBase.PORT_PARAM_QUERY, "id=?", new String[] { id }, (String)null, (String)null, (String)null);
        while (cursor.moveToNext()) {
            p.setPortType(cursor.getInt(cursor.getColumnIndex("porttype")));
            final int i = cursor.getInt(cursor.getColumnIndex("open"));
            if (i == 0) {
                p.setPortOpenState(false);
            }
            else {
                p.setPortOpenState(true);
            }
            p.setBluetoothAddr(cursor.getString(cursor.getColumnIndex("btaddr")));
            p.setUsbDeviceName(cursor.getString(cursor.getColumnIndex("usbname")));
            p.setIpAddr(cursor.getString(cursor.getColumnIndex("ip")));
            p.setPortNumber(cursor.getInt(cursor.getColumnIndex("port")));
            Log.i("LabelDataBase", "id " + cursor.getInt(cursor.getColumnIndex("id")));
            Log.i("LabelDataBase", "PortOpen " + p.getPortOpenState());
            Log.i("LabelDataBase", "PortType " + p.getPortType());
            Log.i("LabelDataBase", "BluetoothAddr " + p.getBluetoothAddr());
            Log.i("LabelDataBase", "UsbName " + p.getUsbDeviceName());
            Log.i("LabelDataBase", "Ip " + p.getIpAddr());
            Log.i("LabelDataBase", "Port " + p.getPortNumber());
        }
        cursor.close();
        return p;
    }
    
    public void deleteDataBase(final String id) {
        final SQLiteDatabase sqliteDatabase = this.dbHelper.getWritableDatabase();
        final int rel = sqliteDatabase.delete("portparam", "id=?", new String[] { id });
        Log.i("LabelDataBase", "rel  " + rel);
    }
    
    public void insertPrinterName(final int id, final String name) {
        final SQLiteDatabase sqliteDatabase = this.dbHelper.getWritableDatabase();
        final ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("name", name);
        final long ret = sqliteDatabase.update("printername", contentValues, "id=?", new String[] { id + "" });
        Log.d("----------------------", ret + "");
    }
    
    public void deletePrinterName(final String id) {
        final SQLiteDatabase sqliteDatabase = this.dbHelper.getWritableDatabase();
        final int rel = sqliteDatabase.delete("printername", "id=?", new String[] { id });
        Log.i("LabelDataBase", "rel delete printer name " + rel);
    }
    
    public String readPrinterName(final int id) {
        final SQLiteDatabase sqliteDatabase = this.dbHelper.getReadableDatabase();
        final Cursor cursor = sqliteDatabase.query("printername", new String[] { "id", "name" }, "id=?", new String[] { id + "" }, (String)null, (String)null, (String)null);
        String name = "";
        if (cursor.moveToNext()) {
            name = cursor.getString(1);
        }
        cursor.close();
        return name;
    }
    
    public void close() {
        if (this.dbHelper != null) {
            this.dbHelper.close();
        }
    }
    
    static {
        PORT_PARAM_QUERY = new String[] { "id", "open", "porttype", "btaddr", "usbname", "ip", "port" };
    }
}

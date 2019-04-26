// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.save;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final int VERSION = 4;
    private static final String TABLE_PORT_PARAM = "CREATE TABLE IF NOT EXISTS portparam(id INTEGER,open INTEGER,porttype INTEGER, btaddr VARCHAR, usbname VARCHAR, ip VARCHAR, port INTEGER)";
    private static final String TABLE_PRINTER_NAME = "create table if not exists printername(id INTEGER, name VARCHAR(20) )";
    
    public DatabaseHelper(final Context context, final String name, final SQLiteDatabase.CursorFactory factory, final int version) {
        super(context, name, factory, version);
    }
    
    public DatabaseHelper(final Context context, final String name, final int version) {
        super(context, name, (SQLiteDatabase.CursorFactory)null, version);
    }
    
    public DatabaseHelper(final Context context, final String name) {
        super(context, name, (SQLiteDatabase.CursorFactory)null, 4);
    }
    
    public void onCreate(final SQLiteDatabase db) {
        System.out.println("create a database");
        db.execSQL("CREATE TABLE IF NOT EXISTS portparam(id INTEGER,open INTEGER,porttype INTEGER, btaddr VARCHAR, usbname VARCHAR, ip VARCHAR, port INTEGER)");
        db.execSQL("create table if not exists printername(id INTEGER, name VARCHAR(20) )");
        for (int i = 0; i < 20; ++i) {
            final ContentValues contentValues = new ContentValues();
            contentValues.put("id", i);
            contentValues.put("name", "");
            db.insert("printername", (String)null, contentValues);
        }
    }
    
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        System.out.println("upgrade a database");
        switch (oldVersion) {
            case 1: {
                db.execSQL("CREATE TABLE IF NOT EXISTS portparam(id INTEGER,open INTEGER,porttype INTEGER, btaddr VARCHAR, usbname VARCHAR, ip VARCHAR, port INTEGER)");
            }
            case 3: {
                db.execSQL("create table if not exists printername(id INTEGER, name VARCHAR(20) )");
                for (int i = 0; i < 20; ++i) {
                    final ContentValues contentValues = new ContentValues();
                    contentValues.put("id", i);
                    contentValues.put("name", "");
                    db.insert("printername", (String)null, contentValues);
                }
                break;
            }
        }
    }
}

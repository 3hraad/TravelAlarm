package it.garybrady.traveldata;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DBhelper extends SQLiteOpenHelper{
    //string to create the database table
    private static final String CREATE_TABLE="create table "+
            constants.TABLE_NAME+" ("+
            constants.KEY_ID+" integer primary key autoincrement, "+
            constants.ADDRESS+" text not null); ";

    private static final String CREATE_GEO_TABLE="create table "+
            constants.GEO_TABLE+" ("+
            constants.KEY_ID+" integer primary key autoincrement, "+
            constants.G_TITLE+" text not null,"+
            constants.G_LAT+" double(20,15) not null,"+
            constants.G_LNG+" double(20,15) not null,"+
            constants.G_ACTIVE+" integer DEFAULT 1"+


            "); ";

/*
    public static final String KEY_ID="_id";
    public static final String GEO_TABLE="geofence";
    public static final String G_TITLE="title";
    public static final String G_LAT="latitude";
    public static final String G_LNG="longitude";
    public static final String G_ACTIVE="active";*/







    public DBhelper(Context context, String name, CursorFactory factory,
                      int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("MyDBhelper onCreate","Creating all the tables");
        try {
            //try to create the table
            db.execSQL(CREATE_TABLE);
            db.execSQL(CREATE_GEO_TABLE);
        } catch(SQLiteException ex) {
            Log.e("Create table exception", ex.getMessage());
        }
    }

    //if the version is different the table will be destroyed and onCreate will be called
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        Log.w("TaskDBAdapter", "Upgrading from version "+oldVersion
                +" to "+newVersion
                +", which will destroy all old data");
        db.execSQL("drop table if exists "+constants.TABLE_NAME);
        db.execSQL("drop table if exists "+constants.GEO_TABLE);
        onCreate(db);
    }
}
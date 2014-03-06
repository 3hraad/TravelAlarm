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
    public DBhelper(Context context, String name, CursorFactory factory,
                      int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v("MyDBhelper onCreate","Creating all the tables");
        try {
            //try to create the table
            db.execSQL(CREATE_TABLE);
        } catch(SQLiteException ex) {
            Log.v("Create table exception", ex.getMessage());
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
        onCreate(db);
    }
}
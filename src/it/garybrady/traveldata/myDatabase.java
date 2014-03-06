package it.garybrady.traveldata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
/*
*This class is used to interact with the database
 */
public class myDatabase {
    private SQLiteDatabase db;
    private final Context context;
    private final DBhelper dbhelper;
    public myDatabase(Context c){
        context = c;
        dbhelper = new DBhelper(context, constants.DATABASE_NAME, null,
                constants.DATABASE_VERSION);
    }

    //close connection to database
    public void close()
    {
        db.close();
    }
    //try to open connection to the database
    public void open() throws SQLiteException
    {
        try {
            db = dbhelper.getWritableDatabase();
        } catch(SQLiteException ex) {
            Log.v("Open database exception caught", ex.getMessage());
            db = dbhelper.getReadableDatabase();
        }
    }

    //insert score after receiving a name, score and level
    public long insertAddress(String address)
    {
        try{
            ContentValues newTaskValue = new ContentValues();
            newTaskValue.put(constants.ADDRESS, address);
            return db.insert(constants.TABLE_NAME, null, newTaskValue);
        } catch(SQLiteException ex) {
            Log.v("Insert into database exception caught",
                    ex.getMessage());
            return -1;
        }
    }

    //returns a most recent address saved
    public String getMostRecentAddress()
    {

        //limit of 1, ordered by id
        Cursor c = db.rawQuery("SELECT " + constants.ADDRESS + " FROM "+constants.TABLE_NAME +"  ORDER BY "+constants.KEY_ID+" DESC LIMIT 1",null);


        String result="";

        int iRow = c.getColumnIndex(constants.KEY_ID);
        int ioption = c.getColumnIndex(constants.ADDRESS);
        int i =0;
        //iterate through each result and add it to the result string
        for(c.moveToFirst(); !c.isAfterLast();c.moveToNext()){
            result=c.getString(ioption);

            i++;
        }
        return result;
    }

    //delete all addresses from the table
    public void clearDB(){
            db.delete(constants.TABLE_NAME, null, null);


    }
}

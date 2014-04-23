package it.garybrady.traveldata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/*
*This class is used to interact with the database
 */
public class myDatabase {
    private SQLiteDatabase db;
    private final Context context;
    private final DBhelper dbhelper;
    public static final String[] KEY_TITLE_ACTIVE = new String[] {constants.KEY_ID,constants.G_TITLE,constants.G_ACTIVE};
    public static final String[] KEY_TITLE = new String[] {constants.KEY_ID,constants.G_TITLE};
    public static final String[] TITLE = new String[] {constants.G_TITLE};
    public static final String[] KEY_TITLE_LATLNG = new String[] {constants.KEY_ID,constants.G_TITLE,constants.G_LAT,constants.G_LNG};

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


    /*
    * Geofence table methods
    * */


    public int getMaxGeoId(){
        //limit of 1, ordered by id
        Cursor c = db.rawQuery("SELECT MAX(" + constants.KEY_ID+") AS max FROM "+ constants.GEO_TABLE+";",null);


        int result=33;

        int iRow = c.getColumnIndex(constants.KEY_ID);
        int ioption = c.getColumnIndex("max");
        int i =0;
        //iterate through each result and add it to the result string
        for(c.moveToFirst(); !c.isAfterLast();c.moveToNext()){
            result=c.getInt(ioption);

            i++;
        }
        c.close();
        return result;
    }

    public long insertGeofence(String title,double lat,double lng)
    {
        try{
            ContentValues newTaskValue = new ContentValues();
            newTaskValue.put(constants.G_TITLE, title);
            newTaskValue.put(constants.G_LAT, lat);
            newTaskValue.put(constants.G_LNG, lng);
            newTaskValue.put(constants.G_ACTIVE, 1);
            return db.insert(constants.GEO_TABLE, null, newTaskValue);
        } catch(SQLiteException ex) {
            Log.v("Insert into database exception caught",
                    ex.getMessage());
            return -1;
        }
    }

    /**
     * Getting all titles
     * */
    public List<String> getAllGeo(){
        List<String> titles = new ArrayList<String>();

        // Select All Query
        Cursor c = db.rawQuery("SELECT " + constants.KEY_ID+", "+ constants.G_TITLE +" FROM "+ constants.GEO_TABLE+";",null);



        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                titles.add(c.getString(1));
            } while (c.moveToNext());
        }

        // closing connection
        c.close();

        // returning lables
        return titles;
    }
    // Get a specific row (by rowId)
    public Cursor getRow(long rowId) {
        String where = constants.KEY_ID + "=" + rowId;
        Cursor c = 	db.query(true, constants.GEO_TABLE, KEY_TITLE_ACTIVE,
                where, null, null, null, null, null);


        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }

    // Get a specific row (by rowId)
    public Cursor getRowLocation(long rowId) {
        String where = constants.KEY_ID + "=" + rowId;
        Cursor c = 	db.query(true, constants.GEO_TABLE, KEY_TITLE_LATLNG,
                where, null, null, null, null, null);


        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     *
     * @return Cursor over all notes
     */
    public Cursor fetchAllActive() {
        String where = constants.G_ACTIVE + "=" + 1;
        Cursor c = db.query(true, constants.GEO_TABLE, KEY_TITLE,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    public Cursor getActiveTitle(int id) {
        String where = constants.G_ACTIVE + "=" + 1+" AND " + constants.KEY_ID+"="+id;
        Cursor c = db.query(true, constants.GEO_TABLE, TITLE,
                where,null, null,null, null, null);
        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }

    public Cursor previousGeo() {
        String where = constants.G_ACTIVE + "=" + 0;
        Cursor c = db.query(true, constants.GEO_TABLE, KEY_TITLE,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }

    public void deactivateGeo(int id){
        try{
            ContentValues cv = new ContentValues();
            cv.put(constants.G_ACTIVE, 0);
            db.update(constants.GEO_TABLE,cv,constants.KEY_ID+"="+id,null);

        } catch(SQLiteException ex) {
            Log.v("Insert into database exception caught",
                    ex.getMessage());

        }
    }

    public long insertAlarm(String bus,String ref, String eta)
    {
        try{
            ContentValues newTaskValue = new ContentValues();
            newTaskValue.put(constants.A_BUS, bus);
            newTaskValue.put(constants.A_REF, ref);
            newTaskValue.put(constants.A_ETA, eta);
            return db.insert(constants.A_TABLE, null, newTaskValue);
        } catch(SQLiteException ex) {
            Log.v("Insert into database exception caught",
                    ex.getMessage());
            return -1;
        }
    }

    //returns a most recent address saved
    public String[] getMostRecentAlarm()
    {

        //limit of 1, ordered by id
        Cursor c = db.rawQuery("SELECT " + constants.A_BUS +", "+constants.A_REF+", "+constants.A_ETA+ " FROM "+constants.A_TABLE +"  ORDER BY "+constants.KEY_ID+" DESC LIMIT 1",null);


        String result[]=new String[3];

        int iRow = c.getColumnIndex(constants.KEY_ID);
        int iBus = c.getColumnIndex(constants.A_BUS);
        int iRef = c.getColumnIndex(constants.A_REF);
        int iEta = c.getColumnIndex(constants.A_ETA);

        //iterate through each result and add it to the result string
        for(c.moveToFirst(); !c.isAfterLast();c.moveToNext()){
            result[0]=c.getString(iBus);
            result[1]=c.getString(iRef);
            result[2]=c.getString(iEta);

        }
        c.close();
        return result;
    }
 }

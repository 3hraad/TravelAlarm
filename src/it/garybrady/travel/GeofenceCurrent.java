package it.garybrady.travel;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import it.garybrady.traveldata.constants;
import it.garybrady.traveldata.myDatabase;

/**
 * Created by Gary on 22/03/14.
 */
public class GeofenceCurrent extends ListActivity {
    myDatabase dba;
    final Context context=this;

    ListView currentGeo;

    View header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_current_geo);

        currentGeo=(ListView)findViewById(android.R.id.list);
        header = View.inflate(this,R.layout.list_view_header_current,null);
        currentGeo.addHeaderView(header);

        fillData();
        registerListClickCallback();


        //loadListView();
    }



    private void fillData() {
        dba=new myDatabase(this);
        dba.open();
        // Get all of the notes from the database and create the item list
        Cursor c = dba.fetchAllActive();
        dba.close();
        startManagingCursor(c);

        String[] from = new String[] { constants.G_TITLE };
        int[] to = new int[] { R.id.text1 };

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.geofence_titles, c, from, to);
        currentGeo.setAdapter(notes);
        if (!c.moveToFirst()){
            Toast.makeText(getApplicationContext(), "No Alarms Active", Toast.LENGTH_LONG).show();

        }
    }

    private void registerListClickCallback() {
        currentGeo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long idInDB) {
                dba.open();
                Cursor cursor = dba.getRow(idInDB);
                dba.close();
                if (cursor.moveToFirst()) {
                    final int idDB = cursor.getInt(0);


                    String title = cursor.getString(1);
                    int active = cursor.getInt(2);
                    String message = "id: " + idDB + "  title: " + title + "  active: " + active;
                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    // set title
                    alertDialogBuilder.setTitle("Are you sure you want to remove this alarm?");

                    // set dialog message
                    alertDialogBuilder
                            //.setMessage("Click yes to exit!")
                            .setCancelable(false)
                            .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    Toast.makeText(getApplicationContext(), "Alarm Removed",Toast.LENGTH_LONG).show();
                                    removeGeo(idDB);
                                    GeofenceCurrent.this.finish();
                                }
                            })
                            .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }


            }
        });
    }

    private void removeGeo(int x) {
        dba=new myDatabase(this);
        dba.open();
        dba.deactivateGeo(x);
        dba.close();
    }
}




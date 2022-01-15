package com.learn.shelterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.learn.shelterapp.Data.PetContract;
import com.learn.shelterapp.Data.PetDbHelper;
import com.learn.shelterapp.Data.PetProvider;

public class CatalogActivity extends AppCompatActivity {

    /** Database helper that will provide us access to the database */
    private PetDbHelper mDbHelper;
    private String LOG_TAG = CatalogActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new PetDbHelper(this);

        displayDatabaseInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it
       // SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
       // Cursor cursor = db.rawQuery("SELECT * FROM " + PetContract.PetEntry.TABLE_NAME, null);
        
        String[] projection = {
                PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_PET_NAME,
                PetContract.PetEntry.COLUMN_PET_BREED,
                PetContract.PetEntry.COLUMN_PET_WEIGHT,
                PetContract.PetEntry.COLUMN_PET_GENDER
        };

      //  Cursor cursor = db.query(PetContract.PetEntry.TABLE_NAME,projection,null,null,null,null,null);
        /** Here SelectionArgs and selection is null since we don't want a specific row we just want the whole table
         *  If we want 1 row with id = 5 then:  selection = "=?" and selectionArgs = {"5"}
         * **/
        Log.i(LOG_TAG,"LINK:  "+PetContract.PetEntry.CONTENT_URI);
        Cursor cursor = getContentResolver().query(PetContract.PetEntry.CONTENT_URI,projection,null,null,null);

        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_view_pet);
            displayView.setText("Number of rows in pets database table: " + cursor.getCount());

            int columnIdIndex = cursor.getColumnIndex(PetContract.PetEntry._ID);
            int columnNameIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME);
            int columnBreedIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED);

            while (cursor.moveToNext()){

                int currentId = cursor.getInt(columnIdIndex);
                String name = cursor.getString(columnNameIndex);
                String breed = cursor.getString(columnBreedIndex);

                displayView.append("\n"+ currentId+" - "+name+" - "+breed);

            }

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertPet() {
        // Gets the database in write mode
       // SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetContract.PetEntry.COLUMN_PET_GENDER, PetContract.PetEntry.GENDER_MALE);
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, 7);

        // Insert a new row for Toto in the database, returning the ID of that new row.
        // The first argument for db.insert() is the pets table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Toto.

        Uri uri = getContentResolver().insert(PetContract.PetEntry.CONTENT_URI,values);
        if(uri!=null){
            Toast.makeText(getBaseContext(),"Pet Saved",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(),"Error Occurred",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
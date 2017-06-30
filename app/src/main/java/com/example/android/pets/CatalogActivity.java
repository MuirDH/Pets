/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.android.pets.data.PetContract.PetEntry;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PET_LOADER = 0;
    PetCursorAdapter cursorAdapter;


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

        // find the ListView which will be populated with the pet data
        ListView petListView = (ListView) findViewById(R.id.list);

        // find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        /**
         * Setup an Adapter to create a list item for each row of pet data in the Cursor. There is
         * no pet data yet (until the loader finishes) so pass in null for the Cursor.
         */
        cursorAdapter = new PetCursorAdapter(this, null);

        // Attach the adapter to the ListView.
        petListView.setAdapter(cursorAdapter);

        // Kick off the loader.
        // Prepare the loader. Either re-connect with an existing one, or start a new one.
        getSupportLoaderManager().initLoader(PET_LOADER, null, this);

    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertPet() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
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
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when a new Loader needs to be created
     *
     * @param id   is the id of the pet
     * @param args is the info.
     * @return a CursorLoader which displays the info.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED};

        /**
         * Create and return a CursorLoader that will take care of creating a Cursor for the data
         * being displayed. This loader will execute the contentProvider's query method on a
         * background thread.
         */
        return new CursorLoader(
                this,                  // Parent activity context
                PetEntry.CONTENT_URI,  // Provider content URI to query
                projection,            // Columns to include in the resulting Cursor
                null,                  // No selection clause
                null,                  // No selection arguments
                null);                 // Default sort order
    }

    /**
     * Called when a previously created loader has finished loading
     *
     * @param loader is the cursor loader
     * @param data   is the pet data being updated
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        /**
         * Swap the new cursor in. (The framework will take care of closing the old cursor once we
         * return.) Update {@link PetCursorAdapter} with this new cursor containing updated pet data
         */
        cursorAdapter.swapCursor(data);

    }

    /**
     * Called when a previously created loader is reset, making the data unavailable
     *
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /**
         * This is called when the last Cursor provided to onLoadFinished() above is about to be
         * closed. We need to make sure we are no longer using it.
         */
        cursorAdapter.swapCursor(null);

    }
}
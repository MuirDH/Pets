package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Pets Created by Muir on 27/06/2017.
 * {@link ContentProvider} for Pets app.
 */

public class PetProvider extends ContentProvider{

    /**
     * Database helper object
     */
    private PetDbHelper dbHelper;

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    /**
     * Initialise the provider and the database helper object.
     * @return true
     */
    @Override
    public boolean onCreate() {
        dbHelper = new PetDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection arguments, and sort
     * order.
     * @param uri is the given URI
     * @param projection is the given projection
     * @param selection is the selection
     * @param selectionArgs are the selection arguments
     * @param sortOrder is the given sort order
     * @return null
     */
    @Override
    public Cursor query (Uri uri, String[] projection, String selection, String[] selectionArgs,
                         String sortOrder) {
        return null;
    }

    /**
     * Returns the MIME type of data for the content URI
     * @param uri is the content URI
     * @return null
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * Insert new data into the provider with the given ContentValues
     * @param uri is the given URI
     * @param contentValues are the given content values
     * @return null
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     * @param uri is the given URI
     * @param selection is the given selection
     * @param selectionArgs is an array of the given selection arguments.
     * @return 0
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     * @param uri is the given URI
     * @param contentValues are the new content values
     * @param selection is the given selection
     * @param selectionArgs is an array of the given selection arguments
     * @return 0
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        return  0;
    }
}

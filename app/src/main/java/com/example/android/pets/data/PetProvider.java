package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import static com.example.android.pets.data.PetContract.*;

/**
 * Pets Created by Muir on 27/06/2017.
 * {@link ContentProvider} for Pets app.
 */

public class PetProvider extends ContentProvider {

    /**
     * URI matcher code for the content URI for the pets table.
     */
    private static final int PETS = 100;

    /**
     * URI matcher code for the content URI for a single pet in the pets table.
     */
    private static final int PET_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code. The input passed into the
     * constructor represents the code to return for the root URI. It's common to use NO_MATCH as
     * the input for this case.
     */
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /*
     * Static initializer. This is run the first time anything is called from this class.
     */
    static {
        /*
         * The calls to addURI() go here, for all of the content URI patterns that the provider
         * should recognise. All paths added to the UriMatcher have a corresponding code to return
         * when a match is found.
         */

        /*
         * The content URI fo the form "content://com.example.android.pets/pets" will map to the
         * integer code {@link #PETS}. This URI is used to provide access to MULTIPLE rows of the
         * pets table.
         */
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_PETS, PETS);

        /*
         * The content URI of the form "content://com.example.android.pets/pets/#" will map to the
         * integer code {@link #PET_ID}. This URI is used to proivde access to ONE single row of the
         * pets table.
         *
         * In this case, the "#" wildcard is used where "#" can be substituted for an integer. For
         * example, "content://com.exammple.android.pets/pets/3" matches, but
         * "content://com.example.android.pets/pets" (without a number at the end) doesn't match.
         */
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_PETS + "/#", PET_ID);
    }

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
     *
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
     *
     * @param uri           is the given URI
     * @param projection    is the given projection
     * @param selection     is the selection
     * @param selectionArgs are the selection arguments
     * @param sortOrder     is the given sort order
     * @return cursor;
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        /**
         * Get readable database
         */
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        /**
         * This cursor will hold the result of the query
         */
        Cursor cursor;

        /**
         * Figure out if the URI matcher can match the URI to a specific code.
         */
        int match = uriMatcher.match(uri);

        switch (match) {
            case PETS:
                /*
                 * for the PETS code, query the pets table directly with the given projection,
                 * selection, selection arguments, and sort order. The cursor could contain multiple
                 * rows of the pets table.
                 */
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);

                break;
            case PET_ID:
                /*
                 * For the PET_ID code, extract out the ID from the URI. For an example URI such as
                 * "content://com.example.android.pets/pets/3", the selection will be "_id=?" and
                 * the selection argument will be a String array containing the actual ID of 3 in
                 * this case.
                 *
                 * For every "?" in the selection, we need to have an element in the selection
                 * arguments that will fill in the "?". Since we have 1 question mark in the
                 * selection, we have 1 String in the selection arguments' String array.
                 */
                selection = PetEntry._ID + "=?";

                selectionArgs = new String[] {
                        String.valueOf(ContentUris.parseId(uri))
                };

                /*
                 * This will perform a query on the pets table where the _id equals 3 to return a
                 * Cursor containing that row of the table.
                 */
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);

                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    /**
     * Returns the MIME type of data for the content URI
     *
     * @param uri is the content URI
     * @return null
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * Insert new data into the provider with the given ContentValues
     *
     * @param uri           is the given URI
     * @param contentValues are the given content values
     * @return null
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     *
     * @param uri           is the given URI
     * @param selection     is the given selection
     * @param selectionArgs is an array of the given selection arguments.
     * @return 0
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     *
     * @param uri           is the given URI
     * @param contentValues are the new content values
     * @param selection     is the given selection
     * @param selectionArgs is an array of the given selection arguments
     * @return 0
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        return 0;
    }
}

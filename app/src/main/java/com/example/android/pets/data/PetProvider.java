package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import static com.example.android.pets.data.PetContract.CONTENT_AUTHORITY;
import static com.example.android.pets.data.PetContract.PATH_PETS;
import static com.example.android.pets.data.PetContract.PetEntry;

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
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        /*
         * Get readable database
         */
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        /*
         * This cursor will hold the result of the query
         */
        Cursor cursor;

        /*
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
    public String getType(@NonNull Uri uri) {
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
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {

        validatePet(contentValues);

        final int match = uriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    public void validatePet(ContentValues contentValues) {
        // Check that the name is not null
        String name = contentValues.getAsString(PetEntry.COLUMN_PET_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        // Check that the Gender is not null and is one of the valid gender types
        Integer gender = contentValues.getAsInteger(PetEntry.COLUMN_PET_GENDER);
        if (gender == null || !PetEntry.isValidGender(gender)){
            throw new IllegalArgumentException("Pet requires a valid gender");
        }

        // Check that the Weight is greater than or equal to 0kg
        Integer weight = contentValues.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
        if (weight != null && weight < 0){
            throw  new IllegalArgumentException("Pet requires a valid weight");
        }

        // No need to check the breed as any value is valid (including null).
    }

    /*
     * Insert a pet into the database with the given content values. Return the new content URI for
     * that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues contentValues) {
        // Get writable database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(PetEntry.TABLE_NAME, null, contentValues);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Once we know the ID of the new row in the table, return the new URI with the ID appended
        // to the end of it.
        return ContentUris.withAppendedId(uri, id);
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
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        // Get writable database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        final int match = uriMatcher.match(uri);
        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args
                return database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
            case PET_ID:
                // Delete a single row given by the ID in the URI
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " +uri);
        }
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
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(contentValues, selection, selectionArgs);
            case PET_ID:
                /*
                 * for the PET_ID code, extract out the ID from the URI, so we know which row to
                 * update. Selection will be "_id=?" and selection arguments will be a String array
                 * containing the actual ID.
                 */
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                return updatePet(contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which cold be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updatePet(ContentValues contentValues, String selection,
                          String[] selectionArgs) {

        if (validatePetUpdate(contentValues)) return 0;


        // Otherwise get writable database to update the data
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement.
        return database.update(PetEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }

    public boolean validatePetUpdate(ContentValues contentValues) {
    /*
     * If the {@link PetEntry#COLUMN_PET_NAME} key is present, check that the name value is not
     * null.
     */
        if (contentValues.containsKey(PetEntry.COLUMN_PET_NAME)) {
            String name = contentValues.getAsString(PetEntry.COLUMN_PET_NAME);
            if (name == null)
                throw new IllegalArgumentException("Pet requires a name");
        }

        /*
         * If the {@link PetEntry#COLUMN_PET_GENDER} key is present, check that the gender value is
         * valid.
         */
        if (contentValues.containsKey(PetEntry.COLUMN_PET_GENDER)){
            Integer gender = contentValues.getAsInteger(PetEntry.COLUMN_PET_GENDER);
            if (gender == null || !PetEntry.isValidGender(gender))
                throw new IllegalArgumentException("Pet requires valid gender");
        }

        /*
         *If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present, check that the weight value is
         * valid.
         */
        if (contentValues.containsKey(PetEntry.COLUMN_PET_WEIGHT)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer weight = contentValues.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight <0)
                throw new IllegalArgumentException("Pet requires valid weight");
        }

        // No need to check the breed as any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        return contentValues.size() == 0;
    }
}

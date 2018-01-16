package com.tp.locator;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by gkomandu on 7/2/2015.
 */
public class ContactContentProvider extends ContentProvider {

    public static DBWrapper database;
    private SQLiteDatabase db;
    static final String PROVIDER_NAME = "com.tp.locator.provider.allowedContacts";
    static final String SETTINGS_PROVIDER_NAME = "com.tp.locator.provider.settings";
    static final String SettingsURL = "content://" + PROVIDER_NAME + "/settings";
   public static final String MainURL = "content://" + PROVIDER_NAME + "/Contacts";
    public static final String SavedLocationsURL = "content://" + PROVIDER_NAME + "/savedlocations";
    static final String ContactsTable = "allowedContacts";
    static final String SettingsTable = "settings";
    static final String LocationsTable = "locations";
    static final String SavedLocationsTable = "savedlocations";
    static final String receivedannouncementsTable = "receivedannouncements";
    private static HashMap<String, String> CONTACTS_PROJECTION_MAP;

    static final Uri CONTENT_URI = Uri.parse(MainURL);

    private String dbName;

    // used for the UriMacher
    private static final int CONTACTS = 10;
    private static final int CONTACT_NAME = 20;


    private static final int SETTINGS = 30;
    private static final int SETTINGS_NAME = 40;

    private static final int LOCATIONS = 50;
    private static final int LOCATIONS_NAME = 60;

    private static final int SAVEDLOCATIONS = 70;
    private static final int SAVEDLOCATIONS_NAME = 80;

    private static final int RECEIVEDANNOUNCEMENTS = 90;
    private static final int RECEIVEDANNOUNCEMENTS_NAME = 100;

    static final UriMatcher MainuriMatcher;
    static{
        MainuriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        MainuriMatcher.addURI(PROVIDER_NAME, "Contacts", CONTACTS);
        MainuriMatcher.addURI(PROVIDER_NAME, "Contacts/#", CONTACT_NAME);
    }



    static final UriMatcher SettingsuriMatcher;
    static{
        SettingsuriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        SettingsuriMatcher.addURI(PROVIDER_NAME, "settings", SETTINGS);
        SettingsuriMatcher.addURI(PROVIDER_NAME, "settings/#", SETTINGS_NAME);
    }

    static final UriMatcher LocationsuriMatcher;
    static{
        LocationsuriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        LocationsuriMatcher.addURI(PROVIDER_NAME, "locations", LOCATIONS);
        LocationsuriMatcher.addURI(PROVIDER_NAME, "locations/#", LOCATIONS_NAME);
    }

    static final UriMatcher SavedLocationsuriMatcher;
    static{
        SavedLocationsuriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        SavedLocationsuriMatcher.addURI(PROVIDER_NAME, "savedlocations", SAVEDLOCATIONS);
        SavedLocationsuriMatcher.addURI(PROVIDER_NAME, "savedlocations/#", SAVEDLOCATIONS_NAME);
    }

    static final UriMatcher ReceivedAnnouncementsuriMatcher;
    static{
        ReceivedAnnouncementsuriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        ReceivedAnnouncementsuriMatcher.addURI(PROVIDER_NAME, "receivedannouncements", RECEIVEDANNOUNCEMENTS);
        ReceivedAnnouncementsuriMatcher.addURI(PROVIDER_NAME, "receivedannouncements/#", RECEIVEDANNOUNCEMENTS_NAME);
    }

    public boolean setDBName(String dbName)
    {
        this.dbName = dbName;
        return true;
    }
    @Override
    public boolean onCreate() {
        if(dbName == null)
        {
            setDBName("GpsTracker.db");
        }
        database = new DBWrapper(getContext(),dbName);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder ){

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String strUri = uri.toString();
        if(strUri.contains("allowedContacts/Contacts")) {
            switch (MainuriMatcher.match(uri)) {
                case CONTACTS:
                    qb.setTables(ContactsTable);
                    qb.setProjectionMap(CONTACTS_PROJECTION_MAP);
                    break;
                case CONTACT_NAME:
                    qb.setTables(ContactsTable);
                    qb.appendWhere(CONTACT_NAME + "LIKE" + uri.getPathSegments().get(1));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
            if (sortOrder == null || sortOrder == "") {
                /**
                 * By default sort on student names
                 */
                sortOrder = "ContactNumber";

            }
        }
        else if(strUri.contains("allowedContacts/settings")) {

            int match =SettingsuriMatcher.match(uri);
            switch (match) {
                case SETTINGS:
                    qb.setTables(SettingsTable);
                    qb.setProjectionMap(CONTACTS_PROJECTION_MAP);
                    break;
                case SETTINGS_NAME:
                    qb.setTables(SettingsTable);
                    qb.appendWhere(CONTACT_NAME + "LIKE" + uri.getPathSegments().get(1));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
            if (sortOrder == null || sortOrder == "") {
                /**
                 * By default sort on student names
                 */
                sortOrder = "ContactNumber";

            }


        }
        else if(strUri.contains("allowedContacts/savedlocations")) {

            int match =SavedLocationsuriMatcher.match(uri);
            switch (match) {
                case SAVEDLOCATIONS:
                    qb.setTables(SavedLocationsTable);
                    qb.setProjectionMap(CONTACTS_PROJECTION_MAP);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
            if (sortOrder == null || sortOrder == "") {
                /**
                 * By default sort on student names
                 */
                sortOrder = "id";

            }


        }
        else if(strUri.contains("allowedContacts/locations")) {

            int match =LocationsuriMatcher.match(uri);
            switch (match) {
                case LOCATIONS:
                    qb.setTables(LocationsTable);
                    qb.setProjectionMap(CONTACTS_PROJECTION_MAP);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
            if (sortOrder == null || sortOrder == "") {
                /**
                 * By default sort on student names
                 */
                sortOrder = "id";

            }


        }
        else if(strUri.contains("allowedContacts/receivedannouncements")) {

            int match =ReceivedAnnouncementsuriMatcher.match(uri);
            switch (match) {
                case RECEIVEDANNOUNCEMENTS:
                    qb.setTables(receivedannouncementsTable);
                    qb.setProjectionMap(CONTACTS_PROJECTION_MAP);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
            if (sortOrder == null || sortOrder == "") {
                /**
                 * By default sort on student names
                 */
                sortOrder = "timestamp";

            }


        }
        Cursor c = qb.query(database.getDatabase(),	projection,	selection, selectionArgs,
                null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int uriType = 0;
        try {
            uriType = MainuriMatcher.match(uri);
        }
        catch(Exception ex)
        {
            if(ex.getMessage().contains("Unknown URI"))
            {
                uriType = SettingsuriMatcher.match(uri);
            }

        }
        if(uriType ==0)
        {
            try {
                uriType = LocationsuriMatcher.match(uri);
            }
            catch(Exception ex)
            {
                if(ex.getMessage().contains("Unknown URI"))
                {
                    uriType = SavedLocationsuriMatcher.match(uri);
                }


            }
        }
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        long id = 0;
        switch (uriType) {
            case CONTACTS:
                id = sqlDB.insert("allowedContacts", null, contentValues);
                break;
            case SETTINGS:
                id = sqlDB.insert("settings", null, contentValues);
                break;
            case LOCATIONS:
                id = sqlDB.insert("locations", null, contentValues);
                break;
            case SAVEDLOCATIONS:
                id = sqlDB.insert("savedlocations", null, contentValues);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (id > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
        //    getContext().getContentResolver().notifyChange(uri, null);
        //   return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        SQLiteDatabase db = database.getWritableDatabase();
        int uriType = 0;
        try {
            uriType = MainuriMatcher.match(uri);
        }
        catch(Exception ex)
        {
            if(ex.getMessage().contains("Unknown URI"))
            {
                uriType = SettingsuriMatcher.match(uri);
            }
        }
        switch (uriType){
            case CONTACTS:
                count = db.delete("allowedContacts", selection, selectionArgs);
                break;
            case CONTACT_NAME:
                String phone = uri.getPathSegments().get(1);
                count = db.delete( "allowedContacts", "ContactNumber" +  " = " + phone +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
             default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;

        SQLiteDatabase db = database.getWritableDatabase();

        int uriType = 0;
        try {
            uriType = MainuriMatcher.match(uri);
        }
        catch(Exception ex)
        {
            if(ex.getMessage().contains("Unknown URI"))
            {
                uriType = SettingsuriMatcher.match(uri);
            }
        }


        switch (uriType){
            case CONTACTS:
                count = db.update("allowedContacts", values,
                        selection, selectionArgs);
                break;
            case CONTACT_NAME:
                count = db.update("allowedContacts", values, "contactNumber" +
                        " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            case SETTINGS:
                count = db.update("settings", values,
                        selection, selectionArgs);
                break;
            case SETTINGS_NAME:
                count = db.update("settings", values, "contactNumber" +
                        " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
    @Override
    public String getType(Uri uri) {

        int uriType = 0;
        try {
            uriType = MainuriMatcher.match(uri);
        }
        catch(Exception ex)
        {
            if(ex.getMessage().contains("Unknown URI"))
            {
                uriType = SettingsuriMatcher.match(uri);
            }
        }
        if(uriType ==0)
        {
            try {
                uriType = LocationsuriMatcher.match(uri);
            }
            catch(Exception ex)
            {

                if(ex.getMessage().contains("Unknown URI"))
                {
                    uriType = SavedLocationsuriMatcher.match(uri);
                }


            }
        }
        switch (uriType){
            /**
             * Get all student records
             */
            case CONTACTS:
                return "com.tp.locator/com.tp.locator.contacts";
            /**
             * Get a particular student
             */
            case CONTACT_NAME:
                return "com.tp.locator/com.tp.locator.contactname";
            case SETTINGS:
                return "com.tp.locator/com.tp.locator.settings";

            /**
             * Get a particular student
             */
            case SETTINGS_NAME:
                return "com.tp.locator/com.tp.locator.settingname";
            case LOCATIONS:
                return "com.tp.locator/com.tp.locator.locations";
            case SAVEDLOCATIONS:
                return "com.tp.locator/com.tp.locator.savedlocations";

                      default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}
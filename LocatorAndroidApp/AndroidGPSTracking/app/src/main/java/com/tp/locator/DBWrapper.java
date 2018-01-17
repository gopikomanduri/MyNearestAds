package com.tp.locator;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;

/**
 * Created by user on 7/2/2015.
 */
public class DBWrapper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "locationTracker.db";
    public static final String CONTACTS_TABLE_NAME = "allowedContacts";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NUMBER = "ContactNumber";
    public static final String CONTACTS_COLUMN_NAME = "Name";
    public static final String CONTACTS_COLUMN_SEX = "sex";
    public static final String OTP_COLUMN_NUMBER = "otpid";


    public static final String CONTACTS_COLUMN_ALLOWED_TIME_START = "allowedStartTime";
    public static final String CONTACTS_COLUMN_ALLOWED_TIME_END = "allowedEndTime";

    public static final String CONTACTS_COLUMN_REGISTERED_MSG = "registeredMsg";

    public static final String CONTACTS_COLUMN_IS_MOCK_ALLOWED = "isMockAllowed";

    public static final String CONTACTS_COLUMN_ALLOWED_MOCK_TIME_START = "mockStartTime";

    public static final String CONTACTS_COLUMN_ALLOWED_MOCK_TIME_END = "mockEndTime";

    public static final String CONTACTS_COLUMN_ALLOWED_MOCK_MSG = "mockMsg";


    private HashMap hp;

    public DBWrapper(Context context,String dbName)
    {
        super(context, dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
       // db.beginTransaction();
        db.execSQL(
                "create table allowedContacts " +
                        "(id integer primary key," +
                        "ContactNumber text," +
                        "ContactName text," +
                        "allowedStartTime DATETIME, " +
                        "allowedEndTime DATETIME," +
                        "registeredMsg text," +
                        "isMockAllowed Integer," +
                        "mockStartTime DATETIME," +
                        "mockEndTime DATETIME," +
                        "mockMsg text)"
        );


        db.execSQL(
                "create table settings " +
                        "(id integer primary key," +
                        "ContactNumber text," +
                        "pwdMsg text," +
                        "emergency Integer," +
                        "locator Integer," +
                        "MockLocation text," +
                        "EmergencyContact1 text, " +
                        "EmergencyContact2 text," +
                        "EmergencyContact3 text)"
        );
        db.execSQL(
                "create table locations " +
                        "(id integer primary key," +
                        "timestamp text," +
                        "fromNumber text," +
                        "latitude text," +
                        "longitude text," +
                        "address text)"
        );
        db.execSQL(
                "create table savedlocations " +
                        "(id integer primary key," +
                        "timestamp text," +
                        "name text," +
                        "professionalname text," +
                        "contact text," +
                        "address text)"
        );
        db.execSQL(
                "create table taggedlocations " +
                        "(id integer primary key," +
                        "timestamp text," +
                        "professionalname text," +
                        "isTagAccepted text," +
                        "latitude text," +
                        "longitude text," +
                        "address text)"
        );
        db.execSQL(
                "create table mynumber " +
                        "(ContactNumber text primary key," +
                        "Name text,"+
                        "sex integer)"
        );
        db.execSQL(
                "create table myannouncements " +
                        "(_id integer primary key autoincrement," +
                        "timestamp integer," +
                        "msg text)"
        );
        db.execSQL(
                "create table receivedannouncements " +
                        "(_id integer primary key autoincrement," +
                        "timestamp integer," +
                        "date text," +
                        "category integer," +
                        "msg text)"
        );

        db.execSQL(
                "create table mybizannouncements " +
                        "(_id integer primary key autoincrement," +
                        "notificationid integer," +
                        "timestamp integer," +
                        "validfrom integer," +
                        "validTill integer," +
                        "msg text)"
        );


        db.execSQL(
                "create table otp " +
                        "(_id integer primary key," +
                        "otpidpk text," +
                        "otpid text)"
        );
        db.execSQL(
                "create table categories "+
                        "(_id integer primary key autoincrement, "+
                        "catname text)"
        );




        db.execSQL(
                "create table emergency "+
                        "(_id integer primary key autoincrement, "+
                        "emergencyname text)"
        );
        db.execSQL(
                "create table social "+
                        "(_id integer primary key autoincrement, "+
                        "socialname text)"
        );
        db.execSQL(
                "create table commercial "+
                        "(_id integer primary key autoincrement, "+
                        "commercialname text)"
        );
//        db.execSQL(
//                "create table friendship "+
//                        "(_id integer primary key autoincrement, "+
//                        "emergencyname text)"
//        );
//        db.execSQL(
//                "create table pinnedcontacts "+
//                        "(_id integer primary key autoincrement, "+
//                        "ismasked integer," +
//                        "contact text)"
//        );

//        db.execSQL(
//                "create table commercialcategories "+
//                        "(_id integer primary key autoincrement, "+
//                        "commercialname text)"
//        );
        db.execSQL(
                "create table commercialcategoriesoffer "+
                        "(_id integer primary key autoincrement, "+
                        "offerid integer," +
                        "commercialcat integer," +
                        "offercode text," +
                        "validfrom text," +
                        "validto text," +
                        "imageId Integer," +
                        "offerFrom Integer," +
                        "offer text)"
        );
        db.execSQL(
                "create table commercialcategoriesofferimages "+
                        "(_id integer primary key autoincrement, "+
                        "offerid integer," +
                        "imageId Integer," +
                        "image text)"
        );

        db.execSQL(
                "create table commercialcategoryimages "+
                        "(_id integer primary key autoincrement, "+
                        "commercialcat integer," +
                        "image text)"
        );

        db.execSQL(
                "create table commercialImage "+
                        "(_id integer primary key autoincrement, "+
                        "commercialFrom integer," +
                        "image text)"
        );


       // db.endTransaction();
    }
    public SQLiteDatabase getDatabase()
    {
        return this.getWritableDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");

        onCreate(db);
    }

    public void insertInitialData()
    {
        insertcategory("Emergency");
        insertcategory("Social");
        insertcategory("Commercial");
        insertcategory("Sports");
    }

    public boolean insertcategory(String categoryname)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

//        Integer pkVal = 1;
//        contentValues.put("_id", pkVal);
        contentValues.put("catname",categoryname);
        db.insertWithOnConflict("categories", null, contentValues, CONFLICT_REPLACE );
        return true;
    }
    public boolean insertemergencycategory(String emergencycategoryname)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Integer pkVal = 1;
        contentValues.put("_id", pkVal);
        contentValues.put("emergencyname",emergencycategoryname);
        db.insertWithOnConflict("emergency", null, contentValues, CONFLICT_REPLACE );
        return true;
    }
    public boolean insertsocialcategory(String socialcategoryname)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Integer pkVal = 1;
        contentValues.put("_id", pkVal);
        contentValues.put("socialname",socialcategoryname);
        db.insertWithOnConflict("social", null, contentValues, CONFLICT_REPLACE );
        return true;
    }
    public boolean insertcommercialcategory(String commercialcategoryname)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Integer pkVal = 1;
        contentValues.put("_id", pkVal);
        contentValues.put("commercialname",commercialcategoryname);
        db.insertWithOnConflict("commercial", null, contentValues, CONFLICT_REPLACE );
        return true;
    }
    /*

            db.execSQL(
                "create table commercialcategoriesoffer "+
                        "(_id integer primary key autoincrement, "+
                        "offerid integer," +
                        "offercode text," +
                        "validfrom text," +
                        "validto text," +
                        "imageId Integer," +
                        "offer text)"
        );
        db.execSQL(
                "create table commercialcategoriesofferimages "+
                        "(_id integer primary key autoincrement, "+
                        "offerid integer," +
                        "imageId Integer," +
                        "image text)"
        );
     */
    public boolean insertcommercialcategoriesoffer(String offerText,
                                                   String validFrom,
                                                   Integer commercialcat ,
                                                   String validTo,
                                                   String offerCode,
                                                   Integer offerId,
                                                   Integer offerFrom,
                                                   Integer imageId
                                                   )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("offerid",offerId);
        contentValues.put("offercode",offerCode);
        contentValues.put("validfrom",validFrom);
        contentValues.put("commercialcat",commercialcat);
        contentValues.put("validto",validTo);
        contentValues.put("offer",offerText);
        contentValues.put("offerFrom",offerFrom);
        contentValues.put("imageId",imageId);

        db.insertWithOnConflict("commercialcategoriesoffer", null, contentValues, CONFLICT_REPLACE );
        return true;
    }
    public boolean insertcommercialcategoriesofferimages(String image,
                                                   Integer offerId,
                                                   Integer imageId
    )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("offerid",offerId);
        contentValues.put("imageId",imageId);
        contentValues.put("image",image);

        db.insertWithOnConflict("commercialcategoriesofferimages", null, contentValues, CONFLICT_REPLACE );
        return true;
    }

    /*


            db.execSQL(
                "create table commercialcategoryimages "+
                        "(_id integer primary key autoincrement, "+
                        "commercialcat integer," +
                        "image text)"
        );

        db.execSQL(
                "create table commercialImage "+
                        "(_id integer primary key autoincrement, "+
                        "commercialFrom integer," +
                        "image text)"
        );
     */
    public boolean insertcommercialcategoryimages(String image,
                                                         Integer commercialcat
    )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("commercialcat",commercialcat);
        contentValues.put("image",image);

        db.insertWithOnConflict("commercialcategoryimages", null, contentValues, CONFLICT_REPLACE );
        return true;
    }
    public boolean insertcommercialImage(String image,
                                         Integer commercialFrom
    )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("commercialFrom",commercialFrom);
        contentValues.put("image",image);

        db.insertWithOnConflict("commercialImage", null, contentValues, CONFLICT_REPLACE );
        return true;
    }

    public boolean insertotpId(String otpId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Integer pkVal = 1;
        contentValues.put("_id", pkVal);
        contentValues.put("otpidpk", "otpid");
        contentValues.put("otpid", otpId);

        db.insertWithOnConflict("otp", null, contentValues, CONFLICT_REPLACE );
        return true;
    }

    public boolean insertContact  (String contactnumber,
                                   String contactname,
                                   String allowedStartTime,
                                   String allowedEndTime,
                                   String registeredMsg,
                                   Integer isMockAllowed,
                                   String mockStartTime,
                                   String mockEndTime,
                                   String mockMsg)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("contactnumber", contactnumber);
        contentValues.put("contactname", contactname);
        contentValues.put("allowedStartTime", allowedStartTime);
        contentValues.put("allowedEndTime", allowedEndTime);
        contentValues.put("registeredMsg", registeredMsg);
        contentValues.put("isMockAllowed", isMockAllowed);
        contentValues.put("mockStartTime", mockStartTime);
        contentValues.put("mockEndTime", mockEndTime);
        contentValues.put("mockMsg", mockMsg);
     //   db.beginTransaction();
        db.insert("allowedContacts", null, contentValues);
     //   db.endTransaction();
        //db.execSQL("insert into ");

        return true;
    }

    public boolean insertMyDetails  (String contactnumber, String Name, Integer sex)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("contactnumber", contactnumber);
        contentValues.put("Name", Name);
        contentValues.put("sex", sex);

        //   db.beginTransaction();
        db.insert("mynumber", null, contentValues);
        //   db.endTransaction();
        //db.execSQL("insert into ");
//        db.setTransactionSuccessful();
 //       db.endTransaction();


        return true;
    }


    public boolean insertSettingsContact  (String contactnumber,
                                   String pwdMsg,
                                           Integer emergency,
                                           Integer locator,
                                   String MockLocation,
                                   String EmergencyContact1,
                                   String EmergencyContact2,
                                           String EmergencyContact3
                                  )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("ContactNumber", contactnumber);
        contentValues.put("pwdMsg", pwdMsg);
        contentValues.put("MockLocation", MockLocation);
        contentValues.put("emergency", emergency);
        contentValues.put("locator", locator);
        contentValues.put("EmergencyContact1", EmergencyContact1);
        contentValues.put("EmergencyContact2", EmergencyContact2);
        contentValues.put("EmergencyContact3", EmergencyContact3);
        db.insert("settings", null, contentValues);

        return true;
    }



    public boolean insertAnn(Long timestamp, String date, Integer category, String msg)
    {
//        "(_id integer primary key autoincrement," +
//                "timestamp integer," +
//                "date text," +
//                "category integer," +
//                "msg text)"

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("timestamp", timestamp);
        contentValues.put("date", date);
        contentValues.put("category", category);
        contentValues.put("msg", msg);
       long ret = db.insert("receivedannouncements", null, contentValues);

        return true;
    }





    public boolean insertlocation  (String latitude,
                                           String longitude,
                                           String address,
                                    String timestamp,
                                    String fromNumber)
    {
             SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        contentValues.put("fromNumber", fromNumber);
        contentValues.put("timestamp", timestamp);
        contentValues.put("address", address);

        db.insert("locations", null, contentValues);

        return true;
    }

    public void clearLogs()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        db.delete("locations",null,null);

      //  db.execSQL("delete from  locations");
        db.delete("receivedannouncements",null,null);
        db.setTransactionSuccessful();
        db.endTransaction();


    }

    public boolean insertSavedLocation  (String name,
                                    String professionalname,
                                    String contact,
                                    String address,
                                    String timestamp)
    {
        /*
        "timestamp text," +
                        "name text," +
                        "professionalname text," +
                        "contact text," +
                        "address text)"
         */
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", name);
        contentValues.put("professionalname", professionalname);
        contentValues.put("contact", contact);
        contentValues.put("timestamp", timestamp);
        contentValues.put("address", address);

        db.insert("savedlocations", null, contentValues);

        return true;
    }

    public boolean insertMyNotification  (String msg)
    {
        /*
                       "create table myannouncements " +
                        "(id integer primary key autoincrement," +
                        "timestamp integer," +
                        "msg text)"
         */
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Long timeStampValue = System.currentTimeMillis();
        contentValues.put("msg", msg);
        contentValues.put("timestamp", timeStampValue);
        db.insert("myannouncements", null, contentValues);

        return true;
    }

    public boolean insertBizNotification  (String msg)
    {
        /*
                   "create table mybizannouncements " +
                        "(id integer primary key autoincrement," +
                        "notificationid integer," +
                        "timestamp integer," +
                        "validfrom integer," +
                        "validTill integer," +
                        "msg text)"
         */
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Long timeStampValue = System.currentTimeMillis();
        contentValues.put("msg", msg);
        contentValues.put("timestamp", timeStampValue);
        db.insert("myannouncements", null, contentValues);

        return true;
    }


    public Cursor getData(String  id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from allowedContacts where id="+id+"", null );
        return res;
    }

    public Cursor getAllCategories(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select catname from categories ", null );
        return res;
    }

    public Cursor getBizCategoryDetails(String tableName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+tableName, null );
        return res;
    }


    public Cursor getMyNum(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select contactnumber from mynumber ", null );
        return res;
    }

    public Cursor getDataFromSettings(String  id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from settings where id="+id+"", null );
        return res;
    }

    public Cursor getDataFromLocations(String  id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from locations where id="+id+"", null );
        return res;
    }

    public Cursor getDataFromSavedLocations(String  id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from savedlocations where id="+id+"", null );
        return res;
    }


    public Cursor getDataFromNumber(String  contactNumber){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from allowedContacts where contactnumber='"+contactNumber+"'", null );
        return res;
    }

    public Cursor getDataFromNumberFromSettings(String  contactNumber){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from settings where contactnumber='"+contactNumber+"'", null );
        return res;
    }

    public Cursor getDataFromNumberFromSavedLocations(String  contactNumber){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from savedlocations where contactnumber='"+contactNumber+"'", null );
        return res;
    }

    public  String getMyNumber()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        try{
            res = db.rawQuery("select * from mynumber ", null);

            res.moveToFirst();
        }
        catch(Exception ex)
        {
            return null;
        }
        if(res.getCount() == 0)
            return null;
        return res.getString(res.getColumnIndex(CONTACTS_COLUMN_NUMBER));
    }

    public  String getMyName()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        try{
            res = db.rawQuery("select * from mynumber ", null);

            res.moveToFirst();
        }
        catch(Exception ex)
        {
            return null;
        }
        if(res.getCount() == 0)
            return null;
        return res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME));
    }
    public  String getMySex()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        try{
            res = db.rawQuery("select * from mynumber ", null);

            res.moveToFirst();
        }
        catch(Exception ex)
        {
            return null;
        }
        if(res.getCount() == 0)
            return null;
        return res.getString(res.getColumnIndex(CONTACTS_COLUMN_SEX));
    }


    public String getOtpToValidate()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        try{
            res = db.rawQuery("select * from otp ", null);

            res.moveToFirst();
        }
        catch(Exception ex)
        {
            return null;
        }
        if(res.getCount() == 0)
            return null;
        return res.getString(res.getColumnIndex(OTP_COLUMN_NUMBER));
    }

    public Integer getidFromNumber(String contactNumber) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        try{

        res = db.rawQuery("select id from allowedContacts where contactNumber='" + contactNumber + "'", null);
        res.moveToFirst();
    }
    catch(Exception ex)
    {
        return 0;
    }
        if(res.getCount() == 0)
            return 0;
        return Integer.parseInt(res.getString(res.getColumnIndex(CONTACTS_COLUMN_ID)));
    }


    public Integer getidFromNumberFromSettings(String contactNumber) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        try{

            res = db.rawQuery("select id from settings where contactNumber='" + contactNumber + "'", null);
            res.moveToFirst();
        }
        catch(Exception ex)
        {
            return 0;
        }
        if(res.getCount() == 0)
            return 0;
        return Integer.parseInt(res.getString(res.getColumnIndex(CONTACTS_COLUMN_ID)));
    }

    public Integer getidFromNumberFromSavedLocations(String contactNumber) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        try{

            res = db.rawQuery("select id from savedlocations where contactNumber='" + contactNumber + "'", null);
            res.moveToFirst();
        }
        catch(Exception ex)
        {
            return 0;
        }
        if(res.getCount() == 0)
            return 0;
        return Integer.parseInt(res.getString(res.getColumnIndex(CONTACTS_COLUMN_ID)));
    }


    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }



    public boolean updateContactwithIsAllowedChange (String contactNumber,Integer isAllowed)
    {
        SQLiteDatabase db = this.getWritableDatabase();
       Integer id = getidFromNumber(contactNumber);
        ContentValues contentValues = new ContentValues();
        String val = isAllowed.toString();
        contentValues.put("allowedStartTime", val);
        db.update("allowedContacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public boolean deleteContact (String contactNumber)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer id = getidFromNumber(contactNumber);
        db.delete("allowedContacts", "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }
    public boolean deletecategories(String category)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("categories", "categoryname = ? ", new String[]{category});
        return true;
    }
    public boolean deleteLocation(String timeStamp)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("savedlocations", "timestamp= ? ", new String[]{timeStamp});
        return true;
    }
    public boolean updateContactwithAllowedTimings (String contactNumber,String allowedStartTime,String allowedEndTime)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer id = getidFromNumber(contactNumber);
        ContentValues contentValues = new ContentValues();
        contentValues.put("allowedStartTime", allowedStartTime);
        contentValues.put("allowedEndTime", allowedEndTime);
        contentValues.put("isMockAllowed", 0);
        db.update("allowedContacts", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }
    public boolean updateContactwithMockDetails (String contactNumber,String mockStartTime,String mockEndTime,String mockMsg)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer id = getidFromNumber(contactNumber);
        ContentValues contentValues = new ContentValues();
        contentValues.put("allowedStartTime", "yes");
        contentValues.put("mockStartTime", mockStartTime);
        contentValues.put("mockEndTime", mockEndTime);
        contentValues.put("isMockAllowed", 1);
        contentValues.put("mockMsg", mockMsg);
        db.update("allowedContacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }


    public boolean updateSettingswithPwdMessage (String contactNumber,String pwdMsg)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer id = getidFromNumber(contactNumber);

        ContentValues contentValues = new ContentValues();
        contentValues.put("pwdMsg",pwdMsg);
        if(id <= 0)
        {

        }
        db.update("settings", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }
    public boolean insertContactsGeneric(String contactNumber,List<String> fields , List<String>values)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        fields.add("ContactNumber");
        values.add(contactNumber);

        ListIterator<String> field = fields.listIterator();
        ListIterator<String> value = values.listIterator();

/*        while(field.hasNext()&&value.hasNext())
        {
            contentValues.put(field.next().toString(),value.next().toString());
        }*/
        while(field.hasNext()&&value.hasNext())
        {
            String curfield = field.next().toString();
            String curValue = value.next().toString();
            if(curfield.intern().equalsIgnoreCase("emergency") == true || curfield.intern().equalsIgnoreCase("locator") == true)
            {
                contentValues.put(curfield,Integer.parseInt(curValue));
            }
            else {
                contentValues.put(curfield, curValue);
            }
        }
        if(contentValues.size() > 0)
        {
            db.insert("allowedContacts", null, contentValues);
        }
        return true;
    }
    public boolean updateContactsGeneric(String contactNumber,List<String> fields , List<String>values)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer id = getidFromNumber(contactNumber);
        if(id <= 0) {
            return insertContactsGeneric(contactNumber,fields, values);

        }
        ContentValues contentValues = new ContentValues();
        ListIterator<String> field = fields.listIterator();
        ListIterator<String> value = values.listIterator();
        while(field.hasNext()&&value.hasNext())
        {
            String curfield = field.next().toString();
            String curValue = value.next().toString();
            if(curfield.intern().equalsIgnoreCase("emergency") == true || curfield.intern().equalsIgnoreCase("locator") == true)
            {
                contentValues.put(curfield,Integer.parseInt(curValue));
            }
            else {
                contentValues.put(curfield, curValue);
            }
        }
        if(contentValues.size() > 0)
        {
            db.update("allowedContacts",contentValues,"id = ? ", new String[] { Integer.toString(id) } );
        }
        return true;
    }
    public boolean insertSettingsGeneric(String contactNumber,List<String> fields , List<String>values)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        fields.add("ContactNumber");
        values.add(contactNumber);

        ListIterator<String> field = fields.listIterator();
        ListIterator<String> value = values.listIterator();

/*        while(field.hasNext()&&value.hasNext())
        {
            contentValues.put(field.next().toString(),value.next().toString());
        }*/
        while(field.hasNext()&&value.hasNext())
        {
            String curfield = field.next().toString();
            String curValue = value.next().toString();
            if(curfield.intern().equalsIgnoreCase("emergency") == true || curfield.intern().equalsIgnoreCase("locator") == true)
            {
                contentValues.put(curfield,Integer.parseInt(curValue));
            }
            else {
                contentValues.put(curfield, curValue);
            }
        }
        if(contentValues.size() > 0)
        {
            db.insert("settings", null, contentValues);
        }
        return true;
    }


    public boolean updateSettingGeneric(String contactNumber,List<String> fields , List<String>values)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer id = getidFromNumberFromSettings(contactNumber);
        if(id <= 0) {
            return insertSettingsGeneric(contactNumber,fields,values);

        }
        ContentValues contentValues = new ContentValues();
        ListIterator<String> field = fields.listIterator();
        ListIterator<String> value = values.listIterator();
        while(field.hasNext()&&value.hasNext())
        {
            String curfield = field.next().toString();
            String curValue = value.next().toString();
            if(curfield.intern().equalsIgnoreCase("emergency") == true || curfield.intern().equalsIgnoreCase("locator") == true)
            {
                contentValues.put(curfield,Integer.parseInt(curValue));
            }
            else {
                contentValues.put(curfield, curValue);
            }
        }
        if(contentValues.size() > 0)
        {
            db.update("settings",contentValues,"id = ? ", new String[] { Integer.toString(id) } );
        }
        return true;

    }

    public boolean updateSettingswithMockLocation (String contactNumber,String MockLocation)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer id = getidFromNumber(contactNumber);
        ContentValues contentValues = new ContentValues();
        contentValues.put("MockLocation", MockLocation);
        db.update("settings", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public boolean updateSettingswithEmergencyContact (String contactNumber,String emergencyContact1,String emergencyContact2,String emergencyContact3)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer id = getidFromNumber(contactNumber);
        ContentValues contentValues = new ContentValues();
        contentValues.put("EmergencyContact1", emergencyContact1);
        contentValues.put("EmergencyContact1", emergencyContact2);
        contentValues.put("EmergencyContact1", emergencyContact3);
        db.update("settings", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public boolean updateSettings (String contactNumber,String emergencyContact1,String emergencyContact2,String emergencyContact3,String MockLocation , String pwdMsg,Integer emergency,Integer locator)
    {        SQLiteDatabase db = this.getWritableDatabase();
        Integer id = getidFromNumber(contactNumber);
        ContentValues contentValues = new ContentValues();
        contentValues.put("MockLocation", MockLocation);
        contentValues.put("pwdMsg", pwdMsg);
        contentValues.put("EmergencyContact1", emergencyContact1);
        contentValues.put("EmergencyContact2", emergencyContact2);
        contentValues.put("EmergencyContact3", emergencyContact3);
        contentValues.put("locator", locator);
        contentValues.put("emergency", emergency);
        if(id == 0)
            insertSettingsContact(contactNumber,
                    pwdMsg,
                    locator,
                    emergency,
                    MockLocation,
                    emergencyContact1,
                    emergencyContact2,
                    emergencyContact3);
        else
            db.update("settings", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

}



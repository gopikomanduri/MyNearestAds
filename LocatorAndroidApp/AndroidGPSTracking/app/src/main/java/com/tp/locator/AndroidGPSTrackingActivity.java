package com.tp.locator;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.tp.locator.Events.TrackEvent;
import com.tp.locator.PhoneUtil.PhoneContact;
import com.tp.locator.PhoneUtil.libphonenumber.src.com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.tp.locator.PhoneUtil.libphonenumber.src.com.google.i18n.phonenumbers.Phonenumber;
import com.tp.locator.Settings.SettingsClass;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.greenrobot.event.EventBus;

public class AndroidGPSTrackingActivity extends Activity implements AdapterView.OnItemClickListener {
	
	Button btnShowLocation;

    ContactAdapter objAdapter;

    ContactAdapter searchObjAdapter;


    private ListView listView;

    EditText inputSearch;

    private Button trackbutton;

    private Button updatebutton;

    public static DBWrapper dbObj;// = new DBWrapper()

    public static Context applicationCtx;


    private List<com.tp.locator.allowedContacts> list = new ArrayList<com.tp.locator.allowedContacts>();

    private List<com.tp.locator.allowedContacts> searchlist = new ArrayList<com.tp.locator.allowedContacts>();

    HashMap<String,allowedContacts> localObjects = new HashMap<String, allowedContacts>();


	
	// GPSTracker class
	GPSTracker gps;

    android.content.ContentResolver cr;

    String MyDetailsURL  = "content://com.tp.locator.provider.allowedContacts/Contacts";

    Uri contactsUri = Uri.parse(MyDetailsURL);
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationCtx = this;
        SensorHandler sensorObj = new SensorHandler(applicationCtx);
       // sensorObj.setCtx(applicationCtx);
        if(ContactContentProvider.database == null) {
            ContactContentProvider.database = new DBWrapper(this, "GpSTracker.db");
            ContactContentProvider.database.getDatabase();
            //dbObj =
        }
        dbObj = ContactContentProvider.database;
      /*  else
        {
         SQLiteDatabase db = ContactContentProvider.database.getDatabase();
            ContactContentProvider.database.onUpgrade(db,0,1);
        }*/
        cr = this.getContentResolver();
     //   ActiveAndroid.initialize(this);
        //MySmsReceiver.setCtx(this);
        setContentView(R.layout.main);


        objAdapter = new ContactAdapter(
                this, R.layout.alluser_row, searchlist);

        listView = (ListView) this.findViewById(R.id.Contactslist);
        inputSearch = (EditText) findViewById(R.id.inputSearch);

        MySmsReceiver.getSettings();

        searchObjAdapter = new ContactAdapter(
                this, R.layout.alluser_row, searchlist);

        //  listView.setOnItemClickListener(this);

        listView.setAdapter(objAdapter);


        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                //AndroidGPSTrackingActivity.this.objAdapter.getFilter().filter(cs);
                searchlist.clear();
                Integer stringlen = cs.length();
                int count = list.size();
                String phoneContact;
                String searchStr = cs.toString();
                searchStr = searchStr.toUpperCase();
                for(int i =0;i<count;++i)
                {

                    phoneContact =list.get(i).contactName.toUpperCase();
                    if(stringlen < phoneContact.length()) {
                        if (phoneContact.contains(searchStr) == true) {
                            searchlist.add(list.get(i));
                        }
                    }
                }
              //  listView.setAdapter(searchObjAdapter);
                searchObjAdapter.notifyDataSetChanged();


            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });






        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();

            }});

        Cursor c = this.managedQuery(contactsUri, null, null, null, null);
        int rowsCnt = c.getCount();

        if(rowsCnt != 0) {
            while(c.moveToNext()){
                int x = c.getColumnIndex("ContactNumber");
                String number = c.getString(x);
                x =  c.getColumnIndex("ContactName");
                String name = c.getString(x);
                x = c.getColumnIndex("registeredMsg");

                String regitMsg = c.getString(x);

                x = c.getColumnIndex("allowedStartTime");
                String allowedStartTime = c.getString(x);

                int tempMock =  c.getInt(c.getColumnIndex("isMockAllowed"));


                allowedContacts obj = new allowedContacts(number,
                        c.getString(c.getColumnIndex("ContactName")),
                        c.getString(c.getColumnIndex("registeredMsg")),
                        c.getString(c.getColumnIndex("allowedStartTime")),
                        c.getString(c.getColumnIndex("allowedEndTime")),
                        tempMock,
                        c.getString(c.getColumnIndex("mockStartTime")),
                        c.getString(c.getColumnIndex("mockEndTime")),
                        c.getString(c.getColumnIndex("mockMsg"))
                );
                String hashStr = sha1Hash(number);
                // Integer hashInt = Integer.parseInt(hashStr);
                list.add(obj);
                searchlist.add(obj);
                localObjects.put(hashStr, obj);
                MySmsReceiver.allowedContactsObj.put(number, obj);
            }


        }
       UpdateListWithPhoneContacts();

/*
        for(int i = 0;i<15;++i)
        {
            list.add(getTestMsg());
        }
*/



        final Context context = this.getApplicationContext();

     /*   trackbutton = (Button) this.findViewById(R.id.trackHim);

        trackbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                MySmsReceiver.sendSmsMsg("w r u",);
            }

        });*/


       updatebutton = (Button) this.findViewById(R.id.UpdateContacts);

       updatebutton.setOnClickListener(new View.OnClickListener()
       {

           @Override
           public void onClick(View arg0) {
               startActivity(getPackageManager().getLaunchIntentForPackage("com.google.android.apps.maps"));

/*               Set<allowedContacts> addedCnts = ContactAdapter.getLatestAddedItems();
               Set<allowedContacts> removedCnts = ContactAdapter.getLatestRemovedItems();
               Iterator<allowedContacts> cnts = addedCnts.iterator();
               allowedContacts temp;
               while (cnts.hasNext()) {
                   temp = cnts.next();
                   ContentValues contentValues = new ContentValues();
                   contentValues.put("contactnumber", temp.contactNumber);
                   contentValues.put("contactname", temp.contactName);
                   contentValues.put("allowedStartTime", temp.allowedStartTime);
                   contentValues.put("allowedEndTime", temp.allowedEndTime);
                   contentValues.put("registeredMsg", temp.registeredMsg);
                   contentValues.put("isMockAllowed", temp.isMockAllowed);
                   contentValues.put("mockStartTime", temp.mockStartTime);
                   contentValues.put("mockEndTime", temp.mockEndTime);
                   contentValues.put("mockMsg", temp.mockMsg);

                   Uri uri = cr.insert(
                           Uri.parse("content://com.tp.locator.provider.allowedContacts/Contacts"), contentValues);
                }
                cnts = removedCnts.iterator();*/
              }

        });


        MySmsReceiver mockHandler = new MySmsReceiver();
        MySmsReceiver.smsHandler objHandler = mockHandler.getsmsHandler();
        objHandler.context = ContactAdapter.contactAdapterCtx;
        TrackEvent objevent = new TrackEvent();
        smsParams param = new smsParams();
        param.isSms = 0;
        param.wireParams= new ParseMsgParams();
        param.wireParams.fromNUm = "00000";
        param.wireParams.isWire = 1;
        param.ctx = this.getApplicationContext();
        //new MySmsReceiver();
        objevent.param = param;
      //  GCMNotificationIntentService.numsList.add("00000");

        EventBus.getDefault().post(objevent);
    }
    private allowedContacts getTestMsg()
    {
        allowedContacts obj = new allowedContacts(
                "8179442558",
                "gopi",
                "w r u",
                "12321",
                "12321",
                0,
                "12321",
                "12321",
                "test");
        return obj;
    }

    private void UpdateListWithPhoneContacts() {
        List<PhoneContact> dbObjects = getNumbersFromPhone();
        Iterator<PhoneContact> finalNumbersIterator = dbObjects.iterator();
        PhoneContact temp;
        while (finalNumbersIterator.hasNext()) {
            temp = finalNumbersIterator.next();
            String hashStr = sha1Hash(temp.contactNumber);
            //Integer hashInt = Integer.parseInt(hashStr);
            if (localObjects.containsKey(hashStr) == false) {
                allowedContacts obj = new allowedContacts(temp.contactNumber, temp.contactName, "w r u", "", "", 0, "", "", "");
                list.add(obj);
                searchlist.add(obj);
            }

        }
    }
    private String parseNumber(String number)
    {
        String temp = "";
        String tempStr = "";
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        boolean isValidNumber = true;
        Phonenumber.PhoneNumber numberProto;
        try {
            numberProto = phoneUtil.parse(number, "");
        }
        catch(Exception ex)
        {
            isValidNumber = false;
            temp = "";

            int x = 20;
        }
        if(isValidNumber == true) {
            int earlierIndex = 0;
            int i =0;
            for (; i < number.length(); ++i) {
                char ch = number.charAt(i);
                if (ch == '+' || ('0' <= ch && ch <= '9')) {

                } else {
                    tempStr = number.substring(earlierIndex, i);
                    temp += tempStr;
                    tempStr = "";
                    earlierIndex = i + 1;
                }

            }
            if(i == number.length())
            {
                if(earlierIndex < i-1)
                    tempStr = number.substring(earlierIndex, i);
            }

        }
        if(tempStr.length() > 0)
            temp += tempStr;
        return temp;

    }
    private List<PhoneContact> getNumbersFromPhone()
    {
        List<PhoneContact> numbersList = new ArrayList<PhoneContact>();
        String countryCode = Utils.getCountryCode(this);

        Cursor phones = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                null, null);
        while (phones.moveToNext()) {

            String name = phones
                    .getString(phones
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            String phoneNumber = phones
                    .getString(phones
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            if(phoneNumber.length() > 5) {
                if (phoneNumber.startsWith("+") == false) {

                    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                    Integer isdcode = phoneUtil.getCountryCodeForRegion(countryCode.toUpperCase());
                    if(phoneNumber.startsWith("0") == true)
                    {
                        String tempNum = phoneNumber.substring(1);
                        phoneNumber = tempNum;
                    }
                    phoneNumber = "+" + isdcode.toString() + phoneNumber;
                }

                phoneNumber = parseNumber(phoneNumber);

                PhoneContact obj = new PhoneContact();
                obj.contactName = name;
                obj.contactNumber = phoneNumber;
                numbersList.add(obj);
            }
        }
            return numbersList;
    }
    private void UpdateNumbers()
    {

        HashMap<String,String> contactsFromPhone = new HashMap<String, String>();
        HashMap<String,String> contactsFromContactDB = new HashMap<String, String>();

        String URL = "content://com.tp.locator.provider.allowedContacts/Contacts";



        Cursor c = this.managedQuery(contactsUri, null, null, null, null);
        int rowsCnt = 0;
        if(c != null)
            rowsCnt = c.getCount();
    }


    String sha1Hash( String toHash )
    {
        String hash = null;
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = toHash.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();

            // This is ~55x faster than looping and String.formating()
            hash = bytesToHex( bytes );
        }
        catch( NoSuchAlgorithmException e )
        {
            e.printStackTrace();
        }
        catch( UnsupportedEncodingException e )
        {
            e.printStackTrace();
        }
        return hash;
    }

    // http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex( byte[] bytes )
    {
        char[] hexChars = new char[ bytes.length * 2 ];
        for( int j = 0; j < bytes.length; j++ )
        {
            int v = bytes[ j ] & 0xFF;
            hexChars[ j * 2 ] = hexArray[ v >>> 4 ];
            hexChars[ j * 2 + 1 ] = hexArray[ v & 0x0F ];
        }
        return new String( hexChars );
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


    }
}
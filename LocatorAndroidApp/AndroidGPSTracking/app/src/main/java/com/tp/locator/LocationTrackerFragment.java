package com.tp.locator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.tp.locator.Events.HandShake;
import com.tp.locator.Events.TrackEvent;
import com.tp.locator.Events.UpdateUI;
import com.tp.locator.PhoneUtil.PhoneContact;
import com.tp.locator.PhoneUtil.libphonenumber.src.com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.tp.locator.PhoneUtil.libphonenumber.src.com.google.i18n.phonenumbers.Phonenumber;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;


import java.util.Iterator;
import java.util.List;
import tech.gusavila92.websocketclient.*;

import de.greenrobot.event.EventBus;


/**
 * Created by user on 7/23/2015.
 */
public class LocationTrackerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    Button btnShowLocation;

    ContactAdapter objAdapter;

    ContactAdapter searchObjAdapter;

    public static Boolean isLaunching;

    private ListView listView;

    EditText inputSearch;

    private Button trackbutton;

    private Button updatebutton;

    public static DBWrapper dbObj;// = new DBWrapper()

    public static Context applicationCtx;

    public static HashMap<String,Integer> updatedWithPermission = new HashMap<String, Integer>();



    private List<allowedContacts> list = new ArrayList<allowedContacts>();

    private List<com.tp.locator.allowedContacts> searchlist = new ArrayList<com.tp.locator.allowedContacts>();

    public static HashMap<String,allowedContacts> localObjects = new HashMap<String, allowedContacts>();

    Activity activity;


    // GPSTracker class
    static GPSTracker gps;

    android.content.ContentResolver cr;

    String MyDetailsURL  = "content://com.tp.locator.provider.allowedContacts/Contacts";

    Uri contactsUri = Uri.parse(MyDetailsURL);

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        isLaunching = false;
        View rootView = inflater.inflate(R.layout.main, container, false);
        return rootView;
    }
    public void initialise()
    {
        Log.d("Gopi..","In initialize");
        if(TabsPagerAdapter.isLoading == true) {
            TabsPagerAdapter.isLoading = false;

            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
         //   EventBus.getDefault().register(this);
        }
        {
            applicationCtx = activity;
            gps = new GPSTracker(activity.getApplicationContext());
            MySmsReceiver.logger.info("GPSTRacker is initialized");
            SensorHandler sensorObj = new SensorHandler(applicationCtx);
            // sensorObj.setCtx(applicationCtx);
            if(ContactContentProvider.database == null) {
                MySmsReceiver.logger.info("GpSTracker.db is initialized");
                ContactContentProvider.database = new DBWrapper(activity, "GpSTracker.db");
                ContactContentProvider.database.getDatabase();
                //dbObj =
            }
            dbObj = ContactContentProvider.database;
      /*  else
        {
         SQLiteDatabase db = ContactContentProvider.database.getDatabase();
            ContactContentProvider.database.onUpgrade(db,0,1);
        }*/
            cr = applicationCtx.getContentResolver();
            objAdapter = new ContactAdapter(
                    activity, R.layout.alluser_row, list);

            listView = (ListView) activity.findViewById(R.id.Contactslist);
            inputSearch = (EditText) activity.findViewById(R.id.inputSearch);


            searchObjAdapter = new ContactAdapter(
                    activity, R.layout.alluser_row, searchlist);

            //  listView.setOnItemClickListener(this);

            listView.setAdapter(objAdapter);
            //     GPSTracker gpsObj = new GPSTracker(activity);

            {
                Intent intent = new Intent(activity,DataService.class);
                activity.startService(intent);
            }

            // if(Utils.isMyServiceRunning(DataService.class,activity) == false)
            {
                Intent intent = new Intent(activity,ContactUpdateService.class);
                activity.startService(intent);
            }
            Log.d("Gopi..","In initialize  before invoking updateItems");
//            GPSTracker tempTracker = new GPSTracker(this.activity.getBaseContext());
//            tempTracker.CheckEnableGPS();
//            tempTracker.getLatnLong();


            MySmsReceiver mockHandler = new MySmsReceiver();
            MySmsReceiver.smsHandler objHandler = mockHandler.getsmsHandler();
            objHandler.context = ContactAdapter.contactAdapterCtx;
            TrackEvent objevent = new TrackEvent();
            smsParams param = new smsParams();
            param.isSms = 0;
            param.wireParams= new ParseMsgParams();
            param.wireParams.fromNUm = "00000";
            param.wireParams.isWire = 1;
            param.ctx = activity.getApplicationContext();
            //new MySmsReceiver();
            objevent.param = param;
            //GCMNotificationIntentService.numsList.add("00000");

            EventBus.getDefault().post(objevent);


            updateItems();
        }

    }

    public void updateItems()
    {
        Log.d("Gopi..","In initialize  invoking updateItems");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.d("Gopi..","In initialize  invoking updateItems  .. searchlist is "+String.valueOf(searchlist.size()));
                // change UI elements here
                if(DataService.newItems.size() > 0 && searchlist.size() == 0)
                    EventBus.getDefault().post(new HandShake());
                if(searchlist.size() > 0)
                {
                    List<com.tp.locator.allowedContacts> temp = new ArrayList<allowedContacts>();
                    for(int i=0;i<list.size();++i) {
                        temp.add(list.get(i));
                    }
                    list.clear();;
                    addItems(temp);
                }
            }
        });
    }



//    WebSocketClient webSocketClient;
//
//    private void createWebSocketClient() {
//        URI uri;
//        try {
//            uri = new URI("ws://162.222.177.34:9090");
//        }
//        catch (URISyntaxException e) {
//            e.printStackTrace();
//            return;
//        }
//
//        webSocketClient = new WebSocketClient(uri) {
//            @Override
//            public void onOpen() {
//                System.out.println("onOpen");
//                webSocketClient.send("Hello, World!");
//            }
//
//            @Override
//            public void onTextReceived(String message) {
//                System.out.println("onTextReceived");
//            }
//
//            @Override
//            public void onBinaryReceived(byte[] data) {
//                System.out.println("onBinaryReceived");
//            }
//
//            @Override
//            public void onPingReceived(byte[] data) {
//                System.out.println("onPingReceived");
//            }
//
//            @Override
//            public void onPongReceived(byte[] data) {
//                System.out.println("onPongReceived");
//            }
//
//            @Override
//            public void onException(Exception e) {
//                System.out.println(e.getMessage());
//            }
//
//            @Override
//            public void onCloseReceived() {
//                System.out.println("onCloseReceived");
//            }
//        };
//
//      //  webSocketClient.setConnectTimeout(10000);
//      //  webSocketClient.setReadTimeout(60000);
//        webSocketClient.addHeader("contact", dbObj.getMyNumber());
//    //    webSocketClient.enableAutomaticReconnection(5000);
//        webSocketClient.connect();
//    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            //MyLogger.setup();
        } catch (Exception e) {
        int x =20;
        }

        activity = getActivity();

        EventBus.getDefault().post(new HandShake());
        initialise();


      //  if(Utils.isMyServiceRunning(ContactUpdateService.class,activity) == false)


        Log.v("ContactsFragment", "onActivityCreated()");


      //  super.onCreate(savedInstanceState);

        //   ActiveAndroid.initialize(this);
        //MySmsReceiver.setCtx(this);
    //    activity.setContentView(R.layout.main);

        /*objAdapter = new ContactAdapter(
                activity, R.layout.alluser_row, searchlist);

        listView = (ListView) activity.findViewById(R.id.Contactslist);
        inputSearch = (EditText) activity.findViewById(R.id.inputSearch);


        searchObjAdapter = new ContactAdapter(
                activity, R.layout.alluser_row, searchlist);


        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // change UI elements here
                if(searchlist.size() > 0)
                {
                    List<com.tp.locator.allowedContacts> temp = new ArrayList<allowedContacts>();
                    for(int i=0;i<searchlist.size();++i) {
                        temp.add(searchlist.get(i));
                    }
                    searchlist.clear();;
                    addItems(temp);
                }
            }
        });*/

      /*  listView.setAdapter(searchObjAdapter);
        searchObjAdapter.notifyDataSetChanged();
        objAdapter.notifyDataSetChanged();*/

        //addItems();
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(final CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                //AndroidGPSTrackingActivity.this.bjAdapter.getFilter().filter(cs);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // change UI elements here
                        searchlist.clear();
                        Integer stringlen = cs.length();
                        int count = list.size();
                        String phoneContact;
                        String searchStr = cs.toString();
                        searchStr = searchStr.toUpperCase();
                        for (int i = 0; i < count; ++i) {

                            phoneContact = list.get(i).contactName.toUpperCase();
                            if (stringlen < phoneContact.length()) {
                                if (phoneContact.contains(searchStr) == true) {
                                    searchlist.add(list.get(i));
                                }
                            }
                        }
                          listView.setAdapter(searchObjAdapter);
                        searchObjAdapter.notifyDataSetChanged();
                    }
                });



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

            }
        });

    //    new LocationTrackerFragmentAsyncdataLoader().execute();


/*
        for(int i = 0;i<15;++i)
        {
            list.add(getTestMsg());
        }
*/



        final Context context = activity.getApplicationContext();

     /*   trackbutton = (Button) this.findViewById(R.id.trackHim);

        trackbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                MySmsReceiver.sendSmsMsg("w r u",);
            }

        });*/


        updatebutton = (Button) activity.findViewById(R.id.UpdateContacts);




        updatebutton.setOnClickListener(new View.OnClickListener() {

            String destUri = "ws://35.196.222.199:8080";

            @Override
            public void onClick(View v) {

                if(LocationTrackerFragment.dbObj == null)
                {
                    if(ContactContentProvider.database == null) {
                        ContactContentProvider.database = new DBWrapper(applicationCtx, "GpSTracker.db");
                        ContactContentProvider.database.getDatabase();
                        //dbObj =
                    }
                    LocationTrackerFragment.dbObj = ContactContentProvider.database;

                }
                if(LocationTrackerFragment.dbObj != null) {
                    LocationTrackerFragment.dbObj.clearLogs();
                    LocationFragment.locationadapter.notifyDataSetInvalidated();
                    LocationFragment.locationadapter.notifyDataSetChanged();
                }
            }
        });
    }




    private class LocationTrackerFragmentAsyncdataLoader extends AsyncTask<Void,Void,Void>
    {

        private final ProgressDialog dialog = new ProgressDialog(activity);

        @Override
        protected void onPreExecute() {
          //  super.onPreExecute();
            dialog.setMessage("Fetching History from Database...");
            dialog.show();
        }


        @Override
        protected void onPostExecute(Void res) {
        //    super.onPostExecute(res);
            dialog.dismiss();
            // adpt.setItemList(result);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // change UI elements here
                    searchObjAdapter.notifyDataSetChanged();
                    objAdapter.notifyDataSetChanged();
                }
            });


        }


        @Override
        protected Void doInBackground(Void... strings) {
            updateContacts();
            return null;
        }
    }
    public void updateContacts()
    {

        if(searchlist.size() == 0 && list.size() == 0) {
//        searchlist.clear();
            //      list.clear();
            //   Cursor c = activity.managedQuery(contactsUri, null, null, null, null);
            Cursor c = activity.getContentResolver().query(contactsUri, null, null,
                    null, null);
            int rowsCnt = c.getCount();

            if (rowsCnt != 0) {
                MySmsReceiver.logger.info("Fetching contacts from phone database");
                while (c.moveToNext()) {
                    int x = c.getColumnIndex("ContactNumber");
                    String number = c.getString(x);
                    x = c.getColumnIndex("ContactName");
                    String name = c.getString(x);
                    x = c.getColumnIndex("registeredMsg");

                    String regitMsg = c.getString(x);

                    x = c.getColumnIndex("allowedStartTime");
                    String allowedStartTime = c.getString(x);

                    int tempMock = c.getInt(c.getColumnIndex("isMockAllowed"));


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
                    //MySmsReceiver.allowedContactsObj.put(number, obj);
                    MySmsReceiver.updateContacts(applicationCtx);
                }


            }
            c.close();
            UpdateListWithPhoneContacts();
        }
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

    private allowedContacts enableMock()
    {
        PhoneContact obj = new PhoneContact();
        obj.contactName = "Mock testing";
        obj.contactNumber = "+910000000000";
        allowedContacts tempObj = new allowedContacts(
                obj.contactNumber,
                obj.contactName,
                "w r u",
                "12321",
                "12321",
                0,
                "12321",
                "12321",
                "test");
        return tempObj;
    }

    private void UpdateListWithPhoneContacts() {
        List<PhoneContact> dbObjects = getNumbersFromPhone();
      //  dbObjects.add(enableMock());
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
     //   MySmsReceiver.logger.info("In number parser. number is "+number);
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
        String countryCode = Utils.getCountryCode(applicationCtx);

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



        //Cursor c = activity.managedQuery(contactsUri, null, null, null, null);
        Cursor c = activity.getContentResolver().query(contactsUri, null, null,
                null, null);
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    public void addItems(List<allowedContacts> obj)
    {

        list.clear();
        searchlist.clear();
        Log.d("Gopi","size of obj is "+String.valueOf(obj.size()));
        for(int i=0;i<obj.size();++i)
        {
            String number = obj.get(i).contactNumber;
            Integer allowedVal = 0;
            if(obj.get(i).isAllowed.isEmpty() == false && (obj.get(i).isAllowed.equalsIgnoreCase("0")||obj.get(i).isAllowed.equalsIgnoreCase("no")))
            {
                allowedVal = 0;
            }
            if(obj.get(i).isAllowed.isEmpty() == false && (obj.get(i).isAllowed.equalsIgnoreCase("1")||obj.get(i).isAllowed.equalsIgnoreCase("yes")))
            {
                allowedVal = 1;
            }
            if(obj.get(i).isAllowed.isEmpty() == false && (obj.get(i).isAllowed.equalsIgnoreCase("2")||obj.get(i).isAllowed.equalsIgnoreCase("mock")))
            {
                allowedVal = 2;
            }
            if(obj.get(i).isAllowed.isEmpty() == true)
            {
                allowedVal = 0;
            }
            if(LocationTrackerFragment.updatedWithPermission.containsKey(number) == true)
            {
                allowedVal = LocationTrackerFragment.updatedWithPermission.get(number);
            }

           if(allowedVal == 0)
           {
               obj.get(i).isAllowed = "No";
           }
            else   if(allowedVal == 1) {
               obj.get(i).isAllowed = "Yes";
           }
            else
           {
               obj.get(i).isAllowed = "Mock";
           }
            if(allowedVal > 0)
            {

                    allowedContacts temp = obj.get(i);
                    MySmsReceiver.allowedContactsObj.put(temp.contactNumber,temp);

            }
            searchlist.add(obj.get(i));
          //  searchlist.add(getTestMsg());
            //  list.add(getTestMsg());
            list.add(obj.get(i));
        //    searchlist = obj;
        }
      //  if(TabsPagerAdapter.isLoading == true)
           // list.add(enableMock());
        listView.setAdapter(objAdapter);
     //   listView.setAdapter(searchObjAdapter);
        searchObjAdapter.notifyDataSetChanged();
        objAdapter.notifyDataSetChanged();
    }
    public void onEvent(UpdateUI event)
    {
         //newItems = PhoneContactsUpdateEvent.addedContacts;
       final List<allowedContacts> obj = DataService.addedItems;
       // for(int i=0;i<obj.size();++i)
         //   searchlist.add(obj.get(i));

       // searchlist = obj;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // change UI elements here
                addItems(obj);
            }
        });
   //     addItems();



        int x = 20;
        Log.d("test","gopi "+String.valueOf(x));
    }

   /* @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


    }*/
}

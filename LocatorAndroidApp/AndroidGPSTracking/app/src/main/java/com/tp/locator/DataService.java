package com.tp.locator;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.tp.locator.Events.HandShake;
import com.tp.locator.Events.PhoneContactsUpdateEvent;
import com.tp.locator.Events.UpdateUI;
import com.tp.locator.PhoneUtil.PhoneContact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by user on 8/1/2015.
 */
public class DataService extends Service {
    android.content.ContentResolver cr;

    Context ctx;

    public  static  Context m_ctx;

    String MyDetailsURL  = "content://com.tp.locator.provider.allowedContacts/Contacts";

    public static List<allowedContacts> appDblist = new ArrayList<allowedContacts>();

    public static HashMap<String,allowedContacts> appDbObjects = new HashMap<String, allowedContacts>();
    public static HashMap<String , String> fromPhone = new HashMap<String, String>();
    public  static HashMap<String,String> fromDb = new HashMap<String, String>();
    public static List<PhoneContact> newItems = new ArrayList<PhoneContact>();
    public static List<allowedContacts> addedItems = new ArrayList<allowedContacts>();
    public static List<String> existingItemsFromappDB = new ArrayList<String>();
    public static DBWrapper dbObj;// = new DBWrapper()


    Uri contactsUri = Uri.parse(MyDetailsURL);
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //EventBus.getDefault().register(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        //super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful

        ctx = this.getBaseContext();
       m_ctx  = this.getBaseContext();
      //  new AsyncdataLoader().execute();
        if(ContactContentProvider.database == null) {
            MySmsReceiver.logger.info("GpSTracker.db is initialized");
            ContactContentProvider.database = new DBWrapper(ctx, "GpSTracker.db");
            dbObj = ContactContentProvider.database;
            //dbObj =
        }
        return Service.START_STICKY;
    }
    public void getDbContacts() {


//        searchlist.clear();
            //      list.clear();
            //   Cursor c = activity.managedQuery(contactsUri, null, null, null, null);
            Cursor c = m_ctx.getContentResolver().query(contactsUri, null, null,
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
                    String hashStr = Utils.sha1Hash(number);
                   if(obj.allowedStartTime.isEmpty() == false)
                   {
                       Integer i = 1;
                       fromPhone.put(hashStr,i.toString());
                   }


                }


            }
            c.close();

    }

    public void onEvent(PhoneContactsUpdateEvent event)
    {
        newItems = PhoneContactsUpdateEvent.addedContacts;
        Iterator<PhoneContact> obj = newItems.iterator();
        List<PhoneContact> newItemsTemp = new ArrayList<PhoneContact>();
        while(obj.hasNext())
        {
            PhoneContact temp = obj.next();
            fromPhone.put(temp.shaValue, "");
        }
        getDbContacts();
        obj = newItems.iterator();
        while(obj.hasNext())
        {
            PhoneContact temp = obj.next();
            Integer i;
            String val = fromPhone.get(temp.shaValue);
            if(val.isEmpty() == false)
             i = Integer.valueOf(val);
            else
             i = 0;

            temp.isallow = i;
            newItemsTemp.add(temp);
        }
        newItems.clear();
        obj = newItemsTemp.iterator();
        while(obj.hasNext()) {
            newItems.add(obj.next());
        }


        InvokeupdateUI();

    }
    public void onEvent(HandShake event)
    {
        //newItems = PhoneContactsUpdateEvent.addedContacts;
        InvokeupdateUI();

    }
    private static allowedContacts enableMock()
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
    public static void InvokeupdateUI()
    {
        Log.d("Gopi..","In update UI of Dataservice .. newItems size is "+String.valueOf(newItems.size())+"   addeditems size is "+String.valueOf(addedItems.size()));
        //Gopi
        //Gopi.. test
        addedItems.clear();
        addedItems.add(enableMock());
        // enable too get contacts .. gopi
        for(int i=0;i<newItems.size();++i)
        {
            if(appDbObjects.containsKey(newItems.get(i).shaValue) == false)
            {
                allowedContacts obj = new allowedContacts(newItems.get(i).contactNumber, newItems.get(i).contactName, "w r u", newItems.get(i).isallow.toString(), "", 0, "", "", "");
                addedItems.add(obj);
            }
            if(newItems.get(i).isallow.toString().equalsIgnoreCase("yes") || newItems.get(i).isallow.toString().equalsIgnoreCase("Mock"))
            {
                allowedContacts temp = appDbObjects.get(newItems.get(i).shaValue);
                MySmsReceiver.allowedContactsObj.put(newItems.get(i).contactNumber,temp);
            }
        }

        Log.d("Gopi..","before posting UpdateUI event addedItems size is  "+String.valueOf(addedItems.size()));

     //   addedItems.add(enableMock());
    //    EventBus.getDefault().post(new UpdateUI(addedItems));
        EventBus.getDefault().post(new UpdateUI());
    }
    public static void addedNewContact(allowedContacts obj)
    {
        String sha = Utils.sha1Hash(obj.contactNumber);
        appDbObjects.put(sha, obj);
        AddUpdateObj addObj = new AddUpdateObj();
        addObj.obj = obj;
        addObj.isAdd = true;
        new AsyncDataInsert().execute(addObj);
    }
    public static void updateContact(String contactNumber,List<String> fields , List<String>values)
    {
        AddUpdateObj obj = new AddUpdateObj();
        obj.obj = new allowedContacts();
        obj.obj.contactNumber = contactNumber;
        obj.isAdd = false;
        obj.fields = fields;
        obj.values = values;
        new AsyncDataInsert().execute(obj);
    }
    public static class AddUpdateObj
    {
        allowedContacts obj;
        Boolean isAdd;
        List<String> fields;
        List<String>values;
    };
    private static class AsyncDataInsert extends AsyncTask<AddUpdateObj,Void,Void>
    {

        @Override
        protected Void doInBackground(AddUpdateObj... params) {
            AddUpdateObj obj = params[0];
            if(obj.isAdd)
                dbObj.insertContact(obj.obj.contactNumber,obj.obj.contactName,
                        "yes",
                        "",
                        "w r u",
                        obj.obj.isMockAllowed,
                        "",
                        "",
                        "temp");
            else
            dbObj.updateSettingGeneric(obj.obj.contactNumber,obj.fields,obj.values);
            return null;
        }
    }

    private class AsyncdataLoader extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... params) {
            getContacts();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
         //   updateUI();
        }

        private void getContacts()
        {
            getContactsFromAppDB();
        }
        private void getContactsFromAppDB()
        {
            Cursor c = ctx.getContentResolver().query(contactsUri, null, null,
                    null, null);
            int rowsCnt = c.getCount();

            if (rowsCnt != 0) {
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

                    String hashStr = Utils.sha1Hash(number);
                    Log.d("GopiHash","hashstr for number = "+number+"  is hashStr"+hashStr);
                    // Integer hashInt = Integer.parseInt(hashStr);
                    existingItemsFromappDB.add(hashStr);
                    appDblist.add(obj);
                    appDbObjects.put(hashStr, obj);

                }


            }
        }

    }

}

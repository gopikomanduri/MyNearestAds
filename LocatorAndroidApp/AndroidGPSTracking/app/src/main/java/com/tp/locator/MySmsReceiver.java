package com.tp.locator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.telephony.gsm.SmsMessage;
import android.text.format.DateFormat;
import android.util.Log;

import com.tp.locator.Events.TrackEvent;
import com.tp.locator.Events.contactsUpdated;
import com.tp.locator.Events.sendLocation;
import com.tp.locator.Events.settings;
import com.tp.locator.PhoneUtil.DateTime;
import com.tp.locator.Settings.SettingsClass;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

import de.greenrobot.event.EventBus;

import static com.tp.locator.DataService.dbObj;

/**
 * Created by user on 5/23/2015.
 */
public class MySmsReceiver extends BroadcastReceiver implements IsmsSender {


    //static Context ctx;
    static String fromAddress;
    private static final String TAG = "MySmsReceiver";
   static ArrayList<String> receivedMsgsFrom = new ArrayList<String>();
    static HashMap<String,allowedContacts> allowedContactsObj =  new HashMap<String, allowedContacts>();
    static String message;

   static  String MyDetailsURL  = "content://com.tp.locator.provider.allowedContacts/Contacts";

    static Uri contactsUri = Uri.parse(MyDetailsURL);

    static String MySettingsURL  = "content://com.tp.locator.provider.allowedContacts/settings";

    static Uri settingsUri = Uri.parse(MySettingsURL);

    static String dbpwdMsg = "";
    static GPSTracker gps ;
    static String dbLoc = "";

    public static Context usableCtx;

    //static  GPSTracker gps;
    public static ConcurrentLinkedQueue<String> requestsFrom = new ConcurrentLinkedQueue<String>();

   static Integer emergencyT = 0;

  public static  Integer locatorT = 0;
    public static MyLogger obj = new MyLogger();
    //public final static Logger LOGGER  = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public static org.slf4j.Logger logger = LoggerFactory.getLogger(MySmsReceiver.class);
   // logger =
    static boolean isEventRegistered = false;
    public static boolean isEventRegisteredInnerClass = false;

    public MySmsReceiver() {
//        if(isEventRegistered == false) {
//
//
//            EventBus.getDefault().register(this);
//            isEventRegistered = true;
//        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        
    }


/*    public static void setCtx(Context ctx)
    {
        ctx = ctx;
    }*/

    public static void addNumber(String phNumber)
    {

        if(phNumber != null)
        {
            if(receivedMsgsFrom != null)
            {
                if(receivedMsgsFrom.contains(phNumber) == false)
                {
                    receivedMsgsFrom.add(phNumber);
                }
            }
        }
    }
    public static void addMsg(String msg)
    {
        message = msg;
    }
    //private void sendSMS(String phoneNumber, String message)
    private void sendSMS(String msg) {
        MySmsReceiver.logger.info("in sendSMS  .. msg is " + msg);
        String tempMsg;


        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        SmsManager sms = SmsManager.getDefault();
        while ((msg != null) && (msg.length() > 0))
        {
            if(msg.length() >=160) {
                tempMsg = msg.substring(0, 159);
                msg = msg.substring(160);
            }
            else {
                tempMsg = msg;
                msg = "";
            }


            for (Iterator<String> it = receivedMsgsFrom.iterator(); it.hasNext(); ) {
                sms.sendTextMessage(it.next(), null, tempMsg, null, null);

            }
    }
        tempMsg = "";
        receivedMsgsFrom.clear();
    }
    public static void sendSmsMsg(String msg,String number,int isWire)
    {
        IResponder responder;
        if(msg == null)
            msg = new String("Mock Location");
        MySmsReceiver.logger.info("in sendSmsMsg  .. msg is " + msg + "  number is " + number);
        if(isWire == 1)
        {
            responder = new NetworkResponder();
        }
        else
        {
            responder = new SmsResponder();
        }
        DateTime dt;
        responder.sendMsg(number,msg,new DateTime());

    }
    public smsHandler getsmsHandler()
    {
        return new smsHandler();
    }
    public static class smsHandler extends AsyncTask<smsParams ,Void,Void>
    {

       public String smsData = null;
        public boolean bHandled = false;

        String fromNUm;

        public Object[] pdus;// = (Object[]) pudsBundle[0].bundleObj.get("pdus");
        public Context context;// = pudsBundle[0].ctx;
        public IsmsSender smsObj;// = pudsBundle[0].smsObj;
        public SmsMessage messages;// = SmsMessage.createFromPdu((byte[]) pdus[0]);



        public boolean parseMsg(smsParams obj) {
            String smsData;
            int isWire;

/*            if (obj.isSms == 1) {
                isWire = 0;*/


//            if(MySmsReceiver.isEventRegisteredInnerClass == false) {
//                EventBus.getDefault().register(this);
//                MySmsReceiver.isEventRegisteredInnerClass = true;
//            }

            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }


            if(obj == null)
    return false;
                if (obj.isSms == 1) {
                    pdus = (Object[]) obj.bundleObj.get("pdus");
                    context = obj.ctx;
                    smsObj = obj.smsObj;
                    isWire = 0;
                    messages = SmsMessage.createFromPdu((byte[]) pdus[0]);
                    smsData = messages.getMessageBody().toString();
                    fromAddress = messages.getOriginatingAddress();
                    fromNUm = fromAddress;

                    if (smsData.equalsIgnoreCase("w r u")) {

                        if (smsData.contains("#" + dbpwdMsg) == true) {
                            MySmsReceiver.logger.info("in doInBackground message contacins pwd . hence sending .. ");
                            sendSmsMsg(dbLoc, fromNUm, 0);
                            cancel(true);
                            return false;
                        }
                    }
                }
            else
                {
                    if(obj.wireParams == null)
                        return false;
                    fromNUm = obj.wireParams.fromNUm;
                    isWire = 1;
                    context = obj.ctx;
                }
            MySmsReceiver.usableCtx = context;
            requestsFrom.add(fromNUm);
            MySmsReceiver.logger.info("creating settings object to fetch locator value from db");
            new SettingsClass(context);


//                    }
               // }

            //}
            return true;
        }
        public void onEvent(settings obj)
        {
            MySmsReceiver.logger.info("in onevent of settings obj");
            int isWire = 1;// hardcoded to send on wire;

            Integer count = MySmsReceiver.allowedContactsObj.size();

              if(count == 0)
                 updateContacts(context);
            else
                  sendLoc(obj.ctx);


        }
        public void onEvent(contactsUpdated obj)
        {
            MySmsReceiver.logger.info("in onEvent(contactsUpdated obj . before invoking sendLoc");
            //sendLoc(0);
        }
        public void sendLoc(Context ctx) {
            MySmsReceiver.logger.info("in sendLoc");
            Integer count = MySmsReceiver.allowedContactsObj.size();
            allowedContacts allowedContactsobj = null;
            getSettings();
            count = MySmsReceiver.allowedContactsObj.size();

            // keeping for hardcode testing..

            if (locatorT == 0) {
                MySmsReceiver.logger.info("locator is off.. hensing NOT sending lcoation");
                cancel(true);

            }
            MySmsReceiver.logger.info("Gopi", "In sms received count of allowedcontacts is " + count.toString());
            Iterator itfromNum = GCMNotificationIntentService.numsList.iterator();

          //  while ((fromNUm = GCMNotificationIntentService.numsList.poll()) != null)
            while (itfromNum.hasNext())
            {
                fromNUm = itfromNum.next().toString();
                if (dbpwdMsg != null)
                    MySmsReceiver.logger.info("in doInBackground  pwdMsg is " + dbpwdMsg.toString());
            if (MySmsReceiver.allowedContactsObj.containsKey(fromNUm) == true) {
                allowedContactsobj = MySmsReceiver.allowedContactsObj.get(fromNUm);
            }
            bHandled = true;

            if (ContactContentProvider.database == null) {
                ContactContentProvider.database = new DBWrapper(ctx, "GpSTracker.db");
                ContactContentProvider.database.getDatabase();
                dbObj = ContactContentProvider.database;
            }
            if (dbObj == null)
                dbObj = ContactContentProvider.database;

            String currentPhoneNum = dbObj.getMyNumber();

            ///   String currentPhoneNum = SaveSharedPreference.getUserName(SplashActivity.staticSplashCtx);

                if(fromNUm.equals("00000"))
                {
                    Log.i(TAG," if(fromNUm.equals(\"00000\") . so sending loc  ");
                    sendLocation(fromNUm);
                    cancel(true);
                }

            else if (allowedContactsobj == null && currentPhoneNum.equalsIgnoreCase(fromNUm) == false ) {
                    Log.i(TAG," In sms received  allowedcontacts dont have value. returning ");
                    MySmsReceiver.logger.info(" received locationrequest from BLOCKED user.. hence canceling");
                Log.d("Gopi", "In sms received  allowedcontacts dont have value. returning");
                sendSmsMsg("from Num : "+ dbObj.getMyNumber() +"\n"+ "user didn't allow to send his/her location", fromNUm, 1);

                cancel(true);
                // return;

            }

            else if ((allowedContactsobj != null) && (allowedContactsobj.isMockAllowed == 1)) {
                    Log.i(TAG," sending MOCK Location ");
                    MySmsReceiver.logger.info(" sending MOCK Location ");
                sendSmsMsg("from Num : "+ dbObj.getMyNumber() +"\n"+ SettingsClass.dbLoc, fromNUm, 1);
                cancel(true);

            }
            else {
                    Log.i(TAG,"before invoking sendLocation");
                    MySmsReceiver.logger.info("before invoking sendLocation");
                sendLocation(fromNUm);
            }
        }
        }

        public void onEvent(MyAddress str) {
            if(requestsFrom.isEmpty() == true)
                return;
//                task = new MyAsyncTask();
            str = GPSTracker.myLoc;

              String url = "http://maps.google.com/maps/api/geocode/json?latlng=" + str.latitude + "," + str.longitude + "&sensor=true";
            //  MySmsReceiver.logger.severe(" url hitting is " + url);

            //  MySmsReceiver.logger.severe(" initialising comsObject");
            comsObject[] comsObj = new comsObject[1];


            comsObj[0] = new comsObject();
            while (requestsFrom.isEmpty() == false)
            {
                String fromNUm = requestsFrom.remove();
                if (fromNUm.intern().equalsIgnoreCase("+910000000000"))
                    comsObj[0].setIsMock(true);
                else
                    comsObj[0].setIsMock(false);
            comsObj[0].fromNumber = fromNUm;
                comsObj[0].toNumber = dbObj.getMyNumber();
          //  comsObj[0].latitude = String.valueOf(latitude);
          //  comsObj[0].longitude = String.valueOf(longitude);
            String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
            comsObj[0].timeStamp = date;
         //   comsObj[0].setUrl("http://maps.google.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&sensor=true");
            comsObj[0].setSmsObj(smsObj);
            comsObj[0].setContext(context);
            comsObj[0].isWire = 1; // hardcoded to work on internet
            comsObj[0].address =  "from Num : "+ dbObj.getMyNumber() +"\n"+ str.toString();
                comsObj[0].setUrl(url);

            receivedMsgsFrom.add(fromNUm);
            MySmsReceiver.logger.info(" before running execute ");
                ReentrantLock lock = new ReentrantLock();

                lock.lock();
                try {
                    {
//                        if(task == null)
//                            task = new MyAsyncTask();
                      //  if (task.getStatus() != Status.RUNNING)
                        new MyAsyncTask().execute(comsObj);

                    }
                }
                finally {
                    lock.unlock();
                }


            JSONObject location;
            String location_string = "";
        }
        }
        public void sendLocation(String fromNUm)
        {
            MySmsReceiver.logger.info("in sendLocation .. for Number "+fromNUm);
            sendLocation obj = new sendLocation();
            obj.fromNum = fromNUm;
            obj.ctx = context;
         //   EventBus.getDefault().post(obj);

            GPSTracker gps = new GPSTracker(this.context);
            gps.getLocation();


            // check if GPS enabled
            /*
            if (gps.canGetLocation()) {

                MySmsReceiver.logger.severe(" while checking canGetLocation()");
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                MySmsReceiver.logger.severe(" latitude is " + String.valueOf(latitude) + " longitude is " + String.valueOf(longitude));

                //JSONObject ret = gps.getLocationInfo();

                GPSTracker.MyAsyncTask task = new GPSTracker.MyAsyncTask();
                String url = "http://maps.google.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&sensor=true";
                MySmsReceiver.logger.severe(" url hitting is " + url);

                MySmsReceiver.logger.severe(" initialising comsObject");
                comsObject[] comsObj = new comsObject[1];

                MySmsReceiver.logger.severe(" fromAddress is " + fromNUm);
                comsObj[0] = new comsObject();
                if (fromNUm.intern().equalsIgnoreCase("+910000000000"))
                    comsObj[0].setIsMock(true);
                else
                    comsObj[0].setIsMock(false);
                comsObj[0].fromNumber = fromNUm;
                comsObj[0].latitude = String.valueOf(latitude);
                comsObj[0].longitude = String.valueOf(longitude);
                String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
                comsObj[0].timeStamp = date;
                comsObj[0].setUrl("http://maps.google.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&sensor=true");
                comsObj[0].setSmsObj(smsObj);
                comsObj[0].setContext(context);
                comsObj[0].isWire = isWire;
                receivedMsgsFrom.add(fromNUm);
                MySmsReceiver.logger.severe(" before running execute ");
                task.execute(comsObj);

                JSONObject location;
                String location_string = "";

            } else {
                MySmsReceiver.logger.severe(" in else of gps.getLocation in MySms receiver");
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                bHandled = false;
                sendSmsMsg("Sorry , couldn't get the location", fromNUm, isWire);
                // gps.showSettingsAlert();
            }
            */
        }


        @Override
        protected Void doInBackground(smsParams... pudsBundle) {

          //  pdus = (Object[]) pudsBundle[0].bundleObj.get("pdus");

                bHandled = parseMsg(pudsBundle[0]);

            if (bHandled) {
                // gopi below line is commented.. plz verify
              //  abortBroadcast();
            }
            return null;
        }
    }
    public void onEvent(TrackEvent obj)
    {
       // new smsHandler().execute(obj.param);
        MySmsReceiver.smsHandler temp = new smsHandler();
        GPSTracker.msContext = obj.param.ctx;
        temp.sendLoc(obj.param.ctx );
    }
    @Override
    public void onReceive(Context context, Intent intent) {

          //  MySmsReceiver.ctx = context;
    //    if (intent.getAction().equals(android.provider.Telephony.SMS_RECEIVED)) {

        Log.d("Gopi", "In sms received ");
		if(MySmsReceiver.gps == null)
            MySmsReceiver.gps = new GPSTracker(context);
        smsParams smsObjects = new smsParams();
        Bundle pudsBundle = intent.getExtras();
        smsObjects.bundleObj = pudsBundle;
        smsObjects.ctx = context;
        smsObjects.smsObj = this;
        new smsHandler().execute(smsObjects);

      //  }
    }
    public static void updateContacts(Context ctx)
    {
        if(ctx == null)
            return;
        //Cursor c = this.managedQuery(contactsUri, null, null, null, null);
        Log.d("Gopi", "updateContacts called");
        Log.d("Gopi", "Context "+ctx.toString());
        MySmsReceiver.allowedContactsObj.clear();
        Cursor c = ctx.getContentResolver().query(contactsUri,  null, null,
                null, null);
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
/*                String hashStr = sha1Hash(number);
                // Integer hashInt = Integer.parseInt(hashStr);
                list.add(obj);
                localObjects.put(hashStr, obj);*/
                MySmsReceiver.allowedContactsObj.put(number, obj);
            }


        }
        c.close();
        MySmsReceiver.logger.info("in getsettings  before invoking contactsUpdated event");
        EventBus.getDefault().post(new contactsUpdated());
    }
    public static void getSettings()
    {


            String contactNumber = SettingsClass.contactNumber;

            dbpwdMsg = SettingsClass.dbpwdMsg;


            dbLoc = SettingsClass.dbLoc;


            emergencyT = SettingsClass.emergencyT;

        MySmsReceiver.logger.info("in getsettings  SettingsClass.locatorT is "+SettingsClass.locatorT);

            locatorT = SettingsClass.locatorT;


    }
    @Override
    public void sendMsg(String msg) {
            sendSMS(msg);
    }


}

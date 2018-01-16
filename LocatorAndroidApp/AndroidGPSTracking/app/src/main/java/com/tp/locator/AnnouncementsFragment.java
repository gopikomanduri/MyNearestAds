package com.tp.locator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.tp.locator.Events.pendingAnnouncements;


import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tp.locator.PhoneUtil.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.greenrobot.event.EventBus;

import static com.tp.locator.SocketService.messagesToPush;

/**
 * Created by Aruna on 31-10-2017.
 */

public class AnnouncementsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    static Activity activity;
    static AnnouncementsFragment thisObj;
    static AnnouncementFragmentAsyncDataLoader dataDyn = null;
    ListView lview;
    static boolean isExecuted =  false;
    private Button btnPushMsg;
    private EditText announcementMsg;
    private Cursor mCursor;

    static AnnouncementGenAdapter annAdapter = null;
    static ArrayList<AnnouncementGenClass> announcementslist = new ArrayList<AnnouncementGenClass>();
    static LinkedList<AnnouncementGenClass> linkedList = new LinkedList<AnnouncementGenClass>();

    static String recvdAnnouncementsURL  = "content://com.tp.locator.provider.allowedContacts/receivedannouncements";
    static Uri recvdAnnouncementsUri = Uri.parse(recvdAnnouncementsURL);
    public static MessageSender messageSender;
    public static GoogleCloudMessaging gcm;
    public static DBWrapper dbObj;// = new DBWrapper()
    public static DateTime dt = new DateTime();
    public  static ConcurrentLinkedQueue<Comms> pendingAnnouncements = new ConcurrentLinkedQueue<Comms>();







    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View rootView = inflater.inflate(R.layout.notification, container, false);
        if(thisObj == null)
            thisObj =  this;
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();
        lview = (ListView) activity.findViewById(R.id.notificationlist);
        btnPushMsg = (Button)activity.findViewById(R.id.btnNotificationMsg);
        announcementMsg = (EditText)activity.findViewById(R.id.notificationtext);
        messageSender = new MessageSender();
        gcm = GoogleCloudMessaging.getInstance(this.activity);
        dbObj = ContactContentProvider.database;
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
       // EventBus.getDefault().register(this);



        btnPushMsg.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {


//                MySmsReceiver mockHandler = new MySmsReceiver();
//                MySmsReceiver.smsHandler objHandler = mockHandler.getsmsHandler();
//                objHandler.context = ContactAdapter.contactAdapterCtx;
//                TrackEvent objevent = new TrackEvent();
//                smsParams param = new smsParams();
//                param.isSms = 0;
//                param.wireParams= new ParseMsgParams();
//                param.wireParams.fromNUm = "00000";
//                param.wireParams.isWire = 1;
//                param.ctx = activity.getApplicationContext();
//                //new MySmsReceiver();
//                objevent.param = param;
//                GCMNotificationIntentService.numsList.add("00000");
//
//                EventBus.getDefault().post(objevent);

             //   new MyLoggerTask().execute();

                    if(LocationTrackerFragment.gps != null) {
                     LocationTrackerFragment.gps.getLocation();
                        LocationTrackerFragment.gps.getLatnLong();
                    }

                String strMsg = announcementMsg.getText() != null ? announcementMsg.getText().toString() : "";


                if(strMsg.length() > 0) {
                    Comms comsObj = new Comms();
                    comsObj.params = new HashMap<String, Object>();
                   String todayDate = dt.getTodaysDate();
                    Long timestamp = System.currentTimeMillis();
                    String steTimeStamp =  timestamp.toString();
                   String fromNumber = dbObj.getMyNumber();
                    String Name = dbObj.getMyName();
                    String sex = dbObj.getMySex();
                    Bundle dataBundle = new Bundle();
                    Map<String,String> internalData = new HashMap<String, String>();

                    internalData.put("ACTION", "ANN");
                    internalData.put("DATE", todayDate);
                    internalData.put("MSG", strMsg+" : "+fromNumber);
                    internalData.put("TimeStamp", steTimeStamp);
                    internalData.put("FROMUSER", fromNumber);
                    internalData.put("Name", Name);
                    internalData.put("Sex", sex);


                    comsObj.params.put("from", fromNumber);
                   // comsObj.params.put("data", internalData);

                    comsObj.params.put("from", fromNumber);

                    if(GPSTracker.geoHash_6.length() > 3) {
                        internalData.put("GEOHASH_3Chars", GPSTracker.geoHash_6.substring(0, 3));
                        comsObj.params.put("data", internalData);
                        Gson gson = new Gson();
                        String msg = gson.toJson(comsObj, Comms.class);

//                            if(SocketCommunicationEstablisher.mysock != null)
//                                SocketCommunicationEstablisher.mysock.send(msg);
                        Log.i("Announcement","before announcing "+msg);
                        messagesToPush.offer(msg);

                    }
                    else
                    {
                        comsObj.params.put("data", internalData);
                        pendingAnnouncements.add(comsObj);
                    }
                }

            }
        });


//        if(dataDyn == null)
//            dataDyn = new AnnouncementsFragment.LocationFragmentAsyncDataLoader();
//        if(AnnouncementsFragment.isExecuted == false)
//            dataDyn.execute("");
        new AnnouncementFragmentAsyncDataLoader().execute("");

        if(annAdapter == null)
        {
            annAdapter = new AnnouncementGenAdapter(activity,R.layout.notification_row, announcementslist);
            lview.setAdapter(annAdapter);
            updateannouncements();
        }



        //gopi .. removed from here to added in oncreateview
        new AnnouncementFragmentAsyncDataLoader().execute();

    }


    public void onEvent(pendingAnnouncements obj)
    {
//        Iterator<Bundle> it = AnnouncementsFragment.pendingAnnouncements.iterator();
//        while(it.hasNext())
//        {
//            Bundle dataBundle = (Bundle)it.next();
//            AnnouncementsFragment.messageSender.sendMessage(dataBundle, AnnouncementsFragment.gcm);
//        }

        while(AnnouncementsFragment.pendingAnnouncements.size() > 0)
        {

            if(GPSTracker.geoHash_6.length() > 3) {
                Comms commsmsg = AnnouncementsFragment.pendingAnnouncements.remove();
                Map<String, String> internalData = (Map<String, String>) commsmsg.params.get("data");

                internalData.put("GEOHASH_3Chars", GPSTracker.geoHash_6.substring(0, 3));
                commsmsg.params.put("data", internalData);
                Gson gson = new Gson();
                String msg = gson.toJson(commsmsg, Comms.class);

//                if(SocketCommunicationEstablisher.mysock != null)
//                    SocketCommunicationEstablisher.mysock.send(msg);
                Log.i("Announcement","Announcing pending announcements"+msg);
                messagesToPush.offer(msg);

            }

            Comms dataBundle = AnnouncementsFragment.pendingAnnouncements.remove();
           // AnnouncementsFragment.messageSender.sendMessage(dataBundle, AnnouncementsFragment.gcm);

        }
    }

    private void updateannouncements() {
        getReceivedAnnouncements();
        if(activity == null)
            return;

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {

//                Iterator<AnnouncementGenClass> reverseOrder = linkedList.descendingIterator();
//                while(reverseOrder.hasNext())
//                {
//                    AnnouncementGenClass obj = reverseOrder.next();
//                    announcementslist.add(obj);
//                }
                lview.setAdapter(annAdapter);
                annAdapter.notifyDataSetChanged();

            }

        });
    }

    public static void getReceivedAnnouncements()
    {
        if(activity == null)
            return;

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Cursor c = activity.getContentResolver().query(recvdAnnouncementsUri, null, null,
                        null, null);
                if(c == null)
                    return;
                int rowsCnt = c.getCount();
                announcementslist.clear();
                linkedList.clear();
                if(rowsCnt !=0)

                {
                    while (c.moveToNext()) {
                        Integer id = c.getInt(c.getColumnIndex("_id"));
                        String timestamp = c.getString(c.getColumnIndex("date"));
                        String msg = c.getString(c.getColumnIndex("msg"));
                        AnnouncementGenClass obj = new AnnouncementGenClass();
                        obj.msg = msg.toString();
                        obj.date = timestamp;

                        linkedList.add(obj);

                    }
                    Iterator<AnnouncementGenClass> reverseOrder = linkedList.descendingIterator();
                    while (reverseOrder.hasNext()) {
                        AnnouncementGenClass tempObj = reverseOrder.next();
                        announcementslist.add(tempObj);
                    }
                    //    locationadapter.notifyDataSetChanged();
                }
                c.close();

            }

        });

    }


    public class AnnouncementFragmentAsyncDataLoader extends AsyncTask<String,Void,Void>
    {
        private final ProgressDialog dialog = new ProgressDialog(activity);

        @Override
        protected Void doInBackground(String... params) {
            AnnouncementsFragment.isExecuted = true;

         //   updateannouncements();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Fetching History from Announcement Database...");
            dialog.show();
        }


        @Override
        protected void onPostExecute(Void res) {
            super.onPostExecute(res);
            dialog.dismiss();
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    //updateannouncements();

                    lview.setAdapter(annAdapter);
                    annAdapter.notifyDataSetChanged();
                }
            });

            // adpt.setItemList(result);
            //..gopi
            //  locationadapter.notifyDataSetChanged();
        }


        /* @Override
         protected Void doInBackground(Void... strings) {

             return null;
         }*/

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
}

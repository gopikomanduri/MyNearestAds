package com.tp.locator;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;
import com.tp.locator.Events.LoginEvent;
import com.tp.locator.Events.TrackEvent;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.greenrobot.event.EventBus;



/**
 * Created by user on 8/22/2015.
 */
public class GCMNotificationIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GCMNotificationIntentService() {
        super("GcmIntentService");
    }

    public static String lastAddress = "";

    public static final String TAG = "GCMNotificationIntentService";

    public static ConcurrentLinkedQueue<String> numsList = new ConcurrentLinkedQueue<String>();

    public static ConcurrentLinkedQueue<String> nearestnumslist = new ConcurrentLinkedQueue<String>();

    public static ConcurrentHashMap<String,Integer> existingNearestNums = new ConcurrentHashMap<String, Integer>();
public static Context tempCtx = null;
    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public void invokeOnHandleIntent(Intent intent)
    {
        onHandleIntent(intent);
    }

    @Override
    public void onHandleIntent(Intent intent) {
      //  Log.d(TAG, "onHandleIntent " + intent.getDataString());

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if(messageType == null)
            return;

        MySmsReceiver.logger.info("\n Msg received . Msg type is "+messageType);

      //  if (extras != null) {
            if ( extras != null && !extras.isEmpty()) {
                if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                        .equals(messageType)) {
                    sendNotification("Send error: " + extras.toString());
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                        .equals(messageType)) {
                    sendNotification("Deleted messages on server: "
                            + extras.toString());
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                        .equals(messageType)) {

                    String sm = extras.get("SM").toString();
                    if("RANDOM".equals((extras.get("SM"))))
                    {
                        String randomNum = extras.get("RANDOM").toString();
                        SocketCommunicationEstablisher.randomNum = randomNum;

                    }
                    if("TRACKRESPONSE".equals(extras.get("SM"))) {

                        MySmsReceiver.logger.info("\n Trackresponse received . response is " + extras.toString());
                        //     Log.d(TAG, "onHandleIntent - CHAT ");
                        //   Intent chatIntent = new Intent("com.android.javapapers.com.chat.chatmessage");
                        // chatIntent.putExtra("CHATMESSAGE",extras.get("CHATMESSAGE").toString());
                        // sendBroadcast(chatIntent);
                        String currentMsgId = "";

                        if (extras.containsKey("MSGID") == true) {
                            currentMsgId = extras.getString("MSGID");
                        }
                      //  if (currentMsgId.equals(SocketService.lastTrackResMsgId) == false)
                        {
                            SocketService.lastTrackResMsgId = currentMsgId;

                            if (LocationTrackerFragment.dbObj == null) {
                                if (ContactContentProvider.database == null) {
                                    ContactContentProvider.database = new DBWrapper(this, "GpSTracker.db");
                                    ContactContentProvider.database.getDatabase();
                                    //dbObj =
                                }
                                LocationTrackerFragment.dbObj = ContactContentProvider.database;
                            }
                        String curAddr = extras.get("Address").toString();

                        if (lastAddress.equals(curAddr) == false) {
                            LocationTrackerFragment.dbObj.insertlocation(extras.get("Geohash_F").toString(), extras.get("GEOHASH_6Chars").toString(), extras.get("Address").toString(), "", extras.get("TOUSER").toString());
                            LocationFragment.getDatabaseLocations();
                            lastAddress = curAddr;
                        }
                    }
                        //gopi ...


//                       LocationFragment.dataDyn.execute(extras.get("Address").toString());



                    }
                    if("ANN".equals(extras.get("SM")))
                    {
//                        "(_id integer primary key autoincrement," +
//                                "timestamp integer," +
//                                "date text," +
//                                "category integer," +
//                                "msg text)"

                        Long timeStamp = Long.valueOf(extras.get("TimeStamp").toString());
                        String date = extras.get("date").toString();
                  //      Integer category = Integer.valueOf(extras.get("category").toString());
                        String msg = extras.get("strMsg").toString();


                        if(AnnouncementsFragment.dbObj == null)
                        {
                            if(ContactContentProvider.database == null) {
                                ContactContentProvider.database = new DBWrapper(this, "GpSTracker.db");
                                ContactContentProvider.database.getDatabase();
                                //dbObj =
                            }
                            AnnouncementsFragment.dbObj = ContactContentProvider.database;

                        }
                        Integer category = 1;

                        AnnouncementsFragment.dbObj.insertAnn(timeStamp, date, category, msg);
                        AnnouncementsFragment.getReceivedAnnouncements();



                    }
                    if("NEAREST".equals(extras.get("SM"))){

                        MySmsReceiver.logger.info("\n Nearest received . response is "+extras.toString());
                        //     Log.d(TAG, "onHandleIntent - CHAT ");
                        //   Intent chatIntent = new Intent("com.android.javapapers.com.chat.chatmessage");
                        // chatIntent.putExtra("CHATMESSAGE",extras.get("CHATMESSAGE").toString());
                        // sendBroadcast(chatIntent);
                            String avaibleCnts = extras.get("CNTS").toString();

                        Gson gsonObj = new Gson();
                        nearestcontactslist cntctsObj = null;
                        cntctsObj = gsonObj.fromJson(avaibleCnts, nearestcontactslist.class);
                        //nearestnumslist.clear();

                      //  if(existingNearestNums.contains(cntctsObj.Contact) == false)
                        {
                            existingNearestNums.put(cntctsObj.Contact,1);
                            nearestnumslist.add(cntctsObj.Contact);
                            NearestPeople.invokeupdatenearestpeople();

                        }



                    }
                   else if("TRACK".equals(extras.get("SM"))){
                        MySmsReceiver.logger.info("\n TRACK received . details are  "+extras.toString());
                        String fromNumber = extras.get("FROMUSER").toString();
                        String currentMsgId = "";
                        if(extras.containsKey("MSGID") == true)
                        {
                            currentMsgId = extras.getString("MSGID");
                        }
                      //  if(currentMsgId.equals(SocketService.lastTrackMsgId) == false) {
                            SocketService.lastTrackMsgId = currentMsgId;
                            numsList.add(fromNumber);
                            MySmsReceiver mockHandler = new MySmsReceiver();
                            MySmsReceiver.smsHandler objHandler = mockHandler.getsmsHandler();
                            objHandler.context = ContactAdapter.contactAdapterCtx;
                            TrackEvent objevent = new TrackEvent();
                            smsParams param = new smsParams();
                            param.isSms = 0;
                            param.wireParams = new ParseMsgParams();
                            param.wireParams.fromNUm = fromNumber;
                            param.wireParams.isWire = 1;
                            param.ctx = tempCtx;
                            if (tempCtx == null)
                                param.ctx = getApplicationContext();
                            //new MySmsReceiver();
                            objevent.param = param;

                            EventBus.getDefault().post(objevent);
                      //  }
                    }
                   else if("SIGNUPRESPONSE".equals(extras.get("SM"))){
                        MySmsReceiver.logger.info("\n SIGNUPRESPONSE received . details are  "+extras.toString());
                        String signupResponse = extras.get("RESULT").toString();
                        String otpStr = extras.get("OTP").toString();
                        LoginEvent res = new LoginEvent();
                        res.isSuccess = signupResponse;
                          EventBus.getDefault().post(res);

                    }
                  //  Log.i(TAG, "SERVER_MESSAGE: " + extras.toString());

                }
            }
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        //}

    }

    private void sendNotification(String msg) {
    //    Log.d(TAG, "Preparing to send notification...: " + msg);
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

      /*  PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, com.android.javapapers.com.chat.SignUpActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.gcm_cloud)
                .setContentTitle("GCM XMPP Message")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());*/
    //    Log.d(TAG, "Notification sent successfully.");
    }
}


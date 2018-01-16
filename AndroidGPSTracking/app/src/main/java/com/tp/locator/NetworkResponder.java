package com.tp.locator;

import android.os.Bundle;
import android.util.Log;

import com.fonfon.geohash.GeoHash;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.tp.locator.PhoneUtil.DateTime;

import java.util.HashMap;
import java.util.Map;

import static com.tp.locator.DataService.dbObj;
import static com.tp.locator.DataService.m_ctx;
import static com.tp.locator.SocketService.messagesToPush;

/**
 * Created by user on 8/22/2015.
 */
public class NetworkResponder implements IResponder {

    public static String geoHash_6 = "";
    public static String lastNum = "";
    public static String lastTime = "";
    public static String lastAddr = "";
    public static String prevGeoHash_6 = "";

    @Override

    public void sendMsg(String toAddress, String msg,DateTime time) {

        MySmsReceiver.logger.info("in sendMsg");
        Comms comsMsg = new Comms();
        comsMsg.params = new HashMap<String, Object>();
        Map<String,String> data = new HashMap<String, String>();
        Bundle dataBundle = new Bundle();
        dataBundle.putString("ACTION", "TRACKRESPONSE");
        data.put("ACTION", "TRACKRESPONSE");
        dataBundle.putString("TOUSER", toAddress);
        data.put("TOUSER", toAddress);
        String currentTimeStamp = time.getTodaysDate()+":"+time.getCurrentTime();

        if (ContactContentProvider.database == null) {
            ContactContentProvider.database = new DBWrapper(m_ctx, "GpSTracker.db");
            ContactContentProvider.database.getDatabase();
            dbObj = ContactContentProvider.database;
        }
        if (dbObj == null)
            dbObj = ContactContentProvider.database;

        String currentPhoneNum = dbObj.getMyNumber();
        String Name = dbObj.getMyName();
        String sex = dbObj.getMySex();
        dataBundle.putString("FROMUSER", currentPhoneNum);
        comsMsg.params.put("from", currentPhoneNum);



        dataBundle.putString("ADDRESS", msg);
        data.put("ADDRESS", msg);
        data.put("Name", Name);
        data.put("sex",sex);

        dataBundle.putString("at", currentTimeStamp);
        data.put("at", currentTimeStamp);
        GeoHash geoHash;
        String str1 = GeoHash.fromCoordinates(GPSTracker.latitude, GPSTracker.longitude).toString();
        String str2 = GeoHash.fromCoordinates(GPSTracker.latitude, GPSTracker.longitude,6).toString();
        geoHash_6 = str2;
        if(GPSTracker.latitude != 0.0 && GPSTracker.longitude != 0.0)
        {
            dataBundle.putString("LAT",String.valueOf(GPSTracker.latitude));
            data.put("LAT",String.valueOf(GPSTracker.latitude));

            dataBundle.putString("LNG",String.valueOf(GPSTracker.longitude));
            data.put("LNG",String.valueOf(GPSTracker.longitude));

            dataBundle.putString("Geohash_F",str1);
            data.put("Geohash_F",str1);

            dataBundle.putString("GEOHASH_6Chars",str2);
            data.put("GEOHASH_6Chars",str2);

            msg += "\n LAT :"+String.valueOf(GPSTracker.latitude);
            msg += "\n LNG :"+String.valueOf(GPSTracker.longitude);
            msg += "\n Geohash_F :"+str1;
            msg += "\n GEOHASH_6Chars :"+str2;

        }
        if((prevGeoHash_6.equals(geoHash_6) == true) && (lastNum.equals(toAddress) == true) && (lastTime.equals(currentTimeStamp) == true))
        {
            return;
        }
        prevGeoHash_6 = geoHash_6;
        lastNum = toAddress;
        lastTime = currentTimeStamp;

        MessageSender msgSender = new MessageSender();
        comsMsg.params.put("data",data);
        Gson gson = new Gson();
        String trackRes = gson.toJson(comsMsg, Comms.class);

        if(ContactAdapter.messageSender == null)
        {
            ContactAdapter.messageSender = new MessageSender();
        }

//        if(SocketCommunicationEstablisher.mysock != null)
//            SocketCommunicationEstablisher.mysock.send(trackRes);
Log.i("NetworkResponder","before sending trackresponse "+trackRes);
        if(trackRes != null)
        messagesToPush.offer(trackRes);

        //Gopi .. commented
//        if(ContactAdapter.gcm != null) {
//            MySmsReceiver.logger.info("in sendMsg ContactAdapter.gcm != null ");
//            ContactAdapter.messageSender.sendMessage(dataBundle, ContactAdapter.gcm);
//        }
//        else
//        {
//            MySmsReceiver.logger.info("in sendMsg ContactAdapter.gcm = = null ");
//            ContactAdapter.gcm = GoogleCloudMessaging.getInstance(MySmsReceiver.usableCtx);
//            MySmsReceiver.logger.info("in sendMsg ContactAdapter.gcm != null . so crreated one .");
//
//            ContactAdapter.messageSender.sendMessage(dataBundle, ContactAdapter.gcm);
//        }

    }
}

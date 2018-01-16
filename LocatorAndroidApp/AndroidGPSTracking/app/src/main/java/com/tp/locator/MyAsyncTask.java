package com.tp.locator;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;

import com.tp.locator.PhoneUtil.DateTime;
import com.tp.locator.Settings.SettingsClass;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public  class MyAsyncTask extends AsyncTask<Object, Void, String> {

    comsObject obj;
    Boolean isMock = false;
    Context ctx;
    String url = "";
    boolean isInvoked = false;
    public  String currentstr = "";
    @Override
    protected String doInBackground(Object... comObj) {
        obj = (comsObject)comObj[0];
        url = obj.getUrl();
        isMock = obj.isMock();
        MySmsReceiver.logger.info("in MyAsyncTask  doInBackground url is " + url.toString());
        ctx = obj.getContext();

        //            if(isEnablingNetworkNow == true)
        //                SystemClock.sleep(5000);
        //   String response = postData(url);  verify for door number later;

        MySmsReceiver.logger.info("in MyAsyncTask doInBackground response is " + obj.address.toString());

        if(isMock == true)
            return SettingsClass.dbLoc;
        //   onPostExecute(response);
        return obj.address.toString();//+"\n response : "+response;
    }

    private void sendResponse(String result)
    {
        JSONObject location , current_location;
        MySmsReceiver.logger.info("in sendResponse result =  " + result.toString());
        String location_string = "";
        StringBuilder mapsRul = new StringBuilder();// = "https://www.google.co.in/maps/@GPSTracker.latitude,GPSTracker.longitude";
        mapsRul.append("https://www.google.co.in/maps/@");
        mapsRul.append(String.valueOf(GPSTracker.latitude));
        mapsRul.append(",");
        mapsRul.append(String.valueOf(GPSTracker.longitude));
        try {
            //     location = new JSONObject(result);
            location_string = result;
            //                if(result != null) {
            //                    current_location = location.getJSONArray("results").getJSONObject(0);
            //                    location_string = current_location.getString("formatted_address");// + "\n url is "+url;
            //
            //                    if ((location_string == null) || (location_string.length() <= 0))
            //                    {
            //                        location_string = mapsRul.toString();
            //                    }
            //               /*     if((location_string.length() <= 0) && (isInvoked == false))
            //                    {
            //                        isInvoked = true;
            //                        postData(url);
            //                    }*/
            //                    Log.d("test", "formattted address:" + location_string);
            //                }
        } catch (Exception e1) {

            MySmsReceiver.logger.info("in sendResponse catch exception is =  " + e1.getMessage().toString());
            e1.printStackTrace();

        }
        finally {
            //     if(isInvoked == true) {
            //      if ((location_string != null) && (location_string.length() > 0))

            //location_string = location_string + "\n lat:"+GPSTracker.latitude+" long:"+GPSTracker.longitude;
            DateTime timestamp = new DateTime();
            location_string = location_string + "\n"+mapsRul+"\n"+"received on"+timestamp.getTodaysDate()+timestamp.getCurrentTime();
            currentstr = location_string;
            MySmsReceiver.logger.info("before sending message   location_string is " + location_string);
            if(isMock == false) {
                obj.isWire = 1;// hardcoded for onwire ...
                if(obj.isWire == 1)
                {
                    IResponder senderobj = new NetworkResponder();
                    senderobj.sendMsg(obj.fromNumber,location_string,timestamp);
                }
                else {
                    obj.getSmsObj().sendMsg(location_string);

                }

            }
            else
            {
                IResponder senderobj = new NetworkResponder();
                senderobj.sendMsg(obj.fromNumber, SettingsClass.dbLoc,timestamp);

                //                    if(LocationTrackerFragment.dbObj != null)
                //                    LocationTrackerFragment.dbObj.insertlocation(obj.latitude,obj.longitude,location_string+obj.getUrl(),obj.timeStamp,obj.fromNumber);
                //                    LocationFragment.getDatabaseLocations();
            }

            //     else {
            //       obj.getSmsObj().sendMsg(url);
            //    }
            //       isInvoked = false;
            MySmsReceiver.logger.info("before disabling all  ");
            GPSTracker.disable();
            //     }
        }
    }

    private String getLongLat(String url)
    {
        MySmsReceiver.logger.info("in getLongLat .. url is " + url);
        DefaultHttpClient client = new DefaultHttpClient();
        String response = "";
        HttpGet httpGet = new HttpGet(url);
        try {
            MySmsReceiver.logger.info("before invoking sleep .. in getLongLat ");
            SystemClock.sleep(10000);
            HttpResponse execute = client.execute(httpGet);
            InputStream content = execute.getEntity().getContent();

            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            String s = "";
            while ((s = buffer.readLine()) != null) {
                response += s;
            }

        } catch (Exception e) {
            MySmsReceiver.logger.info("in getLongLat exception is " + e.getMessage().toString());
            //     Toast.makeText(ctx,"in catch block of http",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        finally {
            MySmsReceiver.logger.info(" before senidng response in getLongLat .. response  = " + response.toString());
            //sendResponse(response);
            return response;
        }
    }
    public String postData(String url) {
        // Create a new HttpClient and Post Header
        MySmsReceiver.logger.info(" in postData .. url = " + url.toString());
        DefaultHttpClient client = new DefaultHttpClient();
        String response = getLongLat(url);

     /*       HttpGet httpGet = new HttpGet(url);
            try {
                SystemClock.sleep(10000);
                HttpResponse execute = client.execute(httpGet);
                InputStream content = execute.getEntity().getContent();

                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }*/
        return response;
    }
    @Override
    protected void onPostExecute(String result)
    {

        MySmsReceiver.logger.info(" in onPostExecute of GPSTracker result is  " + result.toString());
        currentstr = result;
        sendResponse(result);
    }
}

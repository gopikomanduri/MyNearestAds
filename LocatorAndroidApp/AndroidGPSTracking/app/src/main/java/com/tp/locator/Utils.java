package com.tp.locator;


/*
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;*/


import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.telephony.TelephonyManager;

import com.tp.locator.PhoneUtil.libphonenumber.src.com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.tp.locator.PhoneUtil.libphonenumber.src.com.google.i18n.phonenumbers.Phonenumber;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by user on 7/3/2015.
 */
public class Utils {

    private String getDateTime(String str_date) {
        Timestamp ts = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = dateFormat.parse(str_date);

            ts = new Timestamp(date.getTime());
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            ts = null;
        } finally {
            if (ts != null)
                return ts.toString();
            return "";
        }
    }

    public static  boolean isMyServiceRunning(Class<?> serviceClass,Context ctx) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String parseNumber(String number)
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
    public static String sha1Hash( String toHash )
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

    public static String getCountryCode(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(ctx.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();
        return countryCodeValue;
    }

    public static boolean hasActiveInternetConnection(Context context) {
        MySmsReceiver.logger.info("in hasActiveInternetConnection");
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                MySmsReceiver.logger.info("Error checking internet connection");
            }
        } else {
            MySmsReceiver.logger.info("No network available!");

        }
        return false;
    }

    private static boolean isNetworkAvailable(Context context) {
        MySmsReceiver.logger.info("in isNetworkAvailable");
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static Boolean isOnline() {
        try {
            MySmsReceiver.logger.info("in isOnline");
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal == 0);
            return reachable;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isNetworkAvailableWithPing(Context context) {
        MySmsReceiver.logger.info("in isNetworkAvailable with ping example");
        HttpGet httpGet = new HttpGet("http://www.google.com");
        HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        // The default value is zero, that means the timeout is not used.
        int timeoutConnection = 3000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 5000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
        try {
            MySmsReceiver.logger.info("Checking network connection...");
            httpClient.execute(httpGet);
            MySmsReceiver.logger.info("connection OK");
            return true;
        } catch (ClientProtocolException e) {
            MySmsReceiver.logger.info("In catch of  ClientProtocolException." + e.getMessage().toString());
            e.printStackTrace();
        } catch (IOException e) {
            MySmsReceiver.logger.info("In catch of  IOException." + e.getMessage().toString());
            e.printStackTrace();
        }

        MySmsReceiver.logger.info("Internet not available.");
        return false;
    }



    public static class NetworkCheck extends AsyncTask<Context, Void, Boolean> {
        Context ctx;
        @Override
        protected Boolean doInBackground(Context... contexts) {
            ctx = (Context)contexts[0];
            MySmsReceiver.logger.info("in isNetworkAvailable with ping example");
            HttpGet httpGet = new HttpGet("http://www.google.com");
            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is established.
            // The default value is zero, that means the timeout is not used.
            int timeoutConnection = 3000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 5000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
            try {
                MySmsReceiver.logger.info("Checking network connection...");
                httpClient.execute(httpGet);
                MySmsReceiver.logger.info("connection OK");
                return true;
            } catch (ClientProtocolException e) {
                MySmsReceiver.logger.info("In catch of  ClientProtocolException." + e.getMessage().toString());
                e.printStackTrace();
            } catch (IOException e) {
                MySmsReceiver.logger.info("In catch of  IOException." + e.getMessage().toString());
                e.printStackTrace();
            }

            MySmsReceiver.logger.info("Internet not available.");
            return false;
        }
        @Override
        protected void onPostExecute(Boolean result)
        {

            MySmsReceiver.logger.info(" in onPostExecute of utils result is  "+String.valueOf(result));
           if(result == false)
           {
               MySmsReceiver.logger.info(" in getLocation .. haveNetworkConnection is false ");

               GPSTracker.isEnablingNetworkNow = true;
               MySmsReceiver.logger.info(" in getLocation before neablin wifi ");
               GPSTracker.tryEnablingWifi();
               MySmsReceiver.logger.info(" in getLocation checking network connections ");
               GPSTracker.haveNetworkConnection();
               SystemClock.sleep(500);
             //  if(Utils.isNetworkAvailableWithPing(mContext) ==false) {
                   MySmsReceiver.logger.info(" in getLocation before neablin data ");
               GPSTracker.tryEnablingData();
               GPSTracker.haveNetworkConnection();
                   SystemClock.sleep(500);
                 //  if(Utils.isNetworkAvailableWithPing(mContext) == false) {
                       MySmsReceiver.logger.info(" in getLocation returning NULL as no network");

                   //}
               //}

           }
        }
    }


    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder("");
        while(randomStringBuilder.length() < 4) {
//            int randomLength = generator.nextInt(4);
//            char tempChar;
//            for (int i = 0; i < randomLength; i++) {
//                tempChar = (char) (generator.nextInt(96) + 32);
//                randomStringBuilder.append(tempChar);
//            }

            Random random = new Random();
            int value = random.nextInt(9);
            randomStringBuilder.append(value);
        }

        return randomStringBuilder.toString();
    }
}



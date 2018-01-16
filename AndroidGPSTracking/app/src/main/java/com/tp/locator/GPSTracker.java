    package com.tp.locator;

    import android.app.AlertDialog;
    import android.app.Service;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.location.Address;
    import android.location.Geocoder;
    import android.location.Location;
    import android.location.LocationListener;
    import android.location.LocationManager;
    import android.net.ConnectivityManager;
    import android.net.NetworkInfo;
    import android.net.wifi.WifiManager;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.os.HandlerThread;
    import android.os.IBinder;
    import android.os.Looper;
    import android.os.SystemClock;
    import android.provider.Settings;
    import android.text.format.DateFormat;
    import android.util.Log;

    import com.fonfon.geohash.GeoHash;
    import com.tp.locator.Events.pendingAnnouncements;
    import com.tp.locator.PhoneUtil.DateTime;
    import com.tp.locator.Settings.SettingsClass;

    import org.apache.http.HttpResponse;
    import org.apache.http.client.methods.HttpGet;
    import org.apache.http.impl.client.DefaultHttpClient;
    import org.json.JSONObject;

    import java.io.BufferedReader;
    import java.io.InputStream;
    import java.io.InputStreamReader;
    import java.lang.reflect.Method;
    import java.util.List;
    import java.util.Locale;
    import java.util.concurrent.locks.ReentrantLock;
    import java.util.logging.Handler;

    import de.greenrobot.event.EventBus;

    import static com.tp.locator.DataService.dbObj;
    import static com.tp.locator.MySmsReceiver.receivedMsgsFrom;
    import static com.tp.locator.MySmsReceiver.requestsFrom;

    public class GPSTracker extends Service implements LocationListener {

        private  Context mContext;

        private static final String TAG = "GPSTracker";
        public Handler mHandler;

        public static void setMsContext(Context msContext) {
            GPSTracker.msContext = msContext;

        }

        static Context msContext;

        // flag for GPS status
        static boolean isGPSEnabled = false;

        boolean isInvoked = false;

        // flag for network status
        static boolean isNetworkEnabled = false;



        static boolean isEnablingNetworkNow = false;

        // flag for GPS status
        boolean canGetLocation = false;

        Location location; // location
        static double latitude = 0.0; // latitude
        static double longitude = 0.0; // longitude
        static double prevlatitude = 0.0;
        static double prevlongitude = 0.0;
        public static String geoHash_6 = "";
        public  static  String geoHash_F = "";

        public static MyAddress myLoc = new MyAddress();

        // The minimum distance to change Updates in meters
        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1000; // 5000 meters  (5 kms)

        // The minimum time between updates in milliseconds
        //private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

        private static final long MIN_TIME_BW_UPDATES = 1000 * 5 * 1;//*60; // 5 minutes

        // Declaring a Location Manager
        public static LocationManager locationManager;

        static HandlerThread handlerThread;
        static Looper looper;


        public GPSTracker(Context context) {
            this.mContext = context;
           // msContext = context;


            if(handlerThread == null)
                handlerThread = new HandlerThread("MyHandlerThread");
            if(handlerThread.getState() == Thread.State.TERMINATED)
            {
                //  handlerThread.removeCallbacksAndMessages(null);
                handlerThread.getLooper().quit();
                handlerThread.quit();
                handlerThread = new HandlerThread("MyHandlerThread");
                handlerThread.start();
                // Now get the Looper from the HandlerThread
                // NOTE: This call will block until the HandlerThread gets control and initializes its Looper
               // looper = handlerThread.getLooper();
            }
            else if( Thread.State.NEW == handlerThread.getState())
            {
              //  handlerThread.removeCallbacksAndMessages(null);
                handlerThread.start();
                // Now get the Looper from the HandlerThread
                // NOTE: This call will block until the HandlerThread gets control and initializes its Looper
                //looper = handlerThread.getLooper();
            }
            looper = handlerThread.getLooper();



            //Gopi .. commented ..
        //	getLocation();
        }
        public static boolean tryEnablingWifi()
        {
            MySmsReceiver.logger.info("in trying to enable wifi");
            WifiManager wifiManager ;
            wifiManager  = (WifiManager)msContext.getSystemService(msContext.WIFI_SERVICE);
            wifiManager.setWifiEnabled(true);
            return true;
        }
        public static boolean tryEnablingData()
        {
            MySmsReceiver.logger.info("in trying to enable Data");
            Log.i(TAG, "in trying to enable Data");
            boolean isConnected = false;
            ConnectivityManager dataManager;
            try {


                dataManager = (ConnectivityManager) msContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                Method dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
                dataMtd.setAccessible(true);
                dataMtd.invoke(dataManager, true);        //True - to enable data connectivity .
                isConnected = true;
            }
            catch(Exception ex)
            {
                isConnected = false;
            }
            return  isConnected;
        }
        public static void disable()
        {
            MySmsReceiver.logger.info("in trying to disable");
            Log.i(TAG, "in trying to disable");
            if(isEnablingNetworkNow == true)
            {
                MySmsReceiver.logger.info("in trying to disable  isEnablingNetworkNow is true");
                Log.i(TAG,"in trying to disable  isEnablingNetworkNow is true");
                WifiManager wifiManager ;
                wifiManager  = (WifiManager)msContext.getSystemService(msContext.WIFI_SERVICE);
                wifiManager.setWifiEnabled(false);
                MySmsReceiver.logger.info("in trying to disable  wifi is true");
                Log.i(TAG,"in trying to disable  wifi is true");

                ConnectivityManager dataManager;
                try {

                    MySmsReceiver.logger.info("in trying to disable  data is true");
                    dataManager = (ConnectivityManager) msContext.getSystemService(msContext.CONNECTIVITY_SERVICE);
                    Method dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
                    dataMtd.setAccessible(false);
                    dataMtd.invoke(dataManager, false);        //True - to enable data connectivity .
                }
                catch(Exception ex)
                {
                    Log.i(TAG,"in trying to disable  is in catch . exception is " + ex.getMessage().toString());
                    MySmsReceiver.logger.info("in trying to disable  is in catch . exception is " + ex.getMessage().toString());
                }
                finally {
                    isEnablingNetworkNow = false;
                }

            }
            if (!isGPSEnabled && !isNetworkEnabled)
            {
                MySmsReceiver.logger.info("in !isGPSEnabled && !isNetworkEnabled");
                Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
                intent.putExtra("enabled", false);
                msContext.sendBroadcast(intent);
            }
        }

        public static boolean haveNetworkConnection() {
            MySmsReceiver.logger.info("in VERIFICATION OF haveNetworkConnection");
            boolean haveConnectedWifi = false;
            boolean haveConnectedMobile = false;

            ConnectivityManager cm = (ConnectivityManager) msContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected()) {
                        haveConnectedWifi = true;
                    }

                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }
            return haveConnectedWifi || haveConnectedMobile;
        }

        public MyAddress showAddress()
        {


            MySmsReceiver.logger.info("in showAddress");
            Geocoder geocoder;
       //     MyAddress myLoc = new MyAddress();
            List<Address> addresses = null;
            Locale lc = Locale.getDefault();
            if(mContext == null)
                mContext = msContext;
            geocoder = new Geocoder(this.mContext, lc);
    try {
        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        myLoc.latitude = latitude;
        myLoc.longitude = longitude;
        MySmsReceiver.logger.info("in showAddress  address is " + addresses.toString());
        MySmsReceiver.logger.info("\n In showAddress address is "+addresses.toString());
     //   EventBus.getDefault().post(addresses);
    }
    catch(Exception ex)
    {
        addresses = null;
    }
            finally {
        if(addresses != null ) {
            myLoc.address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            myLoc.city = addresses.get(0).getLocality();
            myLoc.state = addresses.get(0).getAdminArea();
            myLoc.country = addresses.get(0).getCountryName();
            myLoc.postalCode = addresses.get(0).getPostalCode();
            myLoc.knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
            MySmsReceiver.logger.info("\n In showAddress address is " + myLoc.toString());

        }
        else
        {
            String location_string = "";
            StringBuilder mapsRul = new StringBuilder();// = "https://www.google.co.in/maps/@GPSTracker.latitude,GPSTracker.longitude";
            mapsRul.append("https://www.google.co.in/maps/@");
            mapsRul.append(String.valueOf(GPSTracker.latitude));
            mapsRul.append(",");
            mapsRul.append(String.valueOf(GPSTracker.longitude));
            myLoc.address = mapsRul.toString();
            MySmsReceiver.logger.info("\n In showAddress sending map url as network is slow is " + myLoc.toString());
        }
      //  if(prevlatitude != latitude || prevlongitude != longitude) {
           // if (MySmsReceiver.smsHandler.task == null || MySmsReceiver.smsHandler.task.getStatus() == AsyncTask.Status.FINISHED)
                //EventBus.getDefault().post(myLoc);
        sendAddress(myLoc);
            prevlongitude = longitude;
            prevlatitude = latitude;
        }
//        else
//        {
//            while (requestsFrom.isEmpty() == false)
//            {
//                String fromNUm = requestsFrom.remove();
//                String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
//
//                IResponder senderobj = new NetworkResponder();
//                senderobj.sendMsg(fromNUm,MyAsyncTask.currentstr,date.toString());
//
//            }
//        }
        return myLoc;

    }


        public boolean CheckEnableGPS(){
    /*        MySmsReceiver.logger.info(" in CheckEnableGPS ");
            String provider = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            MySmsReceiver.logger.info(" in CheckEnableGPS provide is "+provider.toString());
            if(!provider.equals("")){
                MySmsReceiver.logger.info(" in CheckEnableGPS provide is " + provider.toString() + " so returning true");
              return true;
            }
            return false;*/
    if(mContext == null)
        mContext = msContext;
            LocationManager lm = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = false;
            boolean network_enabled = false;
            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch(Exception ex) {}

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch(Exception ex) {}
            if(gps_enabled == false && network_enabled == false)
                return  false;
            return  true;
        }

        public Location getLocation() {
            try {
                MySmsReceiver.logger.info("in getLocation");
                if(mContext == null)
                    mContext = msContext;
                locationManager = (LocationManager) mContext
                        .getSystemService(LOCATION_SERVICE);

                // getting GPS status
                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                MySmsReceiver.logger.info(" in CheckEnableGPS isGPSEnabledfrom locationManager is " + String.valueOf(isGPSEnabled));
                isGPSEnabled = CheckEnableGPS();

                MySmsReceiver.logger.info(" in CheckEnableGPS isGPSEnabledfrom CheckEnableGPS is " + String.valueOf(isGPSEnabled));
                // getting network status
                isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                MySmsReceiver.logger.info(" in CheckEnableGPS isNetworkEnabled is " + String.valueOf(isNetworkEnabled));

                if (!isGPSEnabled && !isNetworkEnabled) {

                    MySmsReceiver.logger.info(" in isGPSEnabled is " + String.valueOf(isGPSEnabled) + "  isNetworkEnabled is " + String.valueOf(isNetworkEnabled) + "  so enabling gps");
                    Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
                    intent.putExtra("enabled", true);
                    if(mContext == null)
                        mContext = msContext;
                    mContext.sendBroadcast(intent);
               //     SystemClock.sleep(5000);

                    isGPSEnabled = locationManager
                            .isProviderEnabled(LocationManager.GPS_PROVIDER);
                    MySmsReceiver.logger.info(" in CheckEnableGPS isGPSEnabledfrom locationManager is second time trying  " + String.valueOf(isGPSEnabled));

                }

                Utils.NetworkCheck task = new Utils.NetworkCheck();

                    Context []obj = new Context[1];
                    obj[0] = msContext;
                // gopi . plz test the below code;
                 //   task.execute(obj).get();

                // commented belowe as the below part is executing in NetworkCheck.. gopi
    /*            if(Utils.isNetworkAvailableWithPing(mContext) == false)
                {
                    MySmsReceiver.logger.info(" in getLocation .. haveNetworkConnection is false ");

                    isEnablingNetworkNow = true;
                    MySmsReceiver.logger.info(" in getLocation before neablin wifi ");
                    tryEnablingWifi();
                    MySmsReceiver.logger.info(" in getLocation checking network connections ");
                     haveNetworkConnection();
                    SystemClock.sleep(5000);
                    if(Utils.isNetworkAvailableWithPing(mContext) ==false) {
                        MySmsReceiver.logger.info(" in getLocation before neablin data ");
                        tryEnablingData();
                        haveNetworkConnection();
                        SystemClock.sleep(5000);
                        if(Utils.isNetworkAvailableWithPing(mContext) == false) {
                            MySmsReceiver.logger.info(" in getLocation returning NULL as no network");
                            return null;
                        }
                    }

                }*/
                MySmsReceiver.logger.info(" in getLocation before invoking getLatLong");
                getLatnLong();

            } catch (Exception e) {
                MySmsReceiver.logger.info(" in getLocation catch .. catch is " + e.getMessage().toString());
                e.printStackTrace();
            }

            return location;
        }


        public void sendAddress(MyAddress str) {
            if(GCMNotificationIntentService.numsList.isEmpty() == true)
                return;
//                task = new MyAsyncTask();
          //  str = GPSTracker.myLoc;

            String url = "http://maps.google.com/maps/api/geocode/json?latlng=" + str.latitude + "," + str.longitude + "&sensor=true";
            //  MySmsReceiver.logger.severe(" url hitting is " + url);

            //  MySmsReceiver.logger.severe(" initialising comsObject");
            comsObject[] comsObj = new comsObject[1];


            comsObj[0] = new comsObject();
            while (GCMNotificationIntentService.numsList.isEmpty() == false)
            {
                String fromNUm = GCMNotificationIntentService.numsList.remove();
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
           //     comsObj[0].setSmsObj(smsObj);
                comsObj[0].setContext(msContext);
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


        public void getLatnLong()
        {
            MySmsReceiver.logger.info(" in getLatnLong");
            this.canGetLocation = true;
            MySmsReceiver.logger.info(" in getLatnLong setted cangetLocation to true");



            // if GPS Enabled get lat/long using GPS Services

            MySmsReceiver.logger.info(" in getLatnLong isGPSEnabled = " + String.valueOf(isGPSEnabled));
            if (isGPSEnabled) {
                MySmsReceiver.logger.info(" in getLatnLong isGPSEnabled in if condition = ");
                if(mContext == null)
                    mContext = msContext;
                locationManager = (LocationManager) mContext
                        .getSystemService(LOCATION_SERVICE);



                if (locationManager != null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            0,0
                            , this,looper);
                    MySmsReceiver.logger.info(" in getLatnLong registered for lat long changes for every 5 sec.. in GPS");
                    Log.d("GPS Enabled", "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            MySmsReceiver.logger.info(" in getLatnLong last known location coordinates are GPS  latitude  = " + String.valueOf(latitude) + "  longitude  = " + String.valueOf(longitude));
                        }
                    }
                }
            }
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        0,
                        0, this, looper);
                Log.d("Network", "Network");
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    MySmsReceiver.logger.info(" in getLatnLong registered for lat long changes for every 5 sec.. in network");
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        MySmsReceiver.logger.info(" in getLatnLong last known location coordinates are network  latitude  = " + String.valueOf(latitude) + "  longitude  = " + String.valueOf(longitude));
                    }
                }
            }
    //        if(latitude != 0.0 && longitude != 0.0)
    //        {
    //            MySmsReceiver.logger.info("sending last known location details");
    //            showAddress();
    //        }

            geoHash_6 = GeoHash.fromCoordinates(GPSTracker.latitude, GPSTracker.longitude,6).toString();
            geoHash_F = GeoHash.fromCoordinates(GPSTracker.latitude, GPSTracker.longitude).toString();
            EventBus.getDefault().post(new pendingAnnouncements());
            showAddress();


        }

        /**
         * Stop using GPS listener
         * Calling this function will stop using GPS in your app
         * */
        public void stopUsingGPS(){
            if(locationManager != null){
                locationManager.removeUpdates(GPSTracker.this);
            }
        }

        /**
         * Function to get latitude
         * */
        public double getLatitude(){
            if(location != null){
                latitude = location.getLatitude();
            }

            // return latitude
            return latitude;
        }

        /**
         * Function to get longitude
         * */
        public double getLongitude(){
            if(location != null){
                longitude = location.getLongitude();
            }

            // return longitude
            return longitude;
        }

        /**
         * Function to check GPS/wifi enabled
         * @return boolean
         * */
        public boolean canGetLocation() {
            return this.canGetLocation;
        }

        /**
         * Function to show settings alert dialog
         * On pressing Settings button will lauch Settings Options
         * */
        public void showSettingsAlert(){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

            // Setting Dialog Title
            alertDialog.setTitle("GPS is settings");

            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivity(intent);
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                }
            });

            // Showing Alert Message
    //        alertDialog.show();
        }

        @Override
        public void onLocationChanged(Location location) {

            MySmsReceiver.logger.info(" in onLocationChanged = ");
            latitude = location.getLatitude();
            longitude = location.getLongitude();
           // if(MySmsReceiver.gps != null)
                locationManager.removeUpdates(this);
            MySmsReceiver.logger.info(" in onLocationChanged = " + String.valueOf(latitude) + "  longitude  = " + String.valueOf(longitude));
    //        MySmsReceiver.logger.info("in onLocationChanged .. before invoking showaddress");
          //  showAddress();
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public IBinder onBind(Intent arg0) {
            return null;
        }

    }


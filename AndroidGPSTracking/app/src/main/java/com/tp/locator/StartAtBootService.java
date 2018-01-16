package com.tp.locator;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by user on 7/21/2015.
 */
public class StartAtBootService extends Service
{
    String MyDetailsURL  = "content://com.tp.locator.provider.allowedContacts/Contacts";

    Uri contactsUri = Uri.parse(MyDetailsURL);
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        Log.v("StartServiceAtBoot", "StartAtBootService Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.v("StartServiceAtBoot", "StartAtBootService -- onStartCommand()");
        Log.d("Gopi", "onCreated called");
        updateContacts();

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }
    private void updateContacts()
    {
        //Cursor c = this.managedQuery(contactsUri, null, null, null, null);
        Log.d("Gopi", "updateContacts called");
        Cursor c = this.getContentResolver().query(contactsUri,  null, null,
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
    }

  /*  String sha1Hash( String toHash )
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
    }*/


	    /*
	     * In Android 2.0 and later, onStart() is depreciated.  Use
	     * onStartCommand() instead, or compile against API Level 5 and
	     * use both.
	     * http://android-developers.blogspot.com/2010/02/service-api-changes-starting-with.html
	    	@Override
	    	public void onStart(Intent intent, int startId)
	    	{
	    		Log.v("StartServiceAtBoot", "StartAtBootService -- onStart()");
	    	}
	     */

    @Override
    public void onDestroy()
    {
        Log.v("StartServiceAtBoot", "StartAtBootService Destroyed");
    }
}

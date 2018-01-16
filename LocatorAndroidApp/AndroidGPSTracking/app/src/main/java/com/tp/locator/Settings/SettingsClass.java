package com.tp.locator.Settings;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.tp.locator.Events.fetchSettingsFromDb;
import com.tp.locator.Events.settings;
import com.tp.locator.MySmsReceiver;

import de.greenrobot.event.EventBus;

/**
 * Created by gkomandu on 12/2/2015.
 */
public class SettingsClass {
    String MySettingsURL  = "content://com.tp.locator.provider.allowedContacts/settings";
    Uri settingsUri = Uri.parse(MySettingsURL);
    String pwdMsg = "";
   public static String dbLoc = "";
   public static String contactNumber = "";
    int rowsCnt = 0;
   public static String emergencyContact1 = "";
    public static String emergencyContact2 = "";
    public static String emergencyContact3 = "";
    public static Integer emergencyT = 0;
    public static Integer locatorT = 1;
    public static String dbpwdMsg = "";


    public void onEvent(fetchSettingsFromDb event)
    {
        new loadContactsAsync().execute(event.ctx);
    }

    public SettingsClass(Context ctx)
    {
        EventBus.getDefault().register(this);
        new loadContactsAsync().execute(ctx);
    }

    public class loadContactsAsync extends AsyncTask<Context,Void,Integer>
    {

        Context ctx;

        @Override
        protected Integer doInBackground(Context... activities) {
            ctx = activities[0];
            loadData(ctx);
            return null;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        public void loadData(Context activity)
        {
            // Cursor c = activity.managedQuery(settingsUri, null, null, null, null);
            Cursor c = activity.getContentResolver().query(settingsUri, null, null,
                    null, null);
            if(c == null)
                return;
            rowsCnt= c.getCount();
            int x = 0;
            if(rowsCnt != 0)
            {
                while(c.moveToNext())
                {
                    x = c.getColumnIndex("ContactNumber");
                    contactNumber = c.getString(x);

                    x = c.getColumnIndex("pwdMsg");
                    dbpwdMsg = c.getString(x);
                    x = c.getColumnIndex("MockLocation");
                    dbLoc = c.getString(x);
                    x = c.getColumnIndex("EmergencyContact1");
                    emergencyContact1 = c.getString(x);
                    x = c.getColumnIndex("EmergencyContact2");
                    emergencyContact2 = c.getString(x);
                    x = c.getColumnIndex("EmergencyContact3");
                    emergencyContact3 = c.getString(x);

                    x = c.getColumnIndex("emergency");
                    emergencyT = c.getInt(x);

                    x = c.getColumnIndex("locator");
                    SettingsClass.locatorT = c.getInt(x);
                    SettingsClass.locatorT = 1;
                    MySmsReceiver.logger.info("in settings class , locator from db is  "+SettingsClass.locatorT);
                    settings obj = new settings();
                    obj.ctx = activity;
                    EventBus.getDefault().post(obj);

                }
            }
            else {
                settings obj = new settings();
                obj.ctx = activity;
              //  EventBus.getDefault().post(obj);
            }
            c.close();
        }
    }


}

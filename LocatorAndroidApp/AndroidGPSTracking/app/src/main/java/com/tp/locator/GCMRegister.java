package com.tp.locator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Created by gkomandu on 8/19/2015.
 */
public class GCMRegister {

    String regId;
    private static final String TAG = "GCMRegister";
    public static final String REG_ID = "regId";
    GoogleCloudMessaging gcm;
    GCMRegister(GoogleCloudMessaging gcm)
    {
        this.gcm = gcm;
    }
    Context m_ctx;
    GCMRegister(Context ctx)
    {
        m_ctx = ctx;

    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(m_ctx);
                    }
                    regId = gcm.register(Config.GOOGLE_PROJECT_ID);
                    Log.d("RegisterActivity", "registerInBackground - regId: "
                            + regId);
                    msg = "Device registered, registration ID=" + regId;
                    storeRegistrationId(regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d(TAG, "Error: " + msg);
                }
                Log.d(TAG, "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d(TAG, "Registered with GCM Server." + msg);
            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(String regId) {
        final SharedPreferences prefs = m_ctx.getSharedPreferences(
                SignUpActivity.class.getSimpleName(), Context.MODE_PRIVATE);
      /*  int appVersion = getAppVersion();
        Log.i(TAG, "Saving regId on app version " + appVersion);*/
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.commit();
    }

}

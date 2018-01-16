package com.tp.locator;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Joe on 6/1/2014.
 */
public class MessageSender {
    private static final String TAG = "MessageSender";
    AsyncTask<Void, Void, String> sendTask;
    AtomicInteger ccsMsgId = new AtomicInteger();

    public void sendMessage(final Bundle data, final GoogleCloudMessaging gcm ) {

        MySmsReceiver.logger.info("in MessageSender sendMsg ");
        sendTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                String id = Integer.toString(ccsMsgId.incrementAndGet());

                try {
                    Log.d(TAG, "messageid: " + id);
                    MySmsReceiver.logger.info("sending msg to GCM . msg is "+data.toString());
                    gcm.send(Config.GOOGLE_PROJECT_ID + "@gcm.googleapis.com", id,
                            data);
                    Log.d(TAG, "After gcm.send successful.");
                } catch (IOException e) {
                    Log.d(TAG, "Exception: " + e);
                    e.printStackTrace();
                }
                MySmsReceiver.logger.info("sent  msg to GCM . msg is "+data.toString()+"  id is "+id.toString());
                return "Message ID: "+id+ " Sent.";
            }

            @Override
            protected void onPostExecute(String result) {
                sendTask = null;
                Log.d(TAG, "onPostExecute: result: " + result);
            }

        };
        sendTask.execute(null, null, null);
    }

}

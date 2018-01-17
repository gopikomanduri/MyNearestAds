package com.tp.locator;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Aruna on 18-12-2017.
 */

public class MsgHandlerAsync extends AsyncTask {

    public static String lastAddress = "";

    public static final String TAG = "GCMNotificationIntentService";

    public static ConcurrentLinkedQueue<String> numsList = new ConcurrentLinkedQueue<String>();

    public static ConcurrentLinkedQueue<String> nearestnumslist = new ConcurrentLinkedQueue<String>();

    public static ConcurrentHashMap<String,Integer> existingNearestNums = new ConcurrentHashMap<String, Integer>();

    public static Context tempCtx = null;

    @Override
    protected Object doInBackground(Object[] params) {
        GCMNotificationIntentService gcmIntent = new GCMNotificationIntentService();
        Intent intent;
        while(true)
        {

            while((intent = SocketService.receivedMsgs.poll()) != null)
            {
                gcmIntent.onHandleIntent(intent);
            }
        }
    }
}

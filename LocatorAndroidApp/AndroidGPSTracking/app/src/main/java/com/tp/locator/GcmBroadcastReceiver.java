package com.tp.locator;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by user on 8/22/2015.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("GcmBroadcastReceiver",
                "onReceive: notification received.");
        //Gopi .. commented here for websock.
//        if(SocketCommunicationEstablisher.mysock == null)
//            SocketCommunicationEstablisher.mysock = MyWebsocketClient.createWebSocketClient();
//        MyWebsocketClient.mctx = context;
        ComponentName comp = new ComponentName(context.getPackageName(),
                com.tp.locator.GCMNotificationIntentService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
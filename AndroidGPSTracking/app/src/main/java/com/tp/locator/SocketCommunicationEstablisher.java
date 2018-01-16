package com.tp.locator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.security.PublicKey;

import tech.gusavila92.websocketclient.WebSocketClient;

/**
 * Created by Aruna on 19-10-2017.
 */

public class SocketCommunicationEstablisher extends BroadcastReceiver {
   // public  static WebSocketClient mysock = null;
    public static String randomNum = "";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("SocketCommunication", "In onReceive");

        context.startService( new Intent(context, GCMNotificationIntentService.class));
        context.startService( new Intent(context, SocketService.class));


    }
}

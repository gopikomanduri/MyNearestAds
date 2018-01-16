package com.tp.locator;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by Aruna on 19-10-2017.
 */

public class WakefulReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, GCMNotificationIntentService.class);
        startWakefulService(context,service);
    }
}

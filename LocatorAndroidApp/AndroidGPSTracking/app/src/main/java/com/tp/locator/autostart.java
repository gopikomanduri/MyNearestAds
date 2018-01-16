package com.tp.locator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by user on 8/2/2015.
 */
public class autostart extends BroadcastReceiver
{

    public void onReceive(Context arg0, Intent arg1)
    {
        Intent intent = new Intent(arg0,ContactUpdateService.class);
        arg0.startService(intent);

        intent = new Intent(arg0,DataService.class);
        arg0.startService(intent);
        Log.i("Autostart", "started");
    }
}
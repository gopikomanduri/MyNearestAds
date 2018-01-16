package com.tp.locator;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Aruna on 19-10-2017.
 */

public class SocketCommsService extends Service {


    public SocketCommsService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
         super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT ", "ondestroy !");
        Intent broadcastIntent = new Intent("com.tp.locator.SocketCommunicationEstablisher");
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime = 0;

    public SocketCommsService(Context appCtx)
    {
        super();
    }

    public void startTimer()
    {
        timer = new Timer();

    }
    public void initializeTimerTask()
    {
        timerTask = new TimerTask() {
            @Override
            public void run() {

            }
        };
    }


    public void stoptimertask()
    {
        if(timer != null)
        {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

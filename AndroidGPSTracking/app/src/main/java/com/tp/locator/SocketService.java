package com.tp.locator;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;

import tech.gusavila92.websocketclient.WebSocketClient;

import static android.app.AlarmManager.ELAPSED_REALTIME;
import static android.os.SystemClock.elapsedRealtime;
import static com.tp.locator.MySmsReceiver.message;

/**
 * Created by Aruna on 26-11-2017.
 */


 class CommsComparator implements Comparator<String>
{
    @Override
    public int compare(String x, String y)
    {
        // Assume neither string is null. Real code should
        // probably be more robust
        // You could also just return x.length() - y.length(),
        // which would be more efficient.
        Gson gson = new Gson();

        int ret = 0;
        Comms comsObj = null;
        try
        {
            if(x != null)
             comsObj = gson.fromJson(x, Comms.class);
            if(comsObj != null)
               ret = 1;
        }
        catch(Exception ex)
        {
            ret = 0;
        }

        return ret;
    }
}

public class SocketService extends Service {
    public  static WebSocketClient mysock = null;
    public static MyKeepAliveAsync aliveAsyncObj = new MyKeepAliveAsync();
    public static MessagePusherAsync msgPusherAsync = new MessagePusherAsync();
    public static String lastTrackMsgId = "";
    public static String lastTrackResMsgId = "";
    public static MySmsReceiver obj = new MySmsReceiver();



    static Comparator<String> comparator = new CommsComparator();

    public static PriorityQueue<String> messagesToPush = new PriorityQueue<String>(100,comparator);
    public static ConcurrentLinkedQueue<Intent> receivedMsgs = new ConcurrentLinkedQueue<Intent>();
   public static GCMNotificationIntentService gcmIntent = new GCMNotificationIntentService();



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("SocketService", "In OnTabRemoved");

        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(
                getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmService.set(ELAPSED_REALTIME, elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }

    public void onCreate() {
        super.onCreate();
        mTimer = new Timer();
        msgTimer = new Timer();
        mTimer.schedule(timerTask, 2000, 2 * 1000);
        msgTimer.schedule(msgTimerTask,1000,1*1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("SocketService", "In onStartCommand");

        try {


        } catch (Exception e) {
            e.printStackTrace();
        }
        MyWebsocketClient.mctx = this;
        GCMNotificationIntentService.tempCtx = this;
        mysock = MyWebsocketClient.createWebSocketClient();
    //    new MsgHandlerAsync().execute();

        // return super.onStartCommand(intent, flags, startId);
        return START_STICKY;

    }

    private Timer mTimer;
    private Timer msgTimer;

    TimerTask msgTimerTask = new TimerTask() {
        @Override
        public void run() {
            new MessagePusherAsync().execute();
        }
    };

    TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
          //  if(aliveAsyncObj.getStatus() != AsyncTask.Status.RUNNING)
            //aliveAsyncObj.execute();
            new MyKeepAliveAsync().execute();
        }
    };

    public void onDestroy() {
        Log.d("SocketService", "In onDestroy");

        try {
          //  mTimer.cancel();
         //   timerTask.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent("com.tp.locator.SensorRestarterBroadcastReceiver");
        intent.putExtra("yourvalue", "torestore");
        sendBroadcast(intent);
    }
}

package tp.com.otpclient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Aruna on 07-12-2017.
 */

public class smsService extends Service {

    public static ConcurrentLinkedQueue<String> receivedContacts = new ConcurrentLinkedQueue<>();
    public static ConcurrentHashMap<String,String> otpSent = new ConcurrentHashMap<>();
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("smsService", "In onStartCommand");

        try {


        } catch (Exception e) {
            e.printStackTrace();
        }

        // return super.onStartCommand(intent, flags, startId);
        return START_STICKY;

    }
}

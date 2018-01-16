package tp.com.otpclient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Aruna on 07-12-2017.
 */

public class OTPReceiver extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("SocketService", "In onStartCommand");

        try {


        } catch (Exception e) {
            e.printStackTrace();
        }

        // return super.onStartCommand(intent, flags, startId);
        return START_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

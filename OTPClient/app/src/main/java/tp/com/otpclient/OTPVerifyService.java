package tp.com.otpclient;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import tech.gusavila92.websocketclient.WebSocketClient;

/**
 * Created by Aruna on 07-12-2017.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class OTPVerifyService extends Service {
    public static WebSocketClient conn;
public static ConcurrentLinkedDeque<Comms> numbersTosendSms = new ConcurrentLinkedDeque<>();
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("OTPVerifyService", "In onStartCommand");

        try {


        } catch (Exception e) {
            e.printStackTrace();
        }
        conn = MyWebsocketClient.createWebSocketClient();
        new VerificationAsync().execute("");

        // return super.onStartCommand(intent, flags, startId);
        return START_STICKY;

    }

}

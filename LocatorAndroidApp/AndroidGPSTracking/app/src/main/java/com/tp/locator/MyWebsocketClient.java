    package com.tp.locator;

    import android.app.Application;
    import android.content.Context;
    import android.content.Intent;
    import android.os.AsyncTask;
    import android.os.CountDownTimer;
    import android.os.Handler;
    import android.os.Looper;
    import android.os.SystemClock;
    import android.util.Log;
    import android.view.View;
    import android.widget.Toast;

    import com.google.gson.Gson;
    import com.tp.locator.Events.MsgRcvd;
    import com.tp.locator.Events.MsgTimer;
    import com.tp.locator.Events.Progress;
    import com.tp.locator.PhoneUtil.DateTime;

    import org.acra.ACRA;

    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.net.URI;
    import java.net.URISyntaxException;
    import java.util.Map;

    import javax.mail.Message;
    import javax.mail.MessagingException;
    import javax.mail.PasswordAuthentication;
    import javax.mail.Transport;
    import javax.mail.internet.AddressException;
    import javax.mail.internet.InternetAddress;
    import javax.mail.internet.MimeMessage;

    import de.greenrobot.event.EventBus;
    import tech.gusavila92.websocketclient.WebSocketClient;

    import static com.tp.locator.DataService.dbObj;
    import static com.tp.locator.MyKeepALiveClass.msgFromServer;
    import static com.tp.locator.MyKeepALiveClass.serverCnt;
    import static com.tp.locator.MyKeepALiveClass.timerObj;
    import static com.tp.locator.SocketService.gcmIntent;
    import static com.tp.locator.SocketService.messagesToPush;
    import static com.tp.locator.SocketService.mysock;
    import static java.lang.Thread.State.WAITING;

    import java.util.Properties;
    import java.util.concurrent.ConcurrentLinkedQueue;

    import javax.mail.Message;
    import javax.mail.MessagingException;
    import javax.mail.PasswordAuthentication;
    import javax.mail.Session;
    import javax.mail.Transport;
    import javax.mail.internet.InternetAddress;
    import javax.mail.internet.MimeMessage;

    /**
     * Created by Aruna on 26-11-2017.
     */

    class MyKeepAliveAsync extends AsyncTask{

//        public static volatile int msgFromServer = 1;
//        public static volatile boolean flag = false;
//        public static volatile int serverCnt = 0;
//        public static volatile CountDownTimer timerObj = null;

        public MyKeepAliveAsync(){
           // EventBus.getDefault().register(this);
        }

//        public  void onEvent(MsgTimer tim)
//        {
//            // initTimer();
//            //   SocketService.mysock = MyWebsocketClient.createWebSocketClient();
//            new TimerAsyncTask().execute();
//
//
//        }



        @Override
        protected Object doInBackground(Object[] params) {
            //while(true)
            {
                //   Thread.sleep(1000*30);

                byte[] msg = new byte[1];
                DateTime timestamp = new DateTime();
                msg[0] = (byte)1;
//                    if((msgFromServer == 0) || (SocketService.mysock == null))
//                    {
//                        SocketService.mysock = MyWebsocketClient.createWebSocketClient();
//                    }
                //   else {
                String tempStr = msg.toString();
                if(dbObj != null)
                {
                    tempStr = dbObj.getMyNumber()+timestamp.getTodaysDate()+timestamp.getCurrentTime();
                }
                else
                {
                    dbObj = ContactContentProvider.database;
                    tempStr = dbObj.getMyNumber() + timestamp.getTodaysDate()+timestamp.getCurrentTime();

                }
                Log.i("MyKeepAliveClass","sending dummy msg "+tempStr);
                messagesToPush.offer(tempStr);

                MyKeepALiveClass.msgFromServer = 0;

                //  }

            }
            return null;
        }
    }
    class MyKeepALiveClass
    {

       public static volatile int msgFromServer = 1;
        public static volatile boolean flag = false;
        public static volatile int serverCnt = 0;
        public static volatile CountDownTimer timerObj = null;
           public MyKeepALiveClass(){

               if (!EventBus.getDefault().isRegistered(this)) {
                   EventBus.getDefault().register(this);
               }
                //EventBus.getDefault().register(this);
            }
    //    @Override



    public  void onEvent(MsgTimer tim)
    {
       // initTimer();
     //   SocketService.mysock = MyWebsocketClient.createWebSocketClient();
        new TimerAsyncTask().execute();


    }

    }

    class TimerAsyncTask extends AsyncTask
            {
    int localCounter = 0;
                @Override
                protected Object doInBackground(Object[] params) {
                    try {
                        Thread.sleep(1000*5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPreExecute() {
                    localCounter = serverCnt;
                }

                @Override
                protected void onPostExecute(Object o) {
                    new MyLoggerTask().execute();
                     if(localCounter >= serverCnt) {
                         ++serverCnt;
                         SocketService.mysock = MyWebsocketClient.createWebSocketClient();
                     }
                        EventBus.getDefault().post(new MsgTimer());
                }
            }

    class LogUtil {
        public static StringBuilder readLogs() {
            StringBuilder logBuilder = new StringBuilder();
            try {
                Process process = Runtime.getRuntime().exec("logcat -d");
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    logBuilder.append(line + "\n");
                }
                Runtime.getRuntime().exec("logcat -c");
            } catch (IOException e) {
                int x = 20;
            }
            return logBuilder;

        }



      //  @Override
        public void run() {
            while(true) {
                try {
                    new MyLoggerTask().execute();
                    Thread.sleep(1000*60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                }

            }
        }

        class MessagePusherAsync extends AsyncTask{
            private static final String TAG = "MessagePusher";

            @Override
            protected Object doInBackground(Object[] params) {
              //  while(true) {
                    while (messagesToPush.isEmpty() == false) {
                        String msg = messagesToPush.poll();
                        if (mysock != null && msg != null) {
                            Log.i(TAG,"Before send . msg = "+msg);
                            MyKeepALiveClass.msgFromServer = 0;
                            //gopi ..
                            mysock.send(msg);

                        }
                    }
             //   }
                return null;
            }
        }

        class MessagePusher extends Thread
    {
        private static final String TAG = "MessagePusher";


        @Override
        public void run() {
            while(true) {
                while (messagesToPush.isEmpty() == false) {
                    String msg = messagesToPush.poll();
                    if (mysock != null && msg != null) {
                        Log.i(TAG,"Before send . msg = "+msg);
                        MyKeepALiveClass.msgFromServer = 0;
                        //gopi ..
                       mysock.send(msg);
                    }
                }
            }
        }
    }
    public class MyWebsocketClient {
        static WebSocketClient webSocketClient;
      //  static  MyKeepALiveClass healthThread = new MyKeepALiveClass();
       // static MessagePusher senderThread = new MessagePusher();
       // static LogUtil logcatSender = new LogUtil();
       public static Context mctx;
        private static final String TAG = "MyWebsocketClient";
        private Handler mHandler = new Handler(Looper.getMainLooper());


        public static  WebSocketClient createWebSocketClient() {
            URI uri;
            try {
                uri = new URI("ws://35.196.222.199:9090");
            }
            catch (URISyntaxException e) {
                e.printStackTrace();
                return null;
            }
//            try {
//                if(senderThread != null && senderThread.isAlive() )
//                    senderThread.wait();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            webSocketClient = new WebSocketClient(uri) {


                @Override
                public void onOpen() {

                  //  messagesToPush.clear();
                    MyKeepALiveClass.msgFromServer = 1;

                    Log.i(TAG,"In OnOpen. ");

                    webSocketClient.send("Hello! reconnecting for "+serverCnt+"th time");
//                    if(senderThread != null && senderThread.getState() == WAITING )
//                    senderThread.notify();
                  //  new TimerAsyncTask().execute();
                    new TimerAsyncTask().execute();


                }

                @Override
                public void onTextReceived(String message) {
                    ++serverCnt;
                    Log.i(TAG,"onTextReceived . "+message);

                    if(timerObj != null)
                    {
                        timerObj.cancel();
                    }


                    MyKeepALiveClass.msgFromServer = 1;

                    Intent intent = new Intent(mctx, GCMNotificationIntentService.class);

                    Gson gson = new Gson();
                    try {
                        Comms comsObj = gson.fromJson(message, Comms.class);

                        if (comsObj != null) {
                            for (Map.Entry<String, Object> entry : comsObj.params.entrySet()) {
                                intent.putExtra(entry.getKey(), entry.getValue().toString());
                                //  System.out.println("key=" + entry.getKey() + ", value=" + entry.getValue());
                            }
                            intent.setAction("com.google.android.c2dm.intent.RECEIVE");
                            intent.putExtra("message_type", "gcm");

                         //   SocketService.receivedMsgs.offer(intent);
                            gcmIntent.onHandleIntent(intent);
                        }
                        {
                            MsgRcvd msgRcvdEvent = new MsgRcvd();
                            msgRcvdEvent.msg = message;
                        //    EventBus.getDefault().post(msgRcvdEvent);

                        }
                    }
                        catch(Exception ex)
                        {
                            MsgRcvd msgRcvdEvent = new MsgRcvd();
                            msgRcvdEvent.msg = message;
                         //   EventBus.getDefault().post(msgRcvdEvent);
                            int x = 20;
                        }

                    System.out.println("onTextReceived");
                    //timerObj.start();

                //    MyKeepALiveClass.initTimer();

                }

                @Override
                public void onBinaryReceived(byte[] data) {
                    System.out.println("onBinaryReceived");
                }

                @Override
                public void onPingReceived(byte[] data) {
                    System.out.println("onPingReceived");
                }

                @Override
                public void onPongReceived(byte[] data) {
                    System.out.println("onPongReceived");
                }

                @Override
                public void onException(Exception e) {
                    Log.i(TAG,"In socket onException. message = "+e.getLocalizedMessage());

                    Log.i(TAG,"In socket onException. "+e.getStackTrace());
                    String st;
                    st = e.getMessage()+"\n"+e.getStackTrace().toString();
                    GMailSender gMailSender = new GMailSender("locatorlogs.com", "bananatable143");
                    msgFromServer = 0;
                    serverCnt = 0;
                    MyKeepALiveClass.flag = true;
                    new TimerAsyncTask().execute();
                }

                @Override
                public void onCloseReceived() {
                 //   SocketCommunicationEstablisher.mysock = MyWebsocketClient.createWebSocketClient();
                    Log.i(TAG,"In socket onCloseReceived. message = ");

                    msgFromServer = 0;
                    MyKeepALiveClass.flag = true;
                    serverCnt = 0;
                    new TimerAsyncTask().execute();
                }


            };

            //  webSocketClient.setConnectTimeout(10000);
            //  webSocketClient.setReadTimeout(60000);
            if (dbObj == null)
                dbObj = ContactContentProvider.database;
            if(dbObj != null && dbObj.getMyNumber() != null)
            webSocketClient.addHeader("contact", dbObj.getMyNumber());
            else
                webSocketClient.addHeader("Random","");
           //    webSocketClient.enableAutomaticReconnection(5000);
            webSocketClient.connect();
            MyKeepALiveClass.msgFromServer = 1;

//            if(healthThread.isAlive() != true)
//            {
//                MyKeepALiveClass.flag=false;
//                healthThread.start();
//            }
//if(senderThread.isAlive() != true)
//    senderThread.start();

//            if(logcatSender.isAlive() != true)
//                logcatSender.start();
  return webSocketClient;
        }

    }


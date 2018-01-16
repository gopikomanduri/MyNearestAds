package com.tp.locator;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;

import com.tp.locator.Events.PhoneContactsUpdateEvent;

/**
 * Created by user on 8/2/2015.
 */
public class ContactUpdateService extends Service {
    android.content.ContentResolver cr;
    ContactListener listener;

    @Override
    public void onCreate() {
  //      super.onCreate();
  //      EventBus.getDefault().register(this);
        cr = getContentResolver();
     //   Gopi .. commented websock
//        if(SocketCommunicationEstablisher.mysock == null)
//        SocketCommunicationEstablisher.mysock = MyWebsocketClient.createWebSocketClient();
//
//        MyWebsocketClient.mctx = this;

        listener = new ContactListener(new Handler());
        listener.setCtx(this);
        // listener.
        cr.registerContentObserver(
                ContactsContract.Contacts.CONTENT_URI,
                true,
                listener);
    }
    public void onEvent(PhoneContactsUpdateEvent event)
    {
       // newItems = PhoneContactsUpdateEvent.addedContacts;

        int x = 20;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

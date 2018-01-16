package com.tp.locator;

/**
 * Created by user on 8/2/2015.
 */

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;

import com.tp.locator.Events.PhoneContactsUpdateEvent;
import com.tp.locator.PhoneUtil.PhoneContact;
import com.tp.locator.PhoneUtil.libphonenumber.src.com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

public class ContactListener extends ContentObserver {

    Context ctx = null;
    private int updateCount = 0;
    android.content.ContentResolver cr;
    private static ContactListener m_currentObj = null;
    static List<PhoneContact> numbersList = new ArrayList<PhoneContact>();
    static List<PhoneContact> addedNumbers = new ArrayList<PhoneContact>();
    ContactUpdateService contactUpdateService = null;
    HashMap<String,PhoneContact> phoneObjects = new HashMap<String, PhoneContact>();
    public ContactListener(Handler handler) {
        super(handler);
      //  contactUpdateService = new ContactUpdateService();
        m_currentObj = this;

    }

    public void setCtx(Context context)
    {
        ctx = context;
        EventBus eventBus = EventBus.getDefault();
      //  EventBus.getDefault().post(new PhoneContactsUpdateEvent(addedNumbers));
        new loadContactsAsync().execute();
    }
    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }


    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        new loadContactsAsync().execute();

    }
    private static allowedContacts enableMock()
    {
        PhoneContact obj = new PhoneContact();
        obj.contactName = "Mock testing";
        obj.contactNumber = "+910000000000";
        allowedContacts tempObj = new allowedContacts(
                obj.contactNumber,
                obj.contactName,
                "w r u",
                "12321",
                "12321",
                0,
                "12321",
                "12321",
                "test");
        return tempObj;
    }

    private class loadContactsAsync extends AsyncTask<Void,Void,Integer>
    {

        @Override
        protected Integer doInBackground(Void... params) {

            int x = 20;
            getNumbersFromPhone();

            Log.d("GopiserviceBG", "In postexecute");
            EventBus eventBus = EventBus.getDefault();
            EventBus.getDefault().post(new PhoneContactsUpdateEvent(addedNumbers));
            return x;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Integer x) {
            Log.d("GopiservicePost", "In postexecute");
            super.onPostExecute(x);

        }

        private void getNumbersFromPhone()
        {
          //  List<PhoneContact> numbersList = new ArrayList<PhoneContact>();
            String countryCode = Utils.getCountryCode(ctx);
            cr = ctx.getContentResolver();

            Cursor phones = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                    null, null);
            while (phones.moveToNext()) {

                String name = phones
                        .getString(phones
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                String phoneNumber = phones
                        .getString(phones
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                if(phoneNumber.length() > 5) {
                    if (phoneNumber.startsWith("+") == false) {

                        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                        Integer isdcode = phoneUtil.getCountryCodeForRegion(countryCode.toUpperCase());
                        if(phoneNumber.startsWith("0") == true)
                        {
                            String tempNum = phoneNumber.substring(1);
                            phoneNumber = tempNum;
                        }
                        phoneNumber = "+" + isdcode.toString() + phoneNumber;
                    }

                    phoneNumber = Utils.parseNumber(phoneNumber);

                    PhoneContact obj = new PhoneContact();
                    obj.contactName = name;
                    obj.contactNumber = phoneNumber;
                    String hashStr = Utils.sha1Hash(phoneNumber);
                    obj.shaValue = hashStr;
                    Log.d("GopiService","at phonenumber ");
                    Log.d("GopiService","at phonenumber "+phoneNumber);
                    if(phoneObjects.containsKey(hashStr) == false) {
                        numbersList.add(obj);
                        addedNumbers.add(obj);
                        phoneObjects.put(hashStr,obj);
                    }
                    else
                    {

                    }
                }
            }

         //   return numbersList;
        }
    }

 /*   @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
    }*/
}


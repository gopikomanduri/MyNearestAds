package com.tp.locator;

import android.telephony.gsm.SmsManager;

import com.tp.locator.PhoneUtil.DateTime;

/**
 * Created by user on 8/22/2015.
 */
public class SmsResponder implements IResponder {
    @Override
    public void sendMsg(String toAddress, String msg,DateTime dateTime) {

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(toAddress, null, msg, null, null);
    }
}

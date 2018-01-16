package com.tp.locator.PhoneUtil;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by user on 5/6/2015.
 */
public class IMEINumber {
    private static String imeiNumber;
    private static Context ctx;
    private static TelephonyManager tel;
    public static void setContext(Context ctx)
    {
        ctx = ctx;
    }
    public static String getImeiNumber()
    {
        if(imeiNumber == null)
        {

            tel = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            imeiNumber = new String();
            imeiNumber = tel.getDeviceId();
        }
        return imeiNumber;
    }
}

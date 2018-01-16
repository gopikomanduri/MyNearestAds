package com.tp.locator.PhoneUtil;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;


/**
 * Created by user on 7/25/2015.
 */
public class invokeCall {
    public static void invokeCall(String number,Activity activity)
    {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        activity.startActivity(callIntent);
    }
}

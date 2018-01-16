package com.tp.locator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;

/**
 * Created by user on 8/19/2015.
 */
public class SaveSharedPreference
{
    static final String PREF_NAME = "username";
    static final String PREF_MOBILE_NUMBER= "mobile";
    static final String PREF_SEX= "sex";
    static HashMap<String,Integer> isAllowed = new HashMap<String, Integer>();

        static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserDetails(Context ctx, String Name, String number, String sex)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_NAME, Name);
        editor.putString(PREF_MOBILE_NUMBER, number);
        editor.putString(PREF_SEX, sex);

        editor.commit();
    }



    public static UserRegistrationDetails getUserDetails(Context ctx)
    {
        UserRegistrationDetails obj = new UserRegistrationDetails();
        obj.userName = getSharedPreferences(ctx).getString(PREF_NAME, "");
        obj.contact = getSharedPreferences(ctx).getString(PREF_MOBILE_NUMBER, "");
        obj.sex = getSharedPreferences(ctx).getString(PREF_SEX, "");
        return obj;
    }
}
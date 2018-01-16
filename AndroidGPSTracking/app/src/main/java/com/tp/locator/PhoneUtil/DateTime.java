package com.tp.locator.PhoneUtil;

import android.util.Log;

import java.util.Calendar;

/**
 * Created by user on 11/22/2015.
 */
public class DateTime {
    //Date format MM/DD/YYYY
    public String getTodaysDate() {

        final Calendar c = Calendar.getInstance();

        return(new StringBuilder()
                .append(c.get(Calendar.MONTH) + 1).append("/")
                .append(c.get(Calendar.DAY_OF_MONTH)).append("/")
                .append(c.get(Calendar.YEAR)).append(" ")).toString();
    }

    //Time format HH:MM:SS:MS
    public String getCurrentTime() {

        final Calendar c = Calendar.getInstance();

        return(new StringBuilder()
                .append(c.get(Calendar.HOUR_OF_DAY)).append(":")
                .append(c.get(Calendar.MINUTE)).append(":")
                .append(c.get(Calendar.SECOND)).append(" ")
                .append(c.get(Calendar.MILLISECOND)).append(" ")).toString();
    }

    //Date format YYYYMMDD
    private String getTodaysDate2() {

        final Calendar c = Calendar.getInstance();
        int todaysDate =     (c.get(Calendar.YEAR) * 10000) +
                ((c.get(Calendar.MONTH) + 1) * 100) +
                (c.get(Calendar.DAY_OF_MONTH));
        Log.w("DATE:",String.valueOf(todaysDate));
        return(String.valueOf(todaysDate));

    }
    public String getTimeStamp()
    {
        return getTodaysDate()+":"+getCurrentTime();
    }

    //Time format HHMMSS
    private String getCurrentTime2() {

        final Calendar c = Calendar.getInstance();
        int currentTime =     (c.get(Calendar.HOUR_OF_DAY) * 10000) +
                (c.get(Calendar.MINUTE) * 100) +
                (c.get(Calendar.SECOND));
        Log.w("TIME:", String.valueOf(currentTime));
        return(String.valueOf(currentTime));

    }
}

package com.tp.locator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by user on 7/23/2015.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    String TAG = "Gopi";
    public static boolean isLoading = true;
    @Override
    public Fragment getItem(int index) {

        Integer IndexInt = index;
        Log.v("switch is ", "");
        // Log.d(TAG, "entered sendUpdatesToUI");
        Log.v(IndexInt.toString(), "");
        Log.d(TAG, IndexInt.toString());
        switch (index) {
            case 0:
                // Contacts fragment activity
                isLoading = true;
                return new LocationTrackerFragment();
           case 1:
                // Messages fragment activity
                return new LocationFragment();
//            case 2:
//                // Followers fragment activity
//                return new SettingsFragment();
            case 2:
                // Followers fragment activity
                return new SavedLocationsFragment();
            case 3:
                // Followers fragment activity
                return new NearestPeople();
            case 4:
                // Followers fragment activity
                return new AnnouncementsFragment();
        /*    case 6:
                // Followers fragment activity
                return new DealsFragment();
            case 7:
                // Followers fragment activity
                return new SettingsFragment();*/
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 5;
    }

}

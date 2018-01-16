package com.tp.locator;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by user on 7/23/2015.
 */
public class MainActivity extends FragmentActivity implements
        TabListener {

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    public static Context applicationCtx;

    // Tab titles
  //  private String[] tabs = { "Location","Traveled Locations","Settings","Saved Locations","Nearest People","Notifications" };
    private String[] tabs = { "Location","Traveled Locations","Saved Locations","Nearest People","Notifications" };
  //  private String[] tabs = { "Location","Traveled Locations" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      //  addShortcut();
        setContentView(R.layout.activity_main);
        applicationCtx = this;

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();

        FragmentManager fm = getSupportFragmentManager();


        android.support.v4.app.FragmentTransaction fragmentTransaction=fm.beginTransaction();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());



        int cnt = mAdapter.getCount();

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        int x = 20;
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        int x = tab.getPosition();
        viewPager.setCurrentItem(x);
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }
    private void addShortcut() {
        Intent shortcutIntent = new Intent(getApplicationContext(), MainActivity.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);
        shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        int flags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT;
        shortcutIntent.addFlags(flags);

        Intent addIntent = new Intent();
        addIntent.putExtra("duplicate", false);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.app_name));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource
                .fromContext(getApplicationContext(), R.drawable.ic_launcher));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);
    }

}


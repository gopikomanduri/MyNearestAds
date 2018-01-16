package com.tp.locator;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TypefaceSpan;

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
    private String[] tabs = { "Location","Traveled Locations","Saved Locations","Nearest People","Notifications","Announcements" };
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
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#303F9F")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#448AFF")));




        FragmentManager fm = getSupportFragmentManager();


        android.support.v4.app.FragmentTransaction fragmentTransaction=fm.beginTransaction();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());



        int cnt = mAdapter.getCount();

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(true);
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

       // viewPager.setBackgroundColor(Color.GREEN);


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
     //   tab.setText(tabs[x]+" !");
        viewPager.setCurrentItem(x);

        Typeface typeface = Typeface.createFromAsset(getAssets(),
                "fonts/Bender-Solid.otf");
         SpannableString s = new SpannableString(tabs[x]);
        s.setSpan(typeface, 0, tabs[x].length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);


//        TypefaceSpan typefaceSpan = new TypefaceSpan("fonts/Bender-Solid.otf");
//        Typeface.createFromAsset(getAssets(), "fonts/Bender-Solid.otf");
//
////        textView.setSpan(typefaceSpan, indexStart, textLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////
////
//      //  SpannableString s = new SpannableString(tabs[x]);
////        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Bender-Solid.otf");
////
////        SS.setSpan (new CustomTypefaceSpan("", font2), 0, 4, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
//
//
//       // s.setSpan(typefaceSpan, 0, tabs[x].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        SpannableString s = new SpannableString(tabs[x]);
//        s.setSpan(new TypefaceSpan(context, context.getString(R.string.custom_font)), 0, s.length(),
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);



        // Update the action bar title with the TypefaceSpan instance
        //actionBar.setTitle(s);
        tab.setText(s +" !");
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        int x = tab.getPosition();
        tab.setText(tabs[x]);
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


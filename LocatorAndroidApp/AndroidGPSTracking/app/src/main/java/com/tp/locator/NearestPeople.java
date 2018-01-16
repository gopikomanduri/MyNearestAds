package com.tp.locator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Aruna on 31-10-2017.
 */

public class NearestPeople extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static Activity activity;
    static NearestPeople thisObj;
    static NearestPeopleAsyncDataLoader dataDyn = null;
    ListView lview;
    static boolean isExecuted =  false;
    static NearestPeopleAdapter npadapter;
    static ArrayList<NearestPeopleClass> nearestpeoplelist = new ArrayList<NearestPeopleClass>();
    static LinkedList<NearestPeopleClass> linkedList = new LinkedList<NearestPeopleClass>();




    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.nearestfriends, container, false);
     //  nearestnumslist.add("8179442558");
        if(thisObj == null)
            thisObj =  this;


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();
        lview = (ListView) activity.findViewById(R.id.nearestppllist);

//        if(dataDyn == null)
//            dataDyn = new NearestPeople.LocationFragmentAsyncDataLoader();
//        if(NearestPeople.isExecuted == false)
//            dataDyn.execute("");
        new NearestPeopleAsyncDataLoader().execute("");

        npadapter = new NearestPeopleAdapter(activity,R.layout.nearest_row,nearestpeoplelist);
        lview.setAdapter(npadapter);
        updatenearestpeople();

        //gopi .. removed from here to added in oncreateview
         new NearestPeopleAsyncDataLoader().execute();

    }
public static  void invokeupdatenearestpeople()
{
    if(thisObj != null)
        thisObj.updatenearestpeople();
}


    private void updatenearestpeople() {
        if(activity == null)
            return;

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if(GCMNotificationIntentService.nearestnumslist == null || GCMNotificationIntentService.nearestnumslist.size() <=0 )
                    return;
                nearestpeoplelist.clear();
                int rowsCnt = GCMNotificationIntentService.nearestnumslist.size();
                Iterator<String> it = GCMNotificationIntentService.nearestnumslist.iterator();
             //   announcementslist.clear();
                if(rowsCnt !=0)

                {
                    while (it.hasNext()) {
                       String contact = it.next();

                        NearestPeopleClass obj = new NearestPeopleClass();
                        obj.number = contact;
                        nearestpeoplelist.add(obj);

                    }


                 //   annAdapter.notifyDataSetChanged();

                    //    locationadapter.notifyDataSetChanged();
                }
                GCMNotificationIntentService.nearestnumslist.clear();
                npadapter.notifyDataSetChanged();

            }

        });
    }

    public class NearestPeopleAsyncDataLoader extends AsyncTask<String,Void,Void>
    {
        private final ProgressDialog dialog = new ProgressDialog(activity);

        @Override
        protected Void doInBackground(String... params) {
            NearestPeople.isExecuted = true;

                updatenearestpeople();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Fetching History from Database...");
            dialog.show();
        }


        @Override
        protected void onPostExecute(Void res) {
            super.onPostExecute(res);
            dialog.dismiss();
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    updatenearestpeople();
                    lview.setAdapter(npadapter);
                    npadapter.notifyDataSetChanged();
                }
            });

            // adpt.setItemList(result);
            //..gopi
            //  locationadapter.notifyDataSetChanged();
        }


        /* @Override
         protected Void doInBackground(Void... strings) {

             return null;
         }*/

    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

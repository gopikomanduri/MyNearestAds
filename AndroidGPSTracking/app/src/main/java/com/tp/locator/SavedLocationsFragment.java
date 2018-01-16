package com.tp.locator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.RadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by user on 1/16/2016.
 */
public class SavedLocationsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {
    static String MySavedLocationsURL  = "content://com.tp.locator.provider.allowedContacts/savedlocations";
    static Uri savedlocationsUri = Uri.parse(MySavedLocationsURL);
    public static Activity activity;
    static SavedLocationAdapter locationadapter;
    static ArrayList<SavedLocationsHistory> locationlist = new ArrayList<SavedLocationsHistory>();
    static ConcurrentLinkedQueue<SavedLocationsHistory> cLocationlist = new ConcurrentLinkedQueue<SavedLocationsHistory>();
    static LinkedList<SavedLocationsHistory> linkedList = new LinkedList<SavedLocationsHistory>();
    static SavedLocationsFragmentAsyncDataLoader dataDyn = null;
    static boolean isExecuted =  false;
    ListView lview;
    static int rowsCnt = 0;
    private SimpleCursorAdapter adapter;
    public static ClipboardManager myClipboard;
    private EditText nameSearch;
    static RadixTree<SavedLocationsHistory> savedLocations = new ConcurrentRadixTree<SavedLocationsHistory>(new DefaultCharArrayNodeFactory());



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.savedlocation_main, container, false);


        return rootView;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInf = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        int id = (int)menuInf.id;
        String addr = locationlist.get(id).address;
        String timestamp = locationlist.get(id).timestamp;
        if(item.getTitle()=="Share"){
           // Toast.makeText(activity, "Share ", Toast.LENGTH_LONG).show();
            ClipData myClip;
            String text = "hello world";
            myClip = ClipData.newPlainText("Address", addr);
            myClipboard.setPrimaryClip(myClip);
            Toast.makeText(activity,"Address copied .",Toast.LENGTH_LONG).show();
        }
        else if(item.getTitle()=="Delete"){
            Toast.makeText(activity,"Delete", Toast.LENGTH_LONG).show();
            if(LocationTrackerFragment.dbObj.deleteLocation(timestamp) == true);
            {

                locationadapter.notifyDataSetChanged();
                getDatabaseLocations();
            }
        }else{
            return false;
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select The Action");
        menu.add(0, v.getId(), 0, "Share");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();
        lview = (ListView) activity.findViewById(R.id.listviewsaved);

        nameSearch = (EditText) activity.findViewById(R.id.searchName);


        myClipboard = (ClipboardManager)activity.getSystemService(activity.CLIPBOARD_SERVICE);
        registerForContextMenu(lview);

//        if(dataDyn == null)
//            dataDyn = new LocationFragmentAsyncDataLoader();
//        if(SavedLocationsFragment.isExecuted == false)
//            dataDyn.execute("");
        new SavedLocationsFragmentAsyncDataLoader().execute("");

        locationadapter = new SavedLocationAdapter(activity,R.layout.savedlocation_row,locationlist);
        lview.setAdapter(locationadapter);
        getDatabaseLocations();

        int x = 50;
        int y = x+ 30;

        nameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String str = s.toString();
                        CharSequence s1 = str.toLowerCase();
                        Iterator<SavedLocationsHistory> values = savedLocations.getValuesForKeysStartingWith(s1).iterator();
                        locationlist.clear();
                        while (values.hasNext()) {

                            SavedLocationsHistory obj = values.next();
                            locationlist.add(obj);
                        }
                        lview.setAdapter(locationadapter);
                        locationadapter.notifyDataSetChanged();

                    }
                });


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    public class SavedLocationsFragmentAsyncDataLoader extends AsyncTask<String,Void,Void>
    {
        private final ProgressDialog dialog = new ProgressDialog(activity);

        @Override
        protected Void doInBackground(String... params) {
            SavedLocationsFragment.isExecuted = true;
            String addr = params[0];
            if(addr.length() > 0)
            {
               // updateTravelLocations(addr);
            }
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

            if(SavedLocationsFragment.activity == null)
                return;

            SavedLocationsFragment.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    Iterator<SavedLocationsHistory> reverseOrder = linkedList.descendingIterator();
                    while(reverseOrder.hasNext())
                    {
                        SavedLocationsHistory obj = reverseOrder.next();

                        locationlist.add(obj);
                        if(obj.name != null && obj.name.length() > 0)
                        savedLocations.put(obj.name.toLowerCase(),obj);
                        if(obj.professionalName != null && obj.professionalName.length() > 0)
                            savedLocations.put(obj.professionalName.toLowerCase(),obj);


                    }
                    lview.setAdapter(locationadapter);
                    locationadapter.notifyDataSetChanged();

                }
            });
        }
        public void updateTravelLocations(String name,String profname , String contact,String address)
        {
            SavedLocationsHistory obj = new SavedLocationsHistory();
            obj.name = name;
            obj.professionalName = profname;
            obj.contact = contact;
            obj.address = address;

            linkedList.addLast(obj);
            locationlist.clear();
            Iterator<SavedLocationsHistory> reverseOrder = linkedList.descendingIterator();
            while(reverseOrder.hasNext())
            {
                SavedLocationsHistory tempObj = reverseOrder.next();
                locationlist.add(tempObj);
            }
            locationadapter.notifyDataSetChanged();
        }
    }
    public static void getDatabaseLocations()
    {
        if(activity == null)
            return;
        Cursor c = activity.getContentResolver().query(savedlocationsUri, null, null,
                null, null);
        if(c == null)
            return;
        rowsCnt = c.getCount();
        linkedList.clear();
        locationlist.clear();
        if (rowsCnt != 0) {
            while (c.moveToNext()) {
                Integer id = c.getInt(c.getColumnIndex("id"));
                String name = c.getString(c.getColumnIndex("name"));
                String profname = c.getString(c.getColumnIndex("professionalname"));
                String contact = c.getString(c.getColumnIndex("contact"));
                String timeStamp = c.getString(c.getColumnIndex("timestamp"));
                String address = c.getString(c.getColumnIndex("address"));
                SavedLocationsHistory obj = new SavedLocationsHistory();
                obj.contact = contact;
                obj.professionalName = profname;
                obj.name = name;
                obj.address = address;
                obj.timestamp = timeStamp;

                linkedList.add(obj);

            }
            Iterator<SavedLocationsHistory> reverseOrder = linkedList.descendingIterator();
            while(reverseOrder.hasNext())
            {
                SavedLocationsHistory tempObj = reverseOrder.next();
                locationlist.add(tempObj);
            }
        }
        c.close();
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

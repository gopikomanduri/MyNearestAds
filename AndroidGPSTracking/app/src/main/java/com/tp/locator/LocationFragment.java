package com.tp.locator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.tp.locator.PhoneUtil.DateTime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by gkomandu on 7/31/2015.
 */

public class LocationFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    static String MyLocationsURL  = "content://com.tp.locator.provider.allowedContacts/locations";

    static Uri locationsUri = Uri.parse(MyLocationsURL);
    static Activity activity;
    static LocationAdapter locationadapter;
    static ArrayList<LocationHistory> locationlist = new ArrayList<LocationHistory>();
    static ConcurrentLinkedQueue<LocationHistory> cLocationlist = new ConcurrentLinkedQueue<LocationHistory>();
    static LinkedList<LocationHistory> linkedList = new LinkedList<LocationHistory>();
    static LocationFragmentAsyncDataLoader dataDyn = null;
    static boolean isExecuted =  false;
    ListView lview;

    private SimpleCursorAdapter adapter;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.location_main, container, false);




        return rootView;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInf = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        int id = (int)menuInf.id;
        String addr = locationlist.get(id).address;//+"\n"+locationlist.get(id).latitude+"\n"+locationlist.get(id).longitude;
        if(item.getTitle()=="Save"){
          //  Toast.makeText(activity,"Save ",Toast.LENGTH_LONG).show();
            initiatePopupWindow(addr);
        }
        else if(item.getTitle()=="Copy")
        {
            MyClipboardManager tempobj = new MyClipboardManager();
            tempobj.copyToClipboard(getActivity().getApplication().getApplicationContext(), addr);
        }
        else if(item.getTitle()=="Navigate")
        {
            int pos1 = addr.indexOf("https://");
            int pos2 = addr.indexOf("received");
          String mapurl = addr.substring(pos1,pos2-1);
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(mapurl));
         //   startActivity(intent);
            int latPos1 = addr.indexOf("LAT :");
            int latPos2 = addr.indexOf("LNG :");
            String latValue = addr.substring(latPos1+5, latPos2-2);
            int lngPos2 = addr.indexOf("Geohash_F");
            String lngValue = addr.substring(latPos2+5,lngPos2-2);

            Uri gmmIntentUri = Uri.parse("geo:"+latValue+","+lngValue);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
          //  String geoUri = "http://maps.google.com/maps?q=loc:" + latValue + "," + lngValue ;


        }
        else if(item.getTitle() == "Share"){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,addr);
            sendIntent.setType("text/plain");
            Intent.createChooser(sendIntent,"Share via");
            startActivity(sendIntent);
        }
        else if(item.getTitle()=="Delete"){
            Toast.makeText(activity,"Delete", Toast.LENGTH_LONG).show();
        }else{
            return false;
        }
        return true;
    }

    private PopupWindow pwindo;

    EditText sname = null;
    EditText sprofName = null;
    EditText scontact = null;
    String selectedAddr;
    private void initiatePopupWindow(String addr) {
        try {
// We need to get the instance of the LayoutInflater

            Button btnSave;
            Button btnCancel;
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.savepopupmenu,
                    (ViewGroup) getActivity().findViewById(R.id.popup_element));
            pwindo = new PopupWindow(layout, 700, 700, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
            sname = (EditText)layout.findViewById(R.id.saveName);
            sprofName = (EditText)layout.findViewById(R.id.saveProfessionalName);
            scontact = (EditText)layout.findViewById(R.id.contact);
            selectedAddr = addr;


            btnSave = (Button) layout.findViewById(R.id.SaveDetails);
            btnCancel = (Button) layout.findViewById(R.id.CancelSave);
            btnCancel.setOnClickListener(cancel_button_click_listener);
            btnSave.setOnClickListener(save_button_click_listener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private View.OnClickListener cancel_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            pwindo.dismiss();

        }
    };


    private View.OnClickListener save_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            String name = "";
            String profName = "";
            String contact = "";
            String address = selectedAddr;
            if(sname != null)
            {
                name = sname.getText().toString();
            }
            if(sprofName != null)
            {
                profName = sprofName.getText().toString();
            }
            if(scontact != null)
            {
                contact = scontact.getText().toString();
            }
            /*
             (String name,
                                    String professionalname,
                                    String contact,
                                    String address,
                                    String timestamp)
             */
            DateTime dt = new DateTime();
            String currentTimeSTamp = dt.getTimeStamp();
            pwindo.dismiss();
            LocationTrackerFragment.dbObj.insertSavedLocation(name, profName, contact, address, currentTimeSTamp);


        }
    };

     //   Read more: http://www.androidhub4you.com/2012/07/how-to-create-popup-window-in-android.html#ixzz3xQiqjgsp


    //Read more: http://www.androidhub4you.com/2012/07/how-to-create-popup-window-in-android.html#ixzz3xQcwxycg


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select The Action");
        menu.add(0, v.getId(), 0, "Copy");
        menu.add(0, v.getId(), 0, "Share");
        menu.add(0, v.getId(), 0, "Navigate");
        menu.add(0, v.getId(), 0, "Save");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "Delete");
    }
//    private void copyToClipBoard() {
//
//        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//        ClipData clip = ClipData.newPlainText(
//                "text label", // What should I set for this "label"?
//                "content to be copied");
//        clipboard.setPrimaryClip(clip);
//        Toast.makeText(AboutActivity.this, "Saved to clip board", Toast.LENGTH_SHORT).show();
//    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();
        lview = (ListView) activity.findViewById(R.id.listview);
        registerForContextMenu(lview);

//        if(dataDyn == null)
//            dataDyn = new LocationFragmentAsyncDataLoader();
//        if(LocationFragment.isExecuted == false)
//            dataDyn.execute("");
        new LocationFragmentAsyncDataLoader().execute("");

       // fillData();
    //    getDatabaseLocations();
//lview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//    @Override
//    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//        String strPos = String.valueOf(position);
//      //  Toast.makeText(activity,"long clicked on "+strPos, Toast.LENGTH_SHORT).show();
//        return true;
//    }
//});
        locationlist.clear();
        locationadapter = new LocationAdapter(activity,R.layout.locations_row,locationlist);
        lview.setAdapter(locationadapter);
        getDatabaseLocations();

        //gopi .. removed from here to added in oncreateview
      //  new LocationFragmentAsyncDataLoader().execute();

    }

    public class LocationFragmentAsyncDataLoader extends AsyncTask<String,Void,Void>
    {
        private final ProgressDialog dialog = new ProgressDialog(activity);

        @Override
        protected Void doInBackground(String... params) {
            LocationFragment.isExecuted = true;
            String addr = params[0];
            if(addr.length() > 0)
            {
                updateTravelLocations(addr);
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
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Iterator<LocationHistory> reverseOrder = linkedList.descendingIterator();
                    locationlist.clear();
                    while(reverseOrder.hasNext())
                    {
                        LocationHistory obj = reverseOrder.next();
                        locationlist.add(obj);
                    }
                    lview.setAdapter(locationadapter);
                    locationadapter.notifyDataSetChanged();
                }
            });

            dialog.dismiss();

            // adpt.setItemList(result);
            //..gopi
          //  locationadapter.notifyDataSetChanged();
        }


       /* @Override
        protected Void doInBackground(Void... strings) {

            return null;
        }*/
        public void updateTravelLocations(final String address)
        {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LocationHistory obj = new LocationHistory();
                    obj.index = "0";
                    obj.latitude = "";
                    obj.longitude = "";
                    obj.address = address;
                    obj.fromNumber = "";
                    obj.timestamp = "";
                    linkedList.addLast(obj);
                    locationlist.clear();
                    Iterator<LocationHistory> reverseOrder = linkedList.descendingIterator();
                    while(reverseOrder.hasNext())
                    {
                        LocationHistory tempObj = reverseOrder.next();
                        locationlist.add(tempObj);
                    }
                    locationadapter.notifyDataSetChanged();
                }
            });



        }
    }

    public void updateTravelLocations(String addr)
    {
        new LocationFragmentAsyncDataLoader().execute(addr);
    }

    public static void getDatabaseLocations()
    {
        if(activity == null)
            return;

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Cursor c = activity.getContentResolver().query(locationsUri, null, null,
                        null, null);
                if(c == null)
                    return;
                int rowsCnt = c.getCount();
                linkedList.clear();
                locationlist.clear();
                if(rowsCnt !=0)

                {
                    while (c.moveToNext()) {
                        Integer id = c.getInt(c.getColumnIndex("id"));
                        String fromNumber = c.getString(c.getColumnIndex("fromNumber"));
                        String latitude = c.getString(c.getColumnIndex("latitude"));
                        String longitude = c.getString(c.getColumnIndex("longitude"));
                        String timeStamp = c.getString(c.getColumnIndex("timestamp"));
                        String address = c.getString(c.getColumnIndex("address"));
                        LocationHistory obj = new LocationHistory();
                        obj.index = id.toString();
                        obj.latitude = latitude;
                        obj.longitude = longitude;
                        obj.address = address;
                        obj.fromNumber = fromNumber;
                        obj.timestamp = timeStamp;
                        //    locationlist.add(obj);
                        // cLocationlist.add(obj);
                        linkedList.add(obj);

                    }
                    Iterator<LocationHistory> reverseOrder = linkedList.descendingIterator();
                    while (reverseOrder.hasNext()) {
                        LocationHistory tempObj = reverseOrder.next();
                        locationlist.add(tempObj);
                    }

                    //    locationadapter.notifyDataSetChanged();
                }
                c.close();

            }

        });

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

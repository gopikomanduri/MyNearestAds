package com.tp.locator;

import android.app.Activity;
 import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aruna on 28-12-2017.
 */

public class AnnouncementBizSendingFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public static AnnouncementBizSendingFragment thisObj;
    public static DBWrapper dbObj;// = new DBWrapper()
    public Spinner categorySpinner;
    public Spinner spinneremergencysections;
    public Spinner spinneremergencybloodgroupsections;
    public Spinner spinnerannouncementscommercialsections;
    public Spinner spinnerannouncementsfoodsections;
    public Spinner spinnerannouncementsbeautyparloursections;
    static Activity activity;



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dbObj = ContactContentProvider.database;
        View rootView = inflater.inflate(R.layout.announcementsbiz, container, false);
        if(thisObj == null)
            thisObj =  this;

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();

        categorySpinner = (Spinner) activity.findViewById(R.id.spinnercategories);
        categorySpinner.setOnItemSelectedListener(this);
        loadCategoriesFromDb();
    }

    private void loadCategoriesFromDb() {
            Cursor c = dbObj.getAllCategories();
        List<String> categories = new ArrayList<String>();
        while(c.moveToNext())
        {
            categories.add(c.getString(0));
        }


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        categorySpinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedCat = (String) parent.getItemAtPosition(position);
        int visibility = view.VISIBLE;
            if(selectedCat.equalsIgnoreCase("Emergency"))
            {
                spinneremergencysections = (Spinner)activity.findViewById(R.id.spinneremergencysections);
                spinneremergencysections.setOnItemSelectedListener(this);
                spinneremergencysections.setVisibility(visibility);

            }
            else if(selectedCat.equalsIgnoreCase("Social"))
            {

            }
            else if(selectedCat.equalsIgnoreCase("Commercial"))
            {
                spinnerannouncementscommercialsections = (Spinner)activity.findViewById(R.id.spinnerannouncementscommercialsections);
              spinnerannouncementscommercialsections.setOnItemSelectedListener(this);
                spinnerannouncementscommercialsections.setVisibility(visibility);
            }
            else if(selectedCat.equalsIgnoreCase("I Need"))
            {

            }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

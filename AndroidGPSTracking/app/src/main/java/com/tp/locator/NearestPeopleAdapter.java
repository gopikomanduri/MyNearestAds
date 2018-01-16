package com.tp.locator;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Aruna on 01-11-2017.
 */

public class NearestPeopleAdapter extends ArrayAdapter<NearestPeopleClass> {

    private final Activity activity;
    private List<NearestPeopleClass> items;

    private int row;

    public NearestPeopleAdapter(Activity act, int row, List<NearestPeopleClass> items) {
        super(act, row, items);
        activity = act;
        this.row = row;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final NearestHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(row, null);

            holder = new NearestHolder();


            holder.locationInfo = (TextView) convertView.findViewById(R.id.nearestContactInfo);

            convertView.setTag(holder);
            convertView.setTag(R.id.nearestContactInfo, holder.locationInfo);
            convertView.setTag(R.id.nearestContactborder, holder.border);


        }
        else
        {
            holder = (NearestHolder) convertView.getTag();
        }

        holder.locationInfo.setText(items.get(position).toString());

        return convertView;
        //  holder.tvnumber.setText(items.get(position).contactNumber);
    }
}

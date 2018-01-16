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
 * Created by gkomandu on 7/31/2015.
 */

public class LocationAdapter extends ArrayAdapter<LocationHistory> {
    private Activity activity;
    private List<LocationHistory> items;

    private int row;
    public LocationAdapter(Activity act, int row, List<LocationHistory> items) {
        super(act, row, items);
        activity = act;
        this.row = row;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
     //   return super.getView(position, convertView, parent);
        final locationHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(row, null);

            holder = new locationHolder();


            holder.locationInfo = (TextView) convertView.findViewById(R.id.locationInfo);

            convertView.setTag(holder);
            convertView.setTag(R.id.locationInfo, holder.locationInfo);
            convertView.setTag(R.id.border, holder.border);


        }
        else
        {
            holder = (locationHolder) convertView.getTag();
        }
            if(holder.locationInfo != null)
        holder.locationInfo.setText(items.get(position).address);

        return convertView;
      //  holder.tvnumber.setText(items.get(position).contactNumber);
    }
}
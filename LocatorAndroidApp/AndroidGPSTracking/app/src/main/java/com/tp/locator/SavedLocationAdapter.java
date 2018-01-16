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
 * Created by user on 1/17/2016.
 */
public class SavedLocationAdapter extends ArrayAdapter<SavedLocationsHistory> {
    private Activity activity;
    private List<SavedLocationsHistory> items;

    private int row;
    public SavedLocationAdapter(Activity act, int row, List<SavedLocationsHistory> items) {
        super(act, row, items);
        activity = act;
        this.row = row;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //   return super.getView(position, convertView, parent);
        final savedLocationHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(row, null);

            holder = new savedLocationHolder();


            holder.savedname = (TextView) convertView.findViewById(R.id.savedname);
            holder.savedprofessionalname = (TextView) convertView.findViewById(R.id.savedprofessionalname);
            holder.savedcontact = (TextView) convertView.findViewById(R.id.savedcontact);
            holder.savedaddress = (TextView) convertView.findViewById(R.id.savedaddress);

            convertView.setTag(holder);
            convertView.setTag(R.id.savedname, holder.savedname);
            convertView.setTag(R.id.savedprofessionalname, holder.savedprofessionalname);
            convertView.setTag(R.id.savedcontact, holder.savedcontact);
            convertView.setTag(R.id.savedaddress, holder.savedaddress);
            convertView.setTag(R.id.border, holder.border);


        }
        else
        {
            holder = (savedLocationHolder) convertView.getTag();
        }

        holder.savedname.setText(items.get(position).name);
        holder.savedprofessionalname.setText(items.get(position).professionalName);
        holder.savedcontact.setText(items.get(position).contact);
        holder.savedaddress.setText(items.get(position).address);

        return convertView;
        //  holder.tvnumber.setText(items.get(position).contactNumber);
    }
}

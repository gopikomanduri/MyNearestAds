package com.tp.locator;

/**
 * Created by user on 7/7/2015.
 */
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class CustomOnItemSelectedListener implements OnItemSelectedListener {

    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
      /*  Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_SHORT).show();
        String selectedval = parent.getItemAtPosition(pos).toString();
        Integer position = (Integer)view.getTag();
        final ContactAdapter.ViewHolder holder;
        holder = (ContactAdapter.ViewHolder) view.getTag();

*/

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}

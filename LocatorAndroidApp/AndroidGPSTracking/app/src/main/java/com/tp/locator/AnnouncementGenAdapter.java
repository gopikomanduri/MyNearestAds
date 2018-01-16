package com.tp.locator;

import android.app.Activity;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Aruna on 10-11-2017.
 */

public class AnnouncementGenAdapter extends ArrayAdapter<AnnouncementGenClass> {
    private Activity activity;
    private List<AnnouncementGenClass> items;
    private int row;
    /**
     * State of ListView item that has never been determined.
     */
    private static final int STATE_UNKNOWN = 0;

    /**
     * State of a ListView item that is sectioned. A sectioned item must
     * display the separator.
     */
    private static final int STATE_SECTIONED_CELL = 1;

    /**
     * State of a ListView item that is not sectioned and therefore does not
     * display the separator.
     */
    private static final int STATE_REGULAR_CELL = 2;

    static String prevrcvdOn = "";

    private int[] mCellStates;

    public static boolean valuesUpdatee = false;

    public AnnouncementGenAdapter(Activity act, int row, List<AnnouncementGenClass> objects) {
        super(act, row, objects);

        this.activity = act;
        this.row = row;
        this.items = objects;

    }


//    public AnnouncementGenAdapter(Context context, Cursor cursor) {
//        super(context, cursor);
//        mCellStates = cursor == null ? null : new int[cursor.getCount()];
//
//    }

   // @Override
//    public void changeCursor(Cursor cursor) {
//        super.changeCursor(cursor);
//        mCellStates = cursor == null ? null : new int[cursor.getCount()];
//    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //   return super.getView(position, convertView, parent);
        final AnnouncmentGenHolder holder;
        boolean needSeparator = false;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(row, null);

            holder = new AnnouncmentGenHolder();


            holder.announcement = (TextView) convertView.findViewById(R.id.notificationText);
            holder.dateHolder = (TextView) convertView.findViewById(R.id.notificationdate);

            convertView.setTag(holder);
            convertView.setTag(R.id.locationInfo, holder.announcement);
            convertView.setTag(R.id.border, holder.border);


        } else {
            holder = (AnnouncmentGenHolder) convertView.getTag();
        }


        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

      //  if (holder.announcement != null) {
            holder.announcement.setText(items.get(position).msg);
            holder.dateHolder.setText(items.get(position).date);
          //  holder.border.setText(items.get(position).border);
       // }
        if(position == 0) {
            needSeparator = true;
            prevrcvdOn = items.get(position).date;
        }
        else
        {
            if(prevrcvdOn.equals(items.get(position).date))
                needSeparator = false;
            else {
                needSeparator = true;
                prevrcvdOn = items.get(position).date;

            }

        }
        if(needSeparator == true)
        {
            holder.dateHolder.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.dateHolder.setVisibility(View.GONE);

        }
        return convertView;
        //  holder.tvnumber.setText(items.get(position).contactNumber);
    }

  //  @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {


        View v = LayoutInflater.from(context).inflate(R.layout.notification_row, parent, false);

        // The following code allows us to keep a reference on the child
        // views of the item. It prevents us from calling findViewById at
        // each getView/bindView and boosts the rendering code.
        final AnnouncmentGenHolder holder;
        holder = new AnnouncmentGenHolder();


        holder.announcement = (TextView) v.findViewById(R.id.notificationText);
        holder.dateHolder = (TextView) v.findViewById(R.id.notificationdate);

        v.setTag(holder);

        return v;
    }

 //   @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final AnnouncmentGenHolder holder = (AnnouncmentGenHolder) view.getTag();
        final int position = cursor.getPosition();

                    /*
             * Separator
             */
        boolean needSeparator = false;

        String msg =
                cursor.getString(cursor.getColumnIndexOrThrow("msg"));
        long rcvdOn = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"));
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(rcvdOn);
        String rcvdDate =  formatter.format(calendar.getTime());
        holder.announcement.setText(msg);
        holder.dateHolder.setText(rcvdDate);


        switch (mCellStates[position]) {
            case STATE_SECTIONED_CELL:
                needSeparator = true;
                break;

            case STATE_REGULAR_CELL:
                needSeparator = false;
                break;

            case STATE_UNKNOWN:
            default:
                // A separator is needed if it's the first itemview of the
                // ListView or if the group of the current cell is different
                // from the previous itemview.
                if (position == 0) {
                    needSeparator = true;
                } else {
                    cursor.moveToPosition(position - 1);

                    CharArrayBuffer tempBuf = new CharArrayBuffer(256);




                    long prevrcvdOn = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"));

                    calendar.setTimeInMillis(prevrcvdOn);
                    String prevrcvdDate =  formatter.format(calendar.getTime());

                    if (prevrcvdDate.length()> 0 && holder.dateHolder.getTextSize() > 0 && prevrcvdDate.equals(holder.dateHolder) == false) {
                        needSeparator = true;
                    }

                    cursor.moveToPosition(position);
                }

                // Cache the result
                mCellStates[position] = needSeparator ? STATE_SECTIONED_CELL : STATE_REGULAR_CELL;
                break;
        }

        if (needSeparator) {
            holder.dateHolder.setVisibility(View.VISIBLE);
        } else {
            holder.dateHolder.setVisibility(View.GONE);
        }




    }
}
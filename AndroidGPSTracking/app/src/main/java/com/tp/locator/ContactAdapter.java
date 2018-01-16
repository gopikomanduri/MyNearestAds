package com.tp.locator;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.tp.locator.DataService.dbObj;
import static com.tp.locator.SocketService.messagesToPush;

/**
 * Created by gkomandu on 7/2/2015.
 */
public class ContactAdapter extends ArrayAdapter<allowedContacts> implements AdapterView.OnItemSelectedListener{

    private Activity activity;
    private List<allowedContacts> items;
    private int row;
    private static final String TAG = "ContactAdapter";
    private allowedContacts objBean;
    static List<allowedContacts> obj = new ArrayList<allowedContacts>();
    static Set<allowedContacts> allowedSetObj = new HashSet<allowedContacts>();
    static Set<allowedContacts> removedSetObj = new HashSet<allowedContacts>();
 //   static HashMap<String,allowedContacts> allowedContactsObj =  new HashMap<String, allowedContacts>();
    ArrayAdapter<String> dataAdapter;
    Spinner spinner1;
    List<String> optionsList = new ArrayList<String>();
    android.content.ContentResolver cr;
    public static MessageSender messageSender;
    public static GoogleCloudMessaging gcm;
    public static Context contactAdapterCtx;



    public ContactAdapter(Activity act, int row, List<allowedContacts> items) {
        super(act, row, items);

        messageSender = new MessageSender();
        cr = act.getContentResolver();
        contactAdapterCtx = getContext();
        this.activity = act;
        this.row = row;
        this.items = items;
        optionsList.add("No");
        optionsList.add("Yes");
        //commented to not allow mock
      //  optionsList.add("Mock");
        gcm = GoogleCloudMessaging.getInstance(this.activity);
    //    dataAdapter = new ArrayAdapter<String>(act,android.R.layout.simple_spinner_item, optionsList);
     //   dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public static Set<allowedContacts> getLatestAddedItems()
    {
        return allowedSetObj;
    }
    public static Set<allowedContacts> getLatestRemovedItems()
    {
        return removedSetObj;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
     //   View view = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(row, null);

            holder = new ViewHolder();



            holder.tvname = (TextView) convertView.findViewById(R.id.tvname);
            holder.tvnumber = (TextView) convertView.findViewById(R.id.tvnumber);
     /*   holder.isAllowed = (CheckBox) view.findViewById(R.id.checkBox1);*/
            holder.isAllowed = (Switch) convertView.findViewById(R.id.toggleButton1);
            holder.trackHim = (Button)convertView.findViewById(R.id.trackHim);


            holder.isAllowed.setTextOn("On"); // displayed text of the Switch whenever it is in checked or on state
            holder.isAllowed.setTextOff("Off"); // displayed text of the Switch whenever it is in unchecked i.e. off state

            holder.trackHim.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Log.d("Gopi", "button clicked");
                    String num = holder.tvnumber.getText().toString();
                    allowedContacts tempobj  = items.get(position);
                 //   if(tempobj.contactNumber.intern().equalsIgnoreCase("+910000000000"))
                    {
                        MySmsReceiver mockHandler = new MySmsReceiver();
                        MySmsReceiver.smsHandler objHandler = mockHandler.getsmsHandler();
                        objHandler.context = getContext();



                        String fromNumber = dbObj.getMyNumber();




                       // String fromNumber = SaveSharedPreference.getUserName(SplashActivity.staticSplashCtx);
                        mockHandler.fromAddress = fromNumber;
                    //    objHandler.parseMsg("w r u",1,mockHandler.fromAddress);
                        Bundle dataBundle = new Bundle();
                        dataBundle.putString("ACTION", "TRACK");
                            dataBundle.putString("TOUSER", num);
                        dataBundle.putString("FROMUSER", fromNumber);
                       // messageSender.sendMessage(dataBundle, gcm);

                        Comms comsObj = new Comms();
                        comsObj.params = new HashMap<String, Object>();
                        Map<String,String> internalData = new HashMap<String, String>();

                        internalData.put("ACTION","TRACK");
                        internalData.put("TOUSER",num);
                        internalData.put("FROMUSER",fromNumber);
                        internalData.put("contact",fromNumber);
                        comsObj.params.put("from", fromNumber);
                        comsObj.params.put("data", internalData);

                        //Type type = new TypeToken<List<Task>>() {}.getType();
                        Gson gson = new Gson();
                        String json = gson.toJson(comsObj, Comms.class);
//                        if(SocketCommunicationEstablisher.mysock != null)
//                        {
//                            SocketCommunicationEstablisher.mysock.send(json);
//                        }
                    Log.i(TAG," Before pushing into messagesToPush queue . msg = "+json);
                        messagesToPush.offer(json);




                    }
                  //  else {
                      //  MySmsReceiver.sendSmsMsg("w r u", tempobj.contactNumber,0);
                    //}
                }

            });


            holder.isAllowed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                                        Object tagVal1 = v.getTag();
                    int getPosition = (Integer)tagVal1;
                    int x  = 30;
                  allowedContacts obj =  items.get(getPosition);
                    x = 40;
                  //  Object tagVal1 = parent.getTag();


                    if(tagVal1 != null) {
                        //    items.get(getPosition).setSelected(view.isChecked()); // Set the value of checkbox to maintain its state.
                      //  String tempVal = parent.getItemAtPosition(pos).toString();

                        //   holder.isAllowed.setSelection(2);

                        allowedContacts tempobj = items.get(getPosition);
                        if (tempobj.isAllowed.intern().equalsIgnoreCase("No") == true) {
                            holder.isAllowed.setChecked(false);
                        } else {
                            holder.isAllowed.setChecked(true);
                        }
                        int l1 = items.size();
                        int l2 = getPosition;
                        if(items.size() > getPosition)
                        {
                       // items.get(getPosition).isAllowed = tempVal ;//parent.getItemAtPosition(pos).toString();
                        if (holder.isAllowed.isChecked() == false) {
                            Integer MockAllowed = 0;//= tempVal.equalsIgnoreCase("Mock") ? 1 : 0;
                            //Gopi .. removing Mock
                            MockAllowed = 0;
                            holder.isAllowed.setChecked(true);
                            if (LocationTrackerFragment.dbObj.getidFromNumber(items.get(getPosition).contactNumber) <= 0) {
                                LocationTrackerFragment.dbObj.insertContact(items.get(getPosition).contactNumber,
                                        items.get(getPosition).contactName,
                                        "yes",
                                        "",
                                        "w r u",
                                        MockAllowed,
                                        "",
                                        "",
                                        "temp"
                                );
                                tempobj.isAllowed = "yes";
                                tempobj.isMockAllowed = MockAllowed;
                                MySmsReceiver.allowedContactsObj.put(tempobj.contactNumber, tempobj);

                            } else //if (tempVal.equalsIgnoreCase("Yes") == true)
                                 {
                                LocationTrackerFragment.dbObj.updateContactwithAllowedTimings(items.get(getPosition).contactNumber,
                                        "yes",
                                        "");
                                tempobj.isAllowed = "yes";
                                tempobj.isMockAllowed = 0;
//                            } else {
//                                LocationTrackerFragment.dbObj.updateContactwithMockDetails(items.get(getPosition).contactNumber, "yes", "", "temp");
//                                tempobj.isAllowed = "yes";
//                                tempobj.isMockAllowed = 1;

                            }
                            /*for(int i=0;i<DataService.newItems.size();++i)
                            {
                                String number = items.get(getPosition).contactNumber;
                                if(DataService.newItems.get(i).contactNumber.equals(number) == true)
                                {
                                    //PhoneContact obj = DataService.newItems.remove(i);

                                }
                            }*/


                            if (MySmsReceiver.allowedContactsObj.containsKey(tempobj.contactNumber) == true) {
                                MySmsReceiver.allowedContactsObj.remove(tempobj.contactNumber);
                                MySmsReceiver.allowedContactsObj.put(tempobj.contactNumber, tempobj);
                            }
                        } else {
                            holder.isAllowed.setChecked(false);
                            tempobj.isAllowed = "no";
                            tempobj.isMockAllowed = 0;
                           // allowedContacts obj = items.get(getPosition);
                            if (LocationTrackerFragment.dbObj.getidFromNumber(items.get(getPosition).contactNumber) > 0) {
                                LocationTrackerFragment.dbObj.deleteContact(items.get(getPosition).contactNumber);
                            }
                            if (MySmsReceiver.allowedContactsObj.containsKey(tempobj.contactNumber) == true) {
                                MySmsReceiver.allowedContactsObj.remove(tempobj.contactNumber);
                            }
                        }
                            String number = items.get(getPosition).contactNumber;
                           // LocationTrackerFragment.dbObj.updateContactwithIsAllowedChange(number,holder.isAllowed.getSelectedItemPosition());
                            //LocationTrackerFragment.dbObj.updateContactwithMockDetails(items.get(getPosition).contactNumber, "yes", "", "temp");
                            if(holder.isAllowed.isChecked() == true)
                            LocationTrackerFragment.updatedWithPermission.put(number,1);
                            else
                                LocationTrackerFragment.updatedWithPermission.put(number,0);
                        }

                    }

                }
            });
     //   holder.isAllowed.setSelection(0);

//            holder.isAllowed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//                    Object tagVal = view.getTag();
//                    Object tagVal1 = parent.getTag();
//
//
//                    if(tagVal1 != null) {
//                        String str = parent.getTag().toString();
//                        int getPosition = (Integer) parent.getTag();  // Here we get the position that we have set for the checkbox using setTag.
//                        //    items.get(getPosition).setSelected(view.isChecked()); // Set the value of checkbox to maintain its state.
//                        String tempVal = parent.getItemAtPosition(pos).toString();
//                        //   holder.isAllowed.setSelection(2);
//
//                        allowedContacts tempobj = items.get(getPosition);
//                        if (tempobj.isAllowed.intern().equalsIgnoreCase("No") == true) {
//                            holder.isAllowed.setSelection(0,false);
//                        } else {
//                            holder.isAllowed.setSelection(tempobj.isMockAllowed + 1);
//                        }
//                        int l1 = items.size();
//                        int l2 = getPosition;
//                        if(items.size() > getPosition)
//                        {
//                        items.get(getPosition).isAllowed = tempVal ;//parent.getItemAtPosition(pos).toString();
//                        if (tempVal.equalsIgnoreCase("No") == false) {
//                            Integer MockAllowed = tempVal.equalsIgnoreCase("Mock") ? 1 : 0;
//                            //Gopi .. removing Mock
//                            MockAllowed = 0;
//                            holder.isAllowed.setSelection(MockAllowed + 1);
//                            if (LocationTrackerFragment.dbObj.getidFromNumber(items.get(getPosition).contactNumber) <= 0) {
//                                LocationTrackerFragment.dbObj.insertContact(items.get(getPosition).contactNumber,
//                                        items.get(getPosition).contactName,
//                                        "yes",
//                                        "",
//                                        "w r u",
//                                        MockAllowed,
//                                        "",
//                                        "",
//                                        "temp"
//                                );
//                                tempobj.isAllowed = "yes";
//                                tempobj.isMockAllowed = MockAllowed;
//                                MySmsReceiver.allowedContactsObj.put(tempobj.contactNumber, tempobj);
//
//                            } else if (tempVal.equalsIgnoreCase("Yes") == true) {
//                                LocationTrackerFragment.dbObj.updateContactwithAllowedTimings(items.get(getPosition).contactNumber,
//                                        "yes",
//                                        "");
//                                tempobj.isAllowed = "yes";
//                                tempobj.isMockAllowed = 0;
//                            } else {
//                                LocationTrackerFragment.dbObj.updateContactwithMockDetails(items.get(getPosition).contactNumber, "yes", "", "temp");
//                                tempobj.isAllowed = "yes";
//                                tempobj.isMockAllowed = 1;
//
//                            }
//                            /*for(int i=0;i<DataService.newItems.size();++i)
//                            {
//                                String number = items.get(getPosition).contactNumber;
//                                if(DataService.newItems.get(i).contactNumber.equals(number) == true)
//                                {
//                                    //PhoneContact obj = DataService.newItems.remove(i);
//
//                                }
//                            }*/
//
//
//                            if (MySmsReceiver.allowedContactsObj.containsKey(tempobj.contactNumber) == true) {
//                                MySmsReceiver.allowedContactsObj.remove(tempobj.contactNumber);
//                                MySmsReceiver.allowedContactsObj.put(tempobj.contactNumber, tempobj);
//                            }
//                        } else {
//                            holder.isAllowed.setSelection(0);
//                            allowedContacts obj = items.get(getPosition);
//                            if (LocationTrackerFragment.dbObj.getidFromNumber(items.get(getPosition).contactNumber) > 0) {
//                                LocationTrackerFragment.dbObj.deleteContact(items.get(getPosition).contactNumber);
//                            }
//                            if (MySmsReceiver.allowedContactsObj.containsKey(tempobj.contactNumber) == true) {
//                                MySmsReceiver.allowedContactsObj.remove(tempobj.contactNumber);
//                            }
//                        }
//                            String number = items.get(getPosition).contactNumber;
//                            int x = holder.isAllowed.isChecked();
//                           // LocationTrackerFragment.dbObj.updateContactwithIsAllowedChange(number,holder.isAllowed.getSelectedItemPosition());
//                            //LocationTrackerFragment.dbObj.updateContactwithMockDetails(items.get(getPosition).contactNumber, "yes", "", "temp");
//                            LocationTrackerFragment.updatedWithPermission.put(number,holder.isAllowed.getSelectedItemPosition());
//                    }
//
//                    }
//
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> adapterView) {
//
//                }
//
//            });

           convertView.setTag(holder);
            convertView.setTag(R.id.tvname, holder.tvname);
            convertView.setTag(R.id.tvnumber, holder.tvnumber);
            convertView.setTag(R.id.toggleButton1, holder.isAllowed);


        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.isAllowed.setTag(position); // This line is important.

        holder.tvname.setText(items.get(position).contactName);
        holder.tvnumber.setText(items.get(position).contactNumber);
      //  holder.isAllowed.setChecked(list.get(position).isSelected());
        String str = items.get(position).isAllowed;
        int mockAllowed = items.get(position).isMockAllowed;
        // hard coded to remove mock
       // mockAllowed = 0;
        if(str != null) {
            if (str.intern().equalsIgnoreCase("No"))
                holder.isAllowed.setChecked(false);
            else if (str.intern().equalsIgnoreCase("Yes") && (mockAllowed == 0))
                holder.isAllowed.setChecked(true);;
            //Gopi .. removed Mock
//            else
//                holder.isAllowed.setSelection(2);
        }
        else
        {

        }
        return convertView;
    }



    public String getCurrentTimeStamp()
    {
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        return ts;
    }


    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
      /*  Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_SHORT).show();*/
        String selectedval = parent.getItemAtPosition(pos).toString();
        Integer position = (Integer)view.getTag();




    }


    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
        String tempVal = parent.getItemAtPosition(i).toString();
        int x = 20;


    }
}

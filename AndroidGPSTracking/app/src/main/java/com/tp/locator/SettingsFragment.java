package com.tp.locator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.tp.locator.Events.gmailEvent;
import com.tp.locator.Settings.SettingsClass;

import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by gkomandu on 7/24/2015.
 */
public class SettingsFragment extends Fragment implements
        AdapterView.OnItemSelectedListener {
    Spinner emergencyContact1;
    Spinner emergencyContact2;
    Spinner emergencyContact3;
    ToggleButton emergencyToggle;
    ToggleButton locatorToggle;
    Button clearLogBtn;

    public static int locatorValue = 1;
    public static int emergencyValue = 0;
    public static String mockAddress = "";
    public ProgressDialog gmailDlg;

    EditText oldPwdMsg;
    EditText newPwdMsg;
    EditText reNewPwdMsg;
    Button updateSettings;

    String dbpwdMsg = "";

    EditText myCntNumber;

    EditText mockLocation;
    Button updateMockLocation;

    Button updatEmergencyContacts;

    Button report;

    String dbEmergencyCnt1;
    String dbEmergencyCnt2;
    String dbEmergencyCnt3;

    private ArrayList<String> list = new ArrayList<String>();

    DBWrapper dbObj;

    Activity activity;
    Context applicationCtx;

   // String MyDetailsURL  = "content://com.tp.locator.provider.allowedContacts/settings";
   String MyDetailsURL  = "content://com.tp.locator.provider.allowedContacts/Contacts";

    String MySettingsURL  = "content://com.tp.locator.provider.allowedContacts/settings";

    Uri contactsUri = Uri.parse(MyDetailsURL);
    Uri settingsUri = Uri.parse(MySettingsURL);

    String pwdMsg = "";
    String dbLoc = "";
    String contactNumber;
    int rowsCnt = 0;
    static boolean isEventRegistered = false;
    gmailEvent mailevent;

    ContactAdapter objAdapter;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.settings, container, false);
        gmailDlg = new ProgressDialog(this.getActivity().getBaseContext());//,"sending report , please wait");
        mailevent = new gmailEvent();
//        if(isEventRegistered == false) {
//
//
//            EventBus.getDefault().register(this);
//            isEventRegistered = true;
//        }

        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.v("Settings", "onActivityCreated()");
        dbObj = LocationTrackerFragment.dbObj;

        activity = getActivity();


        applicationCtx = activity;

        //check why we need this getdatabasecontacts
     //   getDatabaseContacts();

       // activity.setContentView(R.layout.settings);

        emergencyContact1 = (Spinner)activity.findViewById(R.id.Emergency1);
        emergencyContact2 = (Spinner)activity.findViewById(R.id.Emergency2);
        emergencyContact3 = (Spinner)activity.findViewById(R.id.Emergency3);

        emergencyToggle = (ToggleButton)activity.findViewById(R.id.toggleEmergency);
        locatorToggle = (ToggleButton)activity.findViewById(R.id.toggleLocator);
        clearLogBtn = (Button)activity.findViewById(R.id.btnClearLog);

        clearLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LocationTrackerFragment.dbObj == null)
                {
                    if(ContactContentProvider.database == null) {
                        ContactContentProvider.database = new DBWrapper(applicationCtx, "GpSTracker.db");
                        ContactContentProvider.database.getDatabase();
                        //dbObj =
                    }
                    LocationTrackerFragment.dbObj = ContactContentProvider.database;

                }
                if(LocationTrackerFragment.dbObj != null) {
                    LocationTrackerFragment.dbObj.clearLogs();
                    LocationFragment.locationadapter.notifyDataSetChanged();
                }

            }
        });


        emergencyToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean on) {

                List<String> types = new ArrayList<String>();
                List<String> values = new ArrayList<String>();
                contactNumber = myCntNumber.getText().toString();
                types.add("emergency");
                if (on) {
                    Log.i("info", "Button1 is on!");
                    values.add("1");
                    emergencyValue = 1;
                } else {
                    Log.i("info", "Button1 is off!");
                    values.add("0");
                    emergencyValue = 0;
                }
                dbObj.updateSettingGeneric(contactNumber,types,values);
            }
        });
        locatorToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
                List<String> types = new ArrayList<String>();
                List<String> values = new ArrayList<String>();
                contactNumber = myCntNumber.getText().toString();
                types.add("locator");
                if (on) {
                    Log.i("info", "Button1 is on!");
                    values.add("1");
                    locatorValue = 1;
                } else {
                    Log.i("info", "Button1 is off!");
                    values.add("0");
                    locatorValue = 0;
                }
                dbObj.updateSettingGeneric(contactNumber,types,values);

            }
        });

        oldPwdMsg = (EditText)activity.findViewById(R.id.oldPasswordMsg);
        newPwdMsg = (EditText)activity.findViewById(R.id.newPasswordMsg);
        reNewPwdMsg = (EditText)activity.findViewById(R.id.renewPasswordMsg);
        myCntNumber = (EditText)activity.findViewById(R.id.MyContactNumber);
      //  myCntNumber.setText("8179442558");

        mockLocation = (EditText)activity.findViewById(R.id.mockDetails);

        updatEmergencyContacts = (Button)activity.findViewById(R.id.btnEmergencyContacts);

        report = (Button)activity.findViewById(R.id.btnReport);

      //  updateSettings = (Button)activity.findViewById(R.id.Update);



        addListenerOnButton();

     //   loadSpinnerData();
        loadData();

    }


    public void addListenerOnButton() {



        updatEmergencyContacts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
            getDataFromView();


            }

        });

        report.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                mailevent.send = true;
               new ProgressDlg().execute(true);
                new Logsender().execute();



            }

        });
    }
    class ProgressDlg extends AsyncTask<Boolean,Void,Void> {
        boolean isEventRegistered = false;

        ProgressDlg()
        {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        }

        @Override
        protected void onPreExecute() {
            gmailDlg = new ProgressDialog(applicationCtx);
            gmailDlg.setTitle("Please wait...");
            gmailDlg.setMessage("Sending report .");
            gmailDlg.setCancelable(false);
            gmailDlg.setIndeterminate(true);
            gmailDlg.show();
        }

        @Override
        protected Void doInBackground(Boolean... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
        public void onEvent(gmailEvent obj)
        {
            if (gmailDlg != null) {
                gmailDlg.dismiss();
                // closing application
                System.exit(0);
                // To restart the app .. Gopi;
//                Intent intent = getActivity().getPackageManager()
//                        .getLaunchIntentForPackage( getActivity().getPackageName());
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);

            }

        }
    }

    class Logsender extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... params) {

                String filename = "testFile.log";
                StringBuffer stringBuffer = new StringBuffer();
                String aDataRow = "";
                String aBuffer = "";
                try {
                    File myFile = new File("/sdcard/"+filename);
                    FileInputStream fIn = new FileInputStream(myFile);
                    BufferedReader myReader = new BufferedReader(
                            new InputStreamReader(fIn));

                    while ((aDataRow = myReader.readLine()) != null) {
                        aBuffer += aDataRow + "\n";
                    }
                    myReader.close();


                    GMailSender gMailSender = new GMailSender("locatorlogs.com", "bananatable143");
                    gMailSender.sendMail("LOG FILE", aBuffer, "locatorlogs.com", "locatorlogs.com, locatorlogs.com");
                      myFile.delete();
                    myFile.createNewFile();
                    MySmsReceiver.logger = LoggerFactory.getLogger(MySmsReceiver.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            finally {
                    if(mailevent == null)
                        mailevent = new gmailEvent();
                    mailevent.send = false;
                    SystemClock.sleep(1000);
                    EventBus.getDefault().post(mailevent);
                }
            return null;
        }


    }


    private void getDataFromView()
    {
        String oldMsg = oldPwdMsg.getText().toString();
        String newMsg = newPwdMsg.getText().toString();
        String renewMsg = reNewPwdMsg.getText().toString();
        contactNumber = myCntNumber.getText().toString();
        List<String> types = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        if((oldMsg.length() !=0 || rowsCnt == 0 )  && newMsg.length() != 0 && renewMsg . length() !=0 )
        {

            if((oldMsg.intern().equals(dbpwdMsg) != true) && (rowsCnt > 0))
            {
                raiseError("old msg is wrong");
                return;
            }
            if(newMsg.intern().equals(renewMsg) != true)
            {
                raiseError("new msg and re-enter msg didn't match");
                return;
            }
            pwdMsg = newMsg;



        }
        else
        {
          /*  raiseError("messages cannot be empty");
            return;*/
        }
        String mockLoc = mockLocation.getText().toString();


//        String cnt1 = emergencyContact1.getSelectedItem().toString();
//        String cnt2 = emergencyContact2.getSelectedItem().toString();
//        String cnt3 = emergencyContact3.getSelectedItem().toString();
        if(pwdMsg.length() != 0) {
            // dbObj.updateSettingswithPwdMessage(contactNumber,pwdMsg);
            types.add("pwdMsg");
            values.add(pwdMsg);
        }
        //if(dbLoc!=null && mockLoc!=null &&  dbLoc.intern().equalsIgnoreCase(mockLoc) != true)
        if(mockLoc!=null)
        {
            types.add("MockLocation");
            values.add(mockLoc);
        }
//        types.add("EmergencyContact1");
//        types.add("EmergencyContact2");
//        types.add("EmergencyContact3");
//        values.add(cnt1);
//        values.add(cnt2);
//        values.add(cnt3);
            //dbObj.updateSettingswithMockLocation(contactNumber,mockLoc);
        dbObj.updateSettingGeneric(contactNumber,types,values);
        if(mockLoc != null)
            SettingsClass.dbLoc = mockLoc;
    }

    private void raiseError(String errorMsg)
    {

    }

private void loadData()
{
            contactNumber = SettingsClass.contactNumber;

            dbpwdMsg = SettingsClass.dbpwdMsg;
            dbLoc = SettingsClass.dbLoc;
            String Cnt1 = SettingsClass.emergencyContact1;
            String Cnt2 = SettingsClass.emergencyContact2;
            String Cnt3 = SettingsClass.emergencyContact3;

            Integer emergencyT = SettingsClass.emergencyT;


            Integer locatorT = SettingsClass.locatorT;

            boolean isEmergencyChecked = (emergencyT == 1) ? true : false;
            emergencyToggle.setChecked(isEmergencyChecked);


            boolean isLocatorChecked = (locatorT == 1) ? true : false;
            locatorToggle.setChecked(isLocatorChecked);

            mockLocation.setText(dbLoc);
                if(dbLoc != null)
                    SettingsClass.dbLoc = dbLoc;
            int cnt1Index = list.indexOf(Cnt1);
            int cnt2Index = list.indexOf(Cnt2);
            int cnt3Index = list.indexOf(Cnt3);

            emergencyContact1.setSelection(cnt1Index);
            emergencyContact2.setSelection(cnt2Index);
            emergencyContact3.setSelection(cnt3Index);


}
    private void getDatabaseContacts()
    {
      //  Cursor c = activity.managedQuery(contactsUri, null, null, null, null);
        Cursor c = activity.getContentResolver().query(contactsUri, null, null,
                null, null);

        int rowsCnt = c.getCount();
        list.add("Not selected");
        if(rowsCnt != 0) {
            while (c.moveToNext()) {
                int x = c.getColumnIndex("ContactNumber");
                String number = c.getString(x);
                x = c.getColumnIndex("ContactName");
                String name = c.getString(x);
                x = c.getColumnIndex("registeredMsg");

                String regitMsg = c.getString(x);

                x = c.getColumnIndex("allowedStartTime");
                String allowedStartTime = c.getString(x);

                int tempMock = c.getInt(c.getColumnIndex("isMockAllowed"));


                allowedContacts obj = new allowedContacts(number,
                        c.getString(c.getColumnIndex("ContactName")),
                        c.getString(c.getColumnIndex("registeredMsg")),
                        c.getString(c.getColumnIndex("allowedStartTime")),
                        c.getString(c.getColumnIndex("allowedEndTime")),
                        tempMock,
                        c.getString(c.getColumnIndex("mockStartTime")),
                        c.getString(c.getColumnIndex("mockEndTime")),
                        c.getString(c.getColumnIndex("mockMsg"))
                );

                // Integer hashInt = Integer.parseInt(hashStr);
                list.add(obj.contactName);
            }
        }
        c.close();
    }
    private void loadSpinnerData()
    {


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_item, list);
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        emergencyContact1.setAdapter(dataAdapter);
        emergencyContact2.setAdapter(dataAdapter);
        emergencyContact3.setAdapter(dataAdapter);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        String label = parent.getItemAtPosition(position).toString();
        dbEmergencyCnt1 = emergencyContact1.getSelectedItem().toString();
        dbEmergencyCnt2 = emergencyContact2.getSelectedItem().toString();
        dbEmergencyCnt3 = emergencyContact3.getSelectedItem().toString();


        // Showing selected spinner item
    }
    public void onEmergencyClicked(View view) {
        boolean on = ((ToggleButton) view).isChecked();
        List<String> types = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        types.add("emergency");
        if (on) {
            Log.i("info", "Button1 is on!");
           values.add("1");
        } else {
            Log.i("info", "Button1 is off!");
            values.add("0");
        }
        dbObj.updateSettingGeneric(contactNumber,types,values);
        MySmsReceiver.getSettings();
    }
    public void onLocatorClicked(View view) {
        boolean on = ((ToggleButton) view).isChecked();
        List<String> types = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        if (on) {
            Log.i("info", "Button1 is on!");
            values.add("1");
        } else {
            Log.i("info", "Button1 is off!");
            values.add("0");
        }
        dbObj.updateSettingGeneric(contactNumber,types,values);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}


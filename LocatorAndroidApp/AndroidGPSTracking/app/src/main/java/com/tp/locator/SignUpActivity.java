package com.tp.locator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tp.locator.Events.GCMRegistration;
import com.tp.locator.Events.LoginEvent;
import com.tp.locator.Events.Progress;
import com.tp.locator.Events.Timer;
import com.tp.locator.Network.CheckInternet;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import de.greenrobot.event.EventBus;

import static com.tp.locator.DataService.dbObj;
import static com.tp.locator.SocketService.messagesToPush;


public class SignUpActivity extends Activity {

    private static final String TAG = "SignUpActivity";
    public static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";
    Button buttonSignUp;
    Button buttonLogin;
    public String regId;
    static String signUpUser;
    String password;
    AsyncTask<Void, Void, String> sendTask;
    AtomicInteger ccsMsgId = new AtomicInteger();
    GoogleCloudMessaging gcm;
    public Context context;
    private boolean signupFlag = false;
    MessageSender messageSender;
    ProgressDialog dialog;
    public ProgressDialog signupDlg;
    Progress progressEvnt;
    CountDownTimer timerObj = null;
    public static Activity currentActivity;




    public void onEvent (GCMRegistration event)
    {
        //step 2: register with XMPP App Server
//        if(event.regId != null && event.regId.isEmpty() == false && event.regId.length() > 0)
//        {
            EditText mUserName = (EditText) findViewById(R.id.userName);
            EditText mPassword = (EditText)findViewById(R.id.Password);
            signUpUser = mUserName.getText().toString();
            password = mPassword.getText().toString();
            Bundle dataBundle = new Bundle();
            dataBundle.putString("ACTION", "SIGNUP");
            //dataBundle.putString("USER_NAME", "+918179442558");
            if(signUpUser.length() < 10)
            {
                LoginEvent obj = new LoginEvent();
                obj.isSuccess = "Phone number must be 10 digits";
                EventBus.getDefault().post(obj);
                return;
            }
            if(password.length() < 4)
            {
                LoginEvent obj = new LoginEvent();
                obj.isSuccess = "Password must be minimum 4 digits";
                EventBus.getDefault().post(obj);
                return;
            }
            signUpUser = "+91"+signUpUser;
            dataBundle.putString("USER_NAME", signUpUser);
            dataBundle.putString("PASSWORD", password);
            String countryCode = Utils.getCountryCode(SplashActivity.staticSplashCtx);

            CheckInternet netCheck = new CheckInternet(getApplicationContext());
            if(netCheck.isConnectingToInternet() == true) {
                Comms comsMsg = new Comms();
                comsMsg.params = new HashMap<String, Object>();
                Map<String,String> internalData = new HashMap<String, String>();

                internalData.put("ACTION", "SIGNUP");
                internalData.put("USER_NAME", signUpUser);
                internalData.put("PASSWORD", password);
                comsMsg.params.put("data", internalData);
                if(SocketCommunicationEstablisher.randomNum != null)
                comsMsg.params.put("from",SocketCommunicationEstablisher.randomNum);
                else
                {
                    comsMsg.params.put("from",signUpUser);

                }
               final Timer timerObj = new Timer();
                timerObj.canStart = true;
                EventBus.getDefault().post(timerObj);
               // messageSender.sendMessage(dataBundle, gcm);
                Gson gson = new Gson();

                String mymsg = gson.toJson(comsMsg, Comms.class);
//                if(mymsg != null && SocketCommunicationEstablisher.mysock!= null)
//                SocketCommunicationEstablisher.mysock.send(mymsg);
                Log.i("SignUp","requesting signup "+mymsg);
                messagesToPush.offer(mymsg);


            }
            else
            {

                progressEvnt = new Progress();
                progressEvnt.show=false;
                EventBus.getDefault().post(progressEvnt);
                buttonSignUp.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"Please connect to Internet and try again",Toast.LENGTH_SHORT).show();
            }
//        }
//        else
//        {
//            Toast.makeText(context,
//                    "Google GCM RegId Not Available!",
//                    Toast.LENGTH_LONG).show();
//        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivity = this;
      // // EventBus.getDefault().register(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
       dialog = new ProgressDialog(this);
        setContentView(R.layout.activity_sign_up);
        context = getApplicationContext();
        buttonSignUp = (Button) findViewById(R.id.ButtonSignUp);
        messageSender = new MessageSender();
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {


                new ProgressDlg(SignUpActivity.this).execute(true);


                //buttonSignUp.setVisibility(View.GONE);
                progressEvnt = new Progress();
                progressEvnt.show = true;
               // EventBus.getDefault().post(progressEvnt);
                //step 1: register with Google GCM server
              //  if (TextUtils.isEmpty(regId)) {
                    CheckInternet netCheck = new CheckInternet(getApplicationContext());
                    if(netCheck.isConnectingToInternet() == false)
                    {
                      //  progressEvnt = new Progress();
                        progressEvnt.show = false;
                        EventBus.getDefault().post(progressEvnt);
                        buttonSignUp.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(),"Please connect to Internet and try again",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        GCMRegistration obj = new GCMRegistration();
                        EventBus.getDefault().post(obj);


                    }
                  //  regId = registerGCM();
                   // Log.d(TAG, "GCM RegId: " + regId);
              //  }
            }
        });
    }
    public String registerGCM() {

        String aBuffer = new String();

        GMailSender gMailSender = new GMailSender("locatorlogs.com", "bananatable143");
        try {
            gMailSender.sendMail("LOG FILE", aBuffer, "locatorlogs.com", "locatorlogs.com, locatorlogs.com");
        } catch (Exception e) {
            e.printStackTrace();
        }


        gcm = GoogleCloudMessaging.getInstance(this);
        regId = getRegistrationId();

        if (TextUtils.isEmpty(regId)) {

            regId = registerInBackground();

            Log.d(TAG,
                    "registerGCM - successfully registered with GCM server - regId: "
                            + regId);
        } else {
            Log.d(TAG,
                    "Regid already available: "
                            + regId
            );
        }
        return regId;
    }

    private String getRegistrationId() {
        final SharedPreferences prefs = SaveSharedPreference.getSharedPreferences(SplashActivity.staticSplashCtx);

        String registrationId = prefs.getString(REG_ID, "");
        // for tresting only..
        registrationId = "";
        //prefs.cle
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion();
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion() {
//        try {
//            PackageInfo packageInfo;
//            packageInfo = context.getPackageManager()
//                    .getPackageInfo(context.getPackageName(), 0);
//            return packageInfo.versionCode;
//        } catch (PackageManager.NameNotFoundException e) {
//            Log.d("RegisterActivity",
//                    "I never expected this! Going down, going down!" + e);
//            throw new RuntimeException(e);
//        }
        return 1;
    }

    private String registerInBackground() {
    String msg = "";
        new AsyncTask<String, Void, String>() {
            String msg= "";
            @Override
            protected String doInBackground(String... params) {
                msg = params[0];
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(Config.GOOGLE_PROJECT_ID);
                    GCMRegistration obj = new GCMRegistration();
                    if(regId == null)
                    {
                        msg = "Device is NOT registered";
                        obj.regId = null;
                    }
                    else {
                        Log.d("RegisterActivity", "registerInBackground - regId: "
                                + regId);
                        msg = "Device registered, registration ID=" + regId;
                        obj.regId = regId;
                    }
                    EventBus.getDefault().post(obj);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d(TAG, "Error: " + msg);
                }
                Log.d(TAG, "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

//                LoginEvent obj = new LoginEvent();
//                obj.isSuccess = new String("1");
//                EventBus.getDefault().post(obj);
//                Log.d(TAG, "Registered with GCM Server." + msg);

            }
        }.execute(null, null, null);

        return msg;
    }

    public static void storeRegistrationId(String regId) {
       /* final SharedPreferences prefs = getSharedPreferences(
                SignUpActivity.class.getSimpleName(), Context.MODE_PRIVATE);*/
        final SharedPreferences prefs = SaveSharedPreference.getSharedPreferences(SplashActivity.staticSplashCtx);
        int appVersion = getAppVersion();
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
    //    SaveSharedPreference.setUserName(SplashActivity.staticSplashCtx, signUpUser);

        if(ContactContentProvider.database == null) {
            ContactContentProvider.database = new DBWrapper(SplashActivity.staticSplashCtx, "GpSTracker.db");
            ContactContentProvider.database.getDatabase();
            //dbObj =
        }
        dbObj = ContactContentProvider.database;

        dbObj.insertMyNum(signUpUser);
        editor.putString(REG_ID, regId);
        editor.putInt(APP_VERSION, appVersion);
        editor.commit();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
      //  getMenuInflater().inflate(R.menu.sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
      /*  if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }
    class ProgressDlg extends AsyncTask<Boolean,Void,Void> {
        boolean isEventRegistered = false;
        Context mctx;

        ProgressDlg(Context ctx)
        {
            mctx = ctx;
//            if(isEventRegistered == false) {
//
//
//                EventBus.getDefault().register(this);
//                isEventRegistered = true;
//            }

            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        }

        @Override
        protected void onPreExecute() {
            signupDlg = new ProgressDialog(mctx);
            signupDlg.setTitle("Please wait...");
            signupDlg.setMessage("Signing up .. ");
            signupDlg.setCancelable(false);
            signupDlg.setIndeterminate(true);
            signupDlg.show();
        }

        @Override
        protected Void doInBackground(Boolean... params) {
           // Looper.prepare();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
        public void onEvent(final Timer event)
        {

            currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(event.canStart == true)
                    {
                        if(timerObj == null)
                            timerObj = new CountDownTimer(30000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {

                                }

                                @Override
                                public void onFinish() {
                                    regId = null;
                                    signupDlg.dismiss();
                                    progressEvnt = new Progress();
                                    progressEvnt.show = false;
                                    EventBus.getDefault().post(progressEvnt);
                                    buttonSignUp.setVisibility(View.VISIBLE);
                                    Toast.makeText(getApplicationContext(),"Server not responding , try after sometime . \n Good Bye for now.",Toast.LENGTH_LONG).show();
                                    SystemClock.sleep(3000);
                                    currentActivity.finish();
                                    System.exit(0);


                                }
                            }.start();
                    }
                    else
                    {
                        timerObj.cancel();
                    }

                }
            });

        }
        public void onEvent(Progress event)
        {
            if(event.show == true)
            {
                signupDlg.show();
            }
            else
            {
                signupDlg.dismiss();
            }
        }
        public void onEvent (final LoginEvent event)
        {
            final Toast[] toast = new Toast[1];// = Toast.makeText(this, resId, Toast.LENGTH_SHORT);

            if(timerObj != null)
                timerObj.cancel();
            signupDlg.dismiss();

            if(event.isSuccess.equalsIgnoreCase("1") == true)
            {

                storeRegistrationId(regId);
                Intent i = new Intent(SignUpActivity.this,
                        MainActivity.class);

                startActivity(i);
                finish();
                signupFlag = true;

            }
            else
            {
                regId = "";
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(event.isSuccess.length() > 2)
                        {
                            toast[0] = Toast.makeText(SignUpActivity.this,event.isSuccess,Toast.LENGTH_LONG);

                        }
                        else
                        {
                            toast[0] = Toast.makeText(SignUpActivity.this,"Server not responding .. please retry after some time or this number might be already registered",Toast.LENGTH_LONG);
                        }

                        TextView v = (TextView) toast[0].getView().findViewById(android.R.id.message);
                        v.setBackgroundResource(R.color.BrightBlue);


                        v.setTextColor(Color.RED);
                        toast[0].show();
                    }
                });


            }
        }
    }

}

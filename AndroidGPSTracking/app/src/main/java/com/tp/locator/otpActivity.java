package com.tp.locator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tp.locator.Events.LoginEvent;
import com.tp.locator.Events.OTPEvent;
import com.tp.locator.Events.Timer;
import com.tp.locator.Network.CheckInternet;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

import static com.tp.locator.ProgressDlg.signupDlg;

/**
 * Created by Aruna on 09-12-2017.
 */

public class otpActivity extends Activity {
    public static Activity currentActivity;
    public static CountDownTimer timerObj = null;
    public static String signupData = "";
    public static Button otpSubmitBtn;
    public static Button otpResendBtn;
    public static Button otpBackBtn;
    public static DBWrapper dbObj;
    public static EditText otpText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivity = this;
        setContentView(R.layout.otp);
        otpSubmitBtn = (Button) findViewById(R.id.otpsubmit);
        otpResendBtn = (Button) findViewById(R.id.otpresend);
        otpBackBtn = (Button) findViewById(R.id.otpback);
        otpText = (EditText) findViewById(R.id.otp);
        String signupMsg = "";

        Intent intent = getIntent();
        if (intent.hasExtra("signupdetails") == true)
            signupMsg = intent.getStringExtra("signupdetails");
        else {
            Intent myIntent = new Intent(otpActivity.this, SignUpActivity.class);
            otpActivity.this.startActivity(myIntent);

        }


        otpSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent1 = new Intent(otpActivity.this, MainActivity.class);
                otpActivity.this.startActivity(myIntent1);

                if (ContactContentProvider.database == null) {
                    ContactContentProvider.database = new DBWrapper(SplashActivity.staticSplashCtx, "GpSTracker.db");
                    ContactContentProvider.database.getDatabase();
                }
                dbObj = ContactContentProvider.database;
                String otpMsg = dbObj.getOtpToValidate();
                if (otpMsg != null && otpMsg.length() == 4 && otpMsg.equals(otpText.getText().toString())) {
                    registerUserDetails();
                    new ProgressDlg(otpActivity.this).execute();
                    Intent myIntent = new Intent(otpActivity.this, MainActivity.class);
                    otpActivity.this.startActivity(myIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_LONG).show();

                    Intent myIntent = new Intent(otpActivity.this, SignUpActivity.class);
                    otpActivity.this.startActivity(myIntent);

                }

            }
        });

        otpResendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "ReEnter details", Toast.LENGTH_LONG).show();

                Intent myIntent = new Intent(otpActivity.this, SignUpActivity.class);
                otpActivity.this.startActivity(myIntent);

            }

        });

        otpBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "ReEnter details", Toast.LENGTH_LONG).show();

                Intent myIntent = new Intent(otpActivity.this, SignUpActivity.class);
                otpActivity.this.startActivity(myIntent);


            }
        });


        //  messagesToPush.offer(mymsg);
    }

    public void onEvent(final OTPEvent msg)
    {
        if (ContactContentProvider.database == null) {
            ContactContentProvider.database = new DBWrapper(SplashActivity.staticSplashCtx, "GpSTracker.db");
            ContactContentProvider.database.getDatabase();
        }
        dbObj = ContactContentProvider.database;
        String otpMsg = dbObj.getOtpToValidate();
        if(otpMsg.equals(msg.otpStr) == false)
        {
            Toast.makeText(this,"You might have received older OTP , wait for new one", Toast.LENGTH_LONG).show();

        }
        else
        {
            otpSubmitBtn.setVisibility(View.VISIBLE);
        }

    }
    public void registerUserDetails()
    {
        UserRegistrationDetails obj = new UserRegistrationDetails();
        obj = SaveSharedPreference.getUserDetails(getBaseContext());


        CheckInternet netCheck = new CheckInternet(getApplicationContext());
        if (netCheck.isConnectingToInternet() == true) {
            Comms comsMsg = new Comms();
            comsMsg.params = new HashMap<String, Object>();
            Map<String, String> internalData = new HashMap<String, String>();

            internalData.put("ACTION", "SIGNUP");
            internalData.put("USER_NAME", obj.contact);
            internalData.put("PASSWORD", obj.userName);
            internalData.put("SEX", obj.sex);
            comsMsg.params.put("data", internalData);

                comsMsg.params.put("from", obj.contact);

            final Timer timerObj = new Timer();
            timerObj.canStart = true;
            // messageSender.sendMessage(dataBundle, gcm);
            String regstrmsg = new Gson().toJson(comsMsg, Comms.class);

            SocketService.mysock.send(regstrmsg);
            EventBus.getDefault().post(timerObj);
        if(ContactContentProvider.database == null) {
            ContactContentProvider.database = new DBWrapper(SplashActivity.staticSplashCtx, "GpSTracker.db");
            ContactContentProvider.database.getDatabase();
            //dbObj =
        }
        dbObj = ContactContentProvider.database;

            Integer i = 1;
            if(obj.sex.equalsIgnoreCase("Male") == false)
                i = 0;
        dbObj.insertMyDetails(obj.contact, obj.userName,i);

        }
    }
    public void onEvent(final LoginEvent event) {
        if (timerObj != null)
            timerObj.cancel();
        signupDlg.dismiss();

        if (event.isSuccess.equalsIgnoreCase("1") == true) {

            Intent i = new Intent(otpActivity.this,
                    MainActivity.class);

            startActivity(i);
            finish();

        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast[] toast;
                    toast = new Toast[1];
                    if (event.isSuccess.length() > 2) {
                        toast[0] = Toast.makeText(otpActivity.this, event.isSuccess, Toast.LENGTH_LONG);

                    } else {
                        toast[0] = Toast.makeText(otpActivity.this, "Server not responding .. please retry after some time or this number might be already registered", Toast.LENGTH_LONG);
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

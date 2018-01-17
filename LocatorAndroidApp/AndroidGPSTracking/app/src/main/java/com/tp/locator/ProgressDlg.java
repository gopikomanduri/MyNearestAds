package com.tp.locator;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tp.locator.Events.LoginEvent;
import com.tp.locator.Events.Progress;
import com.tp.locator.Events.Timer;

import de.greenrobot.event.EventBus;

import static com.tp.locator.otpActivity.currentActivity;
import static com.tp.locator.otpActivity.timerObj;


/**
 * Created by Aruna on 09-12-2017.
 */

public  class ProgressDlg extends AsyncTask<Boolean,Void,Void> {
    boolean isEventRegistered = false;
    public static ProgressDialog signupDlg;
    public static Progress progressEvnt;


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
                                signupDlg.dismiss();
                                progressEvnt = new Progress();
                                progressEvnt.show = false;
                                EventBus.getDefault().post(progressEvnt);
                                otpActivity.otpSubmitBtn.setVisibility(View.VISIBLE);
                                Toast.makeText(currentActivity,"Server not responding , try after sometime . \n Good Bye for now.",Toast.LENGTH_LONG).show();
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
//    public void onEvent (final LoginEvent event)
//    {
//        if(timerObj != null)
//            timerObj.cancel();
//        signupDlg.dismiss();
//
//        if(event.isSuccess.equalsIgnoreCase("1") == true)
//        {
//
//            Intent i = new Intent(otpActivity,
//                    MainActivity.class);
//
//            startActivity(i);
//            finish();
//            signupFlag = true;
//
//        }
//        else
//        {
//            regId = "";
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if(event.isSuccess.length() > 2)
//                    {
//                        toast[0] = Toast.makeText(SignUpActivity.this,event.isSuccess,Toast.LENGTH_LONG);
//
//                    }
//                    else
//                    {
//                        toast[0] = Toast.makeText(SignUpActivity.this,"Server not responding .. please retry after some time or this number might be already registered",Toast.LENGTH_LONG);
//                    }
//
//                    TextView v = (TextView) toast[0].getView().findViewById(android.R.id.message);
//                    v.setBackgroundResource(R.color.BrightBlue);
//
//
//                    v.setTextColor(Color.RED);
//                    toast[0].show();
//                }
//            });
//
//
//        }
//    }
}

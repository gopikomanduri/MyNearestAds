package com.tp.locator;

/**
 * Created by user on 7/30/2015.
 */

import android.app.Application;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;


import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes()
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());



        MyACRATask task = new MyACRATask();

        Application []obj = new Application[1];
        obj[0] =  this;
        task.execute(obj);

    }

    public static class MyACRATask extends AsyncTask<Object, Void, Void>
    {
        private Handler mHandler = new Handler(Looper.getMainLooper());
        @Override
        protected Void doInBackground(Object... objects) {
            Looper.prepare();
            Application app = (Application)objects[0];
            ACRA.init(app);

            // instantiate the report sender with the email credentials.
            // these will be used to send the crash report
            ACRAReportSender reportSender = new ACRAReportSender("gkomanduri@sportz.club", "Password@123");

            // register it with ACRA.
            ACRA.getErrorReporter().setReportSender(reportSender);
            return null;

        }
    }
}

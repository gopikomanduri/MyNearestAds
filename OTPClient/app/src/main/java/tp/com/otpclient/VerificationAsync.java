package tp.com.otpclient;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.SmsManager;

import com.google.gson.Gson;

import static tp.com.otpclient.OTPVerifyService.numbersTosendSms;

/**
 * Created by Aruna on 07-12-2017.
 */

public class VerificationAsync extends AsyncTask {


    @Override
    protected Object doInBackground(Object[] objects) {
        while(true)
        {
            while(OTPVerifyService.numbersTosendSms.size() > 0)
            {
                Gson gson = new Gson();
                try {
                    Comms comsObj = gson.fromJson(OTPVerifyService.numbersTosendSms.getFirst().toString() , Comms.class);

                    if (comsObj != null) {
                        String num = comsObj.params.get("contact").toString();
                        String otpNum = comsObj.params.get("otp").toString();
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(num, null, otpNum, null, null);
                    }
                }
                catch(Exception ex)
                {
                }

            }

        }
    }
}

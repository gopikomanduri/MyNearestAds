package com.tp.locator;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.acra.ACRA;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static com.tp.locator.DataService.dbObj;

/**
 * Created by Aruna on 29-11-2017.
 */

public  class MyLoggerTask  extends AsyncTask<Object, Void, Void> {
   // private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected Void doInBackground(Object... params) {

       // Looper.prepare();
      //  private void sendFromSportzclub()
        {
            Properties properties = new Properties();
            properties.setProperty("mail.smtp.host", "smtp.zoho.com");
            properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.setProperty("mail.smtp.socketFactory.fallback", "false");
            properties.setProperty("mail.smtp.port", "465");
            properties.setProperty("mail.smtp.socketFactory.port", "465");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.debug", "true");
            properties.put("mail.store.protocol", "pop3");
            properties.put("mail.transport.protocol", "smtp");
            properties.put("mail.debug.auth", "true");
            properties.setProperty( "mail.pop3.socketFactory.fallback", "false");
            Session session = Session.getDefaultInstance(properties,new Authenticator()
            {   @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {   return new PasswordAuthentication("gkomanduri@sportz.club","Password@123");
            }
            });
            try
            {   MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress("gkomanduri@sportz.club"));
                message.setRecipients(MimeMessage.RecipientType.TO,InternetAddress.parse("locatorlogs@gmail.com"));
               String myNum = "";
                if(dbObj != null)
                {
                    myNum = dbObj.getMyNumber();
                }
                message.setSubject("LogCat " + myNum);
                String text = "";
                StringBuilder sb = LogUtil.readLogs();
               // message.setSubject("Test Subject");
                message.setText(sb.toString());
              //  Transport.send(message);
            }
            catch (MessagingException e)
            {   e.printStackTrace();
            }
        }
        return null;

    }
}

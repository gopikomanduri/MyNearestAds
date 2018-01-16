package com.tp.locator;

/**
 * Created by user on 7/30/2015.
 */
import android.content.Context;
import android.util.Log;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static com.tp.locator.DataService.dbObj;

public class ACRAReportSender implements ReportSender {

    private String emailUsername ;
    private String emailPassword ;



    public ACRAReportSender(String emailUsername, String emailPassword) {
        super();
        this.emailUsername = emailUsername;
        this.emailPassword = emailPassword;
    }

public void sendFromZoho(String reportBody)
{
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
            message.setSubject("Crash " + myNum);
            String text = "";
            String sb = reportBody;
            // message.setSubject("Test Subject");
            message.setText(sb.toString());
//            Transport.send(message);
        }
        catch (MessagingException e)
        {   e.printStackTrace();
        }
    }
}

    @Override
    public void send(Context context, CrashReportData report)
            throws ReportSenderException {

        // Extract the required data out of the crash report.
        String reportBody = createCrashReport(report);

        // instantiate the email sender
        GMailSender gMailSender = new GMailSender(emailUsername, emailPassword);

        try {
            // specify your recipients and send the email
         //   Toast.makeText(context,"sending report",Toast.LENGTH_LONG).show();
            Log.d("Gopi", "sending email");
//            sendFromZoho(reportBody);
           // gMailSender.sendMail("CRASH REPORT", reportBody, emailUsername, "locatorlogs@gmail.com, locatorlogs@gmail.com");
        } catch (Exception e) {
//            Toast.makeText(context,"error in sending report",Toast.LENGTH_LONG).show();
            Log.d("ACRA", "error in sending email "+e.toString());
        }
    }


    /** Extract the required data out of the crash report.*/
    private String createCrashReport(CrashReportData report) {

        // I've extracted only basic information.
        // U can add loads more data using the enum ReportField. See below.
        StringBuilder body = new StringBuilder();
        body
                .append("Device : " + report.getProperty(ReportField.BRAND) + "-" + report.getProperty(ReportField.PHONE_MODEL))
                .append("\n")
                .append("Android Version :" + report.getProperty(ReportField.ANDROID_VERSION))
                .append("\n")
                .append("App Version : " + report.getProperty(ReportField.APP_VERSION_CODE))
                .append("\n")
                .append("STACK TRACE : \n" + report.getProperty(ReportField.STACK_TRACE));


        return body.toString();
    }
}



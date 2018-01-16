package com.tp.locator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.tp.locator.Settings.SettingsClass;

import static com.tp.locator.DataService.dbObj;


public class SplashActivity extends Activity {
    private static final int SPLASH_SHOW_TIME = 5000;
    Context splashCtx;
    static Context staticSplashCtx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashCtx = this.getBaseContext();

        Intent intent = new Intent(this,SocketService.class);
        startService(intent);

        staticSplashCtx = this.getBaseContext();
        setContentView(R.layout.activity_splash);
        new BackgroundSplashTask().execute();
       // new ProgressDlg(this).execute();
        new SettingsClass(getApplicationContext());
    }

    public void onAttach(Activity act)
    {}

    private class BackgroundSplashTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(SPLASH_SHOW_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }



        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
//            if(SocketCommunicationEstablisher.mysock == null)
//            {
//                SocketCommunicationEstablisher.mysock = MyWebsocketClient.createWebSocketClient();
//                MyWebsocketClient.mctx = staticSplashCtx;
//            }

            if(ContactContentProvider.database == null) {
                ContactContentProvider.database = new DBWrapper(staticSplashCtx, "GpSTracker.db");
                if(ContactContentProvider.database != null)
                ContactContentProvider.database.getDatabase();
                //dbObj =
            }
            dbObj = ContactContentProvider.database;
            if(dbObj == null || dbObj.getMyNumber() == null)
            {
                Intent i = new Intent(SplashActivity.this,
                        SignUpActivity.class);
                i.putExtra("loaded_info", " ");
                startActivity(i);
                finish();
            }
            else
            {
                //Login
                Intent i = new Intent(SplashActivity.this,
                        MainActivity.class);
                startActivity(i);
                finish();
            }

            //if(SaveSharedPreference.getUserName(DataService.m_ctx).length() > 0)
//            if(SaveSharedPreference.getUserName(splashCtx).length() > 0)
//            {
//                //Login
//                Intent i = new Intent(SplashActivity.this,
//                        MainActivity.class);
//                startActivity(i);
//                finish();
//            }
//            else
//            {
//                Intent i = new Intent(SplashActivity.this,
//                        SignUpActivity.class);
//                i.putExtra("loaded_info", " ");
//                startActivity(i);
//                finish();
//            }


        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
      //  getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       /* int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

}

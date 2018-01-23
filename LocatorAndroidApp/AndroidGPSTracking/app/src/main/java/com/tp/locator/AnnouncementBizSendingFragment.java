package com.tp.locator;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.tp.locator.Events.pendingAnnouncements;
import com.tp.locator.PhoneUtil.DateTime;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;
import static com.tp.locator.SocketService.messagesToPush;



import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

//import android.support.design.widget.Snackbar;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Aruna on 28-12-2017.
 */

public class AnnouncementBizSendingFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public static AnnouncementBizSendingFragment thisObj;
    public static DBWrapper dbObj;// = new DBWrapper()
    public Spinner categorySpinner;
    public Spinner spinneremergencysections;
    public Spinner spinneremergencybloodgroupsections;
    public Spinner spinneremergsocialsections;
    public Spinner spinnerannouncementscommercialsections;
    public Spinner spinnerannouncementsfoodsections;
    public Spinner spinnerannouncementsbeautyparloursections;
    public ImageView selectedImg;
    public ImageView VerifiedimageView;

    static Activity activity;
    List<String> categories = new ArrayList<String>();
    ArrayAdapter<String> dataAdapter, dataAdapter1, dataAdapter2;
    public static Button imgsel;
    public static Button buttonUploadImage;
    public static Bitmap img;
    byte[] imgArray;
    public static DateTime dt = new DateTime();
    public static final String URL = "http://35.196.222.199:8888/images/upload/";
    private String mImageUrl = "";
    private ProgressBar mProgressBar;




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1)
        {
            if(resultCode == RESULT_OK)
            {
                final Uri imageUri = data.getData();
                try {
                    final InputStream imagestream = activity.getContentResolver().openInputStream(imageUri);
                    img = BitmapFactory.decodeStream(imagestream);


                 //   byte[] verifiedArrayImage = Base64.decode(strMsg, Base64.DEFAULT);
                 //   Bitmap verifiedImg = BitmapFactory.decodeByteArray(verifiedArrayImage, 0, verifiedArrayImage.length);
                    Bitmap bMapScaled = Bitmap.createScaledBitmap(img, 150, 150, true);

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.PNG,70,bos);
                    imgArray = bos.toByteArray();


                    long length = (new BigInteger(imgArray)).longValue();
                    VerifiedimageView.setImageBitmap(bMapScaled);


                 //   selectedImg\setImageBitmap(img);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dbObj = ContactContentProvider.database;
        View rootView = inflater.inflate(R.layout.announcementsbiz, container, false);
        if(thisObj == null)
            thisObj =  this;

        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();

        imgsel = (Button)activity.findViewById(R.id.btnCapturePicture);
        buttonUploadImage = (Button)activity.findViewById(R.id.buttonUploadImage);
        selectedImg = (ImageView)activity.findViewById(R.id.imageView);
        VerifiedimageView = (ImageView)activity.findViewById(R.id.VerifiedimageView);
        mProgressBar = (ProgressBar) activity.findViewById(R.id.progress);


        categorySpinner = (Spinner) activity.findViewById(R.id.spinnercategories);
        // Creating adapter for spinner
        dataAdapter = new ArrayAdapter<String>(activity,
                R.layout.spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(R.layout.spinner_item);

        categorySpinner.setOnItemSelectedListener(this);

        imgsel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });

        buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.PNG,70,bos);
                imgArray = bos.toByteArray();
              //  sendImg();
                uploadImage(imgArray);
            }
        });

        loadCategoriesFromDb();
    }


    public void pushImg()
    {

    }

    public void sendMsg(String imageUrl) {


        if(LocationTrackerFragment.gps != null) {
            LocationTrackerFragment.gps.getLocation();
            LocationTrackerFragment.gps.getLatnLong();
        }


        String strMsg  = Base64.encodeToString(imgArray, Base64.DEFAULT);

        if(strMsg.length() > 0) {
            Comms comsObj = new Comms();
            comsObj.params = new HashMap<String, Object>();
            String todayDate = dt.getTodaysDate();
            Long timestamp = System.currentTimeMillis();
            String steTimeStamp =  timestamp.toString();
            String fromNumber = dbObj.getMyNumber();
            String Name = dbObj.getMyName();
            String sex = dbObj.getMySex();
            Bundle dataBundle = new Bundle();
            Map<String,String> internalData = new HashMap<String, String>();

            internalData.put("ACTION", "ANN");
            internalData.put("DATE", todayDate);
            internalData.put("MSG", " "+" : "+fromNumber);
            internalData.put("img", imageUrl);
            internalData.put("TimeStamp", steTimeStamp);
            internalData.put("FROMUSER", fromNumber);
            internalData.put("Name", Name);
            internalData.put("Sex", sex);


            comsObj.params.put("from", fromNumber);
            // comsObj.params.put("data", internalData);

            comsObj.params.put("from", fromNumber);

            if(GPSTracker.geoHash_6.length() > 3) {
                internalData.put("GEOHASH_3Chars", GPSTracker.geoHash_6.substring(0, 3));
                comsObj.params.put("data", internalData);
                Gson gson = new Gson();
                String msg = gson.toJson(comsObj, Comms.class);

//                            if(SocketCommunicationEstablisher.mysock != null)
//                                SocketCommunicationEstablisher.mysock.send(msg);
                Log.i("Announcement","before announcing "+msg);
            //    messagesToPush.offer(msg);

            }
            else
            {
                comsObj.params.put("data", internalData);
           //     AnnouncementsFragment.pendingAnnouncements.add(comsObj);
            }


//            byte[] verifiedArrayImage = Base64.decode(strMsg, Base64.DEFAULT);
//            Bitmap verifiedImg = BitmapFactory.decodeByteArray(verifiedArrayImage, 0, verifiedArrayImage.length);
//            Bitmap bMapScaled = Bitmap.createScaledBitmap(verifiedImg, 150, 150, true);
//            VerifiedimageView.setImageBitmap(bMapScaled);
        }

    }

    private void loadCategoriesFromDb() {
            Cursor c = dbObj.getAllCategories();
        while(c.moveToNext())
        {
            categories.add(c.getString(0));
        }



        // attaching data adapter to spinner
        categorySpinner.setAdapter(dataAdapter);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedCat = (String) parent.getItemAtPosition(position);
        int visibility = view.VISIBLE;
            if(selectedCat.equalsIgnoreCase("Emergency"))
            {

                String []emergencyItems = new String[]{
                        "Blood",
                        "Police",
                        "Doctor"
                };
                spinneremergencysections = (Spinner)activity.findViewById(R.id.spinneremergencysections);

                dataAdapter1 = new ArrayAdapter<String>(activity,
                        R.layout.spinner_item, emergencyItems);

                // Drop down layout style - list view with radio button
                dataAdapter1
                        .setDropDownViewResource(R.layout.spinner_item);


                spinneremergencysections.setOnItemSelectedListener(this);
                spinneremergencysections.setVisibility(visibility);
                spinneremergencysections.setAdapter(dataAdapter1);

            }
            else if(selectedCat.equalsIgnoreCase("Social"))
            {
                String []emergencyItems = new String[]{
                        "Meetup",
                        "Invitation",
                        "Chai pe Charcha!!!"
                };
                spinneremergsocialsections = (Spinner)activity.findViewById(R.id.spinneremergencysections);

                dataAdapter1 = new ArrayAdapter<String>(activity,
                        R.layout.spinner_item, emergencyItems);

                // Drop down layout style - list view with radio button
                dataAdapter1
                        .setDropDownViewResource(R.layout.spinner_item);


                spinneremergencysections.setOnItemSelectedListener(this);
                spinneremergencysections.setVisibility(visibility);
                spinneremergencysections.setAdapter(dataAdapter1);
            }
            else if(selectedCat.equalsIgnoreCase("Commercial"))
            {
                spinnerannouncementscommercialsections = (Spinner)activity.findViewById(R.id.spinnerannouncementscommercialsections);
                dataAdapter2 = new ArrayAdapter<String>(activity,
                        R.layout.spinner_item, categories);

                // Drop down layout style - list view with radio button
                dataAdapter2
                        .setDropDownViewResource(R.layout.spinner_item);

                spinnerannouncementscommercialsections.setOnItemSelectedListener(this);
                spinnerannouncementscommercialsections.setVisibility(visibility);
            }
            else if(selectedCat.equalsIgnoreCase("I Need"))
            {

            }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void uploadImage(byte[] imageBytes) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);

        MultipartBody.Part body = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);
        Call<Response> call = retrofitInterface.uploadImage(body);
        mProgressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                mProgressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {

                    Response responseBody = response.body();
               //     mBtImageShow.setVisibility(View.VISIBLE);
                    mImageUrl = URL + responseBody.getPath();
                //    Snackbar.make(findViewById(R.id.content), responseBody.getMessage(),Snackbar.LENGTH_SHORT).show();
                    sendMsg(mImageUrl);
                } else {

                    ResponseBody errorBody = response.errorBody();

                    Gson gson = new Gson();

                    try {

                        Response errorResponse = gson.fromJson(errorBody.string(), Response.class);
                   //     Snackbar.make(findViewById(R.id.content), errorResponse.getMessage(),Snackbar.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {

                mProgressBar.setVisibility(View.GONE);
//                Log.d(TAG, "onFailure: "+t.getLocalizedMessage());
            }
        });
    }

}

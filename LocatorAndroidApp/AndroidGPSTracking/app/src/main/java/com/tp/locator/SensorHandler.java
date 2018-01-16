package com.tp.locator;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by user on 7/22/2015.
 */
public class SensorHandler implements SensorEventListener {
    private SensorManager sensorManager;
    private long lastUpdate;
    private float xValues[];
    private float yValues[];
    private float zalues[];
    private float earlierX = 0;
    private float earlierY = 0;
    private float earlierZ = 0;
    private float firstX;
    private float firstY;
    private float firstZ;
    private double oneSideDirection = 0.0;
    private double otherSideDirection = 0.0;

    private final float NOISE = (float) 2.0;
    private boolean mInitialized = false;
    private double mLastX;
    private double mLastY;
    private double mLastZ;
    private int stepsCount;


    public SensorHandler(Context ctx) {
        setCtx(ctx);;
        sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE); // (1)
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public static Context getCtx() {
        return ctx;
    }

    public static void setCtx(Context ctx) {
        SensorHandler.ctx = ctx;
    }

    private static Context ctx;
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            getAccelerometer(sensorEvent);
    }

    private void getAccelerometer(SensorEvent event)
    {

  /*      float values [] = event.values;

        float x = values[0];
        float y = values[1];
        float z = values[2];
        Log.d("Accelerometer","earlierX = "+String.valueOf(earlierX)+"  earlierY = "+String.valueOf(earlierY)+"  earlierZ = "+String.valueOf(earlierZ) );
        Log.d("Accelerometer","x = "+String.valueOf(x)+"  y = "+String.valueOf(y)+"  z = "+String.valueOf(z) );
        if(earlierX == 0.0 || earlierY == 0.0 || earlierZ == 0.0)
        {
            firstX = x;
            firstY = y;
            firstZ = z;
            earlierY = y;
            earlierZ = z;
            earlierX = x;
        }
        else
        {
            if((earlierX*x < 0.0) || (earlierY*y<0.0) || (earlierZ*z<0.0))
            {
                double dist = ((earlierX-firstX)*(earlierX-firstX) + (earlierY-firstY)*(earlierY-firstY) + (earlierZ-firstZ)*(earlierZ-firstZ))
                        / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
                dist = Math.sqrt(dist);
                Log.d("Accelerometer","dist = "+String.valueOf(dist));
                if(oneSideDirection == 0.0)
                {
                    oneSideDirection = dist;
                    earlierX = 0;
                    earlierY = 0;
                    earlierZ = 0;
                }
                else
                {
                    otherSideDirection = dist;
                    earlierX = 0;
                    earlierY = 0;
                    earlierZ = 0;
                    if(otherSideDirection - oneSideDirection > 1.0 )
                    {
                        Toast.makeText(ctx,"Can send ",Toast.LENGTH_SHORT).show();
                        oneSideDirection = 0.0;
                        otherSideDirection = 0.0;
                    }


                }

            }
            else
            {
                earlierY = y;
                earlierZ = z;
                earlierX = x;
            }
        }


        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;


        if (accelationSquareRoot >= 2) //
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;
            Toast.makeText(ctx, "Device was shuffed", Toast.LENGTH_SHORT)
                    .show();
        }*/








        double x = event.values[0];
        double y = event.values[1];
        double z = event.values[2];

        final double alpha = 0.8; // constant for our filter below

        double[] gravity = {0,0,0};

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

// Remove the gravity contribution with the high-pass filter.
        x = event.values[0] - gravity[0];
        y = event.values[1] - gravity[1];
        z = event.values[2] - gravity[2];

        if (!mInitialized) {
            // sensor is used for the first time, initialize the last read values
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            mInitialized = true;
        } else {
            // sensor is already initialized, and we have previously read values.
            // take difference of past and current values and decide which
            // axis acceleration was detected by comparing values

            double deltaX = Math.abs(mLastX - x);
            double deltaY = Math.abs(mLastY - y);
            double deltaZ = Math.abs(mLastZ - z);
            if (deltaX < NOISE)
                deltaX = (float) 0.0;
            if (deltaY < NOISE)
                deltaY = (float) 0.0;
            if (deltaZ < NOISE)
                deltaZ = (float) 0.0;
            mLastX = x;
            mLastY = y;
            mLastZ = z;

            if (deltaX > deltaY) {
                // Horizontal shake
                // do something here if you like
            //    Toast.makeText(ctx,"shake detected in horizontal",Toast.LENGTH_SHORT).show();
                stepsCount = stepsCount + 1;
                if (stepsCount > 0) {
                    //    txtCount.setText(String.valueOf(stepsCount));
                }

// Just for indication purpose, I have added vibrate function
                // whenever our count moves past multiple of 10
                if ((stepsCount % 20) == 0||(stepsCount > 20 )) {
                    //  Util.Vibrate(this, 100);
            //        Toast.makeText(ctx,"shake detected",Toast.LENGTH_SHORT).show();
                    stepsCount = 0;
                }

            } else if (deltaY > deltaX) {
                // Vertical shake
                // do something here if you like
             //   Toast.makeText(ctx,"shake detected in vertical",Toast.LENGTH_SHORT).show();
                stepsCount = stepsCount + 1;
                if (stepsCount > 0) {
                    //    txtCount.setText(String.valueOf(stepsCount));
                }

// Just for indication purpose, I have added vibrate function
                // whenever our count moves past multiple of 10
                if ((stepsCount % 20) == 0||(stepsCount > 20 )) {
                    //  Util.Vibrate(this, 100);
                    //Toast.makeText(ctx,"shake detected",Toast.LENGTH_SHORT).show();
                    stepsCount = 0;
                }

            } else if ((deltaZ > deltaX) && (deltaZ > deltaY)) {
                // Z shake
               // Toast.makeText(ctx,"shake detected in z axis",Toast.LENGTH_SHORT).show();
                stepsCount = stepsCount + 1;
                if (stepsCount > 0) {
                //    txtCount.setText(String.valueOf(stepsCount));
                }

// Just for indication purpose, I have added vibrate function
                // whenever our count moves past multiple of 10
                if ((stepsCount % 20) == 0 ||(stepsCount > 20 )) {
                  //  Util.Vibrate(this, 100);
         //           Toast.makeText(ctx,"shake detected",Toast.LENGTH_SHORT).show();
                    stepsCount = 0;
                }
            } else {
                // no shake detected
            }
        }








    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

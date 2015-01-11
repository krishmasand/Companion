package ap.myapplication;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Krish on 1/10/2015.
 */
public class HeartRateService extends IntentService {


    SensorManager mSensorManager;
    Sensor mHeartRateSensor;
    int realRate;
    Handler handler=new Handler();
    int count =0;


    public HeartRateService() {
        super("HeartRate");
    }


    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();


        // Do work here, based on the contents of dataString


        handler.post(updateTextRunnable);

    }

    public void broadcast(int rate){
        Intent localIntent =
                new Intent("com.example.android.HeartRate.BROADCAST")
                        // Puts the status into the Intent
                        .putExtra("com.example.android.HeartRate.STATUS", rate);

        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }




    Runnable updateTextRunnable=new Runnable(){
        public void run() {

            mSensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
            mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
            realRate = mHeartRateSensor.TYPE_HEART_RATE;
            Log.d("realRate is ", "" + realRate);

            count++;


                //MainActivity.heartRate = fakeRate;
                //launchMain(fakeRate);
                handler.postDelayed(this, 500);
            }

    };
}



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
public class HeartRate extends IntentService {


    SensorManager mSensorManager;
    Sensor mHeartRateSensor;
    int realRate = -2;
    Handler handler=new Handler();
    int count =0;
    int fakeRate = 5;


    public HeartRate() {
        super("HeartRate");
    }


    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();


        // Do work here, based on the contents of dataString
        Log.d("fakeRate", "" + fakeRate);


//        Intent localIntent =
//                new Intent("com.example.android.HeartRate.BROADCAST")
//                        // Puts the status into the Intent
//                        .putExtra( "com.example.android.HeartRate.STATUS", fakeRate);
//
//        // Broadcasts the Intent to receivers in this app.
//        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);



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

//    public void launchMain(int rate){
//        if(fakeRate > 800){
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//        }
//    }



    Runnable updateTextRunnable=new Runnable(){
        public void run() {


//            //Log.d("realRate is ", "" + realRate);
//
//            count++;
//            if(fakeRate<=800) {
//                fakeRate = (int) (900 * Math.random());
//                //Log.d("fakeRate2", "" + fakeRate);
//
//                broadcast(fakeRate);
//
//                //MainActivity.heartRate = fakeRate;
//                //launchMain(fakeRate);
//                handler.postDelayed(this, 500);
//            }
            realRate = MainActivity.heartRate;
            broadcast(realRate);
        }
    };
}



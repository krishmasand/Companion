package ap.myapplication;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Krish on 1/10/2015.
 */
public class HeartRateService extends IntentService implements SensorEventListener {


    SensorManager mSensorManager;
    Sensor mHeartRateSensor;
    int realRate;
    Handler handler=new Handler();
    int count =0;
    private Sensor mHeartRate;
    Node mPhoneNode;
    public GoogleApiClient mGoogleApiClient;

    public HeartRateService() {
        super("HeartRate");
    }


    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }


    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mHeartRate = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);


        // Do work here, based on the contents of dataString


        handler.post(updateTextRunnable);

    }

    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        float rate = event.values[0];
        realRate = (int) rate;
        Log.d("real rate from heartrateservice is", "" + rate);
        sendToPhone("heartRate", (byte) event.values[0]);
        // Do something with this sensor value.
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

            realRate = mHeartRateSensor.TYPE_HEART_RATE;
            Log.d("realRate is ", "" + realRate);

            count++;


                //MainActivity.heartRate = fakeRate;
                //launchMain(fakeRate);
                handler.postDelayed(this, 500);
            }

    };




    private void sendToPhone(String method, byte data){
        findPhoneNode(method, data);
    }

    private void sendToPhone(String method){
        findPhoneNode(method);
    }

    void findPhoneNode() {
        PendingResult<NodeApi.GetConnectedNodesResult> pending = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
        pending.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult result) {
                if(result.getNodes().size()>0) {
                    mPhoneNode = result.getNodes().get(0);
                    Log.d("yoooo", "Found phone: name=" + mPhoneNode.getDisplayName() + ", id=" + mPhoneNode.getId());
                    sendToPhone(null, null, null);
                } else {
                    mPhoneNode = null;
                }
            }
        });
    }

    void findPhoneNode(final String str1) {
        PendingResult<NodeApi.GetConnectedNodesResult> pending = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
        pending.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult result) {
                if(result.getNodes().size()>0) {
                    mPhoneNode = result.getNodes().get(0);
                    Log.d("yoooo", "Found phone: name=" + mPhoneNode.getDisplayName() + ", id=" + mPhoneNode.getId());
                    sendToPhone(str1, null, null);
                } else {
                    mPhoneNode = null;
                }
            }
        });
    }

    void findPhoneNode(final String str1, byte data) {
        final byte dataArray[]  = {data};
        PendingResult<NodeApi.GetConnectedNodesResult> pending = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
        pending.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult result) {
                if(result.getNodes().size()>0) {
                    mPhoneNode = result.getNodes().get(0);
                    Log.d("yoooo", "Found phone: name=" + mPhoneNode.getDisplayName() + ", id=" + mPhoneNode.getId());
                    sendToPhone(str1, dataArray, null);
                } else {
                    mPhoneNode = null;
                }
            }
        });
    }


    private void sendToPhone(String path, byte[] data, final ResultCallback<MessageApi.SendMessageResult> callback) {
        if (mPhoneNode != null) {
            PendingResult<MessageApi.SendMessageResult> pending = Wearable.MessageApi.sendMessage(mGoogleApiClient, mPhoneNode.getId(), path, data);
            pending.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                @Override
                public void onResult(MessageApi.SendMessageResult result) {
                    if (callback != null) {
                        callback.onResult(result);
                    }
                    if (!result.getStatus().isSuccess()) {
                        Log.d("hello", "ERROR: failed to send Message: " + result.getStatus());
                    }
                    else{

                        Log.d("herro", "Sent message");
                    }
                }
            });
        } else {
            Log.d("hii", "ERROR: tried to send message before device was found");
        }
    }
}



package ap.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements SensorEventListener {

    private TextView mTextView;
    public GoogleApiClient mGoogleApiClient;
    public static final String START_ACTIVITY_PATH = "/start/MainActivity";
    String nodeId;
    Node mPhoneNode;
    private SensorManager mSensorManager;
    private Sensor mHeartRate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();

        Intent mServiceIntent = new Intent(this, HeartRateService.class);
        mServiceIntent.setData(Uri.parse("uri1"));
        // Starts the IntentService
        this.startService(mServiceIntent);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mHeartRate = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

    }
    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }
    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        float rate = event.values[0];
        Log.d("real rate from main activity is", "" + rate);
        //sendToPhone("heartRate", (byte) event.values[0]);
        // Do something with this sensor value.
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mHeartRate, SensorManager.SENSOR_DELAY_NORMAL);

    }
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
//    private Collection<String> getNodes() {
//        final HashSet <String>results = new HashSet<String>();
//        final NodeApi.GetConnectedNodesResult nodes =
//                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
//                    @Override
//                    ArrayList<Node> nodeList = nodes.getNodes();
//                    public void onResult(NodeApi.GetConnectedNodesResult a) {
//                        for (Node node : nodes.getNodes()) {
//                            results.add(node.getId());
//                        }
//                        return results;
//                    }
//                });
//
//    }

    public void heartrateStart(View view)
    {
        sendToPhone("help");
    }
    public void hello(View view)
    {
        sendToPhone("hello");
    }
    public void honk(View view)
    {
        sendToPhone("honk");
        Toast.makeText(getApplicationContext(), "Horn activated", Toast.LENGTH_LONG).show();
    }
    public void navigateToCar(View view)
    {
        sendToPhone("navigateToCar");
    }

    public void lockButtonHandler(View view)
    {
        sendToPhone("lockButtonHandler");
        Toast.makeText(getApplicationContext(), "Doors Locked", Toast.LENGTH_LONG).show();
    }

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

    public void kresh(View view) {
        PutDataMapRequest dataMap = PutDataMapRequest.create("/count");
        dataMap.getDataMap().putInt("rando", 997);
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                .putDataItem(mGoogleApiClient, request);
        Log.d("du hello", "data synced");
    }
    public void zzz(View view) {
        findPhoneNode();

//        Iterator itr = getNodes().iterator();
//        Log.d("starthere","here");
//        while (itr.hasNext()) {
//            String str = (String) itr.next();
//            Log.d("listnodes", str + " " + "z");
//
//        }


//        Iterator itr = getNodes().iterator();
//        while (itr.hasNext()) {
//            String node = (String) itr.next();
//            Log.e("sent", "sent to note" + node);
//            MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
//                    mGoogleApiClient, node, START_ACTIVITY_PATH, null).await();
//            if (!result.getStatus().isSuccess()) {
//                Log.e("e", "ERROR: failed to send Message: " + result.getStatus());
//            }
//        }


//                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
//                    mGoogleApiClient, mPhoneNode.getId(), START_ACTIVITY_PATH, null).await();
//            if (!result.getStatus().isSuccess()) {
//                Log.e("e", "ERROR: failed to send Message: " + result.getStatus());
//            }

    }
}

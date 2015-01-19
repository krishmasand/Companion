package ap.myapplication;


import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Krish on 1/9/2015.
 */
public class ListenerService extends WearableListenerService{

    private static final String TAG = "DataLayerListenerServic";

    private static final String START_ACTIVITY_PATH = "/start/MainActivity";
    private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";
    public static final String COUNT_PATH = "/count";
    public static final String IMAGE_PATH = "/image";
    public static final String IMAGE_KEY = "photo";
    private static final String COUNT_KEY = "count";
    private static final int MAX_LOG_TAG_LENGTH = 23;
    GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        LOGD(TAG, "onDataChanged: " + dataEvents);
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        if(!mGoogleApiClient.isConnected()) {
            ConnectionResult connectionResult = mGoogleApiClient
                    .blockingConnect(1, TimeUnit.SECONDS);
            if (!connectionResult.isSuccess()) {
                Log.e(TAG, "DataLayerListenerService failed to connect to GoogleApiClient.");
                return;
            }
        }

        // Loop through the events and send a message back to the node that created the data item.
        for (DataEvent event : events) {
            Uri uri = event.getDataItem().getUri();
            String path = uri.getPath();
            if (COUNT_PATH.equals(path)) {
                // Get the node id of the node that created the data item from the host portion of
                // the uri.
                String nodeId = uri.getHost();
                // Set the data of the message to be the bytes of the Uri.
                byte[] payload = uri.toString().getBytes();

                // Send the rpc
                Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, DATA_ITEM_RECEIVED_PATH,
                        payload);
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(START_ACTIVITY_PATH)) {
            String number = "510-364-9907";

            String uri = "tel:" + number.trim() ;
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
        }
        if (messageEvent.getPath().equals("heartrate")) {
//            MainActivity.heartrateStart(this);
//            help();
            MainActivity.heartRate = (int) messageEvent.getData()[0];
        }
        if (messageEvent.getPath().equals("help")) {
            MainActivity.heartrateStart(this);
            help();
        }

        if (messageEvent.getPath().equals("navigateToCar")) {
            MainActivity.navigateToCarWorker(this);
        }
        if (messageEvent.getPath().equals("lockButtonHandler")) {
            MainActivity.lockButtonHandler();
        }
        if (messageEvent.getPath().equals("honk")) {
            MainActivity.hornButtonHandler();
        }
    }


    @Override
    public void onPeerConnected(Node peer) {
        LOGD(TAG, "onPeerConnected: " + peer);
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        LOGD(TAG, "onPeerDisconnected: " + peer);
    }

    public void help(){
        String number = "5103649907";
        Log.d("carlocation","location being checked");
        String texter = generateEmergencyText();
        Log.d("tag",texter);

        SmsManager sm = SmsManager.getDefault();
        ArrayList<String> textString = sm.divideMessage(texter);
        sm.sendMultipartTextMessage(number,null,textString,null,null);
        Toast.makeText(this, "Emergency Contact being texted", Toast.LENGTH_LONG).show();

        String uri = "tel:" + number.trim() ;
        Intent CallIntent = new Intent(Intent.ACTION_CALL);
        CallIntent.setData(Uri.parse(uri));
        CallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(CallIntent);
        Log.d("calling", "called");
    }

    public String generateEmergencyText() {

        HttpClient client = new DefaultHttpClient();

        try {
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            HttpResponse response = client.execute(new HttpGet("http://api.hackthedrive.com/vehicles/" + MainActivity.VIN + "/location/"));
            InputStream a = response.getEntity().getContent();
            String l = CarControl.convertInputStreamToString(a);
            JSONObject object = (JSONObject) new JSONTokener(l).nextValue();
            String emergency = "ALERT: Your friend just hit the 'Help!' button in his Companion app. He may be in trouble.  The " +
                    "police have been notified.  Your friend's last known location is at: http://maps.google.com/maps?daddr=" +
                    object.getString("lat") + "," + object.getString("lat");
            HttpResponse carInfo = client.execute(new HttpGet("http://api.hackthedrive.com/vehicles/" + MainActivity.VIN + "/"));
            a = carInfo.getEntity().getContent();
            l = CarControl.convertInputStreamToString(a);
            object = (JSONObject) new JSONTokener(l).nextValue();
            emergency += " Your friend is driving a " + object.getString("year") + " " + object.get("color") + " " + object.getString("make") + " " + object.getString("model");
            emergency += ": VIN number " + object.get("vin") + ".  " + object.getString("country") + " make.";
            return emergency;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("location failed", e.toString());
            return "";
        }

    }




    public static void LOGD(final String tag, String message) {
        if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message);
        }
    }

}


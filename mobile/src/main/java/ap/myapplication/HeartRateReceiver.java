package ap.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

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

/**
 * Created by Krish on 1/10/2015.
 */
public class HeartRateReceiver extends BroadcastReceiver{

    // Prevents instantiation
    public HeartRateReceiver() {

    }
    // Called when the BroadcastReceiver gets an Intent it's registered to receive

    public void onReceive(Context context, Intent intent) {

        /*
         * Handle Intents here.
         */

        int heartRate = intent.getIntExtra("com.example.android.HeartRate.STATUS", -1);
        Log.d("Receiver", "Fake Rate Received and is " + heartRate);
        if ((heartRate > 9 && heartRate < 31) || heartRate > 116){

                String number = "5103649907";
                Log.d("carlocation","location being checked");
                String texter = generateEmergencyText();
                Log.d("tag",texter);

                SmsManager sm = SmsManager.getDefault();
                ArrayList<String> textString = sm.divideMessage(texter);
                sm.sendMultipartTextMessage(number,null,textString,null,null);
                Toast.makeText(context, "Emergency Contact being texted", Toast.LENGTH_LONG).show();

                String uri = "tel:" + number.trim() ;
                Intent CallIntent = new Intent(Intent.ACTION_CALL);
                CallIntent.setData(Uri.parse(uri));
                CallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(CallIntent);
                Log.d("calling", "called");



        }

        //start activity
        Intent i = new Intent();


    }
    public String generateEmergencyText() {

        HttpClient client = new DefaultHttpClient();

        try {
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            HttpResponse response = client.execute(new HttpGet("http://api.hackthedrive.com/vehicles/" + MainActivity.VIN + "/location/"));
            InputStream a = response.getEntity().getContent();
            String l = CarControl.convertInputStreamToString(a);
            JSONObject object = (JSONObject) new JSONTokener(l).nextValue();
            String emergency = "ALERT: Your friend's smartwatch detected a potentially dangerous heart rate.  The " +
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




}



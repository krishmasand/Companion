package ap.myapplication;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Created by Ankit on 1/10/2015.
 */
public class CarControl extends Activity{
    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
    public static Boolean checkTrunk() {
        Log.d("debug","trunk being checked");
        HttpClient client = new DefaultHttpClient();

        try {
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            HttpResponse response = client.execute(new HttpGet("http://api.hackthedrive.com/vehicles/" + MainActivity.VIN + "/trunk/"));
            InputStream a = response.getEntity().getContent();
            String l = convertInputStreamToString(a);
            JSONObject object = (JSONObject) new JSONTokener(l).nextValue();
            Boolean front = true;
            Boolean rear = true;
            Log.d("trunk checked", object.toString());
            if(object.getString("isFrontOpen") == "true") {
                return false;
            }
            if(object.getString("isRearOpen") == "true") {
                return false;
            }


            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Trunk failed", e.toString());
            return false;
        }
    }
    public static Boolean checkWindow() {
        Log.d("debug","windows being checked");
        HttpClient client = new DefaultHttpClient();

        try {
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            HttpResponse response = client.execute(new HttpGet("http://api.hackthedrive.com/vehicles/" + MainActivity.VIN + "/window/"));
            InputStream a = response.getEntity().getContent();
            String l = convertInputStreamToString(a);
            JSONObject object = (JSONObject) new JSONTokener(l).nextValue();
            Boolean driver = true;
            Boolean passenger = true;
            Log.d("window checked", object.toString());
            if(object.getString("isDriverOpen") == "true") {
                return false;
            }
            if(object.getString("isPassengerOpen") == "true") {
                return false;
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("window failed", e.toString());
            return false;
        }
    }
    public static Boolean checkDoors() {
        Log.d("debug","doors being checked");
        HttpClient client = new DefaultHttpClient();

        try {
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            HttpResponse response = client.execute(new HttpGet("http://api.hackthedrive.com/vehicles/" + MainActivity.VIN + "/door/"));
            InputStream a = response.getEntity().getContent();
            String l = convertInputStreamToString(a);
            JSONObject object = (JSONObject) new JSONTokener(l).nextValue();
            Boolean driver = true;
            Boolean passenger = true;
            Log.d("door checked", object.toString());
            if(object.getString("isDriverFrontOpen") == "true") {
                return false;
            }
            if(object.getString("isDriverRearOpen") == "true") {
                return false;
            }
            if(object.getString("isPassengerFrontOpen") == "true") {
                return false;
            }
            if(object.getString("isPassengerRearOpen") == "true") {
                return false;
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("doors failed", e.toString());
            return false;
        }
    }
    public static int[] fetchLocation() {
        Log.d("debug","location being checked");
        HttpClient client = new DefaultHttpClient();

        try {
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            HttpResponse response = client.execute(new HttpGet("http://api.hackthedrive.com/vehicles/" + MainActivity.VIN + "/location/"));
            InputStream a = response.getEntity().getContent();
            String l = convertInputStreamToString(a);
            JSONObject object = (JSONObject) new JSONTokener(l).nextValue();
            Log.d("door checked", object.toString());
            int[] z = {Integer.parseInt(object.getString("lat")),Integer.parseInt(object.getString("lon"))};
            return z;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("location failed", e.toString());

            return null;
        }
    }
    public static Boolean checkLock() {
            Log.d("debug","lock being checked");
            HttpClient client = new DefaultHttpClient();
            try {
                List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
                HttpResponse response = client.execute(new HttpGet("http://api.hackthedrive.com/vehicles/" + MainActivity.VIN + "/door/"));
                InputStream a = response.getEntity().getContent();
                String l = convertInputStreamToString(a);
                JSONObject object = (JSONObject) new JSONTokener(l).nextValue();
                Log.d("Locks checked", object.toString());
                if(object.getString("isVehicleLocked") == "true") {
                    return true;
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("locks failed", e.toString());
                return false;
            }

    }
    public static String getLocation() throws IOException {
        String line = null;
        line = GET("http://api.hackthedrive.com/vehicles/" + MainActivity.VIN + "/location");
        return line;
    }



    public static String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }
    public static Boolean honkCar() {
        return honkCar(2);
    }

    public static Boolean honkCar(int count) {

        Log.d("trunk", Boolean.toString(CarControl.checkTrunk()));
        Log.d("window", Boolean.toString(CarControl.checkWindow()));
        Log.d("doors", Boolean.toString(CarControl.checkDoors()));
        Log.d("doors", Boolean.toString(CarControl.checkDoors()));

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://api.hackthedrive.com/vehicles/" + MainActivity.VIN + "/horn/");
        try {
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("key", "ankit2015"));

            JSONObject jsonobj = new JSONObject();
            jsonobj.put("key", "ankit2015");
            jsonobj.put("count", count);
            StringEntity se = new StringEntity(jsonobj.toString());
            se.setContentType("application/json;charset=UTF-8");
            post.setEntity(se);

            HttpResponse response = client.execute(post);
            InputStream a = response.getEntity().getContent();
            String l = convertInputStreamToString(a);
            Log.d("honk", l);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("honk failed", e.toString());
            return false;
        }
    }
    public static Boolean toggleHeadlights() {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://api.hackthedrive.com/vehicles/" + MainActivity.VIN + "/lights/");
        try {
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("key", "ankit2015"));

            JSONObject jsonobj = new JSONObject();
            StringEntity se = new StringEntity(jsonobj.toString());
            se.setContentType("application/json;charset=UTF-8");
            post.setEntity(se);

            HttpResponse response = client.execute(post);
            InputStream a = response.getEntity().getContent();
            String l = convertInputStreamToString(a);
            Log.d("lights", l);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("lights failed", e.toString());
            return false;
        }
    }
    public static Boolean lockCar() {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://api.hackthedrive.com/vehicles/" + MainActivity.VIN + "/lock/");
        try {
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("key", "ankit2015"));

            JSONObject jsonobj = new JSONObject();
            jsonobj.put("key", "ankit2015");
            StringEntity se = new StringEntity(jsonobj.toString());
            se.setContentType("application/json;charset=UTF-8");
            post.setEntity(se);

            HttpResponse response = client.execute(post);
            InputStream a = response.getEntity().getContent();
            String l = convertInputStreamToString(a);
            Log.d("lock", l);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("lock failed", e.toString());
            return false;
        }
    }


}

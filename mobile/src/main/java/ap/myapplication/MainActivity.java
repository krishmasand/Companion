package ap.myapplication;





import android.app.Activity;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.StrictMode;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
//import static com.ap.myapplication.ListenerService.LOGD;

import com.google.android.gms.analytics.ExceptionParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;

import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class MainActivity extends ActionBarActivity implements
        DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener, AdapterView.OnItemSelectedListener {
    public static String VIN = "WBY1Z4C55EV273078";
    private GoogleApiClient mGoogleApiClient;
    public byte[] bitches = {1, 2, 3, 4, 5};
    public static int heartRate = -2;
    public static final String START_ACTIVITY_PATH = "/start/MainActivity";
    CarControl carControl1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
        ImageView iv= (ImageView)findViewById(R.id.imageView);
        iv.setImageResource(R.drawable.bmwi3);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.VINs, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        //Register Listener
        spinner.setOnItemSelectedListener(this);



        IntentFilter mHeartRateIntentFilter = new IntentFilter(
                "com.example.android.HeartRate.BROADCAST");
        // Instantiates a new DownloadStateReceiver
        HeartRateReceiver mHeartRateReceiver =
                new HeartRateReceiver();
        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mHeartRateReceiver,
                mHeartRateIntentFilter);
        startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();


    }



    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("hoo", "Connected to Google Api Service");

//        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
    }

    @Override
    protected void onStop() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    public void onConnectionSuspended(int ho) {
        Log.d("hee", "Suspended to Google Api Service");
    }

    public void onConnectionFailed(ConnectionResult ho) {
        Log.d("hzo", "Failed to connect to Google Api Service");
    }


    public void onMessageReceived(MessageEvent messageEvent) {
//        if (messageEvent.getPath().equals(START_ACTIVITY_PATH)) {
//            String number = "510-364-9907";
//
//            String uri = "tel:" + number.trim() ;
//            Intent intent = new Intent(Intent.ACTION_CALL);
//            intent.setData(Uri.parse(uri));
//            startActivity(intent);
//        }

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.d("data changed ho", "DataItem deleted: " + event.getDataItem().getUri());
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.d("data changed hii", "DataItem changed: " + event.getDataItem().getUri());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        VIN = (String) parent.getItemAtPosition(pos);
        Log.d("current VIN", VIN);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    // convert inputstream to String
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }


    public void hello() throws IOException{
        hello(this);
    }

    public static void hello(Context context) throws IOException {
        System.out.println("hi");
        int notificationId = 001;
        String str1 = "hello";
        try {
            str1 = CarControl.getLocation();
        } catch (IOException c) {

        }
        Log.d("herrooo", str1);

// Build intent for notification content
        Intent viewIntent = new Intent(context, MainActivity.class);
        //viewIntent.putExtra(EXTRA_EVENT_ID, eventId);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(context, 0, viewIntent, 0);

        String number = "510-364-9907";

        String uri = "tel:" + number.trim();
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(uri));
        PendingIntent bozo =
                PendingIntent.getActivity(context, 0, intent, 0);


            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(context);
//                            .setSmallIcon(R.drawable.common_signin_btn_icon_disabled_dark)
//                            .setContentTitle("" + heartRate + " + " + str1)
//                            .setContentText(getPublicStations.getData("Fremont"))
//                            .setContentIntent(viewPendingIntent)
//                            .addAction(R.drawable.ic_launcher, "foolianne", bozo);
//        Log.d("chargepoint stuff", getPublicStations.getData("Fremont") );



// Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

// Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }



   public void hello(View view) throws IOException{
            hello();
        }

    public void navigateToCar(View view) {
        navigateToCarWorker();
    }


    public void friends(View view){
        Intent intent = new Intent(this, Friends.class);
        this.startActivity(intent);
    }

    public static void lockButtonHandler() {
        Boolean a = CarControl.lockCar();
        Log.d("lockbuttonstatus", a.toString());
    }

    public void lockButtonHandler(View view) {
        lockButtonHandler();
        Toast.makeText(getApplicationContext(), "Doors Locked", Toast.LENGTH_LONG).show();
    }

    public void hornButtonHandler(View view) {
        CarControl.honkCar();
        Toast.makeText(getApplicationContext(), "Horn activated", Toast.LENGTH_LONG).show();
    }

    public static void hornButtonHandler() {
        CarControl.honkCar();
    }

    public void headlightButtonHandler(View view) {
        CarControl.toggleHeadlights();
        Toast.makeText(getApplicationContext(), "Headlights toggled", Toast.LENGTH_LONG).show();
    }

    public static void heartrateStart(Context context){

                    /*
 * Creates a new Intent to start the HeartRate
 * IntentService. Passes a URI in the
 * Intent's "data" field.
 */
            Intent mServiceIntent = new Intent(context, HeartRate.class);
            mServiceIntent.setData(Uri.parse("uri1"));
            // Starts the IntentService
            context.startService(mServiceIntent);

        }

    public void heartrateStart(){
        heartrateStart(this);

    }

    public void heartrateStart(View view) {
        heartrateStart();
        Toast.makeText(getApplicationContext(), "Heartrate monitoring started", Toast.LENGTH_LONG).show();
    }

    public void chargeHandler(View view) throws IOException{
        int[] loc = CarControl.fetchLocation();
        InputStream a = getPublicStations.getData(loc[0],loc[1]);
//        Log.d("charger",a);
        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(a);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("stationData");

            System.out.println("----------------------------");
            String[] addressStrings = new String[nList.getLength()];
            for (int temp = 0; temp < nList.getLength(); temp++) {

                org.w3c.dom.Node nNode = nList.item(temp);

                System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
//                    Log.d("parserwholethang",eElement.toString());
//                    Log.d("parserid",eElement.getAttribute("stationId"));
//                    Log.d("parseraddress",eElement.getElementsByTagName("Address").item(0).getTextContent());
                    Log.d("address1",eElement.getElementsByTagName("Address").item(0).getTextContent());
                    addressStrings[temp] += eElement.getElementsByTagName("Address").item(0).getTextContent();


//                    System.out.println(eElement.getElementsByTagName("lastname").item(0).getTextContent());
//                    System.out.println(eElement.getElementsByTagName("nickname").item(0).getTextContent());
//                    System.out.println(eElement.getElementsByTagName("salary").item(0).getTextContent());

                }
                Log.d("length", String.valueOf(addressStrings.length));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    public void startListening() {
//        BluetoothService.BluetoothScanner = new BluetoothSCanner();
//        this.getApplicationContext().registerReceiver()
        getApplicationContext().registerReceiver(BluetoothScanner,
                new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));

        getApplicationContext().registerReceiver(BluetoothScanner,
                new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
        getApplicationContext().registerReceiver(LockReceiver, new IntentFilter("lockcar"));
    }
    public final BroadcastReceiver BluetoothScanner = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Broadcaster", intent.toString());
            String trueAction = intent.getAction();
            if(trueAction == BluetoothDevice.ACTION_ACL_CONNECTED || trueAction == BluetoothDevice.ACTION_ACL_DISCONNECTED) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(trueAction)) {
                    Toast.makeText(context, device.getName() + " Device is now connected", Toast.LENGTH_LONG).show();
                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(trueAction)) {
                    String deviceName = device.getName();
                    Toast.makeText(context, deviceName + " Device has disconnected", Toast.LENGTH_LONG).show();
                    if(deviceName.contains("BMW")) {
                        Toast.makeText(context, deviceName + " OMG I FOUND IT", Toast.LENGTH_LONG).show();
                        Log.d("hi","hey");
                        checkStatus();
                    }
//                context.startService(new Intent(context, BluetoothService.class));
                }
            }
        }

    };
    public void navigateToCarWorker(){
        navigateToCarWorker(this);
    }
    public static void navigateToCarWorker(Context context) {
        float lat;
        float lon;
        try {
            String loc = CarControl.getLocation();
            //parser
            int lathelp = loc.indexOf("lat");
            int lonhelp = loc.indexOf("lon");
            lat = Float.parseFloat(loc.substring(lathelp + 5, lonhelp - 2));
            int headinghelp = loc.indexOf("heading");
            lon = Float.parseFloat(loc.substring(lonhelp + 5, headinghelp - 2));
            Log.d("location", Float.toString(lat));
            Log.d("location", Float.toString(lon));

            //set navigation
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://api.hackthedrive.com/vehicles/" + MainActivity.VIN + "/navigation/");
            try {
                List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
                urlParameters.add(new BasicNameValuePair("key", "ankit2015"));

                JSONObject jsonobj = new JSONObject();
                jsonobj.put("label", "Beamer yo");
                jsonobj.put("lat", Float.toString(lat));
                jsonobj.put("lon", Float.toString(lon));

                StringEntity se = new StringEntity(jsonobj.toString());
                se.setContentType("application/json;charset=UTF-8");
                post.setEntity(se);

                HttpResponse response = client.execute(post);
                InputStream a = response.getEntity().getContent();
                String l = convertInputStreamToString(a);
                Log.d("navi", l);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        //Uri.parse("http://maps.google.com/maps?daddr=" + lat + "," + lon));
                        Uri.parse("google.navigation:q="+ lat + "," + lon));
                        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("navi failed", e.toString());
            }
        } catch (Exception e) {
            Log.d("location fetch fail", e.toString());
        }
    }
    public static void navigateToCarWorker(Context context, String VIN) {
        float lat;
        float lon;
        try {
            String loc = CarControl.getLocation();
            //parser
            int lathelp = loc.indexOf("lat");
            int lonhelp = loc.indexOf("lon");
            lat = Float.parseFloat(loc.substring(lathelp + 5, lonhelp - 2));
            int headinghelp = loc.indexOf("heading");
            lon = Float.parseFloat(loc.substring(lonhelp + 5, headinghelp - 2));
            Log.d("location", Float.toString(lat));
            Log.d("location", Float.toString(lon));

            //set navigation
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://api.hackthedrive.com/vehicles/" + VIN + "/navigation/");
            try {
                List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
                urlParameters.add(new BasicNameValuePair("key", "ankit2015"));

                JSONObject jsonobj = new JSONObject();
                jsonobj.put("label", "Beamer yo");
                jsonobj.put("lat", Float.toString(lat));
                jsonobj.put("lon", Float.toString(lon));

                StringEntity se = new StringEntity(jsonobj.toString());
                se.setContentType("application/json;charset=UTF-8");
                post.setEntity(se);

                HttpResponse response = client.execute(post);
                InputStream a = response.getEntity().getContent();
                String l = convertInputStreamToString(a);
                Log.d("navi", l);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        //Uri.parse("http://maps.google.com/maps?daddr=" + lat + "," + lon));
                        Uri.parse("google.navigation:q="+ lat + "," + lon));
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("navi failed", e.toString());
            }
        } catch (Exception e) {
            Log.d("location fetch fail", e.toString());
        }
    }
    public final BroadcastReceiver LockReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("lock", "I Arrived!!!!");
            Log.v("lock",intent.getAction());
        }
    };

    public void statusHandler(View view) {
        Log.d("status","here we are");
        checkStatus();
    }

    public void checkStatus() {
        Log.d("status", "commenced");
        int notificationId = 4;
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Check Car!")
                        .setSmallIcon(R.drawable.common_signin_btn_icon_disabled_dark);
//                    .setContentIntent(viewPendingIntent);
//                    .addAction(R.drawable.ic_launcher, "foolianne", bozo);

// Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        Intent lockCar = new Intent();
// Build the notification and issues it with notification manager.

        String contentText = "";
        if (!CarControl.checkTrunk()) {
            contentText += "check your trunk ";

        }
        if (!CarControl.checkWindow()) {
            contentText += "check your windows ";

        }
        if (!CarControl.checkDoors()) {
            contentText += "check your doors ";

        }
        if (!CarControl.checkLock()) {
            contentText += "check your lock ";
            lockCar.setAction("lockcar");
        }
        if(contentText != "") {
            notificationBuilder.setContentText(contentText);
            
            lockCar.addFlags(99);
            PendingIntent pendingLockCar = PendingIntent.getBroadcast(this, 12345, lockCar, PendingIntent.FLAG_ONE_SHOT);
            notificationBuilder.addAction(17301551, "Lock Car!", pendingLockCar);

            notificationManager.notify(notificationId, notificationBuilder.build());
        }
        else {
            return;
        }
    }
}
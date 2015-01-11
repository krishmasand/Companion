package ap.myapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.ExceptionParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class PowerStations extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        ListView listview = (ListView) findViewById(R.id.listView);
        Friend[] friends = {
                new Friend("WBY1Z4C53EV273080", "Krish"),
                new Friend("WBY1Z4C51EV275894", "Ankit"),
                new Friend("WBY1Z4C58EV275200", "Bob"),
        };
        try {
            PowerStationObj[] a = powerFetcher();
            ArrayAdapter<PowerStationObj> adapter = new ArrayAdapter<PowerStationObj>(this,
                    android.R.layout.simple_list_item_1, a);

            listview.setAdapter(adapter);
        }
        catch(Exception e){
            Log.d("powerfetcher","shit went down yo");
        }


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                String item = ((TextView)view).getText().toString();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        //Uri.parse("http://maps.google.com/maps?daddr=" + lat + "," + lon));
                        Uri.parse("http://maps.google.co.in/maps?q=" + item));
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                getBaseContext().startActivity(intent);

                Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
                MainActivity.navigateToCarWorker(getBaseContext(), item.substring(item.indexOf("-") + 2));

            }
        });
    }
    public PowerStationObj[] powerFetcher() throws IOException{

        float[] loc = CarControl.fetchLocation();
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
            PowerStationObj[] powerList = new PowerStationObj[nList.getLength()];
            System.out.println("----------------------------");
            String addressStrings = new String();
            float[] location = new float[2];
            for (int temp = 0; temp < nList.getLength(); temp++) {

                org.w3c.dom.Node nNode = nList.item(temp);

                System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
//                    Log.d("parserwholethang",eElement.toString());
//                    Log.d("parserid",eElement.getAttribute("stationId"));
//                    Log.d("parseraddress",eElement.getElementsByTagName("Address").item(0).getTextContent());
                    Log.d("address1", eElement.getElementsByTagName("Address").item(0).getTextContent());
//                    Log.d("id", eElement.getElementsByTagName("stationId").item(0).getTextContent());
                    addressStrings = eElement.getElementsByTagName("Address").item(0).getTextContent();
//                    idStrings[temp] += eElement.getElementsByTagName("stationId").item(0).getTextContent();
                    String l = eElement.getElementsByTagName("Geo").item(0).getTextContent();
                    Log.d("geotag",l);
                    location = parseLoc(l);
                    Log.d("geotagconvert",Float.toString(location[0]));
                    Log.d("geotagconvert",Float.toString(location[1]));
                    powerList[temp] = new PowerStationObj(addressStrings,location);
                }

            }
            return powerList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    public static float[] parseLoc(String locString) {
        int a = locString.indexOf("-");
        float[] d = {Float.parseFloat(locString.substring(0,a)),Float.parseFloat(locString.substring(a+1))};
        return d;
    }


}

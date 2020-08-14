package com.myapplicationdev.android.c302_p12_ps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.*;

public class MainActivity extends AppCompatActivity {

    private ListView lv;
    ArrayList<Incident> alIncidents = new ArrayList<Incident>();
    ArrayAdapter aa;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.listView);

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("AccountKey","omAxW/OMRCO1U0WbyG02EQ==");
        client.addHeader("accept","application/json");
        client.get("http://datamall2.mytransport.sg/ltaodataservice/TrafficIncidents", new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                try{
                    JSONArray value = (JSONArray)response.get("value");
                    for(int i = 0; i<value.length();i++){
                        JSONObject incident = (JSONObject)value.get(i);
                        String msg = incident.getString("Message");
                        String ans = msg.substring(msg.indexOf("(")+1,msg.indexOf(")"));
                        Log.w("Word",ans);
                        DateFormat format = new SimpleDateFormat("d/M", Locale.ENGLISH);
                        Date date = format.parse(ans);
                        Log.w("Date", String.valueOf(date));
                        Incident ii = new Incident(incident.getString("Type"),incident.getDouble("Latitude"),incident.getDouble("Longitude"),incident.getString("Message"),date);
                        alIncidents.add(ii);

                    }
                }catch (JSONException | ParseException e){

                }
                aa.notifyDataSetChanged();
            }
        });
        aa = new incidentAdapter(this, alIncidents);
        lv.setAdapter(aa);
        Log.w("Count", String.valueOf(aa.getCount()));

        db = FirebaseFirestore.getInstance();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Incident incident = alIncidents.get(position);
                final String type = incident.getType();
                final Double latitude = incident.getLatitude();
                final Double longitude = incident.getLongitude();
                final String msg = incident.getMessage();
                final Date date = incident.getDate();



            }
        });







    }
    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.optione, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();

        if(id == R.id.ViewAllMarkers){
            return true;
        }else if(id==R.id.Upload){

            for(int i =0; i< aa.getCount();i++){

                Incident incident = alIncidents.get(i);
                final String type = incident.getType();
                final Double latitude = incident.getLatitude();
                final Double longitude = incident.getLongitude();
                final String msg = incident.getMessage();
                final Date date = incident.getDate();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Upload to Firestore");
                builder.setMessage("Proceed to upload to Firestore?");
                builder.setIcon(R.drawable.ic_cloud_upload_24dp);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("type", type);
                        data.put("latitude",latitude);
                        data.put("longitude",longitude);
                        data.put("message",msg);
                        data.put("date",date);
                        db.collection("incidents")
                                .add(data)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("TAG","Error adding document",e);
                                    }
                                });

                    }
                });
                builder.setNegativeButton("CANCEL",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }


            return true;
        }else{
            Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }



}

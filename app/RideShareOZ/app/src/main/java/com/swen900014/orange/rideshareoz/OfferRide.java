package com.swen900014.orange.rideshareoz;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import static com.swen900014.orange.rideshareoz.Resources.*;


/**
 * Created by Qianwen Zhang on 9/12/15.
 * The view activity where users are able to
 * offer a ride as a driver
 */
public class OfferRide extends FragmentActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {
    private final String TAG = "OfferRide";
    private String temp1 = "";
    private String EditStartTime = "";
    private String EditEndTime = "";
    private EditText SpinSN;
    private TextView textSN;
    private TextView textStartTime;
    private CheckBox Check1, Check2;

    private EditText startCityEdit;
    private EditText startSuburbEdit;
    private EditText startStreetEdit;

    private EditText endCityEdit;
    private EditText endSuburbEdit;
    private EditText endStreetEdit;

    private Button btnSubmit, btnDate, btnStartTime, btnArrivalTime;

    private String latS = "";
    private String lonS = "";
    private String latE = "";
    private String lonE = "";

    private String startAddress = "";
    private String endAddress = "";

    private PlaceAutoCompleteAdapter adapter;
    private boolean isFind = false;
    protected GoogleApiClient mGoogleApiClient;

    private static final LatLngBounds BOUNDS_GREATER_Melbourne = new LatLngBounds(
            new LatLng(-38.260720, 144.394492), new LatLng(-37.459846, 145.764740));

    Calendar calendar = Calendar.getInstance();
    private TextView displayDate, displayStartTime, displayArrivalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offerride);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();


        btnSubmit = (Button) findViewById(R.id.button1);
        btnDate = (Button) findViewById(R.id.button8);
        btnStartTime = (Button) findViewById(R.id.button3);
        btnArrivalTime = (Button) findViewById(R.id.button5);
        // btnReset = (Button) findViewById(R.id.button2);


        displayDate = (TextView) findViewById(R.id.displayDate);
        displayStartTime = (TextView) findViewById(R.id.displayStartTime);
        displayArrivalTime = (TextView) findViewById(R.id.displayArrivalTime);


        SpinSN = (EditText) findViewById(R.id.SeatNo);
        textSN = (TextView) findViewById(R.id.txtSeatNo);
        textStartTime = (TextView) findViewById(R.id.startTimeText);
        Check1 = (CheckBox) findViewById(R.id.current1);
        Check2 = (CheckBox) findViewById(R.id.current2);

        startCityEdit = (EditText) findViewById(R.id.srartCityEdit);
        startSuburbEdit = (EditText) findViewById(R.id.startSuberbEdit);
        startStreetEdit = (EditText) findViewById(R.id.startStreetEdit);

        endCityEdit = (EditText) findViewById(R.id.endCityEdit);
        endSuburbEdit = (EditText) findViewById(R.id.endSuberbEdit);
        endStreetEdit = (EditText) findViewById(R.id.endStreetEdit);

       /* check if it offer or find  */
        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra("type")){
            String type = intent.getStringExtra("type");
            if(type.equals("find")){
                SpinSN.setVisibility(View.INVISIBLE);
                textSN.setVisibility(View.INVISIBLE);
                btnStartTime.setVisibility(View.INVISIBLE);
                isFind = true;
            }
        }

        adapter = new PlaceAutoCompleteAdapter(this,
                android.R.layout.simple_expandable_list_item_1, mGoogleApiClient,
                BOUNDS_GREATER_Melbourne, null);
        AutoCompleteTextView EditStart = (AutoCompleteTextView)
                findViewById(R.id.Start);
        EditStart.setOnItemClickListener(mAutoCompleteClickListener);
        EditStart.setAdapter(adapter);
        AutoCompleteTextView EditEnd = (AutoCompleteTextView)
                findViewById(R.id.End);
        EditEnd.setOnItemClickListener(mAutoCompleteClickListener);
        EditEnd.setAdapter(adapter);


        //btnReset.setOnClickListener(new resetOnClickListener());
        btnSubmit.setOnClickListener(this);
        btnDate.setOnClickListener(this);
        btnStartTime.setOnClickListener(this);
        btnArrivalTime.setOnClickListener(this);
        getIntent();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button1) {
            offerRide(v);
            /*if(isFind){
                Intent searchResultsIntent = new Intent(this, MyRidesActivity.class);
                searchResultsIntent.putExtra("type","find");
                //TODO: Fill extras with the search parameters
                searchResultsIntent.putExtra("startLon", lonS);
                searchResultsIntent.putExtra("startLat", latS);
                searchResultsIntent.putExtra("endLon", lonE);
                searchResultsIntent.putExtra("endLat", latE);
                //searchResultsIntent.putExtra("date", "xxx");
                searchResultsIntent.putExtra("arrivalTime", EditEndTime.getText().toString());


                startActivity(searchResultsIntent);


            }
            else
            {

            }
            Intent intent=new Intent(OfferRide.this, MainActivity.class);
                     check if it offer or find
            // Intent intent = this.getIntent();
            if (intent != null && intent.hasExtra("type"))
            {
                String type = intent.getStringExtra("type");
                if(type.equals("find")){
                    SpinSN.setVisibility(View.INVISIBLE);
                    textSN.setVisibility(View.INVISIBLE);

                    isFind = true;
                }
            }
            startActivity(intent);
            */
        }
        if (v.getId() == R.id.button8)
            setDate(v);
         //   new DatePickerDialog(OfferRide.this, listener1, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH)).show();
        if (v.getId() == R.id.button3) setStartTime(v);
          //  new TimePickerDialog(OfferRide.this, listener2, calendar.get(calendar.HOUR_OF_DAY), calendar.get(calendar.MINUTE), true).show();
        if (v.getId() == R.id.button5) setArrivalTime(v);
          //  new TimePickerDialog(OfferRide.this, listener4, calendar.get(calendar.HOUR_OF_DAY), calendar.get(calendar.MINUTE), true).show();

    }




    private AdapterView.OnItemClickListener mAutoCompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceAutoCompleteAdapter.PlaceAutoComplete place = adapter.getItem(position);
            final String placeId = place.placeId;

            Log.i(TAG, "Autocomplete place selected: " + place.description);

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.
                    getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceCallback);

            Toast.makeText(getApplicationContext(), "clicked: " + place.description,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + place.placeId);
        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error : " + places.getStatus().toString());
                places.release();
                return;
            }

            final Place place = places.get(0);

            Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };

    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    public void sendRequest(final Activity activity) {
        final String url = "https://maps.googleapis.com/maps/api/geocode/json?" +
                "address=" + startAddress + ",+Australia&" +
                "key=AIzaSyBhEI1X-PMslBS2Ggq35bOncxT05mWO9bs";

        final StringRequest getStartLocRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            System.out.println(jsonResponse.toString());

                            latS = jsonResponse.getJSONArray("results").getJSONObject(0).
                                    getJSONObject("geometry").getJSONObject("location").
                                    getString("lat");
                            lonS = jsonResponse.getJSONArray("results").getJSONObject(0).
                                    getJSONObject("geometry").getJSONObject("location").
                                    getString("lng");

                            // Check response whether it's accurate, if not remind user

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        getEndpointLoc(activity);
                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        System.out.println("it doesn't work");
                    }
                });

        MyRequest.getInstance(activity).addToRequestQueue(getStartLocRequest);
    }

    private void getEndpointLoc(final Activity activity) {
        final String url = "https://maps.googleapis.com/maps/api/geocode/json?" +
                "address=" + endAddress + ",+Australia&" +
                "key=AIzaSyBhEI1X-PMslBS2Ggq35bOncxT05mWO9bs";

        final StringRequest getEndLocRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            System.out.println(jsonResponse.toString());

                            latE = jsonResponse.getJSONArray("results").getJSONObject(0).
                                    getJSONObject("geometry").getJSONObject("location").
                                    getString("lat");
                            lonE = jsonResponse.getJSONArray("results").getJSONObject(0).
                                    getJSONObject("geometry").getJSONObject("location").
                                    getString("lng");

                            // Check response whether it's accurate, if not remind user

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        /* check if it offer or find  */
                        if ( !isFind){
                            sendRideInfo(activity);
                        }else{
                            Intent searchResultsIntent=new Intent(OfferRide.this, MyRidesActivity.class);
                            searchResultsIntent.putExtra("type","find");
                            searchResultsIntent.putExtra("s_lon",lonS);
                            searchResultsIntent.putExtra("s_lat",latS);
                            searchResultsIntent.putExtra("groupId","55cab5dde81ab31606e4814c");
                            searchResultsIntent.putExtra("e_lon",lonE);
                            searchResultsIntent.putExtra("e_lat",latE);
                            searchResultsIntent.putExtra("arrival_time",EditEndTime.toString());
                            startActivity(searchResultsIntent);
                        }


                        // check response, whether it received
                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        System.out.println("it doesn't work");
                    }
                });

        MyRequest.getInstance(activity).addToRequestQueue(getEndLocRequest);
    }

    private void sendRideInfo(Activity activity) {
        StringRequest OfferRequest = new StringRequest(Request.Method.POST,
                OFFER_RIDE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                System.out.println("response: " + s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();

                System.out.println("Sending post failed!");
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                startAddress = startCityEdit.getText().toString() + "\n" +
                        startSuburbEdit.getText().toString() + "\n" +
                        startStreetEdit.getText().toString();

                endAddress = endCityEdit.getText().toString() + "\n" +
                        endSuburbEdit.getText().toString() + "\n" +
                        endStreetEdit.getText().toString();


                if (Check1.isChecked()) {
                    params.put("s_lat", "111");
                    params.put("s_lon", "111");
                    params.put("start_add", "mine");
                } else {
                    params.put("s_lat", latS);
                    params.put("s_lon", lonS);
                    params.put("start_add", startAddress);
                }

                if (Check2.isChecked()) {
                    params.put("e_lat", "222");
                    params.put("e_lon", "222");
                    params.put("destination", "mine");
                } else {
                    params.put("e_lat", latE);
                    params.put("e_lon", lonE);
                    params.put("destination", endAddress);
                }

                params.put("groupid", "55cab5dde81ab31606e4814c");
                params.put("seat", SpinSN.getText().toString());
                params.put("start_time", EditStartTime.toString() );
                params.put("arrival_time", EditEndTime.toString());
                //params.put("start_time","2222"  );
               // params.put("arrival_time", "2015-09-24T12:04:29.853Z");
                params.put("username", "qianz7@student.unimelb.edu.au");


                return params;
            }

        };

        MyRequest.getInstance(activity).addToRequestQueue(OfferRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void offerRide(View view) {

       if (inputValid())
        {
        startAddress = startCityEdit.getText().toString() + "+" +
                startSuburbEdit.getText().toString() + "+" +
                startStreetEdit.getText().toString();

        endAddress = endCityEdit.getText().toString() + "+" +
                endSuburbEdit.getText().toString() + "+" +
                endStreetEdit.getText().toString();

        startAddress = startAddress.replaceAll(" ", "+");
        endAddress = endAddress.replaceAll(" ", "+");

        sendRequest(this);
        }
         else
        {
            System.out.println("Invalid input in offerRide");
         }




    }
    DatePickerDialog.OnDateSetListener listener1 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String month=String.valueOf(monthOfYear + 1);
            String day=String.valueOf(dayOfMonth );
            if(monthOfYear<10)
                month="0"+String.valueOf(monthOfYear + 1);
            if(dayOfMonth<10)
                day="0"+String.valueOf(dayOfMonth );

            displayDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            temp1 = String.valueOf(year) + "-" +  month + "-" +day + "T";
        }
    };
    TimePickerDialog.OnTimeSetListener listener2 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String hour=String.valueOf(hourOfDay);
            String min=String.valueOf(minute );
            if(hourOfDay<10)
                hour="0"+String.valueOf(hourOfDay);
            if(minute<10)
                min="0"+String.valueOf(minute );
            displayStartTime.setText(hourOfDay + ":" + minute);
            EditStartTime = temp1.toString() + hour+ ":" + min+ ":00.000Z";
        }
    };


    TimePickerDialog.OnTimeSetListener listener4 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String hour=String.valueOf(hourOfDay);
            String min=String.valueOf(minute );
            if(hourOfDay<10)
                hour="0"+String.valueOf(hourOfDay);
            if(minute<10)
                min="0"+String.valueOf(minute );
            displayArrivalTime.setText(hourOfDay + ":" + minute);
            EditEndTime =  temp1.toString() + hour+ ":" + min+ ":00.000Z";
        }
    };







    public void setDate(View view) {
        new DatePickerDialog(OfferRide.this, listener1, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

    }
    public void setStartTime(View view) {
        new TimePickerDialog(OfferRide.this, listener2, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        // EditStartTime = temp1.toString() + String.valueOf(calendar.get(calendar.HOUR_OF_DAY) )+ ":" + String.valueOf(calendar.get(calendar.MINUTE)) + ":00.000Z";
    }
    public void setArrivalTime(View view) {
        new TimePickerDialog(OfferRide.this, listener4, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        // EditEndTime = temp1.toString() +String.valueOf( calendar.get(calendar.HOUR_OF_DAY) )+ ":" + String.valueOf(calendar.get(calendar.MINUTE)) + ":00.000Z";
    }


    //Check whether all information needed for offering a ride
    // has been typed in by user
    public boolean inputValid()
    {
        /*return !(SpinSN.getText().toString().isEmpty() ||
                EditEndTime.toString().isEmpty() ||
                EditStartTime.toString().isEmpty() ||
                startCityEdit.getText().toString().isEmpty() ||
                startSuburbEdit.getText().toString().isEmpty() ||
                startStreetEdit.getText().toString().isEmpty() ||
                endCityEdit.getText().toString().isEmpty() ||
                endSuburbEdit.getText().toString().isEmpty() ||
                endStreetEdit.getText().toString().isEmpty());
        //EditStart.getText().toString().isEmpty() ||u
        //EditEnd.getText().toString().isEmpty());*/
        return true;
    }
}

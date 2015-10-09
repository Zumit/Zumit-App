package com.swen900014.orange.rideshareoz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.swen900014.orange.rideshareoz.Resources.*;
import  com.swen900014.orange.rideshareoz.GPSTracker;

/**
 * Created by Qianwen Zhang on 9/12/15.
 * The view activity where users are able to
 * offer a ride as a driver
 */
public class OfferRide extends FragmentActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener
{
    String[] EVENTS = {"BBQ","OCEAN ROAD","3D PRINTING"};
    String[] GROUPS = {"UNIMELB","UNIMONASH","RMIT"};
    private final String TAG = "OfferRide";
    private String event = "";
    private String group = "";
    private String temp1 = "",SeatNo="1";
    private String EditStartTime = "";
    private String EditEndTime = "";

    String hours="";
    String mins="";
    String houra="";
    String mina="";

    private TextView textSN;
    private TextView textStartTime;
    private CheckBox Check1, Check2;

    private AutoCompleteTextView EditStart;
    private AutoCompleteTextView EditEnd;

    private Spinner spinner;

    private ArrayAdapter<CharSequence> spinnerAdapter;
    private Button btnSubmit,btnReset ,btnDate, btnStartTime, btnArrivalTime,btnSelectEvent, btnSelectGroup;

    private String latS = "";
    private String lonS = "";
    private String latE = "";
    private String lonE = "";
//current GPS location
    private double  latC = 0;
    private double  lonC = 0;
    private String currentAddress = "";
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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offerride);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
        btnSubmit = (Button) findViewById(R.id.button1);
        btnReset= (Button) findViewById(R.id.button2);
        btnDate = (Button) findViewById(R.id.setDateButton);
        btnStartTime = (Button) findViewById(R.id.setStartTimeButton);
        btnArrivalTime = (Button) findViewById(R.id.setEndTimeButton);
        btnSelectEvent = (Button) findViewById(R.id.buttonEvent);
        btnSelectGroup= (Button) findViewById(R.id.buttonGroup);

        spinner = (Spinner)findViewById(R.id.spinner);
        displayDate = (TextView) findViewById(R.id.displayDate);
        displayStartTime = (TextView) findViewById(R.id.displayStartTime);
        displayArrivalTime = (TextView) findViewById(R.id.displayArrivalTime);

        textSN = (TextView) findViewById(R.id.txtSeatNo);
        textStartTime = (TextView) findViewById(R.id.startTimeText);
        Check1 = (CheckBox) findViewById(R.id.current1);
        Check2 = (CheckBox) findViewById(R.id.current2);
       //gps
         GPSTracker gps = new GPSTracker(this);

        // check if GPS enabled
         if(gps.canGetLocation()){
            latC = gps.getLatitude();
            lonC = gps.getLongitude();

        }else{
        // can't get location
        // GPS or Network is not enabled
        // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }


       /* check if it offer or find  */
        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra("type"))
        {
            String type = intent.getStringExtra("type");
            if (type.equals("find"))
            {
               // SpinSN.setVisibility(View.INVISIBLE);
                textSN.setVisibility(View.INVISIBLE);
                btnStartTime.setVisibility(View.INVISIBLE);
                isFind = true;
            }
        }


        EditStart = (AutoCompleteTextView)
                findViewById(R.id.Start);


        EditEnd = (AutoCompleteTextView)
                findViewById(R.id.End);



        //auto-complete adapter
        PlaceAutoCompleteAdapter adapterS = new PlaceAutoCompleteAdapter(this,
                android.R.layout.simple_expandable_list_item_1, mGoogleApiClient,
                BOUNDS_GREATER_MELBOURNE, null, EditStart);
        EditStart.setAdapter(adapterS);

        PlaceAutoCompleteAdapter adapterE = new PlaceAutoCompleteAdapter(this,
                android.R.layout.simple_expandable_list_item_1, mGoogleApiClient,
                BOUNDS_GREATER_MELBOURNE, null, EditEnd);
        EditEnd.setAdapter(adapterE);


        //spinner adapter
        spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.seats,android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SeatNo = String.valueOf(position + 1);
                if (position > 0)
                    Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " selected", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });

        btnSelectEvent.setOnClickListener(this);
        btnSelectGroup.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnDate.setOnClickListener(this);
        btnStartTime.setOnClickListener(this);
        btnArrivalTime.setOnClickListener(this);

        getIntent();
    }





    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.button2)
        {
            EditStart.setText("");
            EditEnd.setText("");
            displayDate.setText("");
            displayStartTime.setText("");
            displayArrivalTime.setText("");

        }
        if (v.getId() == R.id.buttonEvent)
        {
            selectEvent(v);
        }
        if (v.getId() == R.id.buttonGroup)
        {
            selectGroup(v);
        }
        if (v.getId() == R.id.button1)
        {
            offerRide(v);
        }
        if (v.getId() == R.id.setDateButton)
        {
            setDate(v);
        }
        //   new DatePickerDialog(OfferRide.this, listener1, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH)).show();
        if (v.getId() == R.id.setStartTimeButton)
        {
            setStartTime(v);
        }
        //  new TimePickerDialog(OfferRide.this, listener2, calendar.get(calendar.HOUR_OF_DAY), calendar.get(calendar.MINUTE), true).show();
        if (v.getId() == R.id.setEndTimeButton)
        {
            setArrivalTime(v);
        }
        //  new TimePickerDialog(OfferRide.this, listener4, calendar.get(calendar.HOUR_OF_DAY), calendar.get(calendar.MINUTE), true).show();
    }

    private void selectEvent(View v) {

        //receive a list of event

        AlertDialog.Builder builder = new AlertDialog.Builder(OfferRide.this);
        builder.setTitle("Select Event");
        builder.setItems(EVENTS, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(getApplicationContext(), "You have selected" + EVENTS[which], Toast.LENGTH_SHORT).show();
                event =  EVENTS[which];
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void selectGroup(View v) {

        //receive a list of group

        AlertDialog.Builder builder = new AlertDialog.Builder(OfferRide.this);
        builder.setTitle("Select Group");
        builder.setItems(GROUPS, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(getApplicationContext(), "You have selected" + GROUPS[which], Toast.LENGTH_SHORT).show();
                group = GROUPS[which];
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    //reverse Address
    public void reverseAddress(final Activity activity) {
        final String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="
                + latC + "," + lonC + "&key=AIzaSyBhEI1X-PMslBS2Ggq35bOncxT05mWO9bs";
        final StringRequest getCurrentAddressRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonResponse = new JSONObject(response);
                            System.out.println(jsonResponse.toString());

                            currentAddress = jsonResponse.getJSONArray("results").getJSONObject(0)
                                    .getString("formatted_address");



                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener()
                {
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        volleyError.printStackTrace();
                        System.out.println("it doesn't work");
                    }
                });

        MyRequest.getInstance(activity).addToRequestQueue(getCurrentAddressRequest);
    }
    public void sendRequest(final Activity activity)
    {
        String startAddressToGoogle = startAddress.replaceAll(" ", "+");

        final String url = "https://maps.googleapis.com/maps/api/geocode/json?" +
                "address=" + startAddressToGoogle + ",+Australia&" +
                "key=AIzaSyBhEI1X-PMslBS2Ggq35bOncxT05mWO9bs";

        final StringRequest getStartLocRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonResponse = new JSONObject(response);
                            System.out.println(jsonResponse.toString());

                            latS = jsonResponse.getJSONArray("results").getJSONObject(0).
                                    getJSONObject("geometry").getJSONObject("location").
                                    getString("lat");
                            lonS = jsonResponse.getJSONArray("results").getJSONObject(0).
                                    getJSONObject("geometry").getJSONObject("location").
                                    getString("lng");

                            // Check response whether it's accurate, if not remind user

                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        getEndpointLoc(activity);
                    }
                },
                new Response.ErrorListener()
                {
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        volleyError.printStackTrace();
                        System.out.println("it doesn't work");
                    }
                });

        MyRequest.getInstance(activity).addToRequestQueue(getStartLocRequest);
    }

    private void getEndpointLoc(final Activity activity)
    {
        String endAddressToGoogle = endAddress.replaceAll(" ", "+");

        final String url = "https://maps.googleapis.com/maps/api/geocode/json?" +
                "address=" + endAddressToGoogle + ",+Australia&" +
                "key=AIzaSyBhEI1X-PMslBS2Ggq35bOncxT05mWO9bs";

        final StringRequest getEndLocRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonResponse = new JSONObject(response);
                            System.out.println(jsonResponse.toString());

                            latE = jsonResponse.getJSONArray("results").getJSONObject(0).
                                    getJSONObject("geometry").getJSONObject("location").
                                    getString("lat");
                            lonE = jsonResponse.getJSONArray("results").getJSONObject(0).
                                    getJSONObject("geometry").getJSONObject("location").
                                    getString("lng");

                            // Check response whether it's accurate, if not remind user

                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        /* check if it offer or find  */
                        if (!isFind)
                        {
                            sendRideInfo(activity);
                        }
                        else
                        {
                            Intent searchResultsIntent = new Intent(OfferRide.this, MyRidesActivity.class);
                            searchResultsIntent.putExtra("type", "find");
                            searchResultsIntent.putExtra("s_lon", lonS);
                            searchResultsIntent.putExtra("s_lat", latS);
                            searchResultsIntent.putExtra("group_id", "55cab5dde81ab31606e4814c");
                            searchResultsIntent.putExtra("e_lon", lonE);
                            searchResultsIntent.putExtra("e_lat", latE);
                            searchResultsIntent.putExtra("arrival_time", EditEndTime);
                            startActivity(searchResultsIntent);
                        }

                        // check response, whether it received
                    }
                },
                new Response.ErrorListener()
                {
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        volleyError.printStackTrace();
                        System.out.println("it doesn't work");
                    }
                });

        MyRequest.getInstance(activity).addToRequestQueue(getEndLocRequest);
    }

    private void sendRideInfo(final Activity activity)
    {
        StringRequest OfferRequest = new StringRequest(Request.Method.POST,
                OFFER_RIDE_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                System.out.println("response: " + s);

                activity.finish();
                //Intent intent = new Intent(activity, MyRidesActivity.class);
                //activity.startActivity(intent);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                volleyError.printStackTrace();

                System.out.println("Sending post failed!");
            }
        }) {
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();



                if (Check1.isChecked())
                {
                   params.put("s_lat", Double.toString(latC));
                   params.put("s_lon",Double.toString(lonC) );
                   params.put("start_add", currentAddress);
                }
                else
                {
                    params.put("s_lat", latS);
                    params.put("s_lon", lonS);
                    params.put("start_add", startAddress);
                }

                if (Check2.isChecked())
                {
                    params.put("e_lat", Double.toString(latC));
                    params.put("e_lon", Double.toString(lonC) );
                    params.put("destination", currentAddress);
                }
                else
                {
                    params.put("e_lat", latE);
                    params.put("e_lon", lonE);
                    params.put("destination", endAddress);
                }

                params.put("group_id", "55cab5dde81ab31606e4814c");
                params.put("seat", SeatNo.toString());
                params.put("start_time", EditStartTime);
                params.put("arrival_time", EditEndTime);
                params.put("username", User.getCurrentUser().getUsername());

                return params;
            }
        };

        MyRequest.getInstance(activity).addToRequestQueue(OfferRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void offerRide(View view)
    {
        if(Check1.isChecked()||Check2.isChecked())
        {
            reverseAddress(this);
        }
        if (inputValid())
        {
            startAddress = EditStart.getText().toString();
            endAddress = EditEnd.getText().toString();

            sendRequest(this);
        }
        else
        {
            System.out.println("Invalid input in offerRide");
        }
    }

    DatePickerDialog.OnDateSetListener listener1 = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            String month;
            String day;

            if (monthOfYear < 9)
            {
                month = "0" + String.valueOf(monthOfYear + 1);
            }
            else
            {
                month = String.valueOf(monthOfYear + 1);
            }

            if (dayOfMonth < 10)
            {
                day = "0" + String.valueOf(dayOfMonth);
            }
            else
            {
                day = String.valueOf(dayOfMonth);
            }

            displayDate.setText(dayOfMonth + "-" + month + "-" + year);
            temp1 = String.valueOf(year) + "-" + month + "-" + day + "T";
        }
    };


    TimePickerDialog.OnTimeSetListener listener2 = new TimePickerDialog.OnTimeSetListener()
    {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {


            if (hourOfDay < 10)
            {
                hours = "0" + String.valueOf(hourOfDay);
            }
            else
            {
                hours = String.valueOf(hourOfDay);
            }
            if (minute < 10)
            {
                mins = "0" + String.valueOf(minute);
            }
            else
            {
                mins = String.valueOf(minute);
            }

            displayStartTime.setText(hourOfDay + ":" + minute);
            EditStartTime = temp1 + hours+ ":" + mins + ":00.000Z";
        }
    };


    TimePickerDialog.OnTimeSetListener listener4 = new TimePickerDialog.OnTimeSetListener()
    {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {


            if (hourOfDay < 10)
            {
                houra = "0" + String.valueOf(hourOfDay);
            }
            else
            {
                houra = String.valueOf(hourOfDay);
            }

            if (minute < 10)
            {
                mina = "0" + String.valueOf(minute);
            }
            else
            {
                mina = String.valueOf(minute);
            }

            displayArrivalTime.setText(hourOfDay + ":" + minute);
            EditEndTime = temp1 + houra + ":" + mina + ":00.000Z";
            checkTime(hours, mins,houra, mina);
        }
    };

    public void checkTime(String hours,String mins,String houra,String mina){
        if(hours.compareTo(houra)==0)
        {
            if(mins.compareTo(mina)>=0)
            {
                Toast.makeText(getApplicationContext(),"Arrival time must be later than start time!",Toast.LENGTH_SHORT).show();
                btnSubmit.setEnabled(false);}
            else btnSubmit.setEnabled(true);
        }
        else
            if (hours.compareTo(houra)>0)
            {
                Toast.makeText(getApplicationContext(),"Arrival time must be later than start time!",Toast.LENGTH_SHORT).show();
                btnSubmit.setEnabled(false);}
            else btnSubmit.setEnabled(true);
    }

    public void setDate(View view)
    {
        new DatePickerDialog(OfferRide.this, listener1, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    public void setStartTime(View view)
    {
        new TimePickerDialog(OfferRide.this, listener2, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true).show();
    }

    public void setArrivalTime(View view)
    {
        new TimePickerDialog(OfferRide.this, listener4, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true).show();
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

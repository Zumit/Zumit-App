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
import android.widget.DatePicker;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.swen900014.orange.rideshareoz.Resources.BOUNDS_GREATER_MELBOURNE;
import static com.swen900014.orange.rideshareoz.Resources.OFFER_RIDE_URL;

/**
 * Created by Qianwen Zhang on 9/12/15.
 * The view activity where users are able to
 * offer a ride as a driver
 */
public class OfferRide extends FragmentActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener
{



    ArrayList<Group> selectGroups = null;
    ArrayList<Event> selectEvents = null;


    private final String TAG = "OfferRide";
    private String eventId = "";
    private String groupId = "";
    private String temp1 = "",SeatNo="1";
    private String EditStartTime = "";
    private String EditEndTime = "";

    private String hours="";
    private String mins="";
    private String houra="";
    private String mina="";

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

    private boolean isFind = false;
    private boolean isGroup = false;
    private boolean isEvent = false;
    private boolean isToEvent = false;
    private boolean isFromEvent = false;
    private String eventLocation;
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

        Check1 = (CheckBox) findViewById(R.id.current1);
        Check2 = (CheckBox) findViewById(R.id.current2);
       //gps
        /*
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

*/
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
            public void onNothingSelected(AdapterView<?> parent)
            {

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
    public void onRestart(){
        super.onRestart();
        Group.loadGroups(this);
        Event.loadEvents(this);
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
            eventId = "";
            groupId = "";
            btnSelectEvent.setEnabled(true);
            btnSelectGroup.setEnabled(true);
            EditStart.setHint("");
            EditEnd.setHint("");
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
        if (v.getId() == R.id.setStartTimeButton)
        {
            setStartTime(v);
        }
        if (v.getId() == R.id.setEndTimeButton)
        {
            setArrivalTime(v);
        }
    }

    private void selectGroup(View v)
    {

        //receive a list of group
        selectGroups = Group.getMyGroups();
        final String[] groupsArray = new String[selectGroups.size()];
        for (int i = 0; i < selectGroups.size(); i++)
        {
            groupsArray[i] = selectGroups.get(i).getName();
        }


        AlertDialog.Builder builder1 = new AlertDialog.Builder(OfferRide.this);
        builder1.setTitle("Select Group");
        builder1.setItems(groupsArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                btnSelectEvent.setEnabled(false);
                isGroup = true;
                //Toast.makeText(getApplicationContext(), "You have selected" + groupsArray[position], Toast.LENGTH_SHORT).show();
                groupId = selectGroups.get(position).getGroupId();
                Toast.makeText(getApplicationContext(), "You have selected" + groupId, Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = builder1.create();
        alertDialog.show();

    }


     private void selectEvent(View v) {

        /* receive a list of event */

        selectEvents = Event.getAllEvents();
        final String[] eventsArray = new String[selectEvents.size()];
        for(int i=0; i<selectEvents.size(); i++)
        {
            eventsArray[i] = selectEvents.get(i).getName();
        }

        AlertDialog.Builder builder1 = new AlertDialog.Builder(OfferRide.this);
        builder1.setTitle("Select Event");
        builder1.setItems(eventsArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                isEvent = true;
                btnSelectGroup.setEnabled(false);
                Toast.makeText(getApplicationContext(), "You have selected" + eventsArray[position], Toast.LENGTH_SHORT).show();
                eventId = selectEvents.get(position).getEventId();
                eventLocation = selectEvents.get(position).getEventLocation().getAddress().toString();
                endAddress = selectEvents.get(position).getEventLocation().getAddress();
                latE = Double.toString(selectEvents.get(position).getEventLocation().getLat());
                lonE = Double.toString(selectEvents.get(position).getEventLocation().getLon());
                AlertDialog.Builder builder3 = new AlertDialog.Builder(OfferRide.this);
                builder3.setTitle("Select type");
                builder3.setPositiveButton("To this event!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        isToEvent = true;
                        EditEnd.setHint(eventLocation.toString());
                        // EditEnd.setKeyListener(null);
                        Check2.setEnabled(false);
                    }
                });
                builder3.setNegativeButton("From this event!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        isFromEvent = true;
                        EditStart.setHint(eventLocation.toString());
                        // EditStart.setKeyListener(null);
                        Check1.setEnabled(false);
                    }
                });
                AlertDialog alertDialog = builder3.create();
                alertDialog.show();

            }
        });

        AlertDialog alertDialog = builder1.create();
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
                        sendRequest(activity);

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

        MyRequestQueue.getInstance(activity).addToRequestQueue(getCurrentAddressRequest);
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

                       if(!isEvent){ getEndpointLoc(activity);}
                        else sendRideInfo(activity);
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

        MyRequestQueue.getInstance(activity).addToRequestQueue(getStartLocRequest);
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

        MyRequestQueue.getInstance(activity).addToRequestQueue(getEndLocRequest);
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

                if(isGroup) params.put("group_id", groupId);
                if(isEvent) params.put("event_id", eventId);

                params.put("seat", SeatNo.toString());
                params.put("start_time", EditStartTime);
                params.put("arrival_time", EditEndTime);
                params.put("username", User.getCurrentUser().getUsername());
                //params.put("token", MainActivity.getAuthToken(activity.getApplicationContext()));
                return params;
            }
        };

        MyRequestQueue.getInstance(activity).addToRequestQueue(OfferRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
        boolean check = false;
        if (isGroup||isEvent)
            check = true;
        return true;

                /*!((!check)||displayDate.getText().toString().isEmpty()||
                displayStartTime .getText().toString().isEmpty()||
                displayArrivalTime .getText().toString().isEmpty()||
                EditStart.getText().toString().isEmpty()||
                EditEnd.getText().toString().isEmpty()*/


    }

    public void offerRide(View view)
    {
        if(!(isEvent||isGroup))
        {
            System.out.println("Have not choose any group or event!!");
            Toast.makeText(getApplicationContext(),"Must select a group or an event!",Toast.LENGTH_SHORT).show();
        }
        else
        {
        if(isEvent)
        {
            if(isToEvent){
                startAddress = EditStart.getText().toString();
                endAddress = eventLocation;
            }
            else
            if(isFromEvent){
                startAddress = eventLocation ;
                endAddress = EditEnd.getText().toString();
            }
        }
        if(isGroup)
        {
            startAddress = EditStart.getText().toString();
            endAddress = EditEnd.getText().toString();
            Toast.makeText(getApplicationContext(),"Group info"+startAddress,Toast.LENGTH_SHORT).show();
        }

        if (Check1.isChecked()||Check2.isChecked())
        {
            reverseAddress(this);
        }
        else
        {
            if (inputValid()) {
                sendRequest(this);
            }
            else  System.out.println("Invalid Input!!!");
        }
    }
    }


}

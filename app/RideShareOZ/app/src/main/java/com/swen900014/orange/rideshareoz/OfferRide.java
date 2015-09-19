package com.swen900014.orange.rideshareoz;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;


public class OfferRide extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener
{
    private final String TAG = "OfferRide";
    private final static String Offer_Ride_URL = "http://144.6.226.237/ride/create?";
    private Location loc;
    private StringBuffer suffix = new StringBuffer();
    private EditText EditStart, EditEnd, EditDate, EditTime;
    private EditText SpinSN;
    private  CheckBox Check1, Check2;
    //private  Button btnSubmit,btnReset;
    private String latS = "";
    private String lonS = "";
    private String latE = "";
    private String lonE = "";
    private PlaceAutoCompleteAdapter adapter;

    protected GoogleApiClient mGoogleApiClient;


    private static final LatLngBounds BOUNDS_GREATER_Melbourne = new LatLngBounds(
            new LatLng(-38.260720, 144.394492), new LatLng(-37.459846, 145.764740));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offerride);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();


       // btnSubmit = (Button) findViewById(R.id.button1);
       // btnReset = (Button) findViewById(R.id.button2);
        EditStart = (EditText) findViewById(R.id.Start);
        EditEnd = (EditText) findViewById(R.id.End);
        EditDate = (EditText) findViewById(R.id.Date);
        EditTime = (EditText) findViewById(R.id.StartTime);
        SpinSN = (EditText) findViewById(R.id.SeatNo);
        Check1 = (CheckBox) findViewById(R.id.current1);
        Check2 = (CheckBox) findViewById(R.id.current2 );

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
        getIntent();
    }



    // void resetOnClickListener(new View.OnClickListener()
   // {


   // });


    private AdapterView.OnItemClickListener mAutoCompleteClickListener
            = new AdapterView.OnItemClickListener()
    {
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
            = new ResultCallback<PlaceBuffer>()
    {
        @Override
        public void onResult(PlaceBuffer places)
        {
            if (!places.getStatus().isSuccess())
            {
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

    public void sendRequest(final Activity activity, String startAddress, final String endAddress)
    {
        final String url1 = "https://maps.googleapis.com/maps/api/geocode/json?" +
                "address=" + startAddress + ",+Australia&" +
                "key=AIzaSyBhEI1X-PMslBS2Ggq35bOncxT05mWO9bs";

        final StringRequest getLocRequest1 = new StringRequest(Request.Method.GET, url1,
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

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        getEndpointLoc(activity, endAddress);
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

        MyRequest.getInstance(activity).addToRequestQueue(getLocRequest1);
    }

    private void getEndpointLoc(final Activity activity, String endAddress)
    {
        final String url2 = "https://maps.googleapis.com/maps/api/geocode/json?" +
                "address=" + endAddress + ",+Australia&" +
                "key=AIzaSyBhEI1X-PMslBS2Ggq35bOncxT05mWO9bs";

        final StringRequest getLocRequest2 = new StringRequest(Request.Method.GET, url2,   // nested 1
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

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    sendRideInfo(activity);

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

        MyRequest.getInstance(activity).addToRequestQueue(getLocRequest2);
    }

    private void sendRideInfo(Activity activity)
    {
        StringRequest OfferRequest = new StringRequest(Request.Method.POST,
                Offer_Ride_URL, new Response.Listener<String>() {
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

                if (!Check1.isChecked()) {

                    params.put("s_lat", "111");
                    params.put("s_lon", "111");

                } else {
                    params.put("s_lat", latS);
                    params.put("s_lon", lonS);
                }


                if (!Check2.isChecked()) {

                    params.put("e_lat", "222");
                    params.put("e_lon", "222");

                } else {
                    params.put("e_lat", latE);
                    params.put("e_lon", lonE);
                }
                params.put("groupid", "55cab5dde81ab31606e4814c");
                params.put("seat", "5");
                params.put("arrival_time", "20150816");
                params.put("eventid", "1");
                params.put("username", "qianz7@student.unimelb.edu.au");
                return params;
            }
        };

        MyRequest.getInstance(activity).addToRequestQueue(OfferRequest);
    }


    public void offerRide(View view)
    {
        sendRequest(this, EditStart.getText().toString(), EditEnd.getText().toString());
    }
}
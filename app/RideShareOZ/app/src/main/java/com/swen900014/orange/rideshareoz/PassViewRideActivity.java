package com.swen900014.orange.rideshareoz;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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

import static com.swen900014.orange.rideshareoz.User.UserType;


public class PassViewRideActivity extends FragmentActivity
        implements GoogleApiClient.OnConnectionFailedListener
{
    private final static String TAG = "Passenger View Ride";
    //private final static String JOIN_URL = "http://144.6.226.237/test";
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-38.260720, 144.394492), new LatLng(-37.459846, 145.764740));
    //new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362)

    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutoCompleteAdapter adapter;
    private RideRequest rideRequest;
    //private RequestQueue requestQueue;

    private TextView startLabel;
    private TextView endLabel;
    private TextView timeLabel;

    private TextView passText;
    private TextView pickUpLocText;
    private TextView timeInputText;

    private String lat;
    private String lon;

    // Dummy data
    User dummyUser = new User("user1", "email", 123, 0, UserType.DRIVER);
    Ride dummyRide = new Ride("start", "end", "6/09/2015", dummyUser, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_view_ride);

        lat = "";
        lon = "";

        //MyRequest.getInstance(this.getApplicationContext()).
        //        getRequestQueue();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();
        //mGoogleApiClient.connect();

        rideRequest = new RideRequest(dummyRide);

        startLabel = (TextView) findViewById(R.id.startText);
        endLabel = (TextView) findViewById(R.id.endText);
        timeLabel = (TextView) findViewById(R.id.timeText);
        passText = (TextView) findViewById(R.id.passList);

        timeInputText = (TextView) findViewById(R.id.timeInputText);

        adapter = new PlaceAutoCompleteAdapter(this,
                android.R.layout.simple_expandable_list_item_1, mGoogleApiClient,
                BOUNDS_GREATER_SYDNEY, null);

        AutoCompleteTextView pickUpLocText = (AutoCompleteTextView)
                findViewById(R.id.pickUpLocText);
        pickUpLocText.setOnItemClickListener(mAutoCompleteClickListener);
        pickUpLocText.setAdapter(adapter);


        startLabel.setText(dummyRide.getStart());
        endLabel.setText(dummyRide.getEnd());
        timeLabel.setText(dummyRide.getTime());
        passText.setText(dummyUser.getUsername() + ", phone: " + dummyUser.getPhone() + "\n");

        getIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pass_view_ride, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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

    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this, "Could not connect to Google API Client: Error " +
                        connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
    }

    // Button events of sending a request for joining a ride
    public void joinRide(View view)
    {
        rideRequest.sendRequest(this, "Carlton");//1600+Amphitheatre+Parkway

        /*String url = "https://maps.googleapis.com/maps/api/geocode/json?" +
                "address=" + "Carlton" + ",+Australia&" +
                "key=AIzaSyBhEI1X-PMslBS2Ggq35bOncxT05mWO9bs";
        StringRequest locRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonResponse = new JSONObject(response);
                            System.out.println(jsonResponse.toString());

                            lat = jsonResponse.getJSONArray("results").getJSONObject(0).
                                    getJSONObject("geometry").getJSONObject("location").
                                    getString("lat");
                            lon = jsonResponse.getJSONArray("results").getJSONObject(0).
                                    getJSONObject("geometry").getJSONObject("location").
                                    getString("lng");

                            // Check response whether it's accurate, if not remind user

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        // check response, whether it received
                    }
                },
                new Response.ErrorListener()
                {
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        System.out.println("it doesn't work");
                    }
                });
        MyRequest.getInstance(this).addToRequestQueue(locRequest);


        StringRequest joinRequest = new StringRequest(Request.Method.POST,
                JOIN_URL, new Response.Listener<String>()//ride/request
        {
            @Override
            public void onResponse(String s)
            {
                System.out.println("response: " + s);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                System.out.println("Sending post failed!");
            }
        }){
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", "user1");
                params.put("ride_id", "1");
                params.put("p_lat", lat);
                params.put("p_lon", lon);

                return params;
            }
        };

        MyRequest.getInstance(this).addToRequestQueue(joinRequest);*/

        // May receive user_id from server.
    }
}
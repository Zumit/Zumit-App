package com.swen900014.orange.rideshareoz;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.swen900014.orange.rideshareoz.Resources.JOIN_REQUEST_URL;


/**
 * Created by Sangzhuoyang Yu on 9/12/15.
 * It initialize a new activity for the ride
 * from the normal users' view. Users are able
 * to send join request and leave request of the ride
 * to the server.
 */
public class PassViewRideActivity extends FragmentActivity
        implements GoogleApiClient.OnConnectionFailedListener
{
    private final static String TAG = "Passenger View Ride";
    private final static String LEAVE_RIDE_URL = "";
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-38.260720, 144.394492), new LatLng(-37.459846, 145.764740));
    //new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362)

    private String lat = "";
    private String lon = "";
    private String address = "";

    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutoCompleteAdapter adapter;
    //private RideRequest joinRequest;

    private TextView pickUpLocText;

    private TextView cityText;
    private TextView suberbText;
    private TextView streetText;

    private TableLayout passengerList;
    private Ride ride;

    private Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_view_ride);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();
        //mGoogleApiClient.connect();

        thisActivity = this;

        ride = (Ride) getIntent().getSerializableExtra("SelectedRide");

        TextView startLabel = (TextView) findViewById(R.id.startEditPass);
        TextView endLabel = (TextView) findViewById(R.id.endEditPass);
        TextView startTimeLabel = (TextView) findViewById(R.id.startTimeEditPass);
        TextView arrivalTimeLabel = (TextView) findViewById(R.id.arrivalTimeEditPass);
        TextView driverText = (TextView) findViewById(R.id.driverTextPassView);

        TextView inputTabelName = (TextView) findViewById(R.id.inputTableName);
        TextView pickUpLabel = (TextView) findViewById(R.id.pickUpLabel);
        TextView seatsText = (TextView) findViewById(R.id.seatsEditPass);
        pickUpLocText = (TextView) findViewById(R.id.pickUpLocText);
        //timeInputText = (TextView) findViewById(R.id.timeInputText);
        passengerList = (TableLayout) findViewById(R.id.passengerListPass);

        cityText = (TextView) findViewById(R.id.cityEditPass);
        suberbText = (TextView) findViewById(R.id.suberbEditPass);
        streetText = (TextView) findViewById(R.id.streetEditPass);

        Button joinLeaveButton = (Button) findViewById(R.id.joinButton);

        adapter = new PlaceAutoCompleteAdapter(this,
                android.R.layout.simple_expandable_list_item_1, mGoogleApiClient,
                BOUNDS_GREATER_SYDNEY, null);

        AutoCompleteTextView pickUpLocText = (AutoCompleteTextView)
                findViewById(R.id.pickUpLocText);
        pickUpLocText.setOnItemClickListener(mAutoCompleteClickListener);
        pickUpLocText.setAdapter(adapter);

        if (ride.getRideState() == Ride.RideState.VIEWING)
        {
            joinLeaveButton.setText(getString(R.string.joinButton));
        }
        else if (ride.getRideState() == Ride.RideState.JOINED)
        {
            joinLeaveButton.setText(getString(R.string.LeaveRide));
            pickUpLocText.setVisibility(View.INVISIBLE);
            cityText.setVisibility(View.INVISIBLE);
            suberbText.setVisibility(View.INVISIBLE);
            streetText.setVisibility(View.INVISIBLE);
            inputTabelName.setVisibility(View.INVISIBLE);
            pickUpLabel.setVisibility(View.INVISIBLE);
        }

        startLabel.setText(ride.getStart().getAddress());
        endLabel.setText(ride.getEnd().getAddress());
        startTimeLabel.setText(ride.getStartTime());
        arrivalTimeLabel.setText(ride.getArrivingTime());
        driverText.setText(ride.getDriver().getUsername());
        seatsText.setText("" + ride.getSeats());

        // Currently there is no pick up time of passengers
        TextView pickupTimeLabel = (TextView) findViewById(R.id.pickuptimeLabel);
        pickupTimeLabel.setVisibility(View.INVISIBLE);
        TextView pickupTimeText = (TextView) findViewById(R.id.pickupTimeText);
        pickupTimeText.setVisibility(View.INVISIBLE);

        driverText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(thisActivity, UserInfoActivity.class);
                intent.putExtra("Ride", ride);

                thisActivity.startActivity(intent);
            }
        });

        updateView();
    }

    public void updateView()
    {
        ArrayList<Pickup> joinedList = ride.getJoined();

        passengerList.removeAllViews();

        for (final Pickup lift : joinedList)
        {
            TextView pass = new TextView(this);
            pass.setText("name: " + lift.getUser().getUsername());

            if (ride.getRideState() == Ride.RideState.JOINED)
            {
                pass.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(thisActivity, UserInfoActivity.class);
                        intent.putExtra("Ride", ride);
                        intent.putExtra("UserInfo", lift);
                        thisActivity.startActivity(intent);
                    }
                });
            }

            passengerList.addView(pass);
        }
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

    public void onClick(View view)
    {
        if (ride.getRideState() == Ride.RideState.VIEWING)
        {
            if (inputValid())
            {
                address = cityText.getText().toString() + "+" +
                        suberbText.getText().toString() + "+" +
                        streetText.getText().toString();

                address = address.replaceAll(" ", "+");

                sendRequest(this);

                //joinRequest.sendRequest(this, address);//pickUpLocText.getText().toString()
            }
        }
        else if (ride.getRideState() == Ride.RideState.JOINED)
        {
            leaveRide(this);
        }

        updateView();
    }

    public void leaveRide(final Activity activity)
    {
        StringRequest leaveRequest = new StringRequest(Request.Method.POST,
                LEAVE_RIDE_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                System.out.println("response: " + s);

                // Get back to the my rides page
                Intent intent = new Intent(activity, MyRidesActivity.class);
                activity.startActivity(intent);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                volleyError.printStackTrace();
                System.out.println("Sending post failed!");
            }
        }){
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();

                // User name and ride id for the ride to join
                String accountName = User.getCurrentUser().getUsername();
                params.put("username", accountName);
                params.put("ride_id", ride.getRideId());

                return params;
            }
        };

        MyRequest.getInstance(this).addToRequestQueue(leaveRequest);
    }

    public void sendRequest(final Activity activity)
    {
        String address = cityText.getText().toString() + "+" +
                suberbText.getText().toString() + "+" +
                streetText.getText().toString();

        String url = "https://maps.googleapis.com/maps/api/geocode/json?" +
                "address=" + address + ",+Australia&" +
                "key=AIzaSyBhEI1X-PMslBS2Ggq35bOncxT05mWO9bs";

        StringRequest getLocRequest = new StringRequest(Request.Method.GET, url,
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

                        sendJoinRequest(activity);

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

        MyRequest.getInstance(activity).addToRequestQueue(getLocRequest);
    }

    private void sendJoinRequest(final Activity activity)
    {
        StringRequest joinRequest = new StringRequest(Request.Method.POST,
                JOIN_REQUEST_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                System.out.println("response: " + s);

                Intent intent = new Intent(activity, MyRidesActivity.class);
                activity.startActivity(intent);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                volleyError.printStackTrace();

                System.out.println("Sending post failed!");
            }
        }){
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();

                address = cityText.getText().toString() + "\n" +
                        suberbText.getText().toString() + "\n" +
                        streetText.getText().toString();

                params.put("username", User.getCurrentUser().getUsername());
                params.put("ride_id", ride.getRideId());
                params.put("p_lat", lat);
                params.put("p_lon", lon);
                params.put("pickup_add", address);

                return params;
            }
        };

        MyRequest.getInstance(activity).addToRequestQueue(joinRequest);
    }

    // Check whether user has typed in the pickup location
    public boolean inputValid()
    {
        return !cityText.getText().toString().isEmpty() &&
                !suberbText.getText().toString().isEmpty() &&
                !streetText.getText().toString().isEmpty();
        //return !pickUpLocText.getText().toString().isEmpty();
    }
}
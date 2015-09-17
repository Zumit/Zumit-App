package com.swen900014.orange.rideshareoz;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Sangzhuoyang Yu on 9/12/15.
 * It initialize a new activity for the ride
 * from the normal users' view. Users are able
 * to send join request and leave request of the ride,
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

    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutoCompleteAdapter adapter;
    private RideRequest rideRequest;

    private TextView passText;
    private TextView pickUpLocText;

    private Ride ride;

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

        ride = (Ride) getIntent().getSerializableExtra("SelectedRide");
        rideRequest = new RideRequest(ride);

        TextView startLabel = (TextView) findViewById(R.id.startText);
        TextView endLabel = (TextView) findViewById(R.id.endText);
        TextView timeLabel = (TextView) findViewById(R.id.timeText);
        TextView driverText = (TextView) findViewById(R.id.driverTextPassView);
        passText = (TextView) findViewById(R.id.passList);

        TextView inputTabelName = (TextView) findViewById(R.id.inputTableName);
        TextView pickUpLabel = (TextView) findViewById(R.id.pickUpLabel);
        pickUpLocText = (TextView) findViewById(R.id.pickUpLocText);
        //timeInputText = (TextView) findViewById(R.id.timeInputText);

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
            inputTabelName.setVisibility(View.INVISIBLE);
            pickUpLabel.setVisibility(View.INVISIBLE);
        }

        startLabel.setText(ride.getStart().getAddress());
        endLabel.setText(ride.getEnd().getAddress());
        timeLabel.setText(ride.getArrivingTime());
        driverText.setText(ride.getDriver().getUsername() +
                ", phone: " + ride.getDriver().getPhone() +
                ", credit: " + ride.getDriver().getCredit());

        // Currently there is no pick up time of passengers
        TextView pickupTimeLabel = (TextView) findViewById(R.id.pickuptimeLabel);
        pickupTimeLabel.setVisibility(View.INVISIBLE);
        TextView pickupTimeText = (TextView) findViewById(R.id.pickupTimeText);
        pickupTimeText.setVisibility(View.INVISIBLE);

        updateView();
    }

    public void updateView()
    {
        String joinedPass = "";

        ArrayList<Lift> joinedList = ride.getJoined();

        for (Lift lift : joinedList)
        {
            joinedPass += "name: " + lift.getUser().getUsername() +
                    ": phone: " + lift.getUser().getPhone() + "\n";
        }

        passText.setText(joinedPass);
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
            joinRide();
        }
        else if (ride.getRideState() == Ride.RideState.JOINED)
        {
            leaveRide();
        }

        updateView();
    }

    // Button events of sending a request for joining a ride
    public void joinRide()
    {
        rideRequest.sendRequest(this, pickUpLocText.getText().toString());
        //1600+Amphitheatre+Parkway
        //"Carlton"

        // May receive user_id from server.
    }

    public void leaveRide()
    {
        StringRequest leaveRequest = new StringRequest(Request.Method.POST,
                LEAVE_RIDE_URL, new Response.Listener<String>()
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
                volleyError.printStackTrace();
                System.out.println("Sending post failed!");
            }
        }){
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();

                //add your parameters here
                params.put("username", "sangzhouyang@student.unimelb.edu.au");
                params.put("ride_id", "55e7ed577ea19c92ac2d0911");

                return params;
            }
        };

        MyRequest.getInstance(this).addToRequestQueue(leaveRequest);
    }
}
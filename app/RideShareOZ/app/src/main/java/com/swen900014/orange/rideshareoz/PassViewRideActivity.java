package com.swen900014.orange.rideshareoz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.swen900014.orange.rideshareoz.Resources.*;


/**
 * Created by Sangzhuoyang Yu on 9/12/15.
 * It initialize a new activity for the ride
 * from the normal users' view. Users are able
 * to send join request and leave request of the ride
 * to the server.
 */
public class PassViewRideActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener
{
    private final static String TAG = "Passenger View Ride";

    private int score;

    private String lat = "";
    private String lon = "";
    private String address = "";

    protected GoogleApiClient mGoogleApiClient;
    private AutoCompleteTextView pickUpLocText;
    private Spinner spinnerRate;

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
        thisActivity = this;
        ride = (Ride) getIntent().getSerializableExtra("SelectedRide");

        TextView startLabel = (TextView) findViewById(R.id.startEditPass);
        TextView endLabel = (TextView) findViewById(R.id.endEditPass);
        TextView startTimeLabel = (TextView) findViewById(R.id.startTimeEditPass);
        TextView arrivalTimeLabel = (TextView) findViewById(R.id.arrivalTimeEditPass);
        TextView driverText = (TextView) findViewById(R.id.driverTextPassView);
        TextView inputTabelName = (TextView) findViewById(R.id.inputTableName);
        TextView seatsText = (TextView) findViewById(R.id.seatsEditPass);
        TextView rateLabel = (TextView) findViewById(R.id.rateRideLabel);

        // Display ride information
        startLabel.setText(ride.getStart().getAddress());
        endLabel.setText(ride.getEnd().getAddress());
        startTimeLabel.setText(ride.getStartTime());
        arrivalTimeLabel.setText(ride.getArrivingTime());
        driverText.setText(ride.getDriver().getUsername());
        seatsText.setText(ride.getSeats());

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

        // Set up auto-place-complete text view
        pickUpLocText = (AutoCompleteTextView) findViewById(R.id.pickUpLocText);
        passengerList = (TableLayout) findViewById(R.id.passengerListPass);

        PlaceAutoCompleteAdapter adapter = new PlaceAutoCompleteAdapter(this,
                android.R.layout.simple_expandable_list_item_1, mGoogleApiClient,
                BOUNDS_GREATER_MELBOURNE, null, pickUpLocText);
        pickUpLocText.setAdapter(adapter);

        Button joinLeaveButton = (Button) findViewById(R.id.joinButton);
        Button rateButton = (Button) findViewById(R.id.rateRideButton);

        // Display views based on the state of the ride
        if (ride.getRideState() == Ride.RideState.JOINED)
        {
            joinLeaveButton.setText(getString(R.string.LeaveButton));
            joinLeaveButton.setVisibility(View.VISIBLE);
        }
        else if (ride.getRideState() == Ride.RideState.NEW)
        {
            joinLeaveButton.setText(getString(R.string.joinButton));
            inputTabelName.setVisibility(View.VISIBLE);
            pickUpLocText.setVisibility(View.VISIBLE);
            joinLeaveButton.setVisibility(View.VISIBLE);
        }
        else if (ride.getRideState() == Ride.RideState.PASSED)
        {
            // Passenger is allowed to rate the driver if they
            // haven't done that
            if (ride.isDriverRated())
            {
                rateLabel.setVisibility(View.VISIBLE);
                rateButton.setVisibility(View.VISIBLE);
                spinnerRate.setVisibility(View.VISIBLE);

                // Rating spinner
                spinnerRate = (Spinner) findViewById(R.id.spinnerRate);
                spinnerRate.setSelected(false);
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                        R.array.rate_array, android.R.layout.simple_spinner_item);

                spinnerRate.setAdapter(spinnerAdapter);
                spinnerRate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        score = position + 1;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {

                    }
                });
            }
        }

        displayPassengers();
    }

    public void displayPassengers()
    {
        ArrayList<Pickup> joinedList = ride.getJoined();

        passengerList.removeAllViews();

        // Display joined passengers
        for (final Pickup lift : joinedList)
        {
            TextView pass = new TextView(this);
            pass.setText(lift.getUser().getUsername());

            // Only people who joined the ride is able to view
            // other users' information
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
        if (ride.getRideState() == Ride.RideState.NEW)
        {
            if (inputValid())
            {
                address = pickUpLocText.getText().toString();

                joinRide();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Please fill in the address",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else if (ride.getRideState() == Ride.RideState.JOINED)
        {
            sendLeaveRideRequest();
        }
    }

    public void rate(View view)
    {
        if (spinnerRate.isSelected())
        {
            sendRateRequest();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Please select a rating option",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void sendLeaveRideRequest()
    {
        StringRequest leaveRequest = new StringRequest(Request.Method.POST,
                LEAVE_RIDE_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                System.out.println("response: " + s);

                // Get back to the my rides page
                thisActivity.finish();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                volleyError.printStackTrace();
                System.out.println("Sending post failed!");
            }
        })
        {
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();

                // User name and ride id
                params.put("username", User.getCurrentUser().getUsername());
                params.put("ride_id", ride.getRideId());

                return params;
            }
        };

        MyRequest.getInstance(thisActivity).addToRequestQueue(leaveRequest);
    }

    public void joinRide()
    {
        // Retrieve coordinates of the pick up point from google server first
        String addressToGoogle = address.replaceAll(" ", "+");

        String url = "https://maps.googleapis.com/maps/api/geocode/json?" +
                "address=" + addressToGoogle + "key=AIzaSyBhEI1X-PMslBS2Ggq35bOncxT05mWO9bs";

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

                            System.out.println("s" + response);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        sendJoinRequest();

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

        MyRequest.getInstance(thisActivity).addToRequestQueue(getLocRequest);
    }

    private void sendJoinRequest()
    {
        StringRequest joinRequest = new StringRequest(Request.Method.POST,
                JOIN_REQUEST_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                System.out.println("response: " + s);

                thisActivity.finish();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                volleyError.printStackTrace();

                System.out.println("Sending post failed!");
            }
        })
        {
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();

                params.put("username", User.getCurrentUser().getUsername());
                params.put("ride_id", ride.getRideId());
                params.put("p_lat", lat);
                params.put("p_lon", lon);
                params.put("pickup_add", address);

                return params;
            }
        };

        MyRequest.getInstance(thisActivity).addToRequestQueue(joinRequest);
    }

    private void sendRateRequest()
    {
        StringRequest joinRequest = new StringRequest(Request.Method.POST,
                JOIN_REQUEST_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                thisActivity.finish();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                volleyError.printStackTrace();

                System.out.println("Sending rate post failed!");
            }
        })
        {
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();

                params.put("username", User.getCurrentUser().getUsername());
                params.put("ride_id", ride.getRideId());
                params.put("rate", Integer.toString(score));
                params.put("type", "driver");

                return params;
            }
        };

        MyRequest.getInstance(thisActivity).addToRequestQueue(joinRequest);
    }

    // Check whether user has typed in the pickup location
    public boolean inputValid()
    {
        return !pickUpLocText.getText().toString().isEmpty();
    }
}
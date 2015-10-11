package com.swen900014.orange.rideshareoz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static com.swen900014.orange.rideshareoz.Resources.*;


/**
 * Created by Sangzhuoyang Yu on 9/12/15.
 * The view activity where user information are
 * displayed
 */
public class UserInfoActivity extends AppCompatActivity
{
    private Ride ride;
    private Pickup userInfo;
    private Activity thisActivity;
    private int score;  // Rating score

    private Spinner spinnerRate;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Intent intent = getIntent();
        ride = (Ride) intent.getSerializableExtra("Ride");
        Ride.RideState rideState = ride.getRideState();

        if (intent.hasExtra("Pickup"))
        {
            userInfo = (Pickup) intent.getSerializableExtra("Pickup");
        }
        else
        {
            userInfo = new Pickup(ride.getDriver(), ride.getEnd(), true, true);
        }

        thisActivity = this;

        TextView nameText = (TextView) findViewById(R.id.ShowName);
        TextView phoneText = (TextView) findViewById(R.id.ShowPhone);
        TextView emailText = (TextView) findViewById(R.id.ShowEmail);
        TextView creditText = (TextView) findViewById(R.id.ShowCredit);
        TextView departureText = (TextView) findViewById(R.id.ShowDeparture);

        nameText.setText(userInfo.getUser().getUsername());
        phoneText.setText(userInfo.getUser().getPhone());
        emailText.setText(userInfo.getUser().getEmail());
        creditText.setText(Integer.toString(userInfo.getUser().getCredit()));
        departureText.setText(userInfo.getLocation().getAddress());

        // Hide accept and reject options if current user is
        // not driver offering the ride
        if (rideState == Ride.RideState.OFFERING && ride.hasRequest(userInfo.getUser()))
        {
            Button acceptButton = (Button) findViewById(R.id.acceptButton);
            Button rejectButton = (Button) findViewById(R.id.rejectButton);

            acceptButton.setVisibility(View.VISIBLE);
            rejectButton.setVisibility(View.VISIBLE);

            acceptButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    sendAcceptRequest(userInfo, ride, thisActivity);
                }
            });
            rejectButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    sendRejectRequest(ride, userInfo, thisActivity);
                }
            });
        }
        // Show rating options for driver to rate passengers
        else if (rideState == Ride.RideState.PASSED &&
                !User.getCurrentUser().getUsername()
                        .equals(userInfo.getUser().getUsername()) &&
                User.getCurrentUser().getUsername()
                        .equals(ride.getDriver().getUsername()) &&
                !userInfo.isRatedByDriver())
        {
            Button rateButton = (Button) findViewById(R.id.ratePassButton);
            rateButton.setVisibility(View.VISIBLE);

            // Rating spinner
            spinnerRate = (Spinner) findViewById(R.id.spinnerRatePass);
            spinnerRate.setVisibility(View.VISIBLE);
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

    public void ratePass()
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

    private void sendRateRequest()
    {
        StringRequest rateRequest = new StringRequest(Request.Method.POST,
                RATE_USER_URL, new Response.Listener<String>()
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
                params.put("rateeName", userInfo.getUser().getUsername());
                params.put("ride_id", ride.getRideId());
                params.put("rate", Integer.toString(score));
                params.put("type", "driver");

                return params;
            }
        };

        MyRequest.getInstance(thisActivity).addToRequestQueue(rateRequest);
    }

    public void sendAcceptRequest(final Pickup lift, final Ride ride,
                                  final Activity activity)
    {
        StringRequest acceptRequest = new StringRequest(Request.Method.POST,
                ACCEPT_REQUEST_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                System.out.println("response: " + s);

                ride.acceptJoin(lift);

                Intent intent = new Intent(thisActivity, DriverViewRideActivity.class);
                intent.putExtra("SelectedRide", ride);

                activity.startActivity(intent);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                volleyError.printStackTrace();

                System.out.println("Sending accept failed!");
            }
        }){
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();

                params.put("username", lift.getUser().getUsername());
                params.put("ride_id", ride.getRideId());

                return params;
            }
        };

        MyRequest.getInstance(activity).addToRequestQueue(acceptRequest);
    }

    public void sendRejectRequest(final Ride ride, final Pickup lift,
                                  final Activity activity)
    {
        StringRequest rejectRequest = new StringRequest(Request.Method.POST,
                REJECT_REQUEST_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                System.out.println("response: " + s);

                ride.rejectJoin(userInfo);
                Intent intent = new Intent(activity, DriverViewRideActivity.class);
                intent.putExtra("SelectedRide", ride);

                activity.startActivity(intent);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                volleyError.printStackTrace();

                System.out.println("Sending reject failed!");
            }
        }){
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();

                params.put("username", lift.getUser().getUsername());
                params.put("ride_id", ride.getRideId());

                return params;
            }
        };

        MyRequest.getInstance(activity).addToRequestQueue(rejectRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_info, menu);
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
}

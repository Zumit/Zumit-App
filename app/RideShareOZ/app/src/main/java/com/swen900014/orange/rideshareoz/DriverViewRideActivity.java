package com.swen900014.orange.rideshareoz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Sangzhuoyang Yu on 9/12/15.
 * It initialize a new activity for the ride
 * from the drivers' view. The driver is able
 * to cancel the ride, accept or reject requests
 * from passengers.
 */
public class DriverViewRideActivity extends AppCompatActivity
{
    private final static String CANCEL_RIDE_URL = "http://144.6.226.237/ride/cancel";

    private TextView passText;

    private Ride ride;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_view_ride);

        ride = (Ride) getIntent().getSerializableExtra("SelectedRide");

        // Test accepting and rejecting requests
        /*ride.acceptJoin(new Lift(new User("user1", "email", 123, 0, User.UserType.PASSENGER),
                new Location(0.0, 0.0, "carlton")));

        ride.addWaiting(new Lift(new User("user2", "email", 123, 0, User.UserType.PASSENGER),
                new Location(0.0, 0.0, "carlton")));
        */

        RequestAdapter requestAdapter = new RequestAdapter(this, ride.getWaiting(), ride);
        ListView requestList = (ListView) findViewById(R.id.listView_request);
        requestList.setAdapter(requestAdapter);

        TextView startLabel = (TextView) findViewById(R.id.startText);
        TextView endLabel = (TextView) findViewById(R.id.endText);
        TextView timeLabel = (TextView) findViewById(R.id.timeText);
        TextView driverText = (TextView) findViewById(R.id.driverTextDriverView);
        passText = (TextView) findViewById(R.id.passList);

        startLabel.setText(ride.getStart().getAddress());
        endLabel.setText(ride.getEnd().getAddress());
        timeLabel.setText(ride.getArrivingTime());
        driverText.setText(ride.getDriver().getUsername() +
                ", phone: " + ride.getDriver().getPhone() +
                ", credit: " + ride.getDriver().getCredit());

        updateView();
    }


    public void updateView()
    {
        String joinedPass = "";

        ArrayList<Pickup> joinedList = ride.getJoined();

        for (Pickup lift : joinedList) {
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

    public void cancelRide(View view)
    {
        StringRequest cancelRequest = new StringRequest(Request.Method.POST,
                CANCEL_RIDE_URL, new Response.Listener<String>()
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

        MyRequest.getInstance(this).addToRequestQueue(cancelRequest);
    }
}

package com.swen900014.orange.rideshareoz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static com.swen900014.orange.rideshareoz.User.UserType;

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
    private TextView startLabel;
    private TextView endLabel;
    private TextView timeLabel;
    private TextView passText;

    // Dummy data
    User dummyUser = new User("user1", "email", 123, 0, UserType.DRIVER);
    Ride dummyRide = new Ride("start", "end", "6/09/2015", dummyUser, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_view_ride);

        startLabel = (TextView) findViewById(R.id.startText);
        endLabel = (TextView) findViewById(R.id.endText);
        timeLabel = (TextView) findViewById(R.id.timeText);
        passText = (TextView) findViewById(R.id.passList);

        startLabel.setText(dummyRide.getStart());
        endLabel.setText(dummyRide.getEnd());
        timeLabel.setText(dummyRide.getTime());
        passText.setText(dummyUser.getUsername() + ", phone: " + dummyUser.getPhone() + "\n");

        getIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pass_view_ride, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
                Map<String, String> params = new HashMap<String, String>();

                //add your parameters here
                params.put("username", "sangzhouyang@student.unimelb.edu.au");
                params.put("ride_id", "55e7ed577ea19c92ac2d0911");

                return params;
            }
        };

        MyRequest.getInstance(this).addToRequestQueue(cancelRequest);
    }

    // Butten events of sending a request for joining a ride
    public void accept(View view)
    {

    }

    public void reject(View view)
    {

    }
}

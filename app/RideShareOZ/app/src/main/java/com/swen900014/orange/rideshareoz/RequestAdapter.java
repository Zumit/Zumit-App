package com.swen900014.orange.rideshareoz;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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
 * Created by yuszy on 9/16/15.
 */
public class RequestAdapter extends ArrayAdapter<User>
{
    private final static String ACCEPT_REQUEST_URL = "http://144.6.226.237/ride/accept";
    private final static String REJECT_REQUEST_URL = "http://144.6.226.237/ride/reject";

    private final Activity activity;
    private ArrayList<User> data;
    private Ride ride;

    public RequestAdapter(Activity activity, ArrayList<User> data, Ride ride)
    {
        super(activity, -1, data);
        this.activity = activity;
        //this.data = data;
        this.data = ride.getWaiting();
        this.ride = ride;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View row = inflater.inflate(R.layout.list_user_request_row, parent, false);

        final User user = data.get(position);

        TextView label = (TextView) row.findViewById(R.id.userInfo);
        Button acceptButton = (Button) row.findViewById(R.id.acceptButton);
        Button rejectButton = (Button) row.findViewById(R.id.rejectButton);

        if (acceptButton != null)
        {
            acceptButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    sendAcceptRequest(user.getUsername(), "" + ride.getRideId());
                    ride.acceptJoin(user);
                    remove(user);
                    ((DriverViewRideActivity) activity).updateView();
                }
            });
            rejectButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    sendRejectRequest(user.getUsername(), "" + ride.getRideId());
                }
            });
        }

        label.setText("name: " + user.getUsername());

        return row;
    }

    public void sendAcceptRequest(final String username, final String rideId)
    {
        StringRequest acceptRequest = new StringRequest(Request.Method.POST,
                ACCEPT_REQUEST_URL, new Response.Listener<String>()
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

                params.put("username", username);
                params.put("ride_id", rideId);

                return params;
            }
        };

        MyRequest.getInstance(activity).addToRequestQueue(acceptRequest);
    }

    public void sendRejectRequest(final String username, final String rideId)
    {
        StringRequest getLocRequest = new StringRequest(Request.Method.GET, REJECT_REQUEST_URL,
                new Response.Listener<String>()
                {
                    public void onResponse(String response)
                    {
                        try
                        {
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
                        volleyError.printStackTrace();
                        System.out.println("it doesn't work");
                    }
                });

        MyRequest.getInstance(activity).addToRequestQueue(getLocRequest);
    }
}

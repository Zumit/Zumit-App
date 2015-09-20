package com.swen900014.orange.rideshareoz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sangzhuoyang Yu on 9/16/15.
 * Adapter attached to the list view displaying
 * a list of join requests driver received, which can
 * be accepted or rejected
 */
public class RequestAdapter extends ArrayAdapter<Pickup> implements Serializable
{
    private final static String ACCEPT_REQUEST_URL = "http://144.6.226.237/ride/accept";
    private final static String REJECT_REQUEST_URL = "http://144.6.226.237/ride/reject";

    private final Activity activity;
    private ArrayList<Pickup> data;
    private Ride ride;

    public RequestAdapter(Activity activity, ArrayList<Pickup> lift, Ride ride)
    {
        super(activity, -1, lift);
        this.activity = activity;
        this.data = ride.getWaiting();
        this.ride = ride;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View row = inflater.inflate(R.layout.list_user_request_row, parent, false);
        final Pickup lift = data.get(position);
        final User user = lift.getUser();
        //final RequestAdapter adapter = this;

        row.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(activity, UserInfoActivity.class);
                intent.putExtra("UserInfo", lift);
                //intent.putExtra("RequestAdapter", adapter);
                intent.putExtra("Ride", ride);

                activity.startActivity(intent);
            }
        });
        /*Button acceptButton = (Button) row.findViewById(R.id.acceptButton);
        Button rejectButton = (Button) row.findViewById(R.id.rejectButton);

        if (acceptButton != null)
        {
            acceptButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    sendAcceptRequest(user.getUsername(), ride.getRideId(), lift);
                }
            });
            rejectButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    sendRejectRequest(user.getUsername(), ride.getRideId(), lift);
                }
            });
        }*/
        TextView label = (TextView) row.findViewById(R.id.userInfo);
        label.setText("name: " + user.getUsername());

        return row;
    }

    public void sendAcceptRequest(final String username, final String rideId, final Pickup lift)
    {
        StringRequest acceptRequest = new StringRequest(Request.Method.POST,
                ACCEPT_REQUEST_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                System.out.println("response: " + s);

                ride.acceptJoin(lift);
                remove(lift);
                ((DriverViewRideActivity) activity).updateView();
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

                params.put("username", username);
                params.put("ride_id", rideId);

                return params;
            }
        };

        MyRequest.getInstance(activity).addToRequestQueue(acceptRequest);
    }

    public void sendRejectRequest(final String username, final String rideId, final Pickup lift)
    {
        StringRequest rejectRequest = new StringRequest(Request.Method.POST,
                REJECT_REQUEST_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                System.out.println("response: " + s);

                remove(lift);
                ((DriverViewRideActivity) activity).updateView();
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

                params.put("username", username);
                params.put("ride_id", rideId);

                return params;
            }
        };

        MyRequest.getInstance(activity).addToRequestQueue(rejectRequest);
    }
}

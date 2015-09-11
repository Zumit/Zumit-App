package com.swen900014.orange.rideshareoz;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pools;
import android.support.v7.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;


/**
 * Created by yuszy on 9/9/15.
 */
public class RideRequest
{
    private final static String RIDE_URL = "http://144.6.226.237/ride/request";
    private Ride ride;

    public RideRequest(Ride ride)
    {
        this.ride = ride;
    }

    public void sendRequest(final Activity activity, String address)
    {
        RequestQueue queue = Volley.newRequestQueue(activity);

        String url = "https://maps.googleapis.com/maps/api/geocode/json?" +
                "address=" + address + ",+Mountain+View,+CA&" +
                "key=AIzaSyBhEI1X-PMslBS2Ggq35bOncxT05mWO9bs";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    public void onResponse(String response)
                    {
                        String lat = "";
                        String lon = "";

                        try
                        {
                            JSONObject jsonResponse = new JSONObject(response);
                            //output = jsonResponse.toString();

                            lat = jsonResponse.getJSONArray("results").getJSONObject(0).
                                    getJSONObject("geometry").getJSONObject("location").
                                    getString("lat");
                            lon = jsonResponse.getJSONArray("results").getJSONObject(0).
                                    getJSONObject("geometry").getJSONObject("location").
                                    getString("lng");
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        String message = "ride_id=" + ride.getRideId() +
                                "&username=" + "user1" + //passenger.getName
                                "&s_lat=" + lat + "&s_lon=" + lon;

                        PostThread post = new PostThread(RIDE_URL, message);
                        post.start();
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

        queue.add(stringRequest);
    }
}

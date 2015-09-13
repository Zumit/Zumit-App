package com.swen900014.orange.rideshareoz;

import android.app.Activity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by yuszy on 9/9/15.
 */
public class RideRequest
{
    private final static String JOIN_REQUEST_URL = "http://144.6.226.237/ride/request";
    private Ride ride;

    private String lat = "";
    private String lon = "";

    public RideRequest(Ride ride)
    {
        this.ride = ride;
    }

    public void sendRequest(final Activity activity, String address)
    {
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

                        StringRequest joinRequest = new StringRequest(Request.Method.POST,
                                JOIN_REQUEST_URL, new Response.Listener<String>()
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
                                params.put("username", "sangzhouyang@student.unimelb.edu.au");
                                //params.put("user_id", "55ebd381e685d1378386a759");
                                params.put("ride_id", "55e7ed577ea19c92ac2d0911");
                                params.put("p_lat", lat);
                                params.put("p_lon", lon);

                                return params;
                            }
                        };

                        MyRequest.getInstance(activity).addToRequestQueue(joinRequest);

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

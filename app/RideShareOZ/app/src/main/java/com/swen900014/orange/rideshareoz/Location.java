package com.swen900014.orange.rideshareoz;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
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
public class Location
{
    private String lat;
    private String lon;

    public Location()
    {
        lat = "";lat = new String("");
        lon = "";
    }

    public void getLocation(Activity activity)//FragmentActivity
    {
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = "https://maps.googleapis.com/maps/api/geocode/json?" +
                "address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&" +
                "key=AIzaSyBhEI1X-PMslBS2Ggq35bOncxT05mWO9bs";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    public void onResponse(String response)
                    {
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

                            lat = "sssssss111111111111";
                            lon = "zzzzzzzzz22222222";
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        //System.out.println("Test output lat: " + lat);
                    }
                },
                new Response.ErrorListener()
                {
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        System.out.println("it doesn't work");

                        return;
                    }
                });
        System.out.println("Test output lat: " + lat);
        queue.add(stringRequest);
    }

    public String getLat()
    {
        return lat;
    }

    public String getLon()
    {
        return lon;
    }
}

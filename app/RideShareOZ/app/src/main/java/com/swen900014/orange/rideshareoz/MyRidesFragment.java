package com.swen900014.orange.rideshareoz;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.swen900014.orange.rideshareoz.Resources.*;


/**
 * Created by George on 6/09/2015.
 * Display a single ride info in the Myrides activity
 */

public class MyRidesFragment extends Fragment
{
    private RidesAdaptor mRidesAdapter;
    private boolean isSearchResults = false;
    private Intent intent;
    private Activity thisActivity;

    public MyRidesFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

        thisActivity = this.getActivity();

         /* check if it offer or find  */
        intent = this.getActivity().getIntent();
        if (intent != null && intent.hasExtra("type"))
        {
            String type = intent.getStringExtra("type");
            if (type.equals("find"))
            {
                isSearchResults = true;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        //inflater.inflate(R.menu.myridesfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();

        if (id == R.id.action_refresh)
        {
            FetchRidesTask ridesTask = new FetchRidesTask();
            if (isSearchResults)
            {
                //TODO: start task to get serch results
                //search parameters will be taken from the intent
            }
            else
            {
                //TODO: use /user/getRides (POST request)
                ridesTask.execute(GETALL_RIDE_URL);
            }

            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        // Create some dummy data for the ListView.  Here's a sample weekly forecast
        /*Ride[] data = {
                new Ride(Ride.RideState.VIEWING),
                new Ride(Ride.RideState.JOINED),
                new Ride(Ride.RideState.OFFERING),
                new Ride(Ride.RideState.VIEWING),
                new Ride(Ride.RideState.OFFERING),
                new Ride(Ride.RideState.JOINED),
                new Ride(Ride.RideState.OFFERING),
                new Ride(Ride.RideState.JOINED),
                new Ride(Ride.RideState.VIEWING),
                new Ride(Ride.RideState.OFFERING)
        };*/
        //List<Ride> currentRides = new ArrayList<Ride>(Arrays.asList(data));
        List<Ride> currentRides = new ArrayList<>();

        // Now that we have some dummy  data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy data) and
        // use it to populate the ListView it's attached to.
        mRidesAdapter = new RidesAdaptor(getActivity(), (ArrayList<Ride>) currentRides);

        /* ignore the test data and load the actual data from server */
        if (isSearchResults)
        {
            //TODO: start task to get serch results
            sendSearchRequest();
        }
        else
        {
            sendGetRidesRequest();
        }

        View rootView = inflater.inflate(R.layout.fragment_myrides, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_myrides);
        listView.setAdapter(mRidesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                Intent intent;
                Ride selectedRide = mRidesAdapter.getItem(position);
                if (selectedRide.getRideState().equals(Ride.RideState.OFFERING))
                {
                    intent = new Intent(getActivity(), DriverViewRideActivity.class);
                }
                else
                {
                    intent = new Intent(getActivity(), PassViewRideActivity.class);
                }

                intent.putExtra("SelectedRide", selectedRide);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void sendGetRidesRequest()
    {
        StringRequest getRidesRequest = new StringRequest(Request.Method.POST,
                GETUSER_RELAVENT_RIDE_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                System.out.println("response: " + s);

                storeRides(s);
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

                params.put("username", User.getCurrentUser().getUsername());

                return params;
            }
        };

        MyRequest.getInstance(thisActivity).addToRequestQueue(getRidesRequest);
    }

    public void sendSearchRequest()
    {
        //search parameters will be taken from the intent
        String url = SEARCH_RIDE_URL;

        url += "s_lat=" + intent.getStringExtra("s_lat") + "&";
        url += "s_lon=" + intent.getStringExtra("s_lon") + "&";
        url += "e_lat=" + intent.getStringExtra("e_lat") + "&";
        url += "e_lon=" + intent.getStringExtra("e_lon") + "&";
        url += "arrival_time=" + intent.getStringExtra("arrival_time") + "&";
        url += "group_id=" + intent.getStringExtra("group_id");

        StringRequest searchRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                System.out.println("response: " + s);

                storeRides(s);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                volleyError.printStackTrace();

                System.out.println("Sending post failed!");
            }
        });

        MyRequest.getInstance(thisActivity).addToRequestQueue(searchRequest);
    }
    
    private void storeRides(String response)
    {
        if (response != null)
        {
            try
            {
                ArrayList<Ride> serverRides = Ride.fromJson(new JSONArray(response), isSearchResults);
                mRidesAdapter.clear();
                for (Ride listItemRide : serverRides)
                {
                    mRidesAdapter.add(listItemRide);
                }
            } catch (JSONException e)
            {
                e.printStackTrace();
            }

            // New data is back from the server.  Hooray!
        }
    }
}

package com.swen900014.orange.rideshareoz;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by uidu9665 on 6/09/2015.
 */

public class MyRidesFragment extends Fragment {

    private RidesAdaptor mRidesAdapter;

    private Bundle savedInstanceState;
    public MyRidesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        //if (savedInstanceState == null) {
            inflater.inflate(R.menu.myridesfragment, menu);
        //}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            FetchRidesTask weatherTask = new FetchRidesTask();
            weatherTask.execute("94043");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create some dummy data for the ListView.  Here's a sample weekly forecast
        Ride[] data = {new Ride(),
                new Ride(),
                new Ride(),
                new Ride(),
                new Ride(),
                new Ride(),
                new Ride(),
                new Ride(),
                new Ride(),
                new Ride()
                /*"Join 6/23 - UniMelb - 3:00 PM",
                "Offer 6/24 - UniMelb - 21/8",
                "Offer 6/25 - Melb. Cup - 22/17",
                "Pending 6/26 - AFL Final - 18/11",
                "Join 6/27 - Tennis - 21/10",
                "Join 6/28 - City - 23/18",
                "Join 6/29 - UniMelb - 20/7"*/
        };
        List<Ride> currentRides = new ArrayList<Ride>(Arrays.asList(data));

        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mRidesAdapter = new RidesAdaptor(getActivity(), (ArrayList<Ride>)currentRides);
                /*new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_ride, // The name of the layout ID.
                        R.id.list_item_ride_textview, // The ID of the textview to populate.
                        currentRides);*/

        View rootView = inflater.inflate(R.layout.fragment_myrides, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_myrides);
        listView.setAdapter(mRidesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent;
                Ride selectedRide = mRidesAdapter.getItem(position);
                if (selectedRide.getRideState().equals(Ride.RideState.OFFERING)){
                    intent = new Intent(getActivity(), DriverViewRideActivity.class);
                }else{
                    intent = new Intent(getActivity(), PassViewRideActivity.class);
                }

                intent.putExtra("SelectedRide", selectedRide);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public class FetchRidesTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchRidesTask.class.getSimpleName();

        /* The date/time conversion code is going to be moved outside the asynctask later,
         * so for convenience we're breaking it out into its own method now.
         */
        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getRidesDataFromJson(String ridesJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_DRIVER = "driver";
            final String OWM_USERNAME = "username";
            final String OWM_TIME = "start_time";
            final String OWM_START = "start_point";
            final String OWM_END = "end_point";
            final String OWM_PASSENGERS = "passengers";

            JSONObject ridesJson = new JSONObject(ridesJsonStr);
            JSONArray ridesArray = ridesJson.getJSONArray(OWM_LIST);


            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            String[] resultStrs = new String[ridesArray.length()];
            for(int i = 0; i < ridesArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                //String day;
                //String description;
                //String highAndLow;

                // Get the JSON object representing the day
                JSONObject ride = ridesArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                //long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                //dateTime = dayTime.setJulianDay(julianStartDay+i);
                //day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                //JSONObject weatherObject = ride.getJSONArray(OWM_WEATHER).getJSONObject(0);
                //description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                //JSONObject temperatureObject = ride.getJSONObject(OWM_TEMPERATURE);
                //double high = temperatureObject.getDouble(OWM_MAX);
                //double low = temperatureObject.getDouble(OWM_MIN);

                //highAndLow = formatHighLows(high, low);

                String driver = ride.getJSONObject(OWM_DRIVER).getString(OWM_USERNAME);
                String time = (String) ride.get(OWM_TIME);
                String start = "";//(String)ride.get(OWM_START);
                resultStrs[i] = driver + " - " + start + " - " + time;
            }
            return resultStrs;

        }
        @Override
        protected String[] doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String ridesJsonStr = null;

            //String format = "json";
            //String units = "metric";
            int numDays = 7;

            try {
                // Construct the URL for the Rides query
                final String RIDES_BASE_URL = "http://144.6.226.237/ride/getall";//R.string.all_rides_url;
                /*final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";

                Uri builtUri = Uri.parse(RIDES_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .build();*/

                URL url = new URL(RIDES_BASE_URL);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                ridesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getRidesDataFromJson(ridesJsonStr, numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mRidesAdapter.clear();
                for(String dayForecastStr : result) {
                    //mRidesAdapter.add(dayForecastStr);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }
}

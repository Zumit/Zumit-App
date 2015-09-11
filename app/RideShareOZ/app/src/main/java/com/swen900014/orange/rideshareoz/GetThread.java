package com.swen900014.orange.rideshareoz;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by uidu9665 on 11/09/2015.
 */
public class GetThread extends AsyncTask<String,Void, String> {
    final String LogTag = "GetThread";

    String result = null;
    boolean running = true;

    public String getResult() {
        return result;
    }

    public void waitFor() {
        while (running) {
        }
    }


    @Override
    protected String doInBackground(String... params) {




        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String ridesJason = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            String urlAddress = null;

            urlAddress = params[0];

            URL url = new URL(urlAddress);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                Log.e(LogTag, "input Stream empty");
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
                Log.e(LogTag,"Buffer empty");
                return null;
            }
            ridesJason = buffer.toString();
            //Log.e(LogTag, ridesJason );
        } catch (IOException e) {
            Log.e(LogTag, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LogTag , "Error closing stream", e);
                }
            }
        }

        return ridesJason;

    }

    @Override
    protected void onPostExecute(String result) {

        if (result != null) {
            // Successfully retrieved rides

            Log.d(LogTag, result);
            this.result = result;
        } else {
            // No Rides :(
            Log.e(LogTag, "error");
        }

        running = false;
    }
}

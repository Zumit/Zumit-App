package com.example.sangzhuoyangyu.sendjsonpost;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

public class GetRequest extends AppCompatActivity
{
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_request);

        textView = (TextView) findViewById(R.id.editText);

        Intent intent = getIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_get_request, menu);
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

    public void sendGetRequest(View v)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://144.6.226.237/testjson?Yu=say_something";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    public void onResponse(String response) {
                        String output = "";

                        try
                        {
                            output = readJsonStream(new ByteArrayInputStream(
                                    response.getBytes()));
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }

                        textView.setText(output);
                    }
                },
                new Response.ErrorListener()
                {
                    public void onErrorResponse(VolleyError volleyError) {
                        textView.setText("It doesn't work");
                    }
                });

        queue.add(stringRequest);
    }

    public String readJsonStream(InputStream in) throws IOException
    {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));

        try
        {
            return readMessage(reader);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return "";
    }

    public String readMessage(JsonReader reader) throws IOException
    {
        String name = "";
        String message = "";

        reader.beginObject();

        while (reader.hasNext())
        {
            name = reader.nextName();
            message = reader.nextString();
        }

        reader.endObject();
        reader.close();

        return name + ": " + message;
    }
}

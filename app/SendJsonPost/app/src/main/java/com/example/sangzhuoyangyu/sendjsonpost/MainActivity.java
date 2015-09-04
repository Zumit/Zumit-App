package com.example.sangzhuoyangyu.sendjsonpost;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity
{
    private static TextView tv;
    Button pb;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.sentMessage);
        pb = (Button) findViewById(R.id.postButton);
    }

    public void sendPostRequest(View view)
    {
        Intent intent = new Intent(this, GetRequest.class);

        new PostThread().start();

        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
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

    public static class PostThread extends Thread
    {
        public void run() {
            System.out.println("Hello from a thread!");

            String message = tv.getText().toString();

            excutePost("http://144.6.226.237/test", "token=asdf&name=Yu&content=" + message);
        }

        public static String excutePost(String targetURL, String urlParameters)
        {
            URL url;
            HttpURLConnection connection = null;

            try {
                //Create connection
                url = new URL(targetURL);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", "" +
                        Integer.toString(urlParameters.getBytes().length));
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(0);

                //Send request
                DataOutputStream wr = new DataOutputStream (
                        connection.getOutputStream ());
                wr.writeBytes (urlParameters);
                wr.flush ();
                wr.close ();

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();

                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();

                System.out.println("response: " + response.toString());

                return response.toString();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
            finally
            {
                if(connection != null) {
                    connection.disconnect();
                }
            }
        }
    }
}
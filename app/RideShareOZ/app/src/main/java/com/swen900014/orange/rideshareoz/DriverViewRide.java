package com.swen900014.orange.rideshareoz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import static com.swen900014.orange.rideshareoz.User.UserType;


public class DriverViewRide extends AppCompatActivity
{
    private TextView startLabel;
    private TextView endLabel;
    private TextView timeLabel;
    private TextView passText;

    // Dummy data
    User dummyUser = new User("user1", "email", 123, 0, UserType.DRIVER);
    Ride dummyRide = new Ride("start", "end", "6/09/2015", dummyUser, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_view_ride);

        startLabel = (TextView) findViewById(R.id.startText);
        endLabel = (TextView) findViewById(R.id.endText);
        timeLabel = (TextView) findViewById(R.id.timeText);
        passText = (TextView) findViewById(R.id.passList);

        startLabel.setText(dummyRide.getStart());
        endLabel.setText(dummyRide.getEnd());
        timeLabel.setText(dummyRide.getTime());
        passText.setText(dummyUser.getUsername() + ", phone: " + dummyUser.getPhone() + "\n");

        getIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pass_view_ride, menu);
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

    // Butten events of sending a request for joining a ride
    public void accept(View view)
    {

    }

    public void reject(View view)
    {

    }
}

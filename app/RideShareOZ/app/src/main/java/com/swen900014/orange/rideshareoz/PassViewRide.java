package com.swen900014.orange.rideshareoz;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONObject;


public class PassViewRide extends FragmentActivity
        implements GoogleApiClient.OnConnectionFailedListener
{
    private final static String TAG = "Passenger View Ride";
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutoCompleteAdapter adapter;
    private RideRequest rideRequest;

    private TextView startLabel;
    private TextView endLabel;
    private TextView timeLabel;

    private TextView passText;
    private TextView pickUpLocText;
    private TextView timeInputText;

    // Dummy data
    Driver dummyDriver = new Driver("Driver", "email", 123, 0);
    Passenger dummyPassenger = new Passenger("Pass", "email", 123, 0);
    Ride dummyRide = new Ride("start", "end", "6/09/2015", dummyDriver, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_view_ride);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();
        //mGoogleApiClient.connect();

        rideRequest = new RideRequest(dummyRide);

        startLabel = (TextView) findViewById(R.id.startText);
        endLabel = (TextView) findViewById(R.id.endText);
        timeLabel = (TextView) findViewById(R.id.timeText);
        passText = (TextView) findViewById(R.id.passList);

        timeInputText = (TextView) findViewById(R.id.timeInputText);

        adapter = new PlaceAutoCompleteAdapter(this,
                android.R.layout.simple_expandable_list_item_1, mGoogleApiClient,
                BOUNDS_GREATER_SYDNEY, null);

        AutoCompleteTextView pickUpLocText = (AutoCompleteTextView)
                findViewById(R.id.pickUpLocText);
        pickUpLocText.setOnItemClickListener(mAutoCompleteClickListener);
        pickUpLocText.setAdapter(adapter);


        startLabel.setText(dummyRide.getStart());
        endLabel.setText(dummyRide.getEnd());
        timeLabel.setText(dummyRide.getTime());
        passText.setText(dummyPassenger.getName() + ", phone: " + dummyPassenger.getPhone() + "\n");

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

    private AdapterView.OnItemClickListener mAutoCompleteClickListener
            = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceAutoCompleteAdapter.PlaceAutoComplete place = adapter.getItem(position);
            final String placeId = place.placeId;

            Log.i(TAG, "Autocomplete place selected: " + place.description);

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.
                    getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceCallback);

            Toast.makeText(getApplicationContext(), "clicked: " + place.description,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + place.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceCallback
            = new ResultCallback<PlaceBuffer>()
    {
        @Override
        public void onResult(PlaceBuffer places)
        {
            if (!places.getStatus().isSuccess())
            {
                Log.e(TAG, "Place query did not complete. Error : " + places.getStatus().toString());
                places.release();
                return;
            }

            final Place place = places.get(0);

            Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };

    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    // Button events of sending a request for joining a ride
    public void joinRide(View view)
    {
        rideRequest.sendRequest(this, "1600+Amphitheatre+Parkway");

        // May receive user_id from server.
    }
}
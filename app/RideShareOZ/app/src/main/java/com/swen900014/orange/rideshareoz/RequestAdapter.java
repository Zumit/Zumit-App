package com.swen900014.orange.rideshareoz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;


/**
 * Created by Sangzhuoyang Yu on 9/16/15.
 * Adapter attached to the list view displaying
 * a list of join requests driver received, which can
 * be accepted or rejected
 */
public class RequestAdapter extends ArrayAdapter<Pickup>
{
    private final Activity activity;
    private ArrayList<Pickup> data;
    private Ride ride;

    public RequestAdapter(Activity activity, ArrayList<Pickup> lift, Ride ride)
    {
        super(activity, -1, lift);
        this.activity = activity;
        this.data = ride.getWaiting();
        this.ride = ride;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View row = inflater.inflate(R.layout.list_user_request_row, parent, false);
        final Pickup lift = data.get(position);
        final User user = lift.getUser();
        //final RequestAdapter adapter = this;

        row.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(activity, UserInfoActivity.class);
                intent.putExtra("UserInfo", lift);
                intent.putExtra("Ride", ride);

                activity.startActivity(intent);
            }
        });

        TextView label = (TextView) row.findViewById(R.id.userInfo);
        label.setText("name: " + user.getUsername());

        return row;
    }
}

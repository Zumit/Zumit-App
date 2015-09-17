package com.swen900014.orange.rideshareoz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by uidu9665 on 15/09/2015.
 */
public class RidesAdaptor extends ArrayAdapter<Ride> {

    public RidesAdaptor(Context context, ArrayList<Ride> rides) {
        super(context, 0, rides);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Ride ride = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_rides_linear, parent, false);
        }
        // Lookup view for data population
        TextView tvType = (TextView) convertView.findViewById(R.id.textViewType);
        TextView tvFromTo = (TextView) convertView.findViewById(R.id.textViewFromTo);
        TextView tvRequests = (TextView) convertView.findViewById(R.id.textViewRequests);
        TextView tvJoins = (TextView) convertView.findViewById(R.id.textViewJoins);
        // Populate the data into the template view using the data object
        tvType.setText(ride.getRideState().toString());
        tvFromTo.setText(ride.getStart().getAddress() + " To " + ride.getEnd().getAddress());
        tvRequests.setText(Integer.toString(ride.getWaiting().size()));
        tvJoins.setText(Integer.toString(ride.getJoined().size()));
        // Return the completed view to render on screen
        return convertView;
    }
}

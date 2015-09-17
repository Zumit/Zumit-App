package com.swen900014.orange.rideshareoz;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.io.Serializable;

/**
 * Created by yuszy on 9/6/15.
 */
public class Ride implements Serializable
{
    private String rideId;
    private Location start;
    private Location end;
    private String arriving_time;
    private String start_time;
    private User driver;
    private int limit;      //Max number of passengers who can join

    private ArrayList<Pickup> joined;   //joined passengers
    private ArrayList<Pickup> waiting;  //passengers who is waiting
    private RideState rideState = RideState.VIEWING;

    public enum RideState
    {
        OFFERING, JOINED, VIEWING
    }

    public Ride(String start, String end, String arriving_time, User driver, int limit)
    {
        this.start = new Location(start);
        this.end = new Location(end);
        this.arriving_time = arriving_time;
        this.driver = driver;
        this.limit = limit;
        rideId = "0";
        joined = new ArrayList<Pickup>(limit);
        waiting = new ArrayList<Pickup>();
    }

    public Ride(String start, String end, String arriving_time, User driver, int limit,
                ArrayList<Pickup> joined, ArrayList<Pickup> waiting)
    {
        this.start = new Location(start);
        this.end = new Location(end);
        this.arriving_time = arriving_time;
        this.driver = driver;
        this.limit = limit;
        rideId = "0";
        this.joined = (ArrayList<Pickup>) joined.clone();
        this.waiting = (ArrayList<Pickup>) waiting.clone();
    }
    public Ride(JSONObject jsonRide){
        JSONObject tempObj;
        JSONArray tempArray;
        JSONArray tempLocationArray;
        waiting = new ArrayList<Pickup>();
        joined = new ArrayList<Pickup>();
        try {
            tempArray = jsonRide.getJSONArray("start_point");
            start = new Location(tempArray.getDouble(0),tempArray.getDouble(1));

            tempArray = jsonRide.getJSONArray("end_point");
            end = new Location(tempArray.getDouble(0),tempArray.getDouble(1));

            rideId = jsonRide.getString("_id");
            tempObj = jsonRide.getJSONObject("driver");
            driver = new User(tempObj.getString("_id"),tempObj.getString("username"));

            limit = jsonRide.getInt("seats");
            start_time = jsonRide.getString("start_time");

            /* get the list of requests */

            tempArray = jsonRide.getJSONArray("requests");
            for(int i =0; i < tempArray.length(); i++){
                tempObj = tempArray.getJSONObject(i);
                User pass = new User(tempObj.getString("$odi")/*,tempObj.getString("username")*/);
                tempLocationArray =  tempObj.getJSONArray("pickup_point");
                Location loc = new Location(tempLocationArray.getDouble(0),tempLocationArray.getDouble(1));
                waiting.add(new Pickup(pass,loc));
            }

            /* get the list of joins */
            tempArray = jsonRide.getJSONArray("passengers");
            for(int i =0; i < tempArray.length(); i++){
                tempObj = tempArray.getJSONObject(i);
                User pass = new User(tempObj.getString("$odi")/*,tempObj.getString("username")*/);
                tempLocationArray =  tempObj.getJSONArray("pickup_point");
                Location loc = new Location(tempLocationArray.getDouble(0),tempLocationArray.getDouble(1));
                joined.add(new Pickup(pass,loc));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Ride> fromJson(JSONArray ridesJsonArray){
        ArrayList<Ride> rides = new ArrayList<Ride>();
        for(int i =0; i < ridesJsonArray.length(); i++) {
            try {
                rides.add(new Ride(ridesJsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return rides;
    }



    /* Testing */
    public Ride(RideState s){
        this.start = new Location("Epping");
        this.end = new Location("UniMelb");

        this.arriving_time = "13:30:00";
        this.driver = new User("George", "george.nader@gmail.com", 0,0,User.UserType.DRIVER );
        this.limit = 4;
        rideId = "0";
        this.joined = new ArrayList<Pickup>();
        this.waiting = new ArrayList<Pickup>();
        this.rideState = s;

    }

    public boolean isDriver()
    {
        return true;
    }

    public boolean joined()
    {
        return true;
    }

    public boolean acceptJoin(Pickup lift)
    {
        if (joined.size() <= limit)
        {
            joined.add(lift);

            return true;
        }
        else
        {
            return false;
        }
    }

    public void rateDriver()
    {
        driver.rate();
    }

    public void ratePassenger(int index)
    {
        joined.get(index).getUser().rate();
    }

    public void setArrivingTime(String arriving_time)
    {
        this.arriving_time = arriving_time;
    }

    public void setStartTime(String start_time)
    {
        this.start_time = start_time;
    }

    public void setStart(Location start)
    {
        start = start;
    }

    public void setEnd(Location end)
    {
        start = end;
    }

    public void setRideId(String id)
    {
        rideId = id;
    }

    // For testing
    public void addWaiting(Pickup lift)
    {
        waiting.add(lift);
    }

    public String getArrivingTime()
    {
        return arriving_time;
    }

    public String getStartTime()
    {
        return start_time;
    }

    public Location getStart()
    {
        return start;
    }

    public Location getEnd()
    {
        return end;
    }

    public String getRideId()
    {
        return rideId;
    }

    public User getDriver()
    {
        return driver;
    }

    public RideState getRideState()
    {
        return rideState;
    }

    public ArrayList<Pickup> getJoined() {
        return joined;
    }

    public ArrayList<Pickup> getWaiting() {
        return waiting;
    }
}

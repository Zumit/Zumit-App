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
    private String id;
    private String start_point;
    private String end_point;


    private Location start;
    private Location end;
    private String arriving_time;
    private String start_time;
    private User driver;
    private int rideId;
    private int limit;      //Max number of passengers who can join

    private ArrayList<User> joined;   //joined passengers
    private ArrayList<User> waiting;  //passengers who is waiting
    private RideState rideState = RideState.VIEWING;

    public enum RideState
    {
        OFFERING, JOINED, VIEWING
    }

    public Ride(String start, String end, String arriving_time, User driver, int limit)
    {
        start_point = start;
        end_point = end;
        this.arriving_time = arriving_time;
        this.driver = driver;
        this.limit = limit;
        rideId = 0;
        joined = new ArrayList<User>(limit);
        waiting = new ArrayList<User>(limit);
    }

    public Ride(String start, String end, String arriving_time, User driver, int limit,
                ArrayList<User> joined, ArrayList<User> waiting)
    {
        start_point = start;
        end_point = end;
        this.arriving_time = arriving_time;
        this.driver = driver;
        this.limit = limit;
        rideId = 0;
        this.joined = (ArrayList<User>) joined.clone();
        this.waiting = (ArrayList<User>) waiting.clone();
    }
    public Ride(JSONObject jsonRide){
        JSONObject tempObj;
        JSONArray tempArray;
        try {
            id = jsonRide.getString("_id");
            tempObj = jsonRide.getJSONObject("driver");
            driver = new User(tempObj.getString("_id"),tempObj.getString("username"));
            tempArray = jsonRide.getJSONArray("start_point");
            start = new Location(tempArray.getDouble(0),tempArray.getDouble(1));
            tempArray = jsonRide.getJSONArray("end_point");
            end = new Location(tempArray.getDouble(0),tempArray.getDouble(1));
            limit = jsonRide.getInt("seats");
            start_time = jsonRide.getString("start_time");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* Testing */
    public Ride(RideState s){
        start_point = "Epping";
        end_point = "UniMelb";
        this.arriving_time = "13:30:00";
        this.driver = new User("George", "george.nader@gmail.com", 0,0,User.UserType.DRIVER );
        this.limit = 4;
        rideId = 0;
        this.joined = new ArrayList<User>();
        this.waiting = new ArrayList<User>();
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

    public boolean accept_request()
    {
        if (joined.size() <= limit)
        {
            // accept / reject requests

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
        joined.get(index).rate();
    }

    public void setTime(String arriving_time)
    {
        this.arriving_time = arriving_time;
    }

    public void setStart(String start)
    {
        start_point = start;
    }

    public void setEnd(String end)
    {
        end_point = end;
    }

    public void setRideId(int id)
    {
        rideId = id;
    }

    public String getTime()
    {
        return arriving_time;
    }

    public String getStart()
    {
        return start_point;
    }

    public String getEnd()
    {
        return end_point;
    }

    public int getRideId()
    {
        return rideId;
    }

    public RideState getRideState()
    {
        return rideState;
    }

    public ArrayList<User> getJoined() {
        return joined;
    }

    public ArrayList<User> getWaiting() {
        return waiting;
    }
}

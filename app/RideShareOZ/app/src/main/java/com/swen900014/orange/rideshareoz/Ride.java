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


    private Location start;
    private Location end;
    private String arriving_time;
    private String start_time;
    private User driver;
    private int rideId;
    private int limit;      //Max number of passengers who can join

    private ArrayList<Lift> joined;   //joined passengers
    private ArrayList<Lift> waiting;  //passengers who is waiting
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
        rideId = 0;
        joined = new ArrayList<Lift>(limit);
        waiting = new ArrayList<Lift>(limit);
    }

    public Ride(String start, String end, String arriving_time, User driver, int limit,
                ArrayList<Lift> joined, ArrayList<Lift> waiting)
    {
        this.start = new Location(start);
        this.end = new Location(end);
        this.arriving_time = arriving_time;
        this.driver = driver;
        this.limit = limit;
        rideId = 0;
        this.joined = (ArrayList<Lift>) joined.clone();
        this.waiting = (ArrayList<Lift>) waiting.clone();
    }
    public Ride(JSONObject jsonRide){
        JSONObject tempObj;
        JSONArray tempArray;
        JSONArray tempLocationArray;
        waiting = new ArrayList<Lift>();
        joined = new ArrayList<Lift>();
        try {
            tempArray = jsonRide.getJSONArray("start_point");
            start = new Location(tempArray.getDouble(0),tempArray.getDouble(1));

            tempArray = jsonRide.getJSONArray("end_point");
            end = new Location(tempArray.getDouble(0),tempArray.getDouble(1));

            
            id = jsonRide.getString("_id");
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
                waiting.add(new Lift(pass,loc));
            }

            /* get the list of joins */
            tempArray = jsonRide.getJSONArray("passengers");
            for(int i =0; i < tempArray.length(); i++){
                tempObj = tempArray.getJSONObject(i);
                User pass = new User(tempObj.getString("$odi")/*,tempObj.getString("username")*/);
                tempLocationArray =  tempObj.getJSONArray("pickup_point");
                Location loc = new Location(tempLocationArray.getDouble(0),tempLocationArray.getDouble(1));
                joined.add(new Lift(pass,loc));
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
        rideId = 0;
        this.joined = new ArrayList<Lift>();
        this.waiting = new ArrayList<Lift>();
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
        joined.get(index).getUser().rate();
    }

    public void setTime(String arriving_time)
    {
        this.arriving_time = arriving_time;
    }

    public void setStart(Location start)
    {
        start = start;
    }

    public void setEnd(Location end)
    {
        start = end;
    }

    public void setRideId(int id)
    {
        rideId = id;
    }

    public String getTime()
    {
        return arriving_time;
    }

    public Location getStart()
    {
        return start;
    }

    public Location getEnd()
    {
        return end;
    }

    public int getRideId()
    {
        return rideId;
    }

    public RideState getRideState()
    {
        return rideState;
    }

    public ArrayList<Lift> getJoined() {
        return joined;
    }

    public ArrayList<Lift> getWaiting() {
        return waiting;
    }
}

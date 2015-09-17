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
    private String start_point;
    private String end_point;

    private Location start;
    private Location end;
    private String arriving_time;
    private String start_time;
    private User driver;
    private int limit;      //Max number of passengers who can join

    private ArrayList<User> joined;   //joined passengers
    private ArrayList<User> waiting;  //passengers who is waiting
    private RideState rideState = RideState.JOINED;

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
        rideId = "";
        joined = new ArrayList<>(limit);
        waiting = new ArrayList<>();
    }

    public Ride(String start, String end, String arriving_time, User driver, int limit,
                ArrayList<User> joined, ArrayList<User> waiting)
    {
        start_point = start;
        end_point = end;
        this.arriving_time = arriving_time;
        this.driver = driver;
        this.limit = limit;
        rideId = "";
        this.joined = (ArrayList<User>) joined.clone();
        this.waiting = (ArrayList<User>) waiting.clone();
    }

    public Ride(JSONObject jsonRide)
    {
        JSONObject tempObj;
        JSONArray tempArray;

        try
        {
            rideId = jsonRide.getString("_id");
            tempObj = jsonRide.getJSONObject("driver");
            driver = new User(tempObj.getString("_id"),tempObj.getString("username"));
            tempArray = jsonRide.getJSONArray("start_point");
            start = new Location(tempArray.getDouble(0),tempArray.getDouble(1));
            tempArray = jsonRide.getJSONArray("end_point");
            end = new Location(tempArray.getDouble(0),tempArray.getDouble(1));
            limit = jsonRide.getInt("seats");
            start_time = jsonRide.getString("start_time");
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /* Testing */
    public Ride(RideState s)
    {
        start_point = "Epping";
        end_point = "UniMelb";
        this.arriving_time = "13:30:00";
        this.driver = new User("George", "george.nader@gmail.com", 0,0,User.UserType.DRIVER );
        this.limit = 4;
        rideId = "";
        this.joined = new ArrayList<>();
        this.waiting = new ArrayList<>();
        this.rideState = s;
    }

    public boolean isDriver(User user)
    {
        if (user.getUserType() == User.UserType.DRIVER)
        {
            return true;
        }

        return false;
    }

    public boolean joined()
    {
        return true;
    }

    public boolean acceptJoin(User pass)
    {
        if (joined.size() <= limit)
        {
            joined.add(pass);
            waiting.remove(pass);

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

    public void setRideId(String id)
    {
        rideId = id;
    }

    public void addJoined(User pass)
    {
        if (joined.size() <= limit)
        {
            joined.add(pass);
        }
    }

    public void addWaiting(User pass)
    {
        waiting.add(pass);
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

    public String getRideId()
    {
        return rideId;
    }

    public User getDriver()
    {
        return driver;
    }

    public ArrayList<User> getJoined()
    {
        return joined;
    }

    public ArrayList<User> getWaiting()
    {
        return waiting;
    }

    public RideState getRideState()
    {
        return rideState;
    }
}

package com.swen900014.orange.rideshareoz;

import java.util.ArrayList;

/**
 * Created by yuszy on 9/6/15.
 */
public class Ride
{
    private String start_point;
    private String end_point;

    private Location start;
    private Location end;
    private String arriving_time;
    private User driver;
    private int rideId;
    private int limit;      //Max number of passengers who can join
    private ArrayList<User> passengers;

    public Ride(String start, String end, String arriving_time, User driver, int limit)
    {
        start_point = start;
        end_point = end;
        this.arriving_time = arriving_time;
        this.driver = driver;
        this.limit = limit;
        rideId = 0;
        passengers = new ArrayList<User>(limit);
    }

    public boolean accept_request(User new_pass)
    {
        if (passengers.size() <= limit)
        {
            passengers.add(new_pass);

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
        passengers.get(index).rate();
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
}

package com.swen900014.orange.rideshareoz;

import java.util.ArrayList;

/**
 * Created by yuszy on 9/6/15.
 */
public class Ride
{
    private String start_point;
    private String end_point;
    private String time;
    private Driver driver;
    private int rideId;
    private int limit;      //Max number of passengers who can join
    private ArrayList<Passenger> passengers;

    public Ride(String start, String end, String time, Driver driver, int limit)
    {
        start_point = start;
        end_point = end;
        this.time = time;
        this.driver = driver;
        this.limit = limit;
        rideId = 0;
        passengers = new ArrayList<Passenger>(limit);
    }

    public boolean accept_request(Passenger new_pass)
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

    public void setTime(String time)
    {
        this.time = time;
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
        return time;
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

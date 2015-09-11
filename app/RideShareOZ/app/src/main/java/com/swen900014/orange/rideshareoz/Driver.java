package com.swen900014.orange.rideshareoz;

/**
 * Created by yuszy on 9/6/15.
 */
public class Driver
{
    private String name;
    private String email;
    private int phone;
    private int credit;

    public Driver(String name, String email, int phone, int credit)
    {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.credit = credit;
    }

    public void offer_ride(String start, String end, String time, int limit)
    {
        Ride ride = new Ride(start, end, time, this, limit);


    }

    public void rate()
    {

    }

    public String getName()
    {
        return name;
    }

    public String getEmail()
    {
        return email;
    }

    public int getPhone()
    {
        return phone;
    }

    public int getCredit()
    {
        return credit;
    }
}

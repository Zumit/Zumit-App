package com.swen900014.orange.rideshareoz;

/**
 * Created by yuszy on 9/6/15.
 */
public class Passenger
{
    private String name;
    private String email;
    private int phone;
    private int credit;

    public Passenger(String name, String email, int phone, int credit)
    {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.credit = credit;
    }

    public void sendRequest()
    {

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

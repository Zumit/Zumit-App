package com.swen900014.orange.rideshareoz;

/**
 * Created by yuszy on 9/11/15.
 */
public class User
{
    private String name;
    private String email;
    private int phone;
    private int credit;
    private UserType userType;

    public static enum UserType
    {
        PASSENGER, DRIVER
    }

    public User(String name, String email, int phone, int credit, UserType userType)
    {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.credit = credit;
        this.userType = userType;
    }

    public void rate()
    {

    }

    public String getUsername()
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
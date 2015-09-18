package com.swen900014.orange.rideshareoz;

import java.io.Serializable;

/**
 * Created by yuszy on 9/11/15.
 */
public class User implements Serializable
{
    private String id;
    private String name;
    private String email;
    private int phone;
    private int credit;
    private UserType userType;

    private static String currentUserEmail;

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
    public User(String id, String username ) {
        this.id = id;
        this.name = username;
    }

    public User(String id) {
        this.id = id;
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

package com.swen900014.orange.rideshareoz;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Sangzhuoyang Yu on 9/11/15.
 * Encapsulate user data, user can be either a
 * driver or passenger depending on whether the
 * ride is offed by them
 */
public class User implements Serializable
{
    private String name;
    private String email;
    private int phone;
    private int credit;

    private static HashMap<String, User> allUsers = new HashMap<>();

    private static User currentUser;

    public User(String name, String email, int phone, int credit)
    {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.credit = credit;
    }

    public User(String username)
    {
        this.name = username;
        allUsers.put(username, this);
    }

    public static User GetUser(String username)
    {
        if (allUsers.containsKey(username))
        {
            return allUsers.get(username);
        }
        else
        {
            return new User(username);
        }
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

    /*public UserType getUserType()
    {
        return userType;
    }*/

    public int getPhone()
    {
        return phone;
    }

    public int getCredit()
    {
        return credit;
    }

    public static void setCurrentUser(User currentUser)
    {
        User.currentUser = currentUser;
    }

    public static User getCurrentUser()
    {
        return currentUser;
    }
}
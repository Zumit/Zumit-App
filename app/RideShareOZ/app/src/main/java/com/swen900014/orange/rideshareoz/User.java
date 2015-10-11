package com.swen900014.orange.rideshareoz;

import android.app.Activity;

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
    private String phone;
    private int credit;

    private static HashMap<String, User> allUsers = new HashMap<String, User>();
    private static User currentUser;
    private static String token;

    public User(String name, String email, String phone, int credit)
    {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.credit = credit;
    }

    public User(String username)
    {
        this.name = username;
    }

    public static User getUser(String name) {
        return allUsers.get(name);
    }

    public static User addUserIfNotExist(String username, String email, String phone, int credit){
        if(allUsers.containsKey(username)){
            return allUsers.get(username);
        }else{
            User newUser = new User(username, email, phone, credit);
            allUsers.put(newUser.name,newUser);
            return newUser;
        }
    }

    public void rate(int score)
    {
        credit += score;
    }

    public String getUsername()
    {
        return name;
    }

    public String getEmail()
    {
        return email;
    }

    public String getPhone()
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

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public static void updateToken(Activity activity)
    {
        token = MainActivity.getAuthToken(activity.getApplicationContext());
    }

    public static String getToken()
    {
        return token;
    }
}

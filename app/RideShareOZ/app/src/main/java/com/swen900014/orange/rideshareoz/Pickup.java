package com.swen900014.orange.rideshareoz;


/**
 * Created by uidu9665 on 16/09/2015.
 */
public class Pickup
{
    private User user;
    private Location location;

    public Pickup(User user, Location location)
    {
        this.user = user;
        this.location = location;
    }

    public Location getLocation()
    {
        return location;
    }

    public User getUser()
    {
        return user;
    }
}

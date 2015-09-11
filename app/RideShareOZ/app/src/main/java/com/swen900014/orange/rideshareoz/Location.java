package com.swen900014.orange.rideshareoz;

/**
 * Created by yuszy on 9/11/15.
 */
public class Location
{
    private String lat;
    private String lon;

    public Location(String lat, String lon)
    {
        this.lat = lat;
        this.lon = lon;
    }

    public void setLat(String lat)
    {
        this.lat = lat;
    }

    public void setLon(String lon)
    {
        this.lon = lon;
    }

    public String getLat()
    {
        return lat;
    }

    public String getLon()
    {
        return lon;
    }
}

package com.swen900014.orange.rideshareoz;

import java.io.Serializable;

/**
 * Created by yuszy on 9/11/15.
 */
public class Location implements Serializable
{
    private Double lat;
    private Double lon;
    private String address = "dummy";



    private String suburb;

    public Location(Double lat, Double lon, String address)
    {
        this.lat = lat;
        this.lon = lon;
        this.address = address;
    }

    public Location(Double lat, Double lon)
    {
        this.lat = lat;
        this.lon = lon;
    }

    public Location( String address)
    {
        this.address = address;
    }

    public void setLat(Double lat)
    {
        this.lat = lat;
    }

    public void setLon(Double lon)
    {
        this.lon = lon;
    }

    public Double getLat()
    {
        return lat;
    }

    public Double getLon()
    {
        return lon;
    }

    public String getSuburb() {
        return suburb;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }
}


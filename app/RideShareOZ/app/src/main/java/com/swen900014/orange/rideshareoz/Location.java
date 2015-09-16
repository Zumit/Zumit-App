package com.swen900014.orange.rideshareoz;

/**
 * Created by yuszy on 9/11/15.
 */
public class Location
{
    private Double lat;
    private Double lon;
    private String address;



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
}


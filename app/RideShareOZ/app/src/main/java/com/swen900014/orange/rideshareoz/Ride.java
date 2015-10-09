package com.swen900014.orange.rideshareoz;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.io.Serializable;


/**
 * Created by George & Sangzhuoyang Yu on 9/6/15.
 * Encapsulate ride data, including all info needed
 * for a ride, matching the ride data stored on server
 */
public class Ride implements Serializable
{
    private String rideId;
    private Location start;
    private Location end;
    private Group group;
    private String arriving_time;
    private String start_time;
    private User driver;
    private int seats;      // Max number of passengers who can join

    private ArrayList<Pickup> joined;   //joined passengers
    private ArrayList<Pickup> waiting;  //passengers who is waiting
    private RideState rideState = RideState.NEW;
    private RideRegularity rideRegularity = RideRegularity.ONCE;

    public enum RideRegularity implements Serializable
    {
        ONCE, DAILY, WEEKLY
    }

    public enum RideState implements Serializable
    {
        OFFERING, JOINED, VIEWING, NEW, PASSED
    }

    public Ride(String start, String end, String arriving_time, User driver, int limit)
    {
        this.start = new Location(start);
        this.end = new Location(end);
        this.arriving_time = arriving_time;
        this.driver = driver;
        this.seats = limit;
        rideId = "0";
        joined = new ArrayList<>(limit);
        waiting = new ArrayList<>();
    }

    public Ride(String start, String end, String arriving_time, User driver, int limit,
                ArrayList<Pickup> joined, ArrayList<Pickup> waiting)
    {
        this.start = new Location(start);
        this.end = new Location(end);
        this.arriving_time = arriving_time;
        this.driver = driver;
        this.seats = limit;
        rideId = "0";
        this.joined = (ArrayList<Pickup>) joined.clone();
        this.waiting = (ArrayList<Pickup>) waiting.clone();
    }

    public Ride(JSONObject jsonRide)
    {
        JSONObject tempObj;
        JSONArray tempArray;
        JSONArray tempLocationArray;
        waiting = new ArrayList<>();
        joined = new ArrayList<>();

        // Parse json data received and convert them into
        // a Ride object
        try
        {
            // Get start and end address
            String startAddress = jsonRide.getString("start_add");
            String endAddress = jsonRide.getString("destination");

            // Get start lat and lon
            tempArray = jsonRide.getJSONArray("start_point");
            start = new Location(tempArray.getDouble(0), tempArray.getDouble(1), startAddress);

            // Get end lat and lon
            tempArray = jsonRide.getJSONArray("end_point");
            end = new Location(tempArray.getDouble(0), tempArray.getDouble(1), endAddress);

            // Get ride id
            rideId = jsonRide.getString("_id");

            // Get driver info
            tempObj = jsonRide.getJSONObject("driver");
            driver = User.addUserIfNotExist(tempObj.getString("username"), tempObj.getString("username"), tempObj.getString("phone"), 0);

            // Get seat number, start time and arrival time
            seats = jsonRide.getInt("seats");
            arriving_time = jsonRide.getString("arrival_time");
            start_time = jsonRide.getString("start_time");

            /* get the list of requests */

            tempArray = jsonRide.getJSONArray("requests");

            for (int i = 0; i < tempArray.length(); i++)
            {
                tempObj = tempArray.getJSONObject(i);
                JSONObject requestingPassObj = tempObj.getJSONObject("user");
                String username = requestingPassObj.getString("username");

                User pass = User.addUserIfNotExist(username, tempObj.getString("username"), tempObj.getString("phone"), 0);

                //TODO: optimize this using object comparison
                if (User.getCurrentUser().getUsername().equals(username))
                {
                    this.rideState = RideState.VIEWING;
                }
                tempLocationArray = tempObj.getJSONArray("pickup_point");
                Location loc = new Location(tempLocationArray.getDouble(0), tempLocationArray.getDouble(1));
                loc.setAddress(tempObj.getString("pickup_add"));

                waiting.add(new Pickup(pass, loc));
            }

            /* get the list of joins */
            tempArray = jsonRide.getJSONArray("passengers");

            if (tempArray != null)
            {
                for (int i = 0; i < tempArray.length(); i++)
                {
                    tempObj = tempArray.getJSONObject(i);
                    JSONObject joinedPassObj = tempObj.getJSONObject("user");
                    String username = joinedPassObj.getString("username");
                    User pass = User.addUserIfNotExist(username, username, joinedPassObj.getString("phone"), 0);

                    //TODO: optimize this using object comparison
                    if (User.getCurrentUser() == pass)
                    {
                        this.rideState = RideState.JOINED;
                    }

                    tempLocationArray = tempObj.getJSONArray("pickup_point");
                    Location loc = new Location(tempLocationArray.getDouble(0), tempLocationArray.getDouble(1));
                    loc.setAddress(tempObj.getString("pickup_add"));
                    joined.add(new Pickup(pass, loc));
                }
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        } finally
        {
            //TODO: optimize this using object comparison
            try {
                if (this.getDriver() == User.getCurrentUser() ) {
                /* add to the offering list*/
                    this.rideState = RideState.OFFERING;
                }
            }catch (NullPointerException e){
                Log.e("RideParse:", this.getStart().getAddress());
            }
        }
    }

    // Get a json array of all rides data received from server,
    // and convert it to an ArrayList of Ride objects
    public static ArrayList<Ride> fromJson(JSONArray ridesJsonArray, boolean isSearchResults)
    {
        ArrayList<Ride> rides = new ArrayList<>();
        for (int i = 0; i < ridesJsonArray.length(); i++)
        {
            try
            {
                Ride nRide = new Ride(ridesJsonArray.getJSONObject(i));
                if (isSearchResults || (nRide.rideState != RideState.NEW))
                {
                    rides.add(nRide);
                }
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return rides;
    }

    // For Testing without receiving server data
    public Ride(RideState s)
    {
        this.start = new Location("Epping");
        this.end = new Location("UniMelb");

        this.arriving_time = "13:30:00";
        this.driver = new User("George", "george.nader@gmail.com", "000", 0);
        this.seats = 4;
        rideId = "0";
        this.joined = new ArrayList<>();
        this.waiting = new ArrayList<>();
        this.rideState = s;
    }

    public boolean isDriver()
    {
        return true;
    }

    public boolean acceptJoin(Pickup lift)
    {
        for (Pickup pickup : waiting)
        {
            if (pickup.getUser().getUsername().equals(lift.getUser().getUsername()))
            {
                joined.add(lift);
                waiting.remove(pickup);

                return true;
            }
        }

        System.out.println("accept fails");
        return false;
    }

    public boolean rejectJoin(Pickup lift)
    {
        for (Pickup pickup : waiting)
        {
            if (pickup.getUser().getUsername().equals(lift.getUser().getUsername()))
            {
                waiting.remove(pickup);

                return true;
            }
        }

        System.out.println("reject fails");
        return false;
    }

    public boolean hasPass(User pass)
    {
        for (Pickup pick : joined)
        {
            if (pass.getUsername() == pick.getUser().getUsername())
            {
                return true;
            }
        }

        return false;
    }

    public boolean hasRequest(User request)
    {
        for (Pickup pick : waiting)
        {
            if (request.getUsername().equals(pick.getUser().getUsername()))
            {
                return true;
            }
        }

        return false;
    }

    public boolean isDriverRated()
    {
        boolean driverRated = false;

        for (Pickup pickup : joined)
        {
            if (pickup.getUser().getUsername() == User.getCurrentUser().getUsername())
            {
                driverRated = pickup.isDriverRated();
            }
        }

        return driverRated;
    }

    public boolean isPassRated(String passName)
    {
        boolean isPassRated = false;

        for (Pickup pickup : joined)
        {
            if (pickup.getUser().getUsername() == passName)
            {
                isPassRated = pickup.isRatedByDriver();
            }
        }

        return isPassRated;
    }

    public void setArrivingTime(String arriving_time)
    {
        this.arriving_time = arriving_time;
    }

    public void setStartTime(String start_time)
    {
        this.start_time = start_time;
    }

    public void setStart(Location start)
    {
        start = start;
    }

    public void setEnd(Location end)
    {
        start = end;
    }

    public String getSeats()
    {
        return Integer.toString(seats);
    }

    public void setRideId(String id)
    {
        rideId = id;
    }

    // For testing
    public void addWaiting(Pickup lift)
    {
        waiting.add(lift);
    }

    public String getArrivingTime()
    {
        return arriving_time;
    }

    public String getStartTime()
    {
        return start_time;
    }

    public Location getStart()
    {
        return start;
    }

    public Location getEnd()
    {
        return end;
    }

    public String getRideId()
    {
        return rideId;
    }

    public User getDriver()
    {
        return driver;
    }

    public Group getGroup()
    {
        return group;
    }

    public RideState getRideState()
    {
        return rideState;
    }

    public ArrayList<Pickup> getJoined()
    {
        return joined;
    }

    public ArrayList<Pickup> getWaiting()
    {
        return waiting;
    }
}

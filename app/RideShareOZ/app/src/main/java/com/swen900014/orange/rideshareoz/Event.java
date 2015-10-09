package com.swen900014.orange.rideshareoz;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sangzhuoyang Yu & George on 10/2/15.
 * Encapsulate group info.
 */
public class Event implements Serializable
{
    private String eventId;
    private String name;
    private String description;

    private Location eventLocation;
    private String start_time;
    private String end_time;

    private static HashMap<String, Event> allEvents = new HashMap<String, Event>();


    public Event(String groupId, String name, String description, Location location, String s_time, String e_time)
    {
        this.eventId = groupId;
        this.name = name;
        this.description = description;
        this.start_time = s_time;
        this.end_time = e_time;
        this.eventLocation = location;
    }

    public static Event getEvent(String Id){
        return allEvents.get(Id);
    }


    public Event(JSONObject eventJson)
    {

    }

    public String getName() {
        return name;
    }

    public static ArrayList<Event> getAllEvents(){

        ArrayList<Event> events = new ArrayList<Event>();
        events.addAll(allEvents.values());
        return events;
    }
}

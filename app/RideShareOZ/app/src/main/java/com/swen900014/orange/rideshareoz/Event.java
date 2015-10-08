package com.swen900014.orange.rideshareoz;

import org.json.JSONObject;

import java.io.Serializable;
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

    public static Event addGroupIfNotExist(String id, String name, String description, Location location, String s_time, String e_time){
        if(!allEvents.containsKey(id)){
            Event newEvent = new Event(id, name, description,location, s_time,e_time);
            allEvents.put(newEvent.eventId, newEvent);
            return newEvent;
        }else{
            return allEvents.get(id);
        }
    }

    public Event(JSONObject eventJson)
    {

    }

}

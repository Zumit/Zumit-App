package com.swen900014.orange.rideshareoz;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Sangzhuoyang Yu & George on 10/2/15.
 * Encapsulate group info.
 */
public class Group implements Serializable
{
    private String groupId;
    private String name;
    private String description;

    private GroupState groupState;

    public enum GroupState
    {
        JOINED, REQUESTING, NEW
    }

    public Group(String groupId, String name, String description)
    {
        this.groupId = groupId;
        this.name = name;
        this.description = description;

        groupState = GroupState.NEW;
    }

    public Group(JSONObject groupJson)
    {

    }

    public GroupState getGroupState()
    {
        return groupState;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }
}

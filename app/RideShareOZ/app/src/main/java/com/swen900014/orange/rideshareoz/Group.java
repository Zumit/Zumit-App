package com.swen900014.orange.rideshareoz;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Sangzhuoyang Yu & George on 10/2/15.
 * Encapsulate group info.
 */
public class Group implements Serializable
{
    private String groupId;
    private String name;
    private String description;

    private static HashMap<String, Group> allGroups = new HashMap<String, Group>();

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

    public static Group getGroup(String groupId){
        return allGroups.get(groupId);
    }

    public static Group addGroupIfNotExist(String id, String name, String description){
        if(!allGroups.containsKey(id)){
            Group newGroup = new Group(id, name, description);
            allGroups.put(newGroup.groupId, newGroup);
            return newGroup;
        }else{
            return allGroups.get(id);
        }
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

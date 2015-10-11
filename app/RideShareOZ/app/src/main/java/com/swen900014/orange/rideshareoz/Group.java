package com.swen900014.orange.rideshareoz;

import android.app.Activity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sangzhuoyang Yu & George on 10/2/15.
 * Encapsulate group info.
 */
public class Group implements Serializable
{
    private String groupId;
    private String name;
    private String description;

    public static HashMap<String, Group> myGroups = new HashMap<String, Group>();
    private static HashMap<String, Group> requestedGroups = new HashMap<String, Group>();
    private static HashMap<String, Group> allOtherGroups = new HashMap<String, Group>();

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
        return allOtherGroups.get(groupId);
    }

    public static Group addGroupIfNotExist(String id, String name, String description, GroupState state){
        if(!allOtherGroups.containsKey(id) && !myGroups.containsKey(id)){
            Group newGroup = new Group(id, name, description);
            if (state == GroupState.NEW){
                newGroup.groupState = GroupState.NEW;
                allOtherGroups.put(newGroup.groupId, newGroup);
            }else{
                newGroup.groupState = state;
                myGroups.put(newGroup.groupId, newGroup);
            }

            return newGroup;
        }else{
            if (state == GroupState.NEW){
                return allOtherGroups.get(id);
            }else{
                return myGroups.get(id);
            }
        }
    }



    public static void storeGroups(JSONArray groupsJsonArray){

        myGroups.clear();
        requestedGroups.clear();
        allOtherGroups.clear();
        for (int i = 0; i < groupsJsonArray.length(); i++){

            try {
                Group newGroup = new Group(groupsJsonArray.getJSONObject(i));
                if(newGroup.groupState == GroupState.NEW){
                    allOtherGroups.put(newGroup.groupId, newGroup);
                }else if(newGroup.groupState == GroupState.JOINED){
                    myGroups.put(newGroup.groupId, newGroup);
                }else{
                    requestedGroups.put(newGroup.groupId, newGroup);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public static ArrayList<Group> getMyGroups(){
        ArrayList<Group> allGroups = new ArrayList<Group>();
        allGroups.addAll(myGroups.values());
        return allGroups;
    }

    public static ArrayList<Group> getAllGroups(){
        ArrayList<Group> allGroups = new ArrayList<Group>();
        allGroups.addAll(myGroups.values());
        allGroups.addAll(requestedGroups.values());
        allGroups.addAll(allOtherGroups.values());
        return allGroups;
    }


    public static void loadGroups(final Activity activity){
        StringRequest getRidesRequest = new StringRequest(Request.Method.POST,
                Resources.GETALL_GROUP_URL , new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                System.out.println("response: " + s);
                try {
                    storeGroups(new JSONArray(s));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                volleyError.printStackTrace();

                System.out.println("Sending post failed!");
            }
        }){
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();

                //params.put("username", User.getCurrentUser().getUsername());
                params.put("token", MainActivity.getAuthToken(activity.getApplicationContext()));

                return params;
            }
        };

        MyRequest.getInstance(activity).addToRequestQueue(getRidesRequest);
    }

    public Group(JSONObject groupJson) throws JSONException {
        JSONObject tempObj;

        tempObj = groupJson.getJSONObject("group");
        this.groupId = tempObj.getString("_id");
        this.name = tempObj.getString("groupname");
        this.description = tempObj.getString("introduction");

        String state = groupJson.getString("state");
        if(state.equals("joined")){
            this.groupState = GroupState.JOINED;
        }else if(state.equals("request")){
            this.groupState = GroupState.REQUESTING;
        }else{
            this.groupState = GroupState.NEW;
        }

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

package com.todaytodo.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.todaytodo.util.MyDate;

public class ThingList extends ModelBase{
	private List<Thing> thingList = new ArrayList<Thing>();
	private MyDate date;
	private User user;
	
	public ThingList(){
		super();
	}
	
	public ThingList(JSONObject obj, User user) throws JSONException{
		super(obj);
		date = new MyDate(obj.getLong("date"));
		JSONArray array = obj.getJSONArray("thingList");
		for(int i = 0;i<array.length();i++){
			Thing thing = new Thing((JSONObject)array.get(i),this);
			thingList.add(thing);
		}
		this.user = user;
	}

	public List<Thing> getThingList() {
		return thingList;
	}

	public void setThingList(List<Thing> thingList) {
		this.thingList = thingList;
	}

	public MyDate getDate() {
		return date;
	}

	public void setDate(MyDate date) {
		this.date = date;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			super.toJSONObject(obj);
			obj.put("date", date.getTime());
			JSONArray array = new JSONArray();
			for(Thing thing : thingList){
				array.put(thing.toJSONObject());
			}
			obj.put("thingList", array);
			obj.put("user", user.getId());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
}

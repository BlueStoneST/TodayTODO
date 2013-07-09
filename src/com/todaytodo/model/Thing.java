package com.todaytodo.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Thing extends ModelBase{
	private String description;
	private int tomato;
	private String state;
	private ThingList thingList;
	
	public Thing(){
		super();
	}
	
	public Thing(JSONObject obj, ThingList list) throws JSONException{
		super(obj);
		description = obj.getString("description");
		tomato = obj.getInt("tomato");
		state = obj.getString("state");
		this.thingList = list;
	}
	
	//setter and getter

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getTomato() {
		return tomato;
	}

	public void setTomato(int tomato) {
		this.tomato = tomato;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public ThingList getThingList() {
		return thingList;
	}

	public void setThingList(ThingList thingList) {
		this.thingList = thingList;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			super.toJSONObject(obj);
			obj.put("description", description);
			obj.put("tomato", tomato);
			obj.put("state", state);
			obj.put("thingList", thingList.getId());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
}

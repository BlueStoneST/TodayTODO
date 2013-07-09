package com.todaytodo.model;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ModelBase {
	protected String id;
	
	public ModelBase(){
		id = UUID.randomUUID().toString();
	}
	
	public ModelBase(JSONObject obj) throws JSONException{
		id = obj.getString("id");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public abstract JSONObject toJSONObject();
	
	protected JSONObject toJSONObject(JSONObject obj) throws JSONException{
		obj.put("id", id);
		return obj;
	}
	
	public String toString(){
		return toJSONObject().toString();
	}
}

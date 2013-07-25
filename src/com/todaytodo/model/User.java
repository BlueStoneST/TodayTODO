package com.todaytodo.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class User extends ModelBase{
	private List<ThingList> thingListList = new ArrayList<ThingList>();
	private Setting setting = new Setting(this);
	private String name;
	private int tomato;
	private String photo;
	private String notebookId;
	
	//don't touch it. It's evil
	private AccessToken accessToken;
	
	public User(){
		super();
	}
	
	public User(JSONObject obj) throws JSONException{
		super(obj);
		name = obj.getString("name");
		tomato = obj.getInt("tomato");
		if(obj.has("accessToken")){
			accessToken = new AccessToken((JSONObject)obj.get("accessToken"),this);
		}
		if(obj.has("notebookId")){
			notebookId = obj.getString("notebookId");
		}
		if(obj.has("photo")){
			photo = obj.getString("photo");
		}
		if(obj.has("setting")){
			setting = new Setting((JSONObject)obj.getJSONObject("setting"),this);
		}
		JSONArray array = obj.getJSONArray("thingListList");
		for(int i = 0; i<array.length(); i++){
			thingListList.add(new ThingList((JSONObject)array.get(i),this));
		}
	}

	public List<ThingList> getThingListList() {
		return thingListList;
	}

	public void setThingListList(List<ThingList> thingListList) {
		this.thingListList = thingListList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTomato() {
		return tomato;
	}

	public void setTomato(int tomato) {
		this.tomato = tomato;
	}

	public AccessToken getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(AccessToken accessToken) {
		this.accessToken = accessToken;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getNotebookId() {
		return notebookId;
	}

	public void setNotebookId(String notebookId) {
		this.notebookId = notebookId;
	}

	public Setting getSetting() {
		return setting;
	}

	public void setSetting(Setting setting) {
		this.setting = setting;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			super.toJSONObject(obj);
			obj.put("name", name);
			obj.put("tomato", tomato);
			if(accessToken != null){
				obj.put("accessToken", accessToken.toJSONObject());
			}
			if(photo!=null){
				obj.put("photo", photo);
			}
			if(notebookId != null){
				obj.put("notebookId", notebookId);
			}
			obj.put("setting", setting.toJSONObject());
			JSONArray array = new JSONArray();
			for(ThingList thingList : thingListList){
				array.put(thingList.toJSONObject());
			}
			obj.put("thingListList", array);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
}

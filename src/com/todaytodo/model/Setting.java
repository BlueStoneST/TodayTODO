package com.todaytodo.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Setting extends ModelBase {
	protected boolean autoTip = true;
	protected boolean autoDealThing = true;
	protected boolean autoSync = true;
	protected User user;
	
	public Setting(User user){
		super();
		this.user = user;
	}
	
	public Setting(JSONObject obj, User user) throws JSONException{
		super(obj);
		this.autoTip = obj.getBoolean("autoTip");
		this.autoDealThing = obj.getBoolean("autoDealThing");
		this.autoSync = obj.getBoolean("autoSync");
		this.user = user;		
	}
	
	public boolean isAutoTip() {
		return autoTip;
	}

	public void setAutoTip(boolean autoTip) {
		this.autoTip = autoTip;
	}

	public boolean isAutoDealThing() {
		return autoDealThing;
	}

	public void setAutoDealThing(boolean autoDealThing) {
		this.autoDealThing = autoDealThing;
	}

	public boolean isAutoSync() {
		return autoSync;
	}

	public void setAutoSync(boolean autoSync) {
		this.autoSync = autoSync;
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
			obj.put("autoTip", autoTip);
			obj.put("autoDealThing", autoDealThing);
			obj.put("autoSync", autoSync);
			obj.put("user", user.getId());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

}

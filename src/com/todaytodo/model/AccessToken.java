package com.todaytodo.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.sdk.android.Oauth2AccessToken;

public class AccessToken {
	protected String token;
	protected long expiresTime;
	protected User user;
	
	public AccessToken(Oauth2AccessToken token, User user){
		this.token = token.getToken();
		this.expiresTime = token.getExpiresTime();
		this.user = user;
	}
	
	public AccessToken(JSONObject obj, User user) throws JSONException{
		token = obj.getString("token");
		expiresTime = obj.getLong("expiresTime");
		this.user = user;
	}
	
	public JSONObject toJSONObject(){
		JSONObject obj = new JSONObject();
		try {
			obj.put("token", token);
			obj.put("expiresTime", expiresTime);
			obj.put("user", user.getId());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public Oauth2AccessToken getOAuthAccessToken(){
		Oauth2AccessToken token = new Oauth2AccessToken();
		token.setToken(this.token);
		token.setExpiresTime(this.expiresTime);
		return token;
	}

}

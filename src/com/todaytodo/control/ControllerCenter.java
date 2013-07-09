package com.todaytodo.control;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

import com.todaytodo.model.AccessToken;
import com.todaytodo.model.ModelCenter;
import com.todaytodo.model.Thing;
import com.todaytodo.model.ThingList;
import com.todaytodo.util.MyDate;
import com.weibo.sdk.android.Oauth2AccessToken;

public class ControllerCenter {
	private ModelCenter modelCenter;
	private static ControllerCenter uniqueInstance;
	private Map<String, Object> session = new HashMap<String, Object>();

	private ControllerCenter() {
		modelCenter = ModelCenter.getInstance();
	}

	public static ControllerCenter getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new ControllerCenter();
		}
		return uniqueInstance;
	}

	public void insertThing(String des, int tomato, ThingList thingList) {
		Thing thing = new Thing();
		thing.setDescription(des);
		thing.setTomato(tomato);
		thing.setState("unfi");
		thingList.getThingList().add(thing);
		thing.setThingList(thingList);
		modelCenter.save();
	}

	public void deleteThing(Thing thing) {
		thing.getThingList().getThingList().remove(thing);
		modelCenter.save();
	}

	public void modifyThing(Thing thing, String des, int tomato) {
		thing.setDescription(des);
		thing.setTomato(tomato);
		modelCenter.save();
	}

	public void achieveThing(Thing thing) {
		thing.setState("fi");
		int tomato = modelCenter.getUser().getTomato();
		modelCenter.getUser().setTomato(tomato + thing.getTomato());
		modelCenter.save();
	}

	public void delayThing(Thing thing, String state, boolean createTomorrow) {
		delayThingPrivate(thing, state, createTomorrow);
		modelCenter.save();
	}

	public ThingList getThingList(int daysAfterToday) {
		List<ThingList> thingListList = modelCenter.getUser()
				.getThingListList();
		for (ThingList list : thingListList) {
			if (list.getDate().getDaysAfterToday() == daysAfterToday) {
				return list;
			}
		}
		return null;
	}

	public void redo(Thing thing) {
		if (thing.getState().equals("fi")) {
			int tomato = modelCenter.getUser().getTomato();
			modelCenter.getUser().setTomato(tomato - thing.getTomato());
		} else if (thing.getState().equals("dwoc")) {
			int tomato = modelCenter.getUser().getTomato();
			modelCenter.getUser().setTomato(tomato + thing.getTomato());
		}
		thing.setState("unfi");
		modelCenter.save();
	}

	public int daliyCheck() {
		ThingList list = getThingList(-1);
		int tomato = 0;
		if (list != null) {
			for (Thing thing : list.getThingList()) {
				if (thing.getState().equals("unfi")) {
					delayThingPrivate(thing, "dwoc", true);
				}
			}
			modelCenter.dailyRefresh();
			modelCenter.save();
		}
		return tomato;
	}

	public int getLeftTomatoToday() {
		ThingList list = getThingList(0);
		int unfinishTomato = 0;
		if (list != null) {
			for (Thing thing : list.getThingList()) {
				if (thing.getState().equals("unfi")) {
					unfinishTomato += thing.getTomato();
				}
			}
		}
		return unfinishTomato;
	}

	private void delayThingPrivate(Thing thing, String state,
			boolean createTomorrow) {
		thing.setState(state);
		if (createTomorrow) {
			Thing newThing = new Thing();
			newThing.setDescription(thing.getDescription());
			newThing.setTomato(thing.getTomato());
			newThing.setState("unfi");
			ThingList list = getThingList(thing.getThingList()
					.getDate().getDaysAfterToday()+1);
			newThing.setThingList(list);
			list.getThingList().add(newThing);
		}
		if (state.equals("dwoc")) {
			int tomato = modelCenter.getUser().getTomato();
			modelCenter.getUser().setTomato(tomato - thing.getTomato());
		}
	}

	public Map<String, Object> getSession() {
		return session;
	}
	
	public void saveToken(Oauth2AccessToken token){
		modelCenter.getUser()
			.setAccessToken(new AccessToken(token, modelCenter.getUser()));
		modelCenter.save();
	}
	
	public Oauth2AccessToken getToken(){
		AccessToken aToken = modelCenter.getUser().getAccessToken();
		if(aToken==null){
			return null;
		}
		return aToken.getOAuthAccessToken();
	}

}

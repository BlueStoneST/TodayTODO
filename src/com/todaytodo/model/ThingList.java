package com.todaytodo.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.util.IEvernote;
import com.todaytodo.util.MyDate;

public class ThingList extends ModelBase implements IEvernote{
	private List<Thing> thingList = new ArrayList<Thing>();
	private MyDate date;
	private User user;
	private String summary = "";
	private String noteId;
	private boolean sync = false;
	
	public ThingList(){
		super();
	}
	
	public ThingList(JSONObject obj, User user) throws JSONException{
		super(obj);
		date = new MyDate(obj.getLong("date"));
		if(obj.has("summary")){
			summary = obj.getString("summary");
		}
		if(obj.has("noteId")){
			noteId = obj.getString("noteId");
		}
		sync = obj.getBoolean("sync");
		JSONArray array = obj.getJSONArray("thingList");
		for(int i = 0;i<array.length();i++){
			Thing thing = new Thing((JSONObject)array.get(i),this);
			thingList.add(thing);
		}
		this.user = user;
		System.out.println("noteId when construct:"+noteId);
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

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getNoteId() {
		System.out.println("getNoteId:"+noteId);
		return noteId;
	}

	public void setNoteId(String noteId) {
		System.out.println("setNode:"+noteId);
		this.noteId = noteId;
	}

	public boolean isSync() {
		return sync;
	}

	public void setSync(boolean sync) {
		this.sync = sync;
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
			obj.put("summary", summary);
			obj.put("user", user.getId());
			if(noteId != null){
				obj.put("noteId", noteId);
			}
			obj.put("sync", sync);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

	@Override
	public String getTitile() {
		return date.toStr(false) + " 每日安排";
	}

	@Override
	public String getContent() {
		//TODO 
		StringBuilder builder = new StringBuilder();
		builder.append("<div><table>");
		int fi = 0;
		int unfi = 0;
		int dwoc = 0;
		int dwc = 0;
		for(Thing thing : thingList){
			builder.append("<tr><td>");
			if(!thing.getState().equals("fi")){
				builder.append("<en-todo/>");
			}else{
				builder.append("<en-todo checked=\"true\"/>");
			}
			builder.append(thing.getDescription())
				.append("</td><td>番茄*").append(thing.getTomato()).append("</td><td>");
			if(thing.getState().equals("fi")){
				builder.append("<span style=\"color:green\">完成</span>");
				fi += thing.getTomato();
			}else if(thing.getState().equals("unfi")){
				builder.append("<span style=\"color:gray\">未完成</span>");
				unfi += thing.getTomato();
			}else if(thing.getState().equals("dwoc")){
				builder.append("<span style=\"color:red\">因故拖延</span>");
				dwoc += thing.getTomato();
			}else if(thing.getState().equals("dwc")){
				builder.append("<span style=\"color:red\">无故拖延</span>");
				dwc += thing.getTomato();
			}
			builder.append("</td></tr>");
		}
		int total = fi + unfi + dwoc +dwc;
		builder.append("</table></div><hr/><div><h3>完成情况</h3>")
			.append("<table><tr><td>总计番茄</td><td>完成番茄</td><td>未完成番茄</td><td>因故拖延</td><td>无故拖延</td></tr>")
			.append("<tr><td>" + total + "</td><td style=\"color:green\">"
						+ fi + "</td><td style=\"color:gray\">"
						+ unfi + "</td><td style=\"color:red\">"
						+ dwc + "</td><td style=\"color:red\">"
						+ dwoc + "</td></tr></table></div>")
					.append("<hr/><div><h3>今日总结</h3><p>").append(summary)
					.append("</p></div>");
		return EvernoteUtil.NOTE_PREFIX + builder.toString() + EvernoteUtil.NOTE_SUFFIX;
	}

	@Override
	public boolean getSyncState() {
		return sync;
	}
	
}

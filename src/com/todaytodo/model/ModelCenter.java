package com.todaytodo.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.todaytodo.util.MyDate;

import android.os.Environment;

public class ModelCenter {
	private User user;
	
	private static ModelCenter uniqueInstance;
	private static final String FILE_NAME = "today_todo.ini";
	private static final String DIR_NAME = "todaytodo";
	private File sdCardDir = Environment.getExternalStorageDirectory();
	
	private File getFile(){
		String path = sdCardDir.getPath();
		File targetDir = new File(path+"/"+DIR_NAME);
		if(!targetDir.exists()){
			targetDir.mkdir();
		}
		File file = new File(targetDir, FILE_NAME);
		return file;
	}
	
	private ModelCenter() throws IOException{
		File file = getFile();
		String resultStr = "";
		if (file.exists()) {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String r = br.readLine();
			while (r != null) {
				resultStr += r;
				r = br.readLine();
			}
		} else {
			file.createNewFile();
		}
		if(resultStr.equals("")){
			resultStr = "{}";
		}
		JSONObject obj;
		try {
			obj = new JSONObject(resultStr);
			if(obj.has("user")){
				user = new User(obj.getJSONObject("user"));
				dailyRefresh();
			}else{
				initial();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		save();
	}
	
	
	public static ModelCenter getInstance(){
		if (uniqueInstance == null) {
			try {
				uniqueInstance = new ModelCenter();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return uniqueInstance;
	}
	
	public void save(){
		JSONObject obj = new JSONObject();
		if(user!=null){
			try {
				obj.put("user", user.toJSONObject());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		String result = obj.toString();
		
		File file = getFile();
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(result);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public User getUser() {
		return user;
	}
	
	public void dailyRefresh(){
		List<ThingList> thingToDelList = new ArrayList<ThingList>();
		boolean hasToday = false;
		boolean hasTomorrow = false;
		for(ThingList thingList : user.getThingListList()){
			if(thingList.getDate().getDaysAfterToday()<-3){
				thingToDelList.add(thingList);
			}
			if(thingList.getDate().getDaysAfterToday()==0){
				hasToday = true;
			}
			if(thingList.getDate().getDaysAfterToday()==1){
				hasTomorrow = true;
			}
		}
		if(!hasToday){
			ThingList thingList = new ThingList();
			thingList.setDate(new MyDate());
			thingList.setUser(user);
			user.getThingListList().add(thingList);
		}
		if(!hasTomorrow){
			ThingList thingList = new ThingList();
			thingList.setDate(new MyDate(1));
			thingList.setUser(user);
			user.getThingListList().add(thingList);
		}
		for(ThingList thingList: thingToDelList){
			user.getThingListList().remove(thingList);
		}
	}
	
	public void initial(){
		user = new User();
		user.setName("可怜的实验用户");
		user.setTomato(0);
		ThingList thingList0 = new ThingList();
		thingList0.setDate(new MyDate());
		thingList0.setUser(user);
		user.getThingListList().add(thingList0);
		ThingList thingList1 = new ThingList();
		thingList1.setDate(new MyDate(1));
		thingList1.setUser(user);
		user.getThingListList().add(thingList1);
		
		
		Thing thing1 = new Thing();
		thing1.setDescription("每个“番茄时”推荐为20分钟");
		thing1.setTomato(1);
		thing1.setState("unfi");
		
		Thing thing2 = new Thing();
		thing2.setDescription("下拉任务列表，添加任务！");
		thing2.setTomato(1);
		thing2.setState("unfi");
		
		Thing thing3 = new Thing();
		thing3.setDescription("右划任务，完成它！ 赢得番茄");
		thing3.setTomato(1);
		thing3.setState("unfi");
		
		Thing thing4 = new Thing();
		thing4.setDescription("长按任务。操作它");
		thing4.setTomato(1);
		thing4.setState("unfi");
		
		Thing thing5 = new Thing();
		thing5.setDescription("删了这些提示，开始你的番茄之旅！");
		thing5.setTomato(1);
		thing5.setState("unfi");
		
		thing5.setThingList(thingList0);
		thingList0.getThingList().add(thing5);
		thing4.setThingList(thingList0);
		thingList0.getThingList().add(thing4);
		thing3.setThingList(thingList0);
		thingList0.getThingList().add(thing3);
		thing2.setThingList(thingList0);
		thingList0.getThingList().add(thing2);
		thing1.setThingList(thingList0);
		thingList0.getThingList().add(thing1);
	}
	
}

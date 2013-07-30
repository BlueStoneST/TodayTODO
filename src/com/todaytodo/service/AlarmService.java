package com.todaytodo.service;
import java.util.Calendar;

import com.todaytodo.model.ModelCenter;
import com.todaytodo.model.Setting;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class AlarmService{
	private AlarmManager alarm;
	private Setting setting;
	private ModelCenter model;
	private Context context;
    
	private PendingIntent penCheck;
	private PendingIntent penDeal;
	private PendingIntent penSync;
	private Calendar cal23;
	private Calendar cal30;
	
	private static AlarmService uniqueInstance;
	
    private AlarmService(Context ctx){
    	alarm = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
    	model = ModelCenter.getInstance();
    	setting = model.getUser().getSetting();
    	this.context = ctx;
    	init();
    }
    
    public static AlarmService getInstance(Context ctx){
    	if(uniqueInstance == null){
    		uniqueInstance = new AlarmService(ctx);
    	}
    	return uniqueInstance;
    }
    
    private void init(){
    	cal23 = Calendar.getInstance();
    	cal23.set(cal23.get(Calendar.YEAR), cal23.get(Calendar.MONTH), 
    			cal23.get(Calendar.DATE), 23, 00, 0);
    	
    	cal30 = Calendar.getInstance();
    	cal30.set(cal30.get(Calendar.YEAR), cal30.get(Calendar.MONTH), 
    			cal30.get(Calendar.DATE), 0, 30, 0);
    	
    	Intent intentCheck = new Intent(context, SysBroadcast.class);
    	intentCheck.setAction("check_time");
    	penCheck = PendingIntent.getBroadcast(context, 0, intentCheck, 0);
    	
    	Intent intentDeal = new Intent(context, SysBroadcast.class);
    	intentDeal.setAction("daily_load");
    	penDeal = PendingIntent.getBroadcast(context, 0, intentDeal, 0);
    	
    	Intent intentSync = new Intent(context, SysBroadcast.class);
    	intentSync.setAction("auto_sync");
    	penSync = PendingIntent.getBroadcast(context, 0, intentSync, 0);
    	
    	if(setting.isAutoTip()){
    		submitTip();
    	}
    	
    	if(setting.isAutoDealThing()){
    		submitDeal();
    	}
    	
    	if(setting.isAutoSync()){
    		submitSync();
    	}
    }
    
    public void setAlarm(boolean tipChange, boolean dealChange, boolean syncChange){
    	if(tipChange){
    		setting.setAutoTip(!setting.isAutoTip());
    		if(setting.isAutoTip()){
    			submitTip();
    		}else{
    			cancelTip();
    		}
    	}
    	if(dealChange){
    		setting.setAutoDealThing(!setting.isAutoDealThing());
    	}
    	if(syncChange){
    		setting.setAutoSync(!setting.isAutoSync());
    		if(setting.isAutoSync()){
    			submitSync();
    		}else{
    			cancelSync();
    		}
    	}
    	if(tipChange || dealChange || syncChange){
    		model.save();
    	}
    }
    
    private void submitTip(){
    	alarm.setRepeating(AlarmManager.RTC, cal23.getTimeInMillis(), AlarmManager.INTERVAL_DAY, penCheck);
    }
    
    private void submitDeal(){
    	alarm.setRepeating(AlarmManager.RTC, cal30.getTimeInMillis(), AlarmManager.INTERVAL_DAY, penDeal);
    }
    
    private void submitSync(){
    	alarm.setRepeating(AlarmManager.RTC, cal30.getTimeInMillis(), AlarmManager.INTERVAL_DAY, penSync);
    }
    
    private void cancelTip(){
    	alarm.cancel(penCheck);
    }
    
    private void cancelDeal(){
    	alarm.cancel(penDeal);
    }
    
    private void cancelSync(){
    	alarm.cancel(penSync);
    }
}
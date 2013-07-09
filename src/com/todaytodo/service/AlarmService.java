package com.todaytodo.service;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AlarmService extends Service {
	private AlarmManager alarm;
    private final static int checkTime = 22;
	
	@Override
	public IBinder onBind(Intent intent) {
		 return null;
	}

    @Override
    public void onCreate() {
    	super.onCreate();
    	alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
    	Calendar cal0 = Calendar.getInstance();
    	cal0.set(cal0.get(Calendar.YEAR), cal0.get(Calendar.MONTH), 
    			cal0.get(Calendar.DATE), 23, 00, 0);
    	Intent intent0 = new Intent(this, SysBroadcast.class);
    	intent0.setAction("check_time");
    	PendingIntent pin0 = PendingIntent.getBroadcast(this, 0, intent0, 0);
    	
    	Calendar cal1 = Calendar.getInstance();
    	cal1.set(cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), 
    			cal1.get(Calendar.DATE), 0, 30, 0);
    	Intent intent1 = new Intent(this, SysBroadcast.class);
    	intent1.setAction("daily_load");
    	PendingIntent pin1 = PendingIntent.getBroadcast(this, 0, intent1, 0);
    	
    	//intent.putExtra("action","no");
    	//alarm.setRepeating(AlarmManager.RTC, (new Date()).getTime(), AlarmManager.INTERVAL_DAY, pin);
    	//alarm.set(AlarmManager.RTC, (new Date()).getTime()+1000, pin);
    	
    	alarm.setRepeating(AlarmManager.RTC, cal0.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pin0);
    	alarm.setRepeating(AlarmManager.RTC, cal1.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pin1);
    	//alarm.set(AlarmManager., triggerAtMillis, operation)
    }

    @Override
    public void onDestroy() {
        
    }

    
    
}
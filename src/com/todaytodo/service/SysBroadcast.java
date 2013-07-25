package com.todaytodo.service;

import com.todaytodo.MainActivity;
import com.todaytodo.R;
import com.todaytodo.control.ControllerCenter;
import com.todaytodo.control.EvernoteSyncCallback;
import com.todaytodo.model.ModelCenter;
import com.todaytodo.model.Thing;
import com.todaytodo.model.ThingList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

public class SysBroadcast extends BroadcastReceiver {
	private NotificationManager nManager;
	private ControllerCenter controller;
	private final static int NOTIFICATION_ID0 = 0x0001;
	private final static int NOTIFICATION_ID1 = 0x0002;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		controller =  ControllerCenter.getInstance(context);
		//thing all done or tip?
		String action = intent.getAction();
		if(action.equals("check_time")){
			int unfinishTomato = controller.getLeftTomatoToday();
			if(unfinishTomato > 0){
				//send it
				NotificationCompat.Builder mBuilder =
				        new NotificationCompat.Builder(context)
				        .setSmallIcon(R.drawable.ic_launcher)
				        .setContentTitle("今日事，你今日毕了吗？")
				        .setContentText("亲，你还有" + unfinishTomato + "个番茄的任务未处理，未完成的番茄将在1小时候后扣除。点击查看。").setDefaults(Notification.DEFAULT_SOUND);
				Intent resultIntent0 = new Intent(context, MainActivity.class);
				resultIntent0.setAction("today_tip");
				PendingIntent resultPendingIntent0 
					= PendingIntent.getActivity(context, 0, resultIntent0, 0);
				mBuilder.setContentIntent(resultPendingIntent0);
				nManager.notify(NOTIFICATION_ID0, mBuilder.build());
			}
			//tomorrow list empty?
			ThingList listTomorrow = controller.getThingList(1);
			if(listTomorrow != null){
				if(listTomorrow.getThingList().size()==0){
					NotificationCompat.Builder mBuilder =
					        new NotificationCompat.Builder(context)
					        .setSmallIcon(R.drawable.ic_launcher)
					        .setContentTitle("明日事，你安排了吗？")
					        .setContentText("你还木有计划明天的番茄呐！点击查看。").setDefaults(Notification.DEFAULT_SOUND);
					Intent resultIntent1 = new Intent(context, MainActivity.class);
					resultIntent1.setAction("tomorrow_tip");
					PendingIntent resultPendingIntent1
						= PendingIntent.getActivity(context, 0, resultIntent1, 0);
					mBuilder.setContentIntent(resultPendingIntent1);
					nManager.notify(NOTIFICATION_ID1, mBuilder.build());
				}
			}
		}else if(action.equals("daily_load")){
			controller.daliyCheck();
		}else if(action.equals("auto_sync")){
			if(controller.getEvernoteSession().isLoggedIn()){
				controller.sync(new EvernoteSyncCallback() {
					
					@Override
					public void success() {
						//do nothing
					}
					
					@Override
					public void error(String result) {
						//do noting
					}
				});
			}
		}
	}

}

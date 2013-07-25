package com.todaytodo.service;

import java.util.Timer;
import java.util.TimerTask;

import com.todaytodo.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationService {
	NotificationManager nManager;
	Context context;
	private final static int NOTIFICATION_ID = 0x0003;
	
	public NotificationService(Context ctx){
		this.nManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		this.context = ctx;
	}
	
	public void sendBeginSyncNotification(Class cls){
		Intent resultIntent = new Intent(context, cls);
		resultIntent.setAction("evernote_sync");
		PendingIntent resultPendingIntent 
			= PendingIntent.getActivity(context, 0, resultIntent, Intent.FLAG_ACTIVITY_CLEAR_TOP);Notification notification = new Notification(R.drawable.icon,"正在同步到Evernote...",System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		//notification.defaults |= Notification.DEFAULT_SOUND;
		notification.setLatestEventInfo(context, "正在同步到Evernote", "请等待", resultPendingIntent);
		nManager.notify(NOTIFICATION_ID, notification);
	}
	
	public void sendSyncNotification(Class cls){
		Intent resultIntent = new Intent(context, cls);
		resultIntent.setAction("evernote_sync");
		PendingIntent resultPendingIntent 
			= PendingIntent.getActivity(context, 0, resultIntent, Intent.FLAG_ACTIVITY_CLEAR_TOP);Notification notification = new Notification(R.drawable.icon,"成功同步到Evernote！",System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.setLatestEventInfo(context, "成功同步到Evernote", "可以到Evernote查看您的同步哦！", resultPendingIntent);
		nManager.notify(NOTIFICATION_ID, notification);
		Timer timer = new Timer(); 
	    timer.scheduleAtFixedRate(new RemoveNotificationTask(),1,1500);
	}
	
	class RemoveNotificationTask extends TimerTask{ 
        @Override 
        public void run() {                 
           nManager.cancel(NOTIFICATION_ID);      
        }    
    }
}

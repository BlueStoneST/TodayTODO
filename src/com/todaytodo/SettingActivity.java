package com.todaytodo;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.InvalidAuthenticationException;
import com.evernote.client.android.EvernoteSession.EvernoteService;
import com.todaytodo.control.EvernoteSyncCallback;
import com.todaytodo.model.Setting;
import com.todaytodo.service.AlarmService;
import com.todaytodo.service.NotificationService;
import com.todaytodo.util.Global;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.os.Build;

public class SettingActivity extends ActivityBase {
	private CheckBox checkBoxTip;
	private CheckBox checkBoxDeal;
	private CheckBox checkBoxSync;
	private RelativeLayout syncLayout;
	private Button bindingButton;
	private Button saveButton;
	private Button cancelButton;
	
	private boolean tipChange = false;
	private boolean dealChange = false;
	private boolean syncChange = false;
	private EvernoteSession mEvernoteSession;
	private AlarmService alarmService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		alarmService = AlarmService.getInstance(this);
		mEvernoteSession = controller.getEvernoteSession();
		checkBoxTip = (CheckBox)this.findViewById(R.id.as_tip_checkBox);
		checkBoxTip.setChecked(model.getUser().getSetting().isAutoTip());
		checkBoxTip.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				tipChange = !tipChange;
			}
		});
		
		checkBoxDeal = (CheckBox)this.findViewById(R.id.as_deal_checkBox);
		checkBoxDeal.setChecked(model.getUser().getSetting().isAutoDealThing());
		checkBoxDeal.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				dealChange = !dealChange;
			}
		});
		
		checkBoxSync = (CheckBox)this.findViewById(R.id.as_sync_checkBox);
		checkBoxSync.setChecked(model.getUser().getSetting().isAutoSync());
		checkBoxSync.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				syncChange = !syncChange;
				
			}
		});
		
		bindingButton = (Button)this.findViewById(R.id.as_binding_button);
		syncLayout = (RelativeLayout)this.findViewById(R.id.as_sync_layout);
		refreshBinding();
		
		saveButton = (Button)this.findViewById(R.id.as_save_btn);
		saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				alarmService.setAlarm(tipChange, dealChange, syncChange);
				SettingActivity.this.finish();
			}
		});
		cancelButton = (Button)this.findViewById(R.id.as_cancel_btn);
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SettingActivity.this.finish();
			}
		});
		
	}
	
	private void refreshBinding(){
		if(mEvernoteSession.isLoggedIn()){
			syncLayout.setVisibility(View.VISIBLE);
			bindingButton.setText("取消绑定Evernote");
			bindingButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Dialog alertDialog = new AlertDialog.Builder(SettingActivity.this).setTitle("温馨提醒")
							.setMessage("是否取消绑定？")
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									try {
										mEvernoteSession.logOut(SettingActivity.this);
										refreshBinding();
									} catch (InvalidAuthenticationException e) {
										e.printStackTrace();
									}
								}
							}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
								}
							}).create();
					alertDialog.show();
				}
				
			});
		}else{
			syncLayout.setVisibility(View.GONE);
			bindingButton.setText("绑定Evernote");
			//SettingActivity.this.registerForContextMenu(bindingButton);
			bindingButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					final String[] items = { "绑定到Evernote（国际版）", "绑定到印象笔记" };
					Builder builder = new Builder(SettingActivity.this);
					builder.setItems(items, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:
								if (!Global.IS_TEST) {
									mEvernoteSession
											.setEvernoteService(EvernoteSession.EvernoteService.PRODUCTION);
								} else {
									mEvernoteSession
											.setEvernoteService(EvernoteSession.EvernoteService.SANDBOX);
								}
								sendCallback();
								break;
							case 1:
								if (!Global.IS_TEST) {
									mEvernoteSession
											.setEvernoteService(EvernoteSession.EvernoteService.CHINA);
								} else {
									mEvernoteSession
											.setEvernoteService(EvernoteSession.EvernoteService.SANDBOX);
								}
								sendCallback();
								break;
							}
						}

					}).create().show();
				}
				
			});
		}
	}
	
	private void sendCallback(){
		final NotificationService ns = new NotificationService(this);
		ns.sendBeginSyncNotification(SettingActivity.class);
		controller.getSession().put("callback", new EvernoteSyncCallback(){

			@Override
			public void success() {
				ns.sendSyncNotification(MainActivity.class);
				refreshBinding();
			}
			
			@Override
			public void error(String result) {
				Toast.makeText(SettingActivity.this, result, Toast.LENGTH_LONG).show();
			}
			
		});
		mEvernoteSession.authenticate(SettingActivity.this);
		
	}

}

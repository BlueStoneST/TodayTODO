package com.todaytodo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.todaytodo.model.ModelCenter;
import com.todaytodo.model.Thing;
import com.todaytodo.model.ThingList;
import com.todaytodo.service.AlarmService;
import com.todaytodo.service.SysBroadcast;
import com.todaytodo.util.AccessTokenKeeper;
import com.todaytodo.util.MyDate;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.UsersAPI;
import com.weibo.sdk.android.net.RequestListener;
import com.weibo.sdk.android.sso.SsoHandler;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.ColorStateList;
import android.support.v4.app.NotificationCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author Sunny
 * @date 2013/7/9
 * 
 */

public class MainActivity extends ActivityBase {
	//public data elements
	private ThingList thingList = new ThingList();
	private int daysAfterToday = 0;
	private boolean animateLock = false;
	//public interface elements
	private LinearLayout layout;
	private ScrollView scroll;
	private TextView refreshText;
	//value for pull-down
	private int startY;
	private int currentY;
	private boolean record = false;
	private boolean pRecord = false;
	//static value for pull-down
	private static int VAL_REFRESH_HEIGHT = 50;
	private static int VAL_RESPONSE_DISTANCE = 80;
	//value for finish
	private int startX;
	private int startYff;
	private int currentX;
	private int currentYff;
	private Thing currentThing;
	private boolean recordFF = false;
	//static value for finish
	private static final int VAL_MAX_Y_DISTANCE = 50;
	private static final int VAL_MIN_X_DISTANCE = 100;
	//about weibo
	private Weibo mWeibo;
	private static final String CONSUMER_KEY = "3453800613";// 替换为开发者的appkey，例如"1646212860";
	private static final String REDIRECT_URL = "http://www.zaishangke.com";
	public static Oauth2AccessToken accessToken;
	private SsoHandler mSsoHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//step0,initial const
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        VAL_REFRESH_HEIGHT = height / 14;
        VAL_RESPONSE_DISTANCE = height / 9;
		System.out.println(VAL_REFRESH_HEIGHT + "," + VAL_RESPONSE_DISTANCE);
        
		Intent intent = this.getIntent();
		//step1, initialize the public interface elements
		layout = (LinearLayout) this.findViewById(R.id.list_layout);
		scroll = (ScrollView) this.findViewById(R.id.scroll_layout);
		refreshText = (TextView) this
				.findViewById(R.id.refresh_textView);
		// Down-insert motion of Scroll
		scroll.setOnTouchListener(pullDownTouchListener);
		//start the service
		Intent intentTS = new Intent(this, AlarmService.class);
		startService(intentTS);
		//judge the action and refresh the content
		String action = intent.getAction();
		if(action!=null || action.equals("")){
			NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			if (action.equals("tomorrow_tip")) {
				thingList = controller.getThingList(1);
				daysAfterToday = 1;
				nManager.cancel(0x0002);
				refreshList();
			} else if (action.equals("today_tip")) {
				getListAction(0);
				nManager.cancel(0x0001);
			}else{
				getListAction(0);
			}
		} else {
			getListAction(0);
		}
		refreshTitle();
		
		//next and last image button action
		View nextView = this.findViewById(R.id.next_imageView);
		nextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				getListAction(1);
			}

		});

		View lastView = this.findViewById(R.id.pre_imageView);
		lastView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				getListAction(-1);
			}

		});
		
		bindWeibo();
	}

	//after insert result
	@Override
	public void onActivityResult(int request, int result, Intent intent) {
		if (intent != null) {
			if (intent.getAction().equals("insert_return")) {
				thingList = (ThingList) controller.getSession().get("list");
				daysAfterToday = thingList.getDate().getDaysAfterToday();
				refreshList();
				Toast.makeText(getApplicationContext(), "完成任务添加",
						Toast.LENGTH_SHORT).show();
			} else if (intent.getAction()
					.equals("modify_return")) {
				thingList = (ThingList) controller.getSession().get("list");
				daysAfterToday = thingList.getDate().getDaysAfterToday();
				refreshList();
				Toast.makeText(getApplicationContext(), "完成任务修改",
						Toast.LENGTH_SHORT).show();
			}
			controller.getSession().clear();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add("添加任务");
		return true;
	}

	//create context menu
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("任务操作");
		menu.setHeaderIcon(R.drawable.i);
		final Thing thing = (Thing) view.getTag();
		if (thing.getThingList().getDate().getDaysAfterToday() == 0) {
			if (thing.getState().equals("unfi")) {
				menu.add(0, 0, 0, "完成任务");
				menu.getItem(0).setOnMenuItemClickListener(
						new OnMenuItemClickListener() {
							@Override
							public boolean onMenuItemClick(MenuItem arg0) {
								achieveThing(thing);
								return false;
							}
						});
				menu.add(0, 1, 0, "因故拖延");
				menu.getItem(1).setOnMenuItemClickListener(
						new OnMenuItemClickListener() {
							@Override
							public boolean onMenuItemClick(MenuItem arg0) {
								delayThingWithReason(thing);
								return false;
							}
						});
				menu.add(0, 2, 0, "无故拖延");
				menu.getItem(2).setOnMenuItemClickListener(
						new OnMenuItemClickListener() {
							@Override
							public boolean onMenuItemClick(MenuItem arg0) {
								delayThingWithoutReason(thing);
								return false;
							}
						});
				menu.add(0, 3, 0, "编辑任务");
				menu.getItem(3).setOnMenuItemClickListener(
						new OnMenuItemClickListener() {
							@Override
							public boolean onMenuItemClick(MenuItem arg0) {
								modifyThing(thing);
								return false;
							}
						});
				menu.add(0, 4, 0, "删除任务");
				menu.getItem(4).setOnMenuItemClickListener(
						new OnMenuItemClickListener() {
							@Override
							public boolean onMenuItemClick(MenuItem arg0) {
								deleteThing(thing);
								return false;
							}
						});
				menu.add(0, 5, 0, "返回");
			} else {
				menu.add(0, 0, 0, "恢复为未完成");
				menu.getItem(0).setOnMenuItemClickListener(
						new OnMenuItemClickListener() {
							@Override
							public boolean onMenuItemClick(MenuItem arg0) {
								redoThing(thing);
								return false;
							}
						});
				menu.add(0, 1, 0, "返回");
			}
		} else if (thing.getThingList().getDate().getDaysAfterToday() == 1) {
			menu.add(0, 0, 0, "编辑任务");
			menu.getItem(0).setOnMenuItemClickListener(
					new OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem arg0) {
							modifyThing(thing);
							return false;
						}
					});
			menu.add(0, 1, 0, "删除任务");
			menu.getItem(1).setOnMenuItemClickListener(
					new OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem arg0) {
							deleteThing(thing);
							return false;
						}
					});
			menu.add(0, 2, 0, "返回");
		}
		// if tomorrow

	}

	//create menu
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			insertThing();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	
	//thing operator view-action
	private void insertThing() {
		controller.getSession().put("list", thingList);
		Intent intent = new Intent(MainActivity.this, InsertActivity.class);
		intent.setAction("insert");
		startActivityForResult(intent, 0);
	}

	private void deleteThing(final Thing thing) {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("温馨提醒")
				.setMessage("删除了它就无法拯救回来了0_0")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						controller.deleteThing(thing);
						MainActivity.this.refreshList();
						Toast.makeText(getApplicationContext(), "成功删除任务", 0)
								.show();
					}
				}).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
					}
				}).create();
		alertDialog.show();

	}

	private void achieveThing(Thing thing) {
		controller.achieveThing(thing);
		MainActivity.this.refreshList();
		MainActivity.this.refreshTitle();
		MediaPlayer player = MediaPlayer.create(this, R.raw.achieve);
		player.start();
		Toast.makeText(getApplicationContext(),
				"完成任务！收获" + thing.getTomato() + "个番茄！", 0).show();
	}

	private void delayThingWithReason(final Thing thing) {
		Dialog alertDialog = new AlertDialog.Builder(this)
				.setTitle("温馨提醒")
				.setMessage("是否将任务复制到明天？")
				.setPositiveButton("是，我要完成它",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								controller.delayThing(thing, "dwc", true);
								MainActivity.this.refreshList();
								MainActivity.this.refreshTitle();
								Toast.makeText(getApplicationContext(),
										"明天请加油！", 0).show();
							}
						}).setNegativeButton("不，我放弃了", new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						controller.delayThing(thing, "dwc", false);
						MainActivity.this.refreshList();
						MainActivity.this.refreshTitle();
						Toast.makeText(getApplicationContext(), "什么！怎么可以放弃！！",
								0).show();
					}
				}).create();
		alertDialog.show();
	}

	private void delayThingWithoutReason(final Thing thing) {
		Dialog alertDialog = new AlertDialog.Builder(this)
				.setTitle("温馨提醒")
				.setMessage("是否将任务复制到明天？")
				.setPositiveButton("是，我要完成它",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								controller.delayThing(thing, "dwoc", true);
								MainActivity.this.refreshList();
								MainActivity.this.refreshTitle();
								Toast.makeText(
										getApplicationContext(),
										"无故不做任务，扣除" + thing.getTomato()
												+ "个番茄！", 0).show();
							}
						}).setNegativeButton("不，我放弃了", new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						controller.delayThing(thing, "dwoc", false);
						MainActivity.this.refreshList();
						MainActivity.this.refreshTitle();
						Toast.makeText(getApplicationContext(),
								"无故不做任务，扣除" + thing.getTomato() + "个番茄！", 0)
								.show();
					}
				}).create();
		alertDialog.show();
	}

	private void modifyThing(Thing thing) {
		controller.getSession().put("thing", thing);
		controller.getSession().put("list", thingList);
		Intent intent = new Intent(MainActivity.this, InsertActivity.class);
		intent.setAction("modify");
		startActivityForResult(intent, 0);
	}

	private void redoThing(Thing thing) {
		controller.redo(thing);
		MainActivity.this.refreshList();
		MainActivity.this.refreshTitle();
		Toast.makeText(getApplicationContext(), "任务恢复为完成状态", 0).show();
	}

	/**
	 * 
	 * @param pointer
	 *            : 0 - today,1 - next page,-1 - last page
	 */
	private void getListAction(int pointer) {
		if (!animateLock) {
			int temp = daysAfterToday;
			List<Thing> tempList = thingList.getThingList();
			String tip = "";
			switch (pointer) {
			case 0:
				thingList = controller.getThingList(0);
				tip = "程序出错?!";
				break;
			case 1:
				thingList = controller.getThingList(++daysAfterToday);
				tip = "木有下一页";
				break;
			case -1:
				thingList = controller.getThingList(--daysAfterToday);
				tip = "木有上一页";
				break;
			}
			if (thingList == null) {
				Dialog alertDialog = new AlertDialog.Builder(this)
						.setTitle("温馨提醒")
						.setMessage(tip)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).create();
				alertDialog.show();
				if (pointer != 0) {
					daysAfterToday = temp;
					thingList = controller.getThingList(daysAfterToday);
					refreshList();
				}
			} else {
				if (pointer == 0) {
					refreshList();
				} else {
					if (tempList.size() == 0) {
						refreshList();
						AlphaAnimation animation0 = new AlphaAnimation(0, 1);
						animation0.setDuration(500);
						layout.startAnimation(animation0);
					} else {
						animateLock = true;
						AlphaAnimation animation = new AlphaAnimation(1, 0);
						animation.setDuration(500);
						// animation.set
						animation.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationEnd(Animation arg0) {
								animateLock = false;
								refreshList();
								AlphaAnimation animation0 = new AlphaAnimation(
										0, 1);
								animation0.setDuration(500);

								layout.startAnimation(animation0);
							}

							@Override
							public void onAnimationRepeat(Animation arg0) {
							}

							@Override
							public void onAnimationStart(Animation arg0) {
							}

						});

						layout.startAnimation(animation);
					}
				}
			}
		}
	}

	private void refreshTitle() {
		TextView view1 = (TextView) this.findViewById(R.id.user_textView);
		view1.setText(model.getUser().getName());
		TextView view2 = (TextView) this
				.findViewById(R.id.user_tomato_textView);
		view2.setText("累计番茄:" + model.getUser().getTomato());
	}

	
	//**important**
	//the core function of this activity. refresh the activity interface by ThingList
	private void refreshList() {
		MyDate date = thingList.getDate();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String d0 = df.format(date);
		String d1 = "";
		if (date.getDaysAfterToday() == 0) {
			d1 = "今天";
		} else if (date.getDaysAfterToday() == 1) {
			d1 = "明天";
		} else if (date.getDaysAfterToday() == -1) {
			d1 = "昨天";
		} else if (date.getDaysAfterToday() < -1) {
			d1 = Math.abs(date.getDaysAfterToday()) + "天前";
		} else {
			d1 = date.getDaysAfterToday() + "天后";
		}
		TextView textView = (TextView) this.findViewById(R.id.textViewDate);
		textView.setText(d0 + "(" + d1 + ")");

		LayoutInflater mInflater = LayoutInflater.from(this);
		layout.removeAllViews();
		int tomato = 0;
		int tomatoFinished = 0;
		for (final Thing thing : thingList.getThingList()) {
			tomato += thing.getTomato();
			View convertView = mInflater.inflate(R.layout.vlist, null);
			ViewHolder holder = new ViewHolder();
			holder.desText = (TextView) convertView
					.findViewById(R.id.thing_title_textView);
			holder.infoButton = (ImageView) convertView
					.findViewById(R.id.info_imageView);
			holder.tomatoLayout = (LinearLayout) convertView
					.findViewById(R.id.tomato_layout);
			holder.stateText = (TextView) convertView
					.findViewById(R.id.state_textView);
			holder.desText.setText(thing.getDescription());
			holder.tomatoLayout.removeAllViews();
			int tomatoCount = thing.getTomato();
			for (int i = 0; i < tomatoCount; i++) {
				ImageView iView = new ImageView(MainActivity.this);
				iView.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				iView.setImageDrawable(MainActivity.this.getResources()
						.getDrawable(R.drawable.tomato));
				iView.getLayoutParams().height = 30;
				iView.getLayoutParams().width = 30;
				holder.tomatoLayout.addView(iView);
			}
			String state = thing.getState();
			if (state.equals("fi")) {
				holder.infoButton.setImageDrawable(MainActivity.this
						.getResources().getDrawable(R.drawable.btn_2));
				holder.stateText.setText("已完成");
				holder.stateText.setTextColor(MainActivity.this.getResources()
						.getColor(R.color.sys_green));
				SpannableString msp = new SpannableString(
						holder.desText.getText());
				msp.setSpan(new StrikethroughSpan(), 0, msp.length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				holder.desText.setText(msp);
				holder.desText.setTextColor(MainActivity.this.getResources()
						.getColor(R.color.sys_gray));
				tomatoFinished += thing.getTomato();
			} else if (state.equals("unfi")) {
				holder.infoButton.setImageDrawable(MainActivity.this
						.getResources().getDrawable(R.drawable.btn_3));
				holder.stateText.setText("未完成");
				holder.stateText.setTextColor(MainActivity.this.getResources()
						.getColor(R.color.sys_gray));
				if (daysAfterToday == 0) {
					convertView.setOnTouchListener(new FlingRightTouchListener(thing));
				}
			} else if (state.equals("dwc")) {
				holder.infoButton.setImageDrawable(MainActivity.this
						.getResources().getDrawable(R.drawable.btn_0));
				holder.stateText.setText("因故拖延");
				holder.stateText.setTextColor(MainActivity.this.getResources()
						.getColor(R.color.sys_gray));
				holder.desText.setTextColor(MainActivity.this.getResources()
						.getColor(R.color.sys_gray));
			} else if (state.equals("dwoc")) {
				holder.infoButton.setImageDrawable(MainActivity.this
						.getResources().getDrawable(R.drawable.btn_1));
				holder.stateText.setText("无故拖延");
				holder.stateText.setTextColor(MainActivity.this.getResources()
						.getColor(R.color.sys_red));
				holder.desText.setTextColor(MainActivity.this.getResources()
						.getColor(R.color.sys_gray));
			}

			convertView.setTag(thing);
			if (thingList.getDate().getDaysAfterToday() >= 0) {
				MainActivity.this.registerForContextMenu(convertView);
			}

			layout.addView(convertView, 0, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
		}

		TextView textView2 = (TextView) this
				.findViewById(R.id.come_on_textView);
		String comeOnWords = "";
		String tomatoWords = "本日番茄(" + tomatoFinished + "/" + tomato + ") ";
		if (date.getDaysAfterToday() < 0) {
			if (tomato == 0) {
				comeOnWords = "本日木有任务~";
			} else {
				if (tomato == tomatoFinished) {
					comeOnWords = tomatoWords + "光荣完成任务的一天！";
				} else {
					comeOnWords = tomatoWords + "木有完成任务的一天。";
				}
			}
		} else if (date.getDaysAfterToday() == 0) {
			if (tomato == 0) {
				comeOnWords = "今日木有任务~";
			} else {
				if (tomato == tomatoFinished) {
					comeOnWords = tomatoWords + "你的不懈努力太棒了！";
				} else {
					comeOnWords = tomatoWords + "请加油！";
				}
			}
		} else {
			if (tomato == 0) {
				comeOnWords = "请添加任务。";
			} else {
				comeOnWords = "总番茄数" + tomato + " 请继续添加任务。";
			}
		}
		textView2.setText(comeOnWords);

	}
	
	//listener to deal with fling right of the thing
	class FlingRightTouchListener implements OnTouchListener{
		private Thing thing;
		
		public FlingRightTouchListener(Thing thing){
			this.thing = thing;
		}
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			System.out.println("list" + event.getAction() + " "
					+ event.getX() + "," + event.getY());

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// Toast.makeText(getApplicationContext(),
				// Float.toString(event.getX()), 0).show();
				// Toast.LENGTH_SHORT).show();
				//System.out.println("#the x is" + event.getX());
				currentThing = thing;
				startX = (int) event.getX();
				startYff = (int) event.getY();
				recordFF = true;
				break;
			case MotionEvent.ACTION_UP:
				if (recordFF) {
//					System.out.println("the x is"
//							+ event.getX());
					recordFF = false;
					currentYff = (int) event.getY();
					currentX = (int) event.getX();
					if (Math.abs(currentYff - startYff) < VAL_MAX_Y_DISTANCE
							&& currentX - startX > VAL_MIN_X_DISTANCE) {
						achieveThing(currentThing);
					}
				}
				break;
			}
			return false;
			// return gd.onTouchEvent(event);
		}
		
	}
	
	//listener to deal with pull down of the scroll view
	OnTouchListener pullDownTouchListener = new OnTouchListener(){

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// System.out.println(event.getAction()+" "+event.getX()+","+event.getY());
			if (thingList.getDate().getDaysAfterToday() >= 0) {

				final RelativeLayout refreshLayout = (RelativeLayout) MainActivity.this
						.findViewById(R.id.refresh_layout);
				// Toast.makeText(getApplicationContext(),
				// "!"+scroll.getScrollY(), 0).show();
				switch (event.getAction()) {
				
				case MotionEvent.ACTION_MOVE:
					if (!record) {
						if (scroll.getScrollY() == 0) {
							if (!pRecord) {
								pRecord = true;
								startY = (int) event.getY();
							} else {
								int nextY = (int) event.getY();
								pRecord = false;
								if (nextY > startY) {
									record = true;
									return true;
								}
							}

						}
					} else {
						if (scroll.getScrollY() == 0) {
							currentY = (int) event.getY();
							if (currentY - startY > VAL_RESPONSE_DISTANCE) {
								if (getDistance() > VAL_REFRESH_HEIGHT) {
									refreshText.setText("释放开始添加任务...");
								} else {
									refreshText.setText("下拉添加任务...");
								}
//								int marginTop = (int)(Math.sqrt(currentY
//										- startY - VAL_RESPONSE_DISTANCE) * 6) - 50;
								((MarginLayoutParams) refreshLayout
										.getLayoutParams()).height = getDistance();
								//System.out.println(marginTop);
								refreshLayout.requestLayout();
							}
							return true;
						}
					}
					break;
				case MotionEvent.ACTION_UP:
					// System.out.println("mouse is up!");
					if (record) {
						record = false;
						if (getDistance() > VAL_REFRESH_HEIGHT) {
							insertThing();
						}
						((MarginLayoutParams) refreshLayout
								.getLayoutParams()).height = 0;
						refreshText.setText("下拉添加任务...");
						refreshLayout.requestLayout();
					}
					if (recordFF) {
						// Toast.makeText(getApplicationContext(),
						// "+"+event.getX(),
						// Toast.LENGTH_SHORT).show();
						//System.out.println("the x is" + event.getX());
						recordFF = false;
						currentYff = (int) event.getY();
						currentX = (int) event.getX();
						if (Math.abs(currentYff - startYff) < VAL_MAX_Y_DISTANCE
								&& currentX - startX > VAL_MIN_X_DISTANCE) {
							achieveThing(currentThing);
						}
					}
					break;
				}
			}
			return false;
		}
		
	};
	
	private int getDistance(){
		return (int)(Math.sqrt(currentY - startY 
				- VAL_RESPONSE_DISTANCE)*8);
	}

	public final class ViewHolder {
		public ImageView infoButton;
		public TextView desText;
		public LinearLayout tomatoLayout;
		public TextView stateText;
	}

	private void bindWeibo(){
		mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
		accessToken = controller.getToken();
		if (accessToken == null) {
			mWeibo.authorize(MainActivity.this,new AuthDialogListener());
		}
	}
	
	class AuthDialogListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			accessToken = new Oauth2AccessToken(token, expires_in);
			if (accessToken.isSessionValid()) {
				String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
						.format(new java.util.Date(accessToken.getExpiresTime()));

				try {
					Class sso = Class
							.forName("com.weibo.sdk.android.api.WeiboAPI");// 如果支持weiboapi的话，显示api功能演示入口按钮
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				controller.saveToken(accessToken);
				Toast.makeText(MainActivity.this, "链接微博成功", Toast.LENGTH_SHORT).show();
				//TODO read photo and name
				UsersAPI usersApi = new UsersAPI(accessToken);
				
				usersApi.show(values.getLong("uid"), new RequestListener(){

					@Override
					public void onComplete(String str) {
						System.out.println(str);
						try {
							JSONObject obj = new JSONObject(str);
							String pic = obj.getString("avatar_large");
							String name = obj.getString("name");
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}

					@Override
					public void onError(WeiboException arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onIOException(IOException arg0) {
						// TODO Auto-generated method stub
						
					}
					
				});
			}
		}

		@Override
		public void onError(WeiboDialogError e) {
			Toast.makeText(getApplicationContext(),
					"Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(getApplicationContext(), "Auth cancel",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(getApplicationContext(),
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}

	}
}

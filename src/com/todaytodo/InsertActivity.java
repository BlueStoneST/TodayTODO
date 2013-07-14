package com.todaytodo;

import java.text.SimpleDateFormat;

import com.todaytodo.model.Thing;
import com.todaytodo.model.ThingList;
import com.todaytodo.util.MyDate;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class InsertActivity extends ActivityBase {
	private ThingList thingList;
	private Thing thing;
	private String action;
	private int tomato = 1;
	private EditText editText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insert);
		
		editText = (EditText)InsertActivity.this.findViewById(R.id.description_editText);
		
		Intent intent = getIntent();
		action = intent.getAction();
		if(action.equals("insert")){
			thingList = (ThingList)controller.getSession().get("list");
		}else if(action.equals("modify")){
			thingList = (ThingList)controller.getSession().get("list");
			thing = (Thing)controller.getSession().get("thing");
			tomato = thing.getTomato();
			editText.setText(thing.getDescription());
			refreshText();
		}
		controller.getSession().clear();
		Button btn0 = (Button)findViewById(R.id.submit_btn);
		btn0.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				String description = editText.getText().toString();
				
				if(!description.equals("")){
					if(action.equals("insert")){
						controller.insertThing(description,tomato,thingList);
					}else{
						controller.modifyThing(thing,description,tomato);
					}
					Intent intent = new Intent();
					intent.setAction(action+"_return");
					controller.getSession().put("list", thingList);
					InsertActivity.this.setResult(0, intent);
					//Intent intent = new Intent(InsertActivity.this,MainActivity.class);
					//Intent intent = getIntent();
					//InsertActivity.this.setResult(0);
					//startActivityForResult(intent,0);
					InsertActivity.this.finish();
				}else{
					Toast.makeText(getApplicationContext(), "任务描述不能为空",
						     Toast.LENGTH_SHORT).show();
				}
			}
		});
		Button btn1 = (Button)findViewById(R.id.return_btn);
		btn1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				InsertActivity.this.setResult(0);
				InsertActivity.this.finish();
			}
		});
		View view0 = findViewById(R.id.minus_btn);
		view0.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(tomato>1){
					tomato--;
					refreshText();
				}else{
					Toast.makeText(getApplicationContext(), "番茄不能再少了",
						     Toast.LENGTH_SHORT).show();
				}
			}
		});
		View view1 = findViewById(R.id.plus_btn);
		view1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(tomato<8){
					tomato++;
					refreshText();
				}else{
					Toast.makeText(getApplicationContext(), "番茄不能更多了",
						     Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		MyDate date = thingList.getDate();
		TextView textView = (TextView) this.findViewById(R.id.thingTime_textView);
		textView.setText("任务发布于：" + date.toStr(true));
	}
	
	private void refreshText(){
		TextView text = (TextView)findViewById(R.id.number_textView);
		text.setText(Integer.toString(tomato));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.insert, menu);
		return true;
	}

}

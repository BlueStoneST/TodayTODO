package com.todaytodo;

import java.util.List;

import com.todaytodo.control.WeekStatisticUnit;
import com.todaytodo.model.ThingList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeekStatisticsActivity extends ActivityBase {
	private LinearLayout layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_week_statistics);
		layout = (LinearLayout)this.findViewById(R.id.week_statistics_layout);
		refreshList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.week_statistics, menu);
		menu.add(1, 0, 0, "返回");
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			WeekStatisticsActivity.this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void refreshList(){
		LayoutInflater mInflater = LayoutInflater.from(this);
		layout.removeAllViews();
		List<WeekStatisticUnit> unitList = controller.getWeekStatisitic();
		for(final WeekStatisticUnit unit : unitList){
			View convertView = mInflater.inflate(R.layout.vlist_statistics, null);
			TextView weekTextView = (TextView)convertView.findViewById(R.id.vs_week_textView);
			TextView dateTextView = (TextView)convertView.findViewById(R.id.vs_date_textView);
			TextView numTextView = (TextView)convertView.findViewById(R.id.vs_ach_num_textView);
			TextView sumTextView = (TextView)convertView.findViewById(R.id.vs_sum_textView);
			weekTextView.setText(unit.date.toWeekStr(true));
			dateTextView.setText(unit.date.toStr(true));
			numTextView.setText(Integer.toString(unit.finishTomato));
			sumTextView.setText(Integer.toString(unit.totalTomato));
			convertView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View view) {
					LayoutInflater inflater = (LayoutInflater) WeekStatisticsActivity.this
							.getSystemService(LAYOUT_INFLATER_SERVICE);
					View v = inflater.inflate(R.layout.dialog_statistics,
							(ViewGroup) WeekStatisticsActivity.this.findViewById(R.id.daily_statistic_Layout));
					final EditText descriptionTextView = (EditText) v.findViewById(R.id.des_editText);
					TextView numTextView = (TextView) v.findViewById(R.id.achieve_tomato_textView);
					TextView sumTextView = (TextView) v.findViewById(R.id.sum_tomato_textView);
					numTextView.setText(Integer.toString(unit.finishTomato));
					sumTextView.setText(Integer.toString(unit.totalTomato));
					final ThingList thingList = controller.getThingList(unit.date.getDaysAfterToday());
					descriptionTextView.setText(thingList.getSummary());
					AlertDialog dialog = new AlertDialog.Builder(WeekStatisticsActivity.this)
							.setView(v)
							.setPositiveButton("保存",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int which) {
											String summary = descriptionTextView.getText().toString();
											controller.saveThingList(thingList, summary);
										}
									}).setNegativeButton("关闭", new DialogInterface.OnClickListener(){
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											//Do nothing
										}
									}).create();
					
					dialog.show();
				}
			});
			layout.addView(convertView, 0, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
		}
	}

}

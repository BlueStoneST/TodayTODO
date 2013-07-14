package com.todaytodo.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyDate extends Date {
	public MyDate(){
		super();
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		this.setTime(cal.getTimeInMillis());
	}
	
	public MyDate(int daysAfterToday){
		super();
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH) + daysAfterToday, 0, 0, 0);
		this.setTime(cal.getTimeInMillis());
	}
	
	public MyDate(long ms){
		super(ms);
	}
	
	public int getDaysAfterToday(){
		long ms2 = (new Date()).getTime();
		long ms1 = this.getTime();
		int delta = (int)(ms1 - ms2);
		if(delta<=0){
			return msToDay((ms1-ms2));
		}
		return msToDay((ms1-ms2))+1;
	}
	
	private static int msToDay(long ms){
		return (int) (ms /(24 * 60 *60 * 1000));
	}
	
	public boolean equals(MyDate obj){
		return obj.getTime() == this.getTime();
	}
	
	public String toWeekStr(boolean weekComOn){
		Calendar cal = Calendar.getInstance();
		cal.setTime(this);
		Calendar cal0 = Calendar.getInstance();
		cal0.setTime(new MyDate(0));
		int weekNum = cal.get(Calendar.WEEK_OF_MONTH);
		int todayWeekNum = cal0.get(Calendar.WEEK_OF_MONTH);
		String result = "";
		if(weekComOn){
			if(weekNum == todayWeekNum){
				result = "本";
			}else{
				result = "上";
			}
		}
		switch(cal.get(Calendar.DAY_OF_WEEK)){
		case Calendar.SUNDAY:
			result += "周日";
			return result;
		case Calendar.MONDAY:
			result += "周一";
			return result;
		case Calendar.TUESDAY:
			result += "周二";
			return result;
		case Calendar.WEDNESDAY:
			result += "周三";
			return result;
		case Calendar.THURSDAY:
			result += "周四";
			return result;
		case Calendar.FRIDAY:
			result += "周五";
			return result;
		case Calendar.SATURDAY:
			result += "周六";
			return result;
		};
		return null;
	}
	
	public String toStr(boolean daysAfterOn){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String d0 = df.format(this);
		String d1 = "";
		if(daysAfterOn){
			if (this.getDaysAfterToday() == 0) {
				d1 = "今天";
			} else if (this.getDaysAfterToday() == 1) {
				d1 = "明天";
			} else if (this.getDaysAfterToday() == -1) {
				d1 = "昨天";
			} else if (this.getDaysAfterToday() < -1) {
				d1 = Math.abs(this.getDaysAfterToday()) + "天前";
			} else {
				d1 = this.getDaysAfterToday() + "天后";
			}
		}
		return d0 + "(" + d1 + ")";
	}
}

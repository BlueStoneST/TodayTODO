package com.todaytodo.util;

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
}

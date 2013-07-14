package com.todaytodo.control;

import com.todaytodo.model.Thing;
import com.todaytodo.model.ThingList;
import com.todaytodo.util.MyDate;

public class WeekStatisticUnit {
	public MyDate date;
	public int finishTomato = 0;
	public int totalTomato = 0;
	public WeekStatisticUnit(ThingList list){
		this.date = list.getDate();
		for(Thing thing : list.getThingList()){
			if(thing.getState().equals("fi")){
				finishTomato += thing.getTomato();
			}
			totalTomato += thing.getTomato();
		}
	}
}

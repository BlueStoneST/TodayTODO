package com.todaytodo;

import com.todaytodo.control.ControllerCenter;
import com.todaytodo.model.ModelCenter;

import android.app.Activity;

/**
 * 
 * @author Sunny
 * @date 2013-7-9
 *
 */

public class ActivityBase extends Activity {
	protected ControllerCenter controller;
	protected ModelCenter model;
	
	public ActivityBase(){
		super();
		controller = ControllerCenter.getInstance();
		model = ModelCenter.getInstance();
	}
}

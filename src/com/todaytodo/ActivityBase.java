package com.todaytodo;

import com.todaytodo.control.ControllerCenter;
import com.todaytodo.model.ModelCenter;
import com.evernote.client.android.EvernoteSession;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;

/**
 * 
 * @author Sunny
 * @date 2013-7-9
 * 
 */

public class ActivityBase extends Activity {
	protected ControllerCenter controller;
	protected ModelCenter model;

	protected EvernoteSession mEvernoteSession;
	protected final int DIALOG_PROGRESS = 101;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		controller = ControllerCenter.getInstance(this);
		model = ModelCenter.getInstance();
		mEvernoteSession = controller.getEvernoteSession();
	}

	// using createDialog, could use Fragments instead
	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_PROGRESS:
			return new ProgressDialog(ActivityBase.this);
		}
		return super.onCreateDialog(id);
	}

	@Override
	@SuppressWarnings("deprecation")
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DIALOG_PROGRESS:
			((ProgressDialog) dialog).setIndeterminate(true);
			dialog.setCancelable(false);
			((ProgressDialog) dialog)
					.setMessage(getString(R.string.esdk__loading));
		}
	}
}

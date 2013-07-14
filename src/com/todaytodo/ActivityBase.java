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

	// Your Evernote API key. See http://dev.evernote.com/documentation/cloud/
	// Please obfuscate your code to help keep these values secret.
	private static final String CONSUMER_KEY = "bluestonest-9791";
	private static final String CONSUMER_SECRET = "ae20ca44bdb4f955";

	// Initial development is done on Evernote's testing service, the sandbox.
	// Change to HOST_PRODUCTION to use the Evernote production service
	// once your code is complete, or HOST_CHINA to use the Yinxiang Biji
	// (Evernote China) production service.
	private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;

	protected EvernoteSession mEvernoteSession;
	protected final int DIALOG_PROGRESS = 101;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		controller = ControllerCenter.getInstance();
		model = ModelCenter.getInstance();
		
		// Set up the Evernote Singleton Session
		mEvernoteSession = EvernoteSession.getInstance(this, CONSUMER_KEY,
				CONSUMER_SECRET, EVERNOTE_SERVICE);
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

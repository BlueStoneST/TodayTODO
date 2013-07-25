package com.todaytodo.control;

import java.util.List;

import android.content.Context;

import com.evernote.client.android.AsyncNoteStoreClient;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.transport.TTransportException;
import com.todaytodo.model.ModelCenter;
import com.todaytodo.model.ThingList;
import com.todaytodo.model.User;

public class EvernoteSyncFactory {
	
	private static final String CONSUMER_KEY = "bluestonest-1753";
	private static final String CONSUMER_SECRET = "4a4dee9a17e67bd3";
	
	private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;

	private static EvernoteSyncFactory uniqueInstance;
	
	protected EvernoteSession mEvernoteSession;
	
	private EvernoteSyncFactory(Context ctx){
		this.mEvernoteSession = EvernoteSession.getInstance(ctx, CONSUMER_KEY,
				CONSUMER_SECRET, EVERNOTE_SERVICE);
	}
	
	public static EvernoteSyncFactory getInstance(Context ctx){
		if(uniqueInstance==null){
			uniqueInstance = new EvernoteSyncFactory(ctx);
		}
		return uniqueInstance;
	}

	public EvernoteSession getEvernoteSession() {
		return mEvernoteSession;
	}
	
	public void syncUser(ModelCenter model, EvernoteSyncCallback callback){
		EvernoteSyncClient client = new EvernoteSyncClient(mEvernoteSession, callback, model);
		client.sync();
	}
	
}

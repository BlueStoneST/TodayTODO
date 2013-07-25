package com.todaytodo.control;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.evernote.client.android.AsyncNoteStoreClient;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteAttributes;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.transport.TTransportException;
import com.todaytodo.model.ModelCenter;
import com.todaytodo.model.ThingList;
import com.todaytodo.model.User;

public class EvernoteSyncClient {
	public static final int UNFINISHED = 0;
	public static final int SUCCESS = 1;
	public static final int ERROR = 2;
	
	
	private static final String NOTEBOOK_NAME = "今日事_todayTODO";
	
	private EvernoteSession mEvernoteSession;
	private AsyncNoteStoreClient noteClient;
	private EvernoteSyncCallback callback;
	private ModelCenter model;
	private int state;
	private Notebook notebook;
	
	public EvernoteSyncClient(EvernoteSession mEvernoteSession,
			EvernoteSyncCallback callback, ModelCenter model) {
		this.mEvernoteSession = mEvernoteSession;
		this.callback = callback;
		this.model = model;
		state = UNFINISHED;
		try {
			this.noteClient = mEvernoteSession.getClientFactory().createNoteStoreClient();
		} catch (TTransportException e) {
			e.printStackTrace();
			dealWithError("build client error");
		}
	}
	
	public void sync(){
		if(mEvernoteSession.isLoggedIn()){
			noteClient.listNotebooks(new OnClientCallback<List<Notebook>>(){

				@Override
				public void onSuccess(List<Notebook> list) {
					for(Notebook book : list){
						if(book.getName().equals(NOTEBOOK_NAME)){
							notebook = book;
							syncNotebook();
							return;
						}
					}
					if(notebook == null){
						notebook = new Notebook();
						notebook.setName(NOTEBOOK_NAME);
						noteClient.createNotebook(notebook, new OnClientCallback<Notebook>(){

							@Override
							public void onSuccess(final Notebook myBook) {
								notebook = myBook;
								syncNotebook();
							}

							@Override
							public void onException(Exception exception) {
								exception.printStackTrace();
								dealWithError("create notebook error");
							}
							
						});
					}
				}

				@Override
				public void onException(Exception exception) {
					exception.printStackTrace();
					dealWithError("get notebook error");
				}
				
			});
		}else{
			dealWithError("need to login");
		}
	}
	
	private void syncNotebook(){
		model.getUser().setNotebookId(notebook.getGuid());
		boolean needSync = false;
		for(final ThingList thingList : model.getUser().getThingListList()){
			if( ( !thingList.isSync() ) && thingList.getThingList().size() > 0){
				needSync = true;
				System.out.println("noteId:" + thingList.getNoteId());
				if(thingList.getNoteId()==null){
					insertNote(thingList);
				}else{
					noteClient.getNote(thingList.getNoteId(), true, false, false, false, 
						new OnClientCallback<Note>() {
						
						@Override
						public void onSuccess(Note data) {
							if( data == null ){
								insertNote(thingList);
								System.out.println("no note");
							}else{
								updateNote(data, thingList);
								System.out.println("update");
							}
						}
						
						@Override
						public void onException(Exception exception) {
							exception.printStackTrace();
							if(exception instanceof InvocationTargetException){
								insertNote(thingList);
								System.out.println("no note");
							}else{
								System.out.println(exception);
								dealWithError("get note error");
							}
						}
					});
				}
			}
		}
		if(!needSync){
			judgeSyncSuccess();
			System.out.println("all sync");
		}
	}
	
	private void insertNote(final ThingList thingList){
		Note data = new Note();
		data.setTitle(thingList.getTitile());
		data.setContent(thingList.getContent());
		data.setNotebookGuid(notebook.getGuid());
		NoteAttributes na = new NoteAttributes();
		na.setContentClass("amazingst.todaytodo");
		data.setAttributes(na);
		noteClient.createNote(data, new OnClientCallback<Note>() {
			
			@Override
			public void onSuccess(Note data) {
				thingList.setSync(true);
				String str = new String(data.getGuid().toCharArray().clone());
				thingList.setNoteId(str);
				judgeSyncSuccess();
			}
			
			@Override
			public void onException(Exception exception) {
				exception.printStackTrace();
				callback.error("insert note error");
			}
		});
	}
	
	private void updateNote(Note note, final ThingList thingList){
		note.setTitle(thingList.getTitile());
		note.setContent(thingList.getContent());
		note.setNotebookGuid(notebook.getGuid());
		noteClient.updateNote(note, new OnClientCallback<Note>() {
			
			@Override
			public void onSuccess(Note data) {
				thingList.setSync(true);
				String str = new String(data.getGuid().toCharArray().clone());
				thingList.setNoteId(str);
				//System.out.println("noteId after:" + thingList.getNoteId());
				judgeSyncSuccess();
			}
			
			@Override
			public void onException(Exception exception) {
				exception.printStackTrace();
				callback.error("insert note error");
			}
		});
	}
	
	private void judgeSyncSuccess(){
		if(state != UNFINISHED){
			return;
		}
		for(ThingList list : model.getUser().getThingListList()){
			if(( !list.isSync() ) && list.getThingList().size() > 0){
				return;
			}
		}
		state = SUCCESS;
		model.save();
		callback.success();
	}
	
	private void dealWithError(String errorReason){
		if(state == UNFINISHED){
			callback.error(errorReason);
			state = ERROR;
		}
	}
	
}

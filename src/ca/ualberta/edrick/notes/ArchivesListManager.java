package ca.ualberta.edrick.notes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;


public class ArchivesListManager {
	Context context;
	
	static final String prefFile = "ArchivesList";
	static final String listKey = "archivesList";
	
	static private ArchivesListManager archivesListManager = null;
	
	public static void initManager(Context context) {
		if (archivesListManager == null) {
			archivesListManager = new ArchivesListManager(context);
			if (context == null) {
				throw new RuntimeException("missing context for ArchivesListManager");
			}
			archivesListManager = new ArchivesListManager(context);
		}
	}
	
	public static ArchivesListManager getManager() {
		if (archivesListManager == null) {
			throw new RuntimeException("Did not initialize Manager");
		}
		return archivesListManager;
	}
	
	public ArchivesListManager(Context context) {
		this.context = context;
	}
	
	public ToDoList loadArchivesList() throws ClassNotFoundException, IOException {
		SharedPreferences settings = context.getSharedPreferences(prefFile, Context.MODE_PRIVATE);
		String archivesListData = settings.getString(listKey, "");
		if (archivesListData.equals("")) {
			return new ToDoList();
		} else {
			return archivesListFromString(archivesListData);
		}
		
	}

	private ToDoList archivesListFromString(String archivesListData) throws ClassNotFoundException, IOException {
		ByteArrayInputStream bi = new ByteArrayInputStream(Base64.decode(archivesListData, Base64.DEFAULT));
		ObjectInputStream oi = new ObjectInputStream(bi);
		return (ToDoList)oi.readObject();
		
	}
	
	private String archivesListToString(ToDoList list) throws IOException {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream oo = new ObjectOutputStream(bo);
		oo.writeObject(list);
		oo.close();
		byte bytes[] = bo.toByteArray();
		return Base64.encodeToString(bytes, Base64.DEFAULT);
	}
	
	public void saveArchivesList(ToDoList list) throws IOException {
		SharedPreferences settings = context.getSharedPreferences(prefFile, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putString(listKey, archivesListToString(list));
		editor.commit();
	}

	
}

package ca.ualberta.edrick.notes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;


public class ToDoListManager {
	Context context;
	
	static final String prefFile = "ToDoList";
	static final String listKey = "todoList";
	
	static private ToDoListManager todoListManager = null;
	
	public static void initManager(Context context) {
		if (todoListManager == null) {
			todoListManager = new ToDoListManager(context);
			if (context == null) {
				throw new RuntimeException("missing context for ToDoListManager");
			}
			todoListManager = new ToDoListManager(context);
		}
	}
	
	public static ToDoListManager getManager() {
		if (todoListManager == null) {
			throw new RuntimeException("Did not initialize Manager");
		}
		return todoListManager;
	}
	
	public ToDoListManager(Context context) {
		this.context = context;
	}
	
	public ToDoList loadToDoList() throws ClassNotFoundException, IOException {
		SharedPreferences settings = context.getSharedPreferences(prefFile, Context.MODE_PRIVATE);
		String todoListData = settings.getString(listKey, "");
		if (todoListData.equals("")) {
			return new ToDoList();
		} else {
			return todoListFromString(todoListData);
		}
		
	}

	private ToDoList todoListFromString(String todoListData) throws ClassNotFoundException, IOException {
		ByteArrayInputStream bi = new ByteArrayInputStream(Base64.decode(todoListData, Base64.DEFAULT));
		ObjectInputStream oi = new ObjectInputStream(bi);
		return (ToDoList)oi.readObject();
		
	}
	
	private String todoListToString(ToDoList list) throws IOException {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream oo = new ObjectOutputStream(bo);
		oo.writeObject(list);
		oo.close();
		byte bytes[] = bo.toByteArray();
		return Base64.encodeToString(bytes, Base64.DEFAULT);
	}
	
	public void saveToDoList(ToDoList list) throws IOException {
		SharedPreferences settings = context.getSharedPreferences(prefFile, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putString(listKey, todoListToString(list));
		editor.commit();
	}

	
}

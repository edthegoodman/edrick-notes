package ca.ualberta.edrick.notes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class ToDoList implements Serializable {
	
	/**
	 * ToDoList Serialization ID
	 */
	private static final long serialVersionUID = 7292657333395976816L;
	private ArrayList<ToDoItem> todoList = null;
	private transient ArrayList<Listener> listeners;
	
	public ToDoList() {
		todoList = new ArrayList<ToDoItem>();
		listeners = new ArrayList<Listener>();
	}
	
	public ToDoList(Collection<ToDoItem> items) {
		todoList = new ArrayList<ToDoItem>(items);
		listeners = new ArrayList<Listener>();

	}
	
	private ArrayList<Listener> getListeners() {
		if (listeners == null ) {
			listeners = new ArrayList<Listener>();
		}
		return listeners;
	}
	
	public Collection<ToDoItem> getList() {
		return todoList;
	}
	
	public void addToDo(ToDoItem item) {
		todoList.add(item);
		notifyListeners();
	}

	public void removeToDo(ToDoItem item) {
		todoList.remove(item);
		notifyListeners();
	}

	public void removeAll() {
		todoList.clear();
		notifyListeners();
	}
	
	public int size() {
		return todoList.size();
	}
	
	public boolean contains(ToDoItem item) {
		return todoList.contains(item);
	}
	
	public void notifyListeners() {
		for (Listener listener : getListeners()) {
			listener.update();
		}
	}

	public ToDoItem getToDoItem(int pos) {
		return todoList.get(pos);
	}
	
	public void addListener(Listener l) {
		getListeners().add(l);
	}

	public void removeListener(Listener l) {
		getListeners().remove(l);
	}

	public void addAll(Collection<ToDoItem> items) {
		todoList.addAll(items);
	}
	
	public void clear() {
		todoList.clear();
	}
}

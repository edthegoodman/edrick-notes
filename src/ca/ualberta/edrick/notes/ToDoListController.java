package ca.ualberta.edrick.notes;

import java.io.IOException;

public class ToDoListController {

	// Lazy Singleton
	private static ToDoList todoList = null;
	static public ToDoList getToDoList() {
		if (todoList == null) {
			try {
				todoList = ToDoListManager.getManager().loadToDoList();
				todoList.addListener(new Listener() {
					@Override
					public void update() {
						saveToDoList();
					}
				});
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException("Could not deserialize ToDoList from ToDoListManager");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException("Could not deserialize ToDoList from ToDoListManager");
			}
		}
		return todoList;
	}

	static public void saveToDoList() {
		try {
			ToDoListManager.getManager().saveToDoList(getToDoList());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Could not deserialize ToDoList from ToDoListManager");
		}
	}

	public void addToDo(ToDoItem item) {
		getToDoList().addToDo(item);
	}

	public void removeToDo(ToDoItem item) {
		getToDoList().removeToDo(item);
	}

	public void archiveToDo(ToDoItem item) {
		// TODO
	}

	public void emailToDo(ToDoItem item) {
		// TODO
	}

	public void removeAll() {
		getToDoList().removeAll();
	}

	public void archiveAll() {
		// TODO
	}
}

package ca.ualberta.edrick.notes;

import java.io.IOException;

public class ArchivesListController {

	// Lazy Singleton
	private static ToDoList archivesList = null;
	static public ToDoList getToDoList() {
		if (archivesList == null) {
			try {
				archivesList = ArchivesListManager.getManager().loadArchivesList();
				archivesList.addListener(new Listener() {
					@Override
					public void update() {
						saveToDoList();
					}
				});
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException("Could not deserialize ToDoList from ToDoListManager");
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Could not deserialize ToDoList from ToDoListManager");
			}
		}
		return archivesList;
	}

	static public void saveToDoList() {
		try {
			ArchivesListManager.getManager().saveArchivesList(getToDoList());
		} catch (IOException e) {
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

	public void removeAll() {
		getToDoList().removeAll();
	}

}

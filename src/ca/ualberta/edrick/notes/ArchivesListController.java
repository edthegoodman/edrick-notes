/*
Student Picker: Randomly pick students to answer questions

Copyright (C) 2014 Abram Hindle abram.hindle@softwareprocess.ca
Copyright (C) 2014 Edrick de Guzman edrick@ualberta.ca

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.

*/
package ca.ualberta.edrick.notes;

import java.io.IOException;

/** This class was implemented by explicitly following Dr. Abram Hindle's StudentPicker videos 
 * https://www.youtube.com/playlist?list=PL240uJOh_Vb4PtMZ0f7N8ACYkCLv0673O 
*/
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

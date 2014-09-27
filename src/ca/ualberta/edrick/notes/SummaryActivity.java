/*
edrick-notes : A simple checklist app that can take todo items, check 
			   them off, archive, unarchive, delete, and email.

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

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class SummaryActivity extends Activity {
	
	/** This Activity is solely used for displaying statistics of ToDoList and ArchivesList */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summary);
		
		/** Initialize managers
		 * The managers are used to obtain the data from both lists
		 */
		ToDoListManager.initManager(this.getApplicationContext());
		ArchivesListManager.initManager(this.getApplicationContext());
		
		TextView total_todo = (TextView) findViewById(R.id.total_num_todo);
		TextView todo_notdone = (TextView) findViewById(R.id.notdone_todo);
		TextView todo_done = (TextView) findViewById(R.id.done_todo);
		TextView total_archived = (TextView) findViewById(R.id.total_num_archived);
		TextView archived_notdone = (TextView) findViewById(R.id.not_done_archived);
		TextView archived_done = (TextView) findViewById(R.id.done_archived);
		
		total_todo.setText("Total Number of ToDo Items : " + getTotalToDo());
		todo_notdone.setText("Items not done : " + getNotDoneToDo());
		todo_done.setText("Items done : " + getDoneToDo());
		total_archived.setText("Total Number of Archived Items : " + getTotalArchived());
		archived_notdone.setText("Archived items not done : " + getNotDoneArchived());
		archived_done.setText("Archived items done : " + getDoneArchived());
		 
	}

	public int getTotalToDo() {
		return ToDoListController.getToDoList().size();
	}
	
	public int getNotDoneToDo () {
		int notdone_count = 0;
		for (int i = 0; i < ToDoListController.getToDoList().size(); i++) {
			if (!ToDoListController.getToDoList().getToDoItem(i).isDone()) {
				notdone_count++;
			}
		}
		return notdone_count;
	}
	
	public int getDoneToDo () {
		int done_count = 0;
		for (int i = 0; i < ToDoListController.getToDoList().size(); i++) {
			if (ToDoListController.getToDoList().getToDoItem(i).isDone()) {
				done_count++;
			}
		}
		return done_count;
	}
	
	public int getTotalArchived() {
		return ArchivesListController.getToDoList().size();
	}
	
	public int getNotDoneArchived () {
		int notdone_count = 0;
		for (int i = 0; i < ArchivesListController.getToDoList().size(); i++) {
			if (!ArchivesListController.getToDoList().getToDoItem(i).isDone()) {
				notdone_count++;
			}
		}
		return notdone_count;
	}
	
	public int getDoneArchived () {
		int done_count = 0;
		for (int i = 0; i < ArchivesListController.getToDoList().size(); i++) {
			if (ArchivesListController.getToDoList().getToDoItem(i).isDone()) {
				done_count++;
			}
		}
		return done_count;
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.summary, menu);
		return true;
	}
}

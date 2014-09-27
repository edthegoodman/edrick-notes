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

import java.util.ArrayList;
import java.util.Collection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class ArchivesActivity extends Activity {

	private ArchivesListAdapter archivesAdapter;
	private ListView archivesView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_archives);

		/** Method sequence call */
		instantiateArchivesView();

	}

	public void instantiateArchivesView() {
		// Initialize Managers for Archives List and ToDo List
		ArchivesListManager.initManager(this.getApplicationContext());
		ToDoListManager.initManager(this.getApplicationContext());

		Collection<ToDoItem> items = ArchivesListController.getToDoList().getList();
		final ToDoList archivesList = new ToDoList(items);

		// Build the Adapter (Converts items into appropriate views)
		archivesAdapter = new ArchivesListAdapter(ArchivesActivity.this, archivesList);	

		// Notify adapter that archives arrived
		archivesAdapter.notifyDataSetChanged();

		// Configure list view
		archivesView = (ListView) findViewById(R.id.archives_list_view);
		archivesView.setAdapter(archivesAdapter);
		archivesAdapter.deselectAll();

		/** Observer, whenever ToDoList changes, this gets updated */
		/** This was taken directly from Dr. Hindles' StudentPicker
		 */
		ArchivesListController.getToDoList().addListener(new Listener() {
			@Override
			public void update() {
				System.out.println("Updating...");
				archivesList.clear();
				Collection<ToDoItem> items = ArchivesListController.getToDoList().getList();
				archivesList.addAll(items);
				archivesAdapter.notifyDataSetChanged();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.archives, menu);
		return true;
	}

	/** All the buttons and menus are implemented by calling the corresponding method to the button/menu
	 * in the "onClick" field of the buttons'/menus' xml file, this idea was taken from Dr. Hindle's StudentPicker
	 * video.
	 */
	
	/** Menu Item for Delete Archive */
	public void deleteArchive(View view) {

		final Collection<ToDoItem> items = ArchivesListController.getToDoList().getList();
		final ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);	
		final int count = listUpdate.size();

		/** The idea of dialogs cames from http://developer.android.com/guide/topics/ui/dialogs.html
		 */
		AlertDialog.Builder alert = new AlertDialog.Builder(ArchivesActivity.this);
		alert.setMessage("Do you want to continue deleting?");
		alert.setCancelable(true);

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				for (int i = 0; i < count; i++) {
					if (listUpdate.get(i).isChecked()) {
						ArchivesListController.getToDoList().removeToDo(listUpdate.get(i));
					}
				}
			}
		});
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do Nothing
			}
		});
		alert.show();
	}

	/** Menu Item for Unarchive Archive */
	public void unarchiveArchive(View view) {

		final Collection<ToDoItem> items = ArchivesListController.getToDoList().getList();
		final ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);	
		final int count = listUpdate.size();

		/** The idea of dialogs cames from http://developer.android.com/guide/topics/ui/dialogs.html
		 */
		AlertDialog.Builder alert = new AlertDialog.Builder(ArchivesActivity.this);
		alert.setMessage("Do you want to continue unarchiving?");
		alert.setCancelable(true);

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				for (int i = 0; i < count; i++) {
					if (listUpdate.get(i).isChecked()) {
						ToDoListController.getToDoList().addToDo(listUpdate.get(i));
						ArchivesListController.getToDoList().removeToDo(listUpdate.get(i));
					}
				}
			}
		});
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do Nothing
			}
		});
		alert.show();
	}


	/** Menu Item for Email Archive */
	/** Emailing was done by sending intents to another activity such as an email client, and this was made possible through the use of intents.
	 *  The idea came from http://developer.android.com/training/sharing/send.html.
	 */
	public void emailArchive(View view) {
		final Collection<ToDoItem> items = ArchivesListController.getToDoList().getList();
		final ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);	
		final int count = listUpdate.size();

		// Open Alert Dialog asking for Email Address
		/** The idea of using LinearLayout.LayoutParams to set EditText component as the view of AlertDialog was taken from
		 * user "Raghunandan" over at stackoverflow.com.
		 * http://stackoverflow.com/questions/18799216/how-to-make-a-edittext-box-in-a-dialog
		 */
		/** The idea of dialogs cames from http://developer.android.com/guide/topics/ui/dialogs.html
		 */
		AlertDialog.Builder emailAlert = new AlertDialog.Builder(ArchivesActivity.this);
		emailAlert.setMessage("Enter E-mail Adress");
		final EditText emailaddress = new EditText(ArchivesActivity.this);  
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		emailaddress.setLayoutParams(layoutParams);
		emailAlert.setView(emailaddress);
		emailAlert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(ArchivesActivity.this, "Email Address : " + emailaddress.getText().toString(), Toast.LENGTH_SHORT).show();
				Intent email = new Intent(Intent.ACTION_SEND);
				email.setType("plaint/text");
				email.putExtra(Intent.EXTRA_EMAIL, new String[]{emailaddress.getText().toString()});
				email.putExtra(Intent.EXTRA_SUBJECT, "Your ToDo Items");

				// Using string builder to concatenate the items to be passed to an email client
				StringBuilder sb = new StringBuilder();;

				for(int i = 0; i < count; i++) {
					if(listUpdate.get(i).isChecked()){
						if(listUpdate.get(i).isDone()) {
							sb.append("[X]   " + listUpdate.get(i).getItemName().toString() + "\n");
							sb.append("\n");
						} else {
							sb.append("[   ]   " + listUpdate.get(i).getItemName().toString() + "\n");
							sb.append("\n");
						}
					} 
				}
				email.putExtra(Intent.EXTRA_TEXT, sb.toString());

				startActivity(Intent.createChooser(email, "Choose an Email client :"));
			}
		});
		emailAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do Nothing
			}
		});
		emailAlert.show();
	}

	/** Menu Item for Delete All */
	public void deleteAllArchives(MenuItem menu) {
		/** The idea of dialogs cames from http://developer.android.com/guide/topics/ui/dialogs.html
		 */
		AlertDialog.Builder alert = new AlertDialog.Builder(ArchivesActivity.this);
		alert.setMessage("Do you want to continue deleting?");
		alert.setCancelable(true);

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (ArchivesListController.getToDoList().getList().size() != 0) {
					ArchivesListController.getToDoList().removeAll();
				}
			}
		});
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do Nothing
			}
		});
		alert.show();
	}

	/** Menu Item for Unarchive All Archives */
	public void unarchiveAllArchives(MenuItem menu) {

		final Collection<ToDoItem> items = ArchivesListController.getToDoList().getList();
		final ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);	
		final int count = listUpdate.size();

		/** The idea of dialogs cames from http://developer.android.com/guide/topics/ui/dialogs.html
		 */
		AlertDialog.Builder alert = new AlertDialog.Builder(ArchivesActivity.this);
		alert.setMessage("Do you want to continue unarchiving all items?");
		alert.setCancelable(true);

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				for (int i = 0; i < count; i++) {
					ToDoListController.getToDoList().addToDo(listUpdate.get(i));
					ArchivesListController.getToDoList().removeToDo(listUpdate.get(i));
				}
			}
		});
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do Nothing
			}
		});
		alert.show();
	}

	/** Menu Item for Email All Archives */
	/** Emailing was done by sending intents to another activity such as an email client, and this was made possible through the use of intents.
	 *  The idea came from http://developer.android.com/training/sharing/send.html.
	 */
	public void emailAllArchives(MenuItem menu) {
		final Collection<ToDoItem> items = ArchivesListController.getToDoList().getList();
		final ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);	
		final int count = listUpdate.size();

		// Open Alert Dialog asking for Email Address
		/** The idea of dialogs cames from http://developer.android.com/guide/topics/ui/dialogs.html
		 */
		/** The idea of using LinearLayout.LayoutParams to set EditText component as the view of AlertDialog was taken from
		 * user "Raghunandan" over at stackoverflow.com.
		 * http://stackoverflow.com/questions/18799216/how-to-make-a-edittext-box-in-a-dialog
		 */
		AlertDialog.Builder emailAlert = new AlertDialog.Builder(ArchivesActivity.this);
		emailAlert.setMessage("Enter E-mail Adress");
		final EditText emailaddress = new EditText(ArchivesActivity.this);  
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		emailaddress.setLayoutParams(layoutParams);
		emailAlert.setView(emailaddress);
		emailAlert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(ArchivesActivity.this, "Email Address : " + emailaddress.getText().toString(), Toast.LENGTH_SHORT).show();
				Intent email = new Intent(Intent.ACTION_SEND);
				email.setType("plain/text");
				email.putExtra(Intent.EXTRA_EMAIL, new String[]{emailaddress.getText().toString()});
				email.putExtra(Intent.EXTRA_SUBJECT, "Your ToDo Items");

				// Using string builder to concatenate the items to be passed to an email client
				StringBuilder sb = new StringBuilder();;

				for(int i = 0; i < count; i++) {
					if(listUpdate.get(i).isDone()) {
						sb.append("[X]   " + listUpdate.get(i).getItemName().toString() + "\n");
						sb.append("\n");
					} else {
						sb.append("[   ]   " + listUpdate.get(i).getItemName().toString() + "\n");
						sb.append("\n");
					}
				}
				email.putExtra(Intent.EXTRA_TEXT, sb.toString());
				startActivity(Intent.createChooser(email, "Choose an Email client :"));
			}
		});
		emailAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do Nothing
			}
		});
		emailAlert.show();
	}

	/** Menu action to go into Summary Activity */
	public void openSummary(MenuItem menu) {
		Intent intent = new Intent(ArchivesActivity.this, SummaryActivity.class);
		startActivity(intent);
	}
}

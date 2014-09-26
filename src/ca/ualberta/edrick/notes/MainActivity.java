package ca.ualberta.edrick.notes;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.http.protocol.HTTP;

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

public class MainActivity extends Activity {

	private EditText entryText;
	private ListView todoListView;
	private CustomAdapter adapter;

	/** List of Problems/Things to do 
	   1) UML Docs, Licenses, README
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		entryText = (EditText) findViewById(R.id.editText1);

		instantiateListView();
	}

	/** Instantiate the ListView with the items */
	private void instantiateListView() {
		// Initialize Managers for Archives List and ToDo List
		ToDoListManager.initManager(this.getApplicationContext());
		ArchivesListManager.initManager(this.getApplicationContext());

		Collection<ToDoItem> items = ToDoListController.getToDoList().getList();
		final ToDoList list = new ToDoList(items);
		
		// Build the Adapter (Converts items into appropriate views)
		adapter = new CustomAdapter(this, list);

		// Configure list view.
		todoListView = (ListView) findViewById(R.id.listViewMain);
		todoListView.setAdapter(adapter);
		adapter.deselectAll();


		/** Observer, whenever ToDoList changes, this gets updated */
		ToDoListController.getToDoList().addListener(new Listener() {
			@Override
			public void update() {
				System.out.println("Updating...");
				list.clear();
				Collection<ToDoItem> items = ToDoListController.getToDoList().getList();
				list.addAll(items);
				adapter.notifyDataSetChanged();
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/** Listener for add Button */
	public void addItems(View v) {
		ToDoListController.getToDoList().addToDo(new ToDoItem(entryText.getText().toString(),false,false));
		System.out.println("Add Clicked");
		Collection<ToDoItem> items = ToDoListController.getToDoList().getList();
		entryText.setText("");
		ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);
		System.out.println("list update: " + listUpdate.size());
		Toast.makeText(this, "Entries Added", Toast.LENGTH_SHORT).show();
	}

	/** Listener for delete Button */
	public void deleteItems(View v) {

		final Collection<ToDoItem> items = ToDoListController.getToDoList().getList();
		final ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);	
		final int count = listUpdate.size();

		AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
		alert.setMessage("Do you want to continue deleting?");
		alert.setCancelable(true);

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				for (int i = 0; i < count; i++) {
					if (listUpdate.get(i).isChecked()) {
						System.out.println("THIS ITEM IS TO BE REMOVED");
						ToDoListController.getToDoList().removeToDo(listUpdate.get(i));
						System.out.println("List Update : " + items.size());
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

	/** Listener for archive Button */
	public void archiveItems(View v) {

		final Collection<ToDoItem> items = ToDoListController.getToDoList().getList();
		final ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);
		final int count = listUpdate.size();

		for (int i = 0; i < count; i++) {
			if (listUpdate.get(i).isChecked()) {
				System.out.println("THIS ITEM IS TO BE REMOVED");
				ArchivesListController.getToDoList().addToDo(listUpdate.get(i));
				System.out.println("ArchivesList count : " + ArchivesListController.getToDoList().size());
				ToDoListController.getToDoList().removeToDo(listUpdate.get(i));
				System.out.println("List Update : " + items.size());
			}
		}
	}

	/** Listener for email Button */
	public void emailItems(View v) {

		final Collection<ToDoItem> items = ToDoListController.getToDoList().getList();
		final ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);	
		final int count = listUpdate.size();

		// Open Alert Dialog asking for Email Address
		AlertDialog.Builder emailAlert = new AlertDialog.Builder(MainActivity.this);
		emailAlert.setMessage("Enter E-mail Adress");
		final EditText emailaddress = new EditText(MainActivity.this);  
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		emailaddress.setLayoutParams(lp);
		emailAlert.setView(emailaddress);
		emailAlert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(MainActivity.this, "Email Address : " + emailaddress.getText().toString(), Toast.LENGTH_SHORT).show();
				Intent email = new Intent(Intent.ACTION_SEND);
				email.setType(HTTP.PLAIN_TEXT_TYPE);
				email.putExtra(Intent.EXTRA_EMAIL, new String[]{emailaddress.getText().toString()});
				email.putExtra(Intent.EXTRA_SUBJECT, "Your ToDo Items");

				// StringBuilder
				StringBuilder sb = new StringBuilder();;

				for(int i = 0; i < count; i++) {
					if(listUpdate.get(i).isChecked()){
						if(listUpdate.get(i).isDone()) {
							sb.append("[X]   " + listUpdate.get(i).getItemName().toString() + "\n");
							sb.append("\n");
						} else {
							sb.append("[   ]   " + listUpdate.get(i).getItemName().toString() + '\n');
							sb.append("\n");
						}
					} 
				}
				email.putExtra(Intent.EXTRA_TEXT, sb.toString());

				//need this to prompts email client only
				email.setType("message/rfc822");

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

	/** Menu action button for "Delete All" */
	public void deleteAll(MenuItem menu) {
		final Collection<ToDoItem> items = ToDoListController.getToDoList().getList();

		AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
		alert.setMessage("Do you want to continue deleting?");
		alert.setCancelable(true);
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (ToDoListController.getToDoList().getList().size() != 0) {
					System.out.println("THIS ITEM IS TO BE REMOVED");
					ToDoListController.getToDoList().removeAll();
					System.out.println("List Update : " + items.size());
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

	/** Menu action button for "Email All" */
	public void emailAll(MenuItem menu) {
		final Collection<ToDoItem> items = ToDoListController.getToDoList().getList();
		final ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);	
		final int count = listUpdate.size();

		// Open Alert Dialog asking for Email Address
		AlertDialog.Builder emailAlert = new AlertDialog.Builder(MainActivity.this);
		emailAlert.setMessage("Enter E-mail Adress");
		final EditText emailaddress = new EditText(MainActivity.this);  
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		emailaddress.setLayoutParams(lp);
		emailAlert.setView(emailaddress);
		emailAlert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(MainActivity.this, "Email Address : " + emailaddress.getText().toString(), Toast.LENGTH_SHORT).show();
				Intent email = new Intent(Intent.ACTION_SEND);
				email.setType(HTTP.PLAIN_TEXT_TYPE);
				email.putExtra(Intent.EXTRA_EMAIL, new String[]{emailaddress.getText().toString()});
				email.putExtra(Intent.EXTRA_SUBJECT, "Your ToDo Items");

				// StringBuilder
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

				//need this to prompts email client only
				email.setType("message/rfc822");

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

	/** Menu action button for "Archive All" */
	public void archiveAll(MenuItem menu) {
		final Collection<ToDoItem> items = ToDoListController.getToDoList().getList();
		final ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);	
		final int count = listUpdate.size();

		AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
		alert.setMessage("Do you want to continue archiving all items?");
		alert.setCancelable(true);

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				for (int i = 0; i < count; i++) {
					System.out.println("THIS ITEM IS TO BE REMOVED");
					ArchivesListController.getToDoList().addToDo(listUpdate.get(i));
					System.out.println("ToDoList count : " + ArchivesListController.getToDoList().size());
					ToDoListController.getToDoList().removeToDo(listUpdate.get(i));
					System.out.println("ArchivesList count : " + items.size());
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
	
	/** Menu action to go into ArchivesActivity */
	public void openArchives(MenuItem menu) {
		Intent intent = new Intent(MainActivity.this, ArchivesActivity.class);
		startActivity(intent);
	}
	
	/** Menu action to go into Summary Activity */
	public void openSummary(MenuItem menu) {
		Intent intent = new Intent(MainActivity.this, SummaryActivity.class);
		startActivity(intent);
	}
}

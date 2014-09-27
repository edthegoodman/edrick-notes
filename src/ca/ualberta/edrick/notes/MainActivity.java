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

public class MainActivity extends Activity {

	private EditText entryText;
	private ListView todoListView;
	private CustomAdapter adapter;

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
		/** This was taken directly from Dr. Hindles' StudentPicker
		 */
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
		// Inflate the menu, this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/** All the buttons and menus are implemented by calling the corresponding method to the button/menu
	 * in the "onClick" field of the buttons'/menus' xml file, this idea was taken from Dr. Hindle's StudentPicker
	 * video.
	 */
	
	/** Listener for add Button */
	public void addItems(View v) {
		ToDoListController.getToDoList().addToDo(new ToDoItem(entryText.getText().toString(),false,false));
		entryText.setText("");
		Toast.makeText(this, "Entry Added", Toast.LENGTH_SHORT).show();
	}

	/** Listener for delete Button */
	public void deleteItems(View v) {

		final Collection<ToDoItem> items = ToDoListController.getToDoList().getList();
		final ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);	
		final int count = listUpdate.size();

		/** The idea of dialogs cames from http://developer.android.com/guide/topics/ui/dialogs.html
		 */
		AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
		alert.setMessage("Do you want to continue deleting?");
		alert.setCancelable(true);

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				for (int i = 0; i < count; i++) {
					if (listUpdate.get(i).isChecked()) {
						ToDoListController.getToDoList().removeToDo(listUpdate.get(i));
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
				ArchivesListController.getToDoList().addToDo(listUpdate.get(i));
				ToDoListController.getToDoList().removeToDo(listUpdate.get(i));
			}
		}
	}

	/** Listener for email Button */
	/** Emailing was done by sending intents to another activity such as an email client, and this was made possible through the use of intents.
	 *  The idea came from http://developer.android.com/training/sharing/send.html.
	 */
	public void emailItems(View v) {

		final Collection<ToDoItem> items = ToDoListController.getToDoList().getList();
		final ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);	
		final int count = listUpdate.size();

		// Open Alert Dialog asking for Email Address
		/** The idea of using LinearLayout.LayoutParams to set EditText component as the view of AlertDialog was taken from
		 * user "Raghunandan" over at stackoverflow.com.
		 * http://stackoverflow.com/questions/18799216/how-to-make-a-edittext-box-in-a-dialog
		 */
		/** The idea of dialogs cames from http://developer.android.com/guide/topics/ui/dialogs.html
		 */
		AlertDialog.Builder emailAlert = new AlertDialog.Builder(MainActivity.this);
		emailAlert.setMessage("Enter E-mail Adress");
		final EditText emailaddress = new EditText(MainActivity.this);  
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		emailaddress.setLayoutParams(layoutParams);
		emailAlert.setView(emailaddress);
		emailAlert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(MainActivity.this, "Email Address : " + emailaddress.getText().toString(), Toast.LENGTH_SHORT).show();
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
							sb.append("[   ]   " + listUpdate.get(i).getItemName().toString() + '\n');
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

	/** Menu action button for "Delete All" */
	public void deleteAll(MenuItem menu) {
		/** The idea of dialogs cames from http://developer.android.com/guide/topics/ui/dialogs.html
		 */
		AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
		alert.setMessage("Do you want to continue deleting?");
		alert.setCancelable(true);
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (ToDoListController.getToDoList().getList().size() != 0) {
					ToDoListController.getToDoList().removeAll();
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
	/** Emailing was done by sending intents to another activity such as an email client, and this was made possible through the use of intents.
	 *  The idea came from http://developer.android.com/training/sharing/send.html.
	 */
	public void emailAll(MenuItem menu) {
		final Collection<ToDoItem> items = ToDoListController.getToDoList().getList();
		final ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);	
		final int count = listUpdate.size();

		// Open Alert Dialog asking for Email Address
		/** The idea of using LinearLayout.LayoutParams to set EditText component as the view of AlertDialog was taken from
		 * user "Raghunandan" over at stackoverflow.com.
		 * http://stackoverflow.com/questions/18799216/how-to-make-a-edittext-box-in-a-dialog
		 */
		/** The idea of dialogs cames from http://developer.android.com/guide/topics/ui/dialogs.html
		 */
		AlertDialog.Builder emailAlert = new AlertDialog.Builder(MainActivity.this);
		emailAlert.setMessage("Enter E-mail Adress");
		final EditText emailaddress = new EditText(MainActivity.this);  
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		emailaddress.setLayoutParams(layoutParams);
		emailAlert.setView(emailaddress);
		emailAlert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(MainActivity.this, "Email Address : " + emailaddress.getText().toString(), Toast.LENGTH_SHORT).show();
				Intent email = new Intent(Intent.ACTION_SEND);
				email.setType("plaint/text");
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

	/** Menu action button for "Archive All" */
	public void archiveAll(MenuItem menu) {
		final Collection<ToDoItem> items = ToDoListController.getToDoList().getList();
		final ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);	
		final int count = listUpdate.size();

		/** The idea of dialogs cames from http://developer.android.com/guide/topics/ui/dialogs.html
		 */
		AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
		alert.setMessage("Do you want to continue archiving all items?");
		alert.setCancelable(true);

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				for (int i = 0; i < count; i++) {
					ArchivesListController.getToDoList().addToDo(listUpdate.get(i));
					ToDoListController.getToDoList().removeToDo(listUpdate.get(i));
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

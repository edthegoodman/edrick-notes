package ca.ualberta.edrick.notes;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

// Array of options --> ArrayAdapter --> ListView

// List view: {composed of a set of views:da_item.xml}

public class MainActivity extends Activity {
	private Button addButton;
	private Button deleteButton;
	private Button archiveButton;
	private Button emailButton;
	private EditText entryText;
	private ListView todoListView;
	private ArrayList<ToDoItem> myItems;
	private ArrayList<ToDoItem> moveList;
	private ArrayList<ToDoItem> movedDataList;
	private CustomAdapter adapter;
	private Integer todoCounter;
	public final static String MOVE_DATA_LIST = "ca.ualberta.edrick.notes.MOVE_DATA";
	public final static String EXTRA_MESSAGE = "ca.ualberta.edrick.notes.MESSAGE";
	public final static String OUTSTATE_ARRAY_LIST = "ca.ualberta.edrick.notes.ARRAY_LIST";
	public final static String TODO_ARRAY_LIST = "ca.ualberta.edrick.notes.TODO_LIST";
	public SharedPreferences myTODOprefs;
	public SharedPreferences.Editor myTODOeditor;
	private int counter=0;
	//private ListView list;
	private Object mActionMode;
	public int selectedItem = -1;

	/** List of Problems/Things to do 
	 * 1) Entries are saved but not their checkbox state
	 * 2) No Emailing Yet
	 * 3) No Delete/Archive/Email All
	 * 4) Hidden settings are not showing on real device
	 * 5) Add a "Summary" Tab for,
	 	- total number of TODO items checked (meaning TODO items that was accomplished)
		- total number of TODO items left unchecked
		- total number of archived TODO items 
		- total number of checked archived TODO items
		- total number of unchecked archived TODO items
	   6) Save state of item (checked or not) when archiving
	 */

	// Counter for when user sees the app, user sees the app when resume method is called
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//addButton = (Button) findViewById(R.id.button_enter);
		//deleteButton = (Button) findViewById(R.id.button_delete);
		//archiveButton = (Button) findViewById(R.id.button_archive);
		//emailButton = (Button) findViewById(R.id.button_email);

		entryText = (EditText) findViewById(R.id.editText1);		
		instantiateListView();
		//createButtons();
	}
	
	/** Instantiate the ListView with the items */
	private void instantiateListView() {
		// Create list of items
		ToDoListManager.initManager(this.getApplicationContext());

		Collection<ToDoItem> items = ToDoListController.getToDoList().getList();
		final ToDoList list = new ToDoList(items);

		// Build the Adapter (Converts items into appropriate views)
		adapter = new CustomAdapter(this, list);

		// Configure list view.
		todoListView = (ListView) findViewById(R.id.listViewMain);
		todoListView.setAdapter(adapter);

		
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
		Toast.makeText(this, "Add Button", Toast.LENGTH_SHORT).show();
		ToDoListController.getToDoList().addToDo(new ToDoItem(entryText.getText().toString(),false,false));
		System.out.println("Add Clicked");
		Collection<ToDoItem> items = ToDoListController.getToDoList().getList();
		entryText.setText("");
		ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);
		System.out.println("list update: " + listUpdate.size());
	}

	/** Listener for delete Button */
	public void deleteItems(View v) {
		Toast.makeText(this, "Delete Button", Toast.LENGTH_SHORT).show();

		final Collection<ToDoItem> items = ToDoListController.getToDoList().getList();
		final ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);	
		final int count = listUpdate.size();

		AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
		alert.setMessage("Do you want to continue deleting?");
		alert.setCancelable(true);

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
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
		Toast.makeText(this, "Archive Button", Toast.LENGTH_SHORT).show();
		/** getcheckeditempositions test */
		int count = todoListView.getCount();
		SparseBooleanArray checkedItems = todoListView.getCheckedItemPositions();
		System.out.println("CHECKED ITEMS : " + checkedItems.size());
	}

	/** Listener for email Button */
	public void emailItems(View v) {
		Toast.makeText(this, "Email Button", Toast.LENGTH_SHORT).show();

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
						} else {
							sb.append("[   ]   " + listUpdate.get(i).getItemName().toString() + '\n');
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

	/** Menu action button for "Archives", intent is passed here */
	public void openArchives(MenuItem menu) {
		Intent intent = new Intent(MainActivity.this, ArchivesActivity.class);
		//intent.putStringArrayListExtra(MOVE_DATA_LIST, moveList);
		if (moveList != null) {
			startActivityForResult(intent, 69);
			moveList.clear();
		} else {
			startActivityForResult(intent, 69);
		}
	}

	/** Menu action button for "Delete All" */
	public void deleteAll(MenuItem menu) {
		Toast.makeText(this, "Delete All Button", Toast.LENGTH_SHORT).show();
		final Collection<ToDoItem> items = ToDoListController.getToDoList().getList();

		AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
		alert.setMessage("Do you want to continue deleting?");
		alert.setCancelable(true);
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
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
		Toast.makeText(this, "Email All Button", Toast.LENGTH_SHORT).show();
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
					} else {
						sb.append("[   ]   " + listUpdate.get(i).getItemName().toString() + '\n');
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
		Toast.makeText(this, "Archive All Button", Toast.LENGTH_SHORT).show();
	}

	
}

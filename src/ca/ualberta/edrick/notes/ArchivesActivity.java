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

public class ArchivesActivity extends Activity {

	private CustomAdapter archivesAdapter;
	private ListView archivesView;
	public final static String UNARCHIVE_DATA_LIST = "ca.ualberta.edrick.notes.UNARCHIVED_ITEMS";
	public int selectedItem = -1;


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

		System.out.println("INITIALIZATION...");

		System.out.println("Instantiate items count : " + items.size());
		System.out.println("ArchivesList count : " + archivesList.size());

		// Build the Adapter (Converts items into appropriate views)
		archivesAdapter = new CustomAdapter(ArchivesActivity.this, archivesList);	

		// Notify adapter that archives arrived
		archivesAdapter.notifyDataSetChanged();

		// Configure list view
		archivesView = (ListView) findViewById(R.id.archives_list_view);
		archivesView.setAdapter(archivesAdapter);

		/** Observer, whenever ToDoList changes, this gets updated */
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

	/** Menu Item for Delete Archive */
	public void deleteArchive(View view) {

		final Collection<ToDoItem> items = ArchivesListController.getToDoList().getList();
		final ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);	
		final int count = listUpdate.size();

		System.out.println("Current list count before delete : " + count);
		System.out.println("Items count : " + items.size());

		AlertDialog.Builder alert = new AlertDialog.Builder(ArchivesActivity.this);
		alert.setMessage("Do you want to continue deleting?");
		alert.setCancelable(true);

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				for (int i = 0; i < count; i++) {
					if (listUpdate.get(i).isChecked()) {
						System.out.println("THIS ITEM IS TO BE REMOVED");
						ArchivesListController.getToDoList().removeToDo(listUpdate.get(i));
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

	/** Menu Item for Unarchive Archive */
	public void unarchiveArchive(View view) {

		final Collection<ToDoItem> items = ArchivesListController.getToDoList().getList();
		final ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);	
		final int count = listUpdate.size();

		AlertDialog.Builder alert = new AlertDialog.Builder(ArchivesActivity.this);
		alert.setMessage("Do you want to continue unarchiving?");
		alert.setCancelable(true);

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				for (int i = 0; i < count; i++) {
					if (listUpdate.get(i).isChecked()) {
						System.out.println("THIS ITEM IS TO BE REMOVED");
						ToDoListController.getToDoList().addToDo(listUpdate.get(i));
						System.out.println("ToDoList count : " + ArchivesListController.getToDoList().size());
						ArchivesListController.getToDoList().removeToDo(listUpdate.get(i));
						System.out.println("ArchivesList count : " + items.size());
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
	public void emailArchive(View view) {
		final Collection<ToDoItem> items = ArchivesListController.getToDoList().getList();
		final ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);	
		final int count = listUpdate.size();

		// Open Alert Dialog asking for Email Address
		AlertDialog.Builder emailAlert = new AlertDialog.Builder(ArchivesActivity.this);
		emailAlert.setMessage("Enter E-mail Adress");
		final EditText emailaddress = new EditText(ArchivesActivity.this);  
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		emailaddress.setLayoutParams(lp);
		emailAlert.setView(emailaddress);
		emailAlert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(ArchivesActivity.this, "Email Address : " + emailaddress.getText().toString(), Toast.LENGTH_SHORT).show();
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

	/** Menu Item for Delete All */
	public void deleteAllArchives(MenuItem menu) {
		final Collection<ToDoItem> items = ArchivesListController.getToDoList().getList();
		final ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);	
		final int count = listUpdate.size();

		System.out.println("Current list count before delete : " + count);
		System.out.println("Items count : " + items.size());

		AlertDialog.Builder alert = new AlertDialog.Builder(ArchivesActivity.this);
		alert.setMessage("Do you want to continue deleting?");
		alert.setCancelable(true);

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				for (int i = 0; i < count; i++) {
					System.out.println("THIS ITEM IS TO BE REMOVED");
					ArchivesListController.getToDoList().removeToDo(listUpdate.get(i));
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

	/** Menu Item for Unarchive All Archives */
	public void unarchiveAllArchives(MenuItem menu) {

		final Collection<ToDoItem> items = ArchivesListController.getToDoList().getList();
		final ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);	
		final int count = listUpdate.size();

		AlertDialog.Builder alert = new AlertDialog.Builder(ArchivesActivity.this);
		alert.setMessage("Do you want to continue unarchiving all items?");
		alert.setCancelable(true);

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				for (int i = 0; i < count; i++) {
					System.out.println("THIS ITEM IS TO BE REMOVED");
					ToDoListController.getToDoList().addToDo(listUpdate.get(i));
					System.out.println("ToDoList count : " + ArchivesListController.getToDoList().size());
					ArchivesListController.getToDoList().removeToDo(listUpdate.get(i));
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

	/** Menu Item for Email All Archives */
	public void emailAllArchives(MenuItem menu) {
		final Collection<ToDoItem> items = ArchivesListController.getToDoList().getList();
		final ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);	
		final int count = listUpdate.size();

		// Open Alert Dialog asking for Email Address
		AlertDialog.Builder emailAlert = new AlertDialog.Builder(ArchivesActivity.this);
		emailAlert.setMessage("Enter E-mail Adress");
		final EditText emailaddress = new EditText(ArchivesActivity.this);  
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		emailaddress.setLayoutParams(lp);
		emailAlert.setView(emailaddress);
		emailAlert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(ArchivesActivity.this, "Email Address : " + emailaddress.getText().toString(), Toast.LENGTH_SHORT).show();
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

	/** Menu action to go into Summary Activity */
	public void openSummary(MenuItem menu) {
		Intent intent = new Intent(ArchivesActivity.this, SummaryActivity.class);
		startActivity(intent);
	}
	
	@Override
	public void onBackPressed() {
		System.out.println("Back button was pressed...");
		super.onBackPressed();
		finish();
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}
}

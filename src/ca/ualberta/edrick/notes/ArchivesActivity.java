package ca.ualberta.edrick.notes;

import java.util.ArrayList;
import java.util.Collection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class ArchivesActivity extends Activity {

	/** Plan for ArchivesActivity
	 * 1) Populate function
	 * 		- retrieve ArrayList<String> from intent
	 * 		- 
	 * 2) Unarchive Function
	 * 3) Email Function
	 * 4) 
	 */

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
		// Create List of Items, these items come from Main Activity
		ArchivesListManager.initManager(this.getApplicationContext());
		
		Collection<ToDoItem> items = ArchivesListController.getToDoList().getList();
		final ToDoList archivesList = new ToDoList(items);
		
		System.out.println("INITIALIZATION...");

		// Add Test archives
		archivesList.addToDo(new ToDoItem("Archive 1",false,true));
		archivesList.addToDo(new ToDoItem("Archive 2",false,true));
		archivesList.addToDo(new ToDoItem("Archive 3",false, true));
		
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
		Toast.makeText(ArchivesActivity.this, "Delete Archive Button", Toast.LENGTH_SHORT).show();
		
		final Collection<ToDoItem> items = ArchivesListController.getToDoList().getList();
		final ArrayList<ToDoItem> listUpdate = new ArrayList<ToDoItem>(items);	
		final int count = listUpdate.size();
		
		System.out.println("Current list count before delete : " + count);

		AlertDialog.Builder alert = new AlertDialog.Builder(ArchivesActivity.this);
		alert.setMessage("Do you want to continue deleting?");
		alert.setCancelable(true);

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
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
		Toast.makeText(ArchivesActivity.this, "Unarchive Archive Button", Toast.LENGTH_SHORT).show();
	}
	
	/** Menu Item for Email Archive */
	public void emailArchive(View view) {
		Toast.makeText(ArchivesActivity.this, "Email Archive Button", Toast.LENGTH_SHORT).show();
	}
	
	/** Menu Item for Delete All */
	public void deleteAllArchives(MenuItem menu) {
		Toast.makeText(ArchivesActivity.this, "Delete All Archives Button", Toast.LENGTH_SHORT).show();
	}
	
	/** Menu Item for Unarchive All Archives */
	public void unarchiveAllArchives(MenuItem menu) {
		Toast.makeText(ArchivesActivity.this, "Unarchive All Archives Button", Toast.LENGTH_SHORT).show();
	}
	
	/** Menu Item for Email All Archives */
	public void emailAllArchives(MenuItem menu) {
		Toast.makeText(ArchivesActivity.this, "Email All Archives Button", Toast.LENGTH_SHORT).show();
	}
	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
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

package ca.ualberta.edrick.notes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

// Array of options --> ArrayAdapter --> ListView

// List view: {composed of a set of views:da_item.xml}

public class MainActivity extends Activity {
	private Button addButton;
	//private Button deleteButton;
	//private Button archiveButton;
	private EditText entryText;
	private ListView todoListView;
	private ArrayList<String> myItems;
	private ArrayList<String> moveList;
	private ArrayList<String> movedDataList;
	private ArrayAdapter<String> adapter;
	private TextView textcounter;
	private Integer todoCounter;
	public final static String MOVE_DATA_LIST = "ca.ualberta.edrick.notes.MOVE_DATA";
	public final static String EXTRA_MESSAGE = "ca.ualberta.edrick.notes.MESSAGE";
	public final static String OUTSTATE_ARRAY_LIST = "ca.ualberta.edrick.notes.ARRAY_LIST";
	public final static String TODO_ARRAY_LIST = "ca.ualberta.edrick.notes.TODO_LIST";
	public SharedPreferences myTODOprefs;
	public SharedPreferences.Editor myTODOeditor;
	private int counter=0;
	private ListView list;


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

		addButton = (Button) findViewById(R.id.button_enter);
		//deleteButton = (Button) findViewById(R.id.button_delete);
		//archiveButton = (Button) findViewById(R.id.button_archive);
		entryText = (EditText) findViewById(R.id.editText1);

		instantiateListView();
		itemSelection();
		createButtons();
	}

	// Used for saving state
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		counter++;
		//restoreListViewState();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if (moveList != null) {
			System.out.println("AUTOSAVE");
			Set<String> autoSaveSet = new HashSet<String>(moveList);

			SharedPreferences autoSavePref = getSharedPreferences("autoSave", MODE_PRIVATE);
			SharedPreferences.Editor autoSaveEditor = autoSavePref.edit();

			// Store Set<String>
			autoSaveEditor.putStringSet("autoSaveSet", autoSaveSet);
			autoSaveEditor.commit();
		}

		finish();
	}
	
	// Retrieve Items from autosave
	public ArrayList<String> retrieveAutoSave() {
		SharedPreferences autoSavePref = getSharedPreferences("autoSave", MODE_PRIVATE);
		Set<String> defaultSet = new HashSet<String>();
		Set<String> autoSaveSet = autoSavePref.getStringSet("autoSaveSet", defaultSet);
		ArrayList<String> retrievedAutoSaveList = new ArrayList<String>(autoSaveSet);
		return retrievedAutoSaveList;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putInt("counter", counter);
		Log.d("edrick", counter + " was saved");
		SparseBooleanArray checkedItemPositions = todoListView.getCheckedItemPositions();
		int checkedItemCount = todoListView.getCount();

		// Store checked items' position and boolean state

		for(int i=checkedItemCount-1; i >= 0; i--){
			if(checkedItemPositions.get(i)){
				saveItemsState(String.valueOf(i),checkedItemPositions.get(i));
			}
		}
		checkedItemPositions.clear();
		adapter.notifyDataSetChanged();	
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		counter = savedInstanceState.getInt("counter");
		Log.d("edrick", counter + " was restored");
		// Restore value of members from saved state
		Log.d("RESTORE", "RESTORED");
		//restoreListViewState();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.archive_action:
			openArchives();
			return true;
		case R.id.action_settings:
			//openSettings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/** Menu action button for "Archives", intent is passed here */
	public void openArchives() {
		Intent intent = new Intent(MainActivity.this, ArchivesActivity.class);
		intent.putStringArrayListExtra(MOVE_DATA_LIST, moveList);
		if (moveList != null) {
			startActivityForResult(intent, 69);
			moveList.clear();
		} else {
			startActivityForResult(intent, 69);
		}


	}

	/** Save ArrayList<String> into Set<String> and put into preferences */
	private void saveCurrentList(ArrayList<String> items) {
		// TODO Auto-generated method stub
		Set<String> todoSet = new HashSet<String>();
		System.out.println("items size : " + String.valueOf(items.size()));
		todoSet.addAll(items);

		System.out.println("Saved : " + String.valueOf(todoSet.size()));

		SharedPreferences currentListPref = getSharedPreferences("currentList", MODE_PRIVATE);
		SharedPreferences.Editor currentListEditor = currentListPref.edit();

		// Store Set<String>
		currentListEditor.putStringSet("CurrentTODO", todoSet);
		currentListEditor.commit();

	}

	/** Retrieve saved Set<String> from preference and return as ArrayList<String> */
	private ArrayList<String> retrieveSavedList() {
		// TODO Auto-generated method stub
		SharedPreferences currentListPref = getSharedPreferences("currentList", MODE_PRIVATE);
		Set<String> defaultSet = new HashSet<String>();

		// Extract todoSet from preferences
		Set<String> todoSet = currentListPref.getStringSet("CurrentTODO", defaultSet);

		// Convert Set<String> into ArrayList<String>
		ArrayList<String> currentTODOList = new ArrayList<String>(todoSet);

		System.out.println("Retrieved : " + String.valueOf(currentTODOList.size()));

		return currentTODOList;
	}

	/** Instantiate the ListView with the items */
	private void instantiateListView() {
		// Create list of items
		myItems = retrieveSavedList();

		System.out.println("myItems : " + String.valueOf(myItems.size()));

		// Build the Adapter (Converts items into appropriate views)
		adapter = new ArrayAdapter<String>(
				this,												// Context for the activity
				android.R.layout.simple_list_item_multiple_choice,	// layout to use (create)
				myItems);			// Items to be displayed

		// Configure list view.
		todoListView = (ListView) findViewById(R.id.listViewMain);
		todoListView.setAdapter(adapter);
		textcounter = (TextView) findViewById(R.id.counterView);

		// Restore saved items from OnBackPressed
		moveList = retrieveAutoSave();
		if (moveList != null) {
			System.out.println("RETRIEVED AUTO SAVE : " + moveList.size());
		}

		// Long clicks
		todoListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		todoListView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				// TODO Auto-generated method stub
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub
				// Inflate the menu for the CAB
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.cab, menu);
				return true;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				// TODO Auto-generated method stub
				// Initiate SparseBooleanArray
				SparseBooleanArray checkedItemPositions = todoListView.getCheckedItemPositions();
				int checkedItemCount = todoListView.getCount();
				Integer todoCounter = myItems.size();

				switch (item.getItemId()) {

				/** Case when delete is pressed from CAB */
				case R.id.cab_delete:

					for(int i=checkedItemCount-1; i >= 0; i--){
						if(checkedItemPositions.get(i)){
							adapter.remove(myItems.get(i));
							saveCurrentList(myItems);
						}
					}
					checkedItemPositions.clear();
					adapter.notifyDataSetChanged();
					textcounter.setText(todoCounter.toString());

					mode.finish(); // Action picked, so close the CAB
					return true;

					/** Case when archive is pressed from CAB */
				case R.id.cab_archive:

					// The Array list for todo's that are being sent to the archives
					moveList = new ArrayList<String>();

					for(int i=checkedItemCount-1; i >= 0; i--){
						if(checkedItemPositions.get(i)){
							moveList.add(myItems.get(i));
							adapter.remove(myItems.get(i));
							saveCurrentList(myItems);
						}
					}
					checkedItemPositions.clear();
					adapter.notifyDataSetChanged();
					textcounter.setText(todoCounter.toString());

					mode.finish();
					return true;
				default:
					return false;
				}
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position,
					long id, boolean checked) {
				// Show count
				int checkCount = todoListView.getCheckedItemCount();
				// Display on CAB
				mode.setTitle(checkCount + " Selected");

			}
		});
	}

	/** Save state of todoListView checkboxes */
	public void saveItemsState(String itemDesc, boolean state) {
		SharedPreferences itemStatePref = getSharedPreferences("itemStates", MODE_PRIVATE);
		SharedPreferences.Editor itemStateEditor = itemStatePref.edit();

		itemStateEditor.putBoolean(itemDesc, state);
		itemStateEditor.commit();


		/*int checkedItemCount = todoListView.getCheckedItemCount();
		itemStateEditor.putInt("checkCount", checkedItemCount);
		SparseBooleanArray checkedItemPositions = todoListView.getCheckedItemPositions();

		for(int i=checkedItemCount-1; i >= 0; i--){
			if(checkedItemPositions.get(i)){

				// Save the Items position in preferences
				itemStateEditor.putBoolean("item" + i  , checkedItemPositions.get(i));
				System.out.println("This item is CHECKED, it's position is" + i);
				itemStateEditor.commit();
			}
		}*/
	}

	/** Restore state of todoListView */
	public void restoreListViewState() {
		int itemCount = todoListView.getCount();
		//SparseBooleanArray checkedItemPositions = todoListView.getCheckedItemPositions();

		System.out.println("ITEM COUNT = " + itemCount);

		// For each item, pass the key into a getBoolean function to get the boolean for that item
		for(int i=itemCount-1; i >= 0; i--){
			//if(checkedItemPositions.get(i)){
			System.out.println("RETRIEVE THE CHECKBOX STATE");
			todoListView.setItemChecked(i, retrieveItemsState(String.valueOf(i)));
			//}
		}
	}

	/** Retrieve state of todoListView checkboxes */
	public boolean retrieveItemsState(String itemPosition) {
		SharedPreferences itemStatePref = getSharedPreferences("itemStates", MODE_PRIVATE);
		boolean state = itemStatePref.getBoolean(itemPosition, false);

		return state;
	}

	/** Instantiate buttons and their OnClickListeners() */
	public void createButtons() {
		/** Add Button On Click Listener */
		addButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				myItems.add(entryText.getText().toString());
				entryText.setText("");
				adapter.notifyDataSetChanged();

				// Save to preferences
				saveCurrentList(myItems);

				// count number of entries
				todoCounter = myItems.size();
				textcounter.setText(todoCounter.toString());	


			}
		});
		/*
		// delete button (deprecated)
		deleteButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				SparseBooleanArray checkedItemPositions = todoListView.getCheckedItemPositions();
				int checkedItemCount = todoListView.getCount();

				for(int i=checkedItemCount-1; i >= 0; i--){
					if(checkedItemPositions.get(i)){
						adapter.remove(myItems.get(i));
						saveCurrentList(myItems);
					}
				}
				checkedItemPositions.clear();
				adapter.notifyDataSetChanged();
				Integer todoCounter = myItems.size();
				textcounter.setText(todoCounter.toString());
			}
		});
		// archive button (deprecated)
		archiveButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SparseBooleanArray checkedItemPositions = todoListView.getCheckedItemPositions();
				int checkedItemCount = todoListView.getCount();

				// The arraylist for todo's that are being sent to the archives
				moveList = new ArrayList<String>();

				for(int i=checkedItemCount-1; i >= 0; i--){
					if(checkedItemPositions.get(i)){
						moveList.add(myItems.get(i));
						adapter.remove(myItems.get(i));
						saveCurrentList(myItems);
					}
				}
				checkedItemPositions.clear();
				adapter.notifyDataSetChanged();
				Integer todoCounter = myItems.size();
				textcounter.setText(todoCounter.toString());
			}

		});
		 */
	}

	// Call back when the textview is clicked (it shows a little message when an item is clicked)
	// Make this into an item selection
	private void itemSelection() {
		list = (ListView) findViewById(R.id.listViewMain);
		//list.setLongClickable(true);
		//list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked,
					int position, long id) {
				TextView textView = (TextView) viewClicked;

				textView.setPaintFlags(~textView.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG);
				// Store item desc and boolean state in SharedPref
				saveItemsState(textView.getText().toString(), true);

				//String message = "You clicked # " + position + ", which is string: " + textView.getText().toString();
				//Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 69) {
			if (resultCode == Activity.RESULT_OK) { 
				movedDataList = data.getStringArrayListExtra(ArchivesActivity.UNARCHIVE_DATA_LIST);
				// Check if new archives exist
				if (movedDataList != null) {
					System.out.println("NEW DATA FROM INTENT EXISTS...");
					myItems.addAll(movedDataList);
					System.out.println("myItems size : " + myItems.size());
					saveCurrentList(myItems);
					// Clear movedDataList
					movedDataList.clear();
					Log.d("CLEARED MOVED DATA LIST SIZE", String.valueOf(movedDataList.size()));
					Log.d("TODO_LIST_SIZE",String.valueOf(myItems.size()));

				} else {
					Log.d("ELSE", "NO NEW DATA");
				}
			} 
		}
	}
}

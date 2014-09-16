package ca.ualberta.edrick.notes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ArchivesActivity extends Activity {

	/** Plan for ArchivesActivity
	 * 1) Populate function
	 * 		- retrieve ArrayList<String> from intent
	 * 		- 
	 * 2) Unarchive Function
	 * 3) Email Function
	 * 4) 
	 */

	private ArrayAdapter<String> archivesAdapter;
	private ListView archivesView;
	private ArrayList<String> movedDataList;
	private ArrayList<String> savedArchivedList;
	//private Button unarchiveButton;
	//private Button deleteButton;
	//private Button emailButton;
	private TextView archivesCounterView;
	private int archivesCounter = 0;
	private ArrayList<String> moveList;
	public final static String UNARCHIVE_DATA_LIST = "ca.ualberta.edrick.notes.UNARCHIVED_ITEMS";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_archives);

		// Buttons
		//unarchiveButton = (Button) findViewById(R.id.unarchive_button);
		//deleteButton = (Button) findViewById(R.id.delete_button);
		//emailButton = (Button) findViewById(R.id.email_button);

		// Archives Counter
		//archivesCounterView = (TextView) findViewById(R.id.archives_counter_text_view);

		// Extract intent passed from MainActivity
		Intent intent = getIntent();
		movedDataList = intent.getStringArrayListExtra(MainActivity.MOVE_DATA_LIST);

		// Check if savedInstanceState is empty
		/*if (savedInstanceState != null) {
			// Restore value of members from saved state
			ArrayList<String> myItems = savedInstanceState.getStringArrayList(MainActivity.OUTSTATE_ARRAY_LIST);
			Log.d("2nd Activity", "arraylist is NOT empty");

		} else {
			Log.d("2nd Activity", "arraylist is empty");
		}
		 */

		/** Method sequence call */
		instantiateArchivesView();
		//createButtons();
		//countArchives();

	}

	public void instantiateArchivesView() {
		// Create List of Items
		//movedDataList = retrieveArchivedList();
		savedArchivedList = retrieveArchivedList();

		System.out.println("INITIALIZATION...");
		System.out.println("myArchives : " + String.valueOf(savedArchivedList.size()));

		// Build the Adapter (Converts items into appropriate views)
		archivesAdapter = new ArrayAdapter<String>(
				ArchivesActivity.this,								// Context for the activity
				android.R.layout.simple_list_item_multiple_choice,	// layout to use (create)
				savedArchivedList);	

		// Notify adapter that archives arrived
		archivesAdapter.notifyDataSetChanged();

		// Configure list view
		archivesView = (ListView) findViewById(R.id.archives_list_view);
		archivesView.setAdapter(archivesAdapter);
		archivesCounterView = (TextView) findViewById(R.id.counterView);

		// Save to preferences
		System.out.println("SAVING TO ARCHIVED LIST FROM INITIALIZATION...");
		saveArchivedList(savedArchivedList);
	}

	/*
	public void createButtons() {

		// Delete Button On Click Listener
		deleteButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				System.out.println("DELETE BUTTON WAS CLICKED FROM ARCHIVES ACTIVITY...");
				SparseBooleanArray checkedItemPositions = archivesView.getCheckedItemPositions();
				int itemCount = archivesView.getCount();

				for(int i=itemCount-1; i >= 0; i--){
					if(checkedItemPositions.get(i)){
						archivesAdapter.remove(savedArchivedList.get(i));
						Log.d("CURRENT ARCHIVED LIST SIZE : ", String.valueOf(savedArchivedList.size()));
						saveArchivedList(savedArchivedList);
					}
				}
				checkedItemPositions.clear();
				archivesAdapter.notifyDataSetChanged();

			}
		});

		//Unarchive Button On Click Listener
		unarchiveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("UNARCHIVE BUTTON WAS CLICKED FROM ARCHIVES ACTIVITY...");
				SparseBooleanArray checkedItemPositions = archivesView.getCheckedItemPositions();
				int itemCount = archivesView.getCount();

				// The arraylist for todo's that are being sent to the archives
				moveList = new ArrayList<String>();

				for(int i=itemCount-1; i >= 0; i--){
					if(checkedItemPositions.get(i)){
						moveList.add(savedArchivedList.get(i));
						archivesAdapter.remove(savedArchivedList.get(i));
						saveArchivedList(savedArchivedList);
						Log.d("CURRENT ARCHIVED LIST SIZE : ", String.valueOf(savedArchivedList.size()));
					}
				}
				// Send result intent
				Intent intentResult = new Intent();
				intentResult.putStringArrayListExtra(UNARCHIVE_DATA_LIST, moveList);
				System.out.println("Sending back items...");
				setResult(Activity.RESULT_OK, intentResult);


				checkedItemPositions.clear();
				archivesAdapter.notifyDataSetChanged();

				//finish();

			}
		});
	}*/

	public ArrayList<String> retrieveArchivedList() {
		// TODO Auto-generated method stub

		System.out.println("RETRIEVING LIST...");
		SharedPreferences currentArchivedListPref = getSharedPreferences("archivedList", MODE_PRIVATE);
		Set<String> defaultSet = new HashSet<String>();

		// Extract archivedSet from preferences
		Set<String> archiveSet = currentArchivedListPref.getStringSet("currentArchived", defaultSet);
		Log.d("CURRENT SET SIZE FROM C.ARCHIVED PREF...",String.valueOf(archiveSet.size()));

		// Convert Set<String> into ArrayList<String>
		ArrayList<String> currentArchivedList = new ArrayList<String>(archiveSet);

		System.out.println("CURRENT ARCHIVED LSIT SIZE : " + String.valueOf(currentArchivedList.size()));

		return currentArchivedList;
	}

	public void saveArchivedList(ArrayList<String> list) {
		// TODO Auto-generated method stub

		System.out.println("SAVING ARCHIVED LIST INTO SET");
		Set<String> archiveSet = new HashSet<String>();

		// Check if new archives exist
		if (movedDataList != null) {
			System.out.println("NEW DATA FROM INTENT EXISTS...");
			list.addAll(movedDataList);
			archiveSet.addAll(list);
			
			// Clear movedDataList
			movedDataList.clear();
			Log.d("CLEARED MOVED DATA LIST SIZE", String.valueOf(movedDataList.size()));
			Log.d("ARCHIVE_SET_SIZE",String.valueOf(archiveSet.size()));

		} else {
			Log.d("ELSE", "NO NEW DATA");
			archiveSet.addAll(list);
		}


		System.out.println("NEW ARCHIVED LIST SIZE : " + String.valueOf(archiveSet.size()));

		SharedPreferences currentListPref = getSharedPreferences("archivedList", MODE_PRIVATE);
		SharedPreferences.Editor currentListEditor = currentListPref.edit();

		// Store Set<String>
		currentListEditor.putStringSet("currentArchived", archiveSet);
		currentListEditor.commit();
	}

	public void emailSelectedArchives(ArrayList<String> list) {

	}

	/** Not Implemented */
	public void countArchives() {
		Integer todoCounter = savedArchivedList.size();
		Log.d("COUNTER", todoCounter.toString());
		archivesCounterView.setText("TEST");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.archives, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		SparseBooleanArray checkedItemPositions = archivesView.getCheckedItemPositions();
		int itemCount = archivesView.getCount();
		switch (id) {
		case android.R.id.home:
			// When Up Button is pressed, the 
			System.out.println("Up Button was pressed, returning to home...");
			finish();
			return true;
		case R.id.unarchive_action2:
			System.out.println("UNARCHIVE BUTTON WAS CLICKED FROM ARCHIVES ACTIVITY...");

			// The arraylist for todo's that are being sent to the archives
			moveList = new ArrayList<String>();

			for(int i=itemCount-1; i >= 0; i--){
				if(checkedItemPositions.get(i)){
					moveList.add(savedArchivedList.get(i));
					archivesAdapter.remove(savedArchivedList.get(i));
					saveArchivedList(savedArchivedList);
					Log.d("CURRENT ARCHIVED LIST SIZE : ", String.valueOf(savedArchivedList.size()));
				}
			}
			// Send result intent
			Intent intentResult = new Intent();
			intentResult.putStringArrayListExtra(UNARCHIVE_DATA_LIST, moveList);
			System.out.println("Sending back items...");
			setResult(Activity.RESULT_OK, intentResult);


			checkedItemPositions.clear();
			archivesAdapter.notifyDataSetChanged();
			return true;
		case R.id.delete_action2:
			System.out.println("DELETE BUTTON WAS CLICKED FROM ARCHIVES ACTIVITY...");
			

			for(int i=itemCount-1; i >= 0; i--){
				if(checkedItemPositions.get(i)){
					archivesAdapter.remove(savedArchivedList.get(i));
					Log.d("CURRENT ARCHIVED LIST SIZE : ", String.valueOf(savedArchivedList.size()));
					saveArchivedList(savedArchivedList);
				}
			}
			checkedItemPositions.clear();
			archivesAdapter.notifyDataSetChanged();
			return true;
		case R.id.email_action2:
			return true;
		case R.id.unarchive_all2:
			return true;
		case R.id.delete_all2:
			return true;
		case R.id.email_all2:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		/*if (id == android.R.id.home) {
			// When Up Button is pressed, the 
			System.out.println("Up Button was pressed, returning to home...");
			finish();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}*/
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

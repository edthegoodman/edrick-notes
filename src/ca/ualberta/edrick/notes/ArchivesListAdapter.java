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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * The idea of using a custom adapter is based off of Lars Vogel's Android(ListView) - Tutorial,
 * http://www.vogella.com/tutorials/AndroidListView/article.html
 * My custom adapter extends BaseAdapter due to talks about extending ArrayAdapter as being "bad" 
 */
public class ArchivesListAdapter extends BaseAdapter {
	
	private ToDoList objects;
	private final Context context;
	
	public ArchivesListAdapter (Context context, ToDoList items) {
		this.objects = items;
		this.context = context;
	}

	@Override
	public int getCount() {
		return objects.size();
	}

	@Override
	public Object getItem(int position) {
		return objects.getToDoItem(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/** The getView method was inspired from user Shayan Pourvatan over at stackoverflow, 
	 * http://stackoverflow.com/questions/22933674/listview-custom-adapter-and-checkboxes,
	 * alongside user s1ni5t3r's getView() method
	 * http://stackoverflow.com/questions/12647001/listview-with-custom-adapter-containing-checkboxes
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		View v = convertView;
		ToDoItem todo = objects.getToDoItem(position);

		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);			
			v = inflater.inflate (R.layout.todo_row, parent, false);
			holder = new ViewHolder();
			holder.cb = (CheckBox) v.findViewById(R.id.check);
			holder.tv = (TextView) v.findViewById(R.id.item_name);
			holder.sb = (CheckBox) v.findViewById(R.id.star);
			holder.sb.setClickable(false);
			holder.sb.setEnabled(false);

			if (todo != null) {				

				holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						int getPos = (Integer) buttonView.getTag();
						objects.getToDoItem(getPos).setChecked(buttonView.isChecked());
					}
				});
			}
			v.setTag(holder);
			v.setTag(R.id.item_name, holder.tv);
			v.setTag(R.id.check, holder.cb);
			v.setTag(R.id.star, holder.sb);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		/** The fix to the problem of having random selection when scrolling through the ListView came from
		 *  http://www.lalit3686.blogspot.in/2012/06/today-i-am-going-to-show-how-to-deal.html, the idea was borrowed to
		 *  implement my own fix. The idea was used to implement lines 78 to 84.
		 */
		
		holder.sb.setTag(Integer.valueOf(position));
		holder.cb.setTag(Integer.valueOf(position));

		holder.cb.setChecked(todo.isChecked());
		holder.sb.setChecked(todo.isDone());

		holder.tv.setText(todo.getItemName());

		return v;
	}

	public void deselectAll() {
		if (objects.size() != 0) {
			for (int i = 0; i < objects.size(); i++) {
				objects.getToDoItem(i).setChecked(false);
			}
		}
	}
	
	/** Reimplemented the same idea for having a ViewHolder inner class from user iGio90 over at stackoverflow,
	 * http://stackoverflow.com/questions/22933674/listview-custom-adapter-and-checkboxes
	 */
	private static class ViewHolder {
		TextView tv;
		CheckBox cb;
		CheckBox sb;
	}
}

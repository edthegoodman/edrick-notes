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
	
	private static class ViewHolder {
		TextView tv;
		CheckBox cb;
		CheckBox sb;
	}
}

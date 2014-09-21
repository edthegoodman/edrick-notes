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

public class CustomAdapter extends BaseAdapter {

	private ToDoList objects;
	private final Context context;
	//private LayoutInflater inflater;

	public CustomAdapter (Context context, ToDoList items) {
		this.objects = items;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return objects.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return objects.getToDoItem(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

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

			System.out.println("Objects Size" + objects.size());
			if (todo != null) {				

				holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						int getPos = (Integer) buttonView.getTag();
						objects.getToDoItem(getPos).setChecked(buttonView.isChecked());
						if(isChecked){
							System.out.println("Checked");
							System.out.println("getPos : " + String.valueOf(getPos));
							//objects.get(getPos).setChecked(buttonView.isChecked());
							System.out.println("Is set checked? " + String.valueOf(objects.getToDoItem(getPos).isChecked()));
						}else{
							System.out.println("Not Checked");
							System.out.println("getPos : " + String.valueOf(getPos));
							//objects.get(getPos).setChecked(buttonView.isChecked());
							System.out.println("Is set checked? " + String.valueOf(objects.getToDoItem(getPos).isChecked()));
						}
					}
				});
				holder.sb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						int getPos = (Integer) buttonView.getTag();
						objects.getToDoItem(getPos).setDone(buttonView.isChecked());
						if(isChecked){
							System.out.println("Done");
							System.out.println("getPos : " + String.valueOf(getPos));
							//objects.get(getPos).setDone(buttonView.isChecked());
							System.out.println("Is set done? " + String.valueOf(objects.getToDoItem(getPos).isDone()));
						}else{
							System.out.println("Not Done");
							System.out.println("getPos : " + String.valueOf(getPos));
							//objects.get(getPos).setDone(buttonView.isChecked());
							System.out.println("Is set done? " + String.valueOf(objects.getToDoItem(getPos).isDone()));
						}
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

	private static class ViewHolder {
		TextView tv;
		CheckBox cb;
		CheckBox sb;
	}

	public void remove(ToDoItem toDoItem) {
		// TODO Auto-generated method stub
		objects.removeToDo(toDoItem);
	}

}



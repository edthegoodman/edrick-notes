package ca.ualberta.edrick.notes;

import java.io.Serializable;

public class ToDoItem implements Serializable {

	private static final long serialVersionUID = -8032702111838739502L;

	private String itemName;
	private Boolean checked;
	private Boolean done;

	public ToDoItem(String name, Boolean check, Boolean done) {
		this.itemName = name;
		this.checked = check;
		this.done = done;
	}

	public String getItemName() {
		return itemName;
	}

	public Boolean isChecked() {
		return checked;
	}

	public Boolean isDone() {
		return done;
	}

	public void setItemName(String name) {
}

	public void setChecked (Boolean bool) {
		checked = bool;
	}

	public void setDone (Boolean bool) {
		done = bool;
	}

	public String toString() {
		return getItemName();
	}

	public boolean equals(Object compareItem) {
		if (compareItem != null && compareItem.getClass()==this.getClass() ) {
			return this.equals((ToDoItem)compareItem);
		} else {
			return false;
		}
	}

	public boolean equals(ToDoItem compareItem) {
		if(compareItem == null) {
			return false;
		}
		return getItemName().equals(compareItem.getItemName());
	}

	public int hashCode() {
		return ("ToDoItem:"+getItemName()).hashCode();
	}
}

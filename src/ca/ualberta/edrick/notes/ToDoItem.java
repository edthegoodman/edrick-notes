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

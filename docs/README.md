edrick-notes
============

Assignment 1 CMPUT 301

Author : Edrick de Guzman
Project Name: edrick-notes
GitHub Repository: https://github.com/gitforschool/edrick-notes
GitHub Cloning : https://github.com/gitforschool/edrick-notes.git
Project Description: To design and implement a simple, attractive, and easy-to-use TODO list application. The application is able to record TODO items, and allow to check that items are done, and archive items you don't want to see anymore. The app allows the following:

	- Adding or removing of TODO items
	- Checking TODO items off
	- Selecting and archiving TODO items
	- Browsing visible and archived TODO items
	- Emailing TODO items (including archived items)
	- Data Persistence (automatically saving of list)
	
Basic Usage:
	Entering a new TODO item:
		-	Enter text into the text field and press "Enter" button
	Checking off TODO items:
		-	To indicate that the TODO is done, press on the Star button beside the TODO item
		-	The Star will light up indicating that it has been checked off
	Delete, Archive and Emailing:
		-	The empty boxes on the left handside are used for the selection of items
		-	Select the items desired and press either "Remove", "Archive" or "Email" to perform the functions
		-	Pressing "Delete" deletes the item(s)
		-	Pressing "Archive" archives the item(s) and removes it from the TODO List
		-	Pressing "Email" will ask to enter the email to be sent to and will bring up the available email clients
			in the user's device. The client will contain the selected items that were desired for emailing.
		-	Delete All, Archive All and Email All does the same functions correspondingly but does it for all the items
			in the list (These buttons are menus that are not shown visibly)
	Opening Archives:
		-	To open the press on the "Archives" menu
		-	Archives has the same functionality as the Main Activity which is the main list,
			the only difference is that it does not have a textfield and a button for adding.
			Also, instead of "Archive" it has "Unarchive" which unarchives the selected item and brings it to the
			main list in its current state(Done/Not Done).
		-	While in the Archives, the user cannot change the state of the archived item (eg. if a TODO item that was
			checked-off was archived, it will appear checked-off in the Archives and the user won't be able to change 			it.)
		-	To return to the main list, just simply press back or the up key
	Accessing the Summary:
		-	To access the Summary, press the Summary menu (Both the main list and the archives have this menu)
		-	This summary shows the following:
				+ Total number of TODO items in the main list
				+ Total number of TODO items that are not done (not checked off in the star)
				+ Total number of TODO items that are done (checked off in the star)
				+ Total number of TODO items in the archives
				+ Total number of TODO items in the archives that are not done (not checked off in the star)
				+ Total number of TODO items in the archives that are done (checked off in the star)
		-	To go back to the main list, just simply press back or the up key
		
Resources:

	1) Dr. Abram Hindle's StudentPicker App's video was explicitly followed and modified for the 	implementation of the 	controller(StudentListController.java), manager(StudentListManager.java) 	and listener(Listener.java) 	classes/interfaces as well as the list(StudentList) class to 	implement my own version of the classes (ToDoList.java, 	ToDoListController.java, 	ToDoListManager.java, ArchivesListController.java, ArchivesListManager.java).
	Link : https://www.youtube.com/playlist?list=PL240uJOh_Vb4PtMZ0f7N8ACYkCLv0673O
	
	2) All the buttons and menus are implemented by calling the corresponding method to the 	button/menu
	in the "onClick" field of the buttons'/menus' xml file, this idea was taken from Dr. Hindle's 	StudentPicker
	video.
	
	3) The tutorials in the android developer site were used for learning and guidance to get
	familiarized with the Android Development Tools.
	Link : http://developer.android.com/training/index.html
	
	4) Emailing was done by sending intents to another activity such as an email client, and this 	was made possible through the use of intents.
	The idea came from 
	Link : http://developer.android.com/training/sharing/send.html.
	
	5) The idea to develop and implement the dialog boxes for some actions were taken from
	the Android Developer site.
	Link : http://developer.android.com/guide/topics/ui/dialogs.html
	
	6) The idea of using LinearLayout.LayoutParams to set EditText component as the view of 	AlertDialog was taken from
	user "Raghunandan" over at stackoverflow.com
	Link : http://stackoverflow.com/questions/18799216/how-to-make-a-edittext-box-in-a-dialog
	
	7) The idea of using a custom adapter is based off of Lars Vogel's Android(ListView) - 	Tutorial but the custom adapters implemented here extends BaseAdapter due to talks about 	extending ArrayAdapter as being "bad"
	Link : http://www.vogella.com/tutorials/AndroidListView/article.html
	
	8) The getView method was inspired from user Shayan Pourvatan over at stackoverflow, 
	Link : http://stackoverflow.com/questions/22933674/listview-custom-adapter-and-checkboxes,
	alongside user s1ni5t3r's getView() method
	Link : 	http://stackoverflow.com/questions/12647001/listview-with-custom-adapter-containing-checkboxes
	
	9) The fix to the problem of having random selection when scrolling through the ListView came 	from http://www.lalit3686.blogspot.in/2012/06/today-i-am-going-to-show-how-to-deal.html, the 	idea was borrowed to implement my own fix. The idea was used to implement lines 88 to 94 	(CustomAdapter.java) and lines 78 to 84(ArchivesListAdapter.java).
	
	10) Reimplemented the same idea for having a ViewHolder inner class from user iGio90 over 	at 	tackoverflow
	Link : http://stackoverflow.com/questions/22933674/listview-custom-adapter-and-checkboxes
	
	11) The default android icons were used from the sdk directory.
	
	12) The license used for Dr. Abram Hindle's Student Picker App (GPL3)
		/*
		Student Picker: Randomly pick students to answer questions

		Copyright (C) 2014 Abram Hindle abram.hindle@softwareprocess.ca
		opyright (C) 2014 Edrick de Guzman edrick@ualberta.ca

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
	
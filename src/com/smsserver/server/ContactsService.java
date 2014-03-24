package com.smsserver.server;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

public class ContactsService {
	private ContentResolver content;
	private Map<String, String> contacts;
	
	public ContactsService(ContentResolver content) {
		this.content = content;
		this.contacts = new HashMap<String, String>();
		this.load();
	}
	
	public void load() {
		Cursor cursor = content.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		if (cursor.getCount() > 0) {
			// First get the contact ids
			while (cursor.moveToNext()) {
				String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
				
				Cursor phoneCursor = content.query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
						null, 
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
						new String[] { id }, 
						null);
				
				// Once we have the id, we can do a phone number look up
				while (phoneCursor.moveToNext()) {
					String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					
					number = number.replaceAll("-|\\s|\\(|\\)|\\+1", "");
					contacts.put(number, name);
					Log.i("sms:contacts", ""+name + " :: " + number);
				}
				phoneCursor.close();
				
			}
			cursor.close();
		}
	}
	
	public Map<String, String> getContacts() {
		return this.contacts;
	}
}

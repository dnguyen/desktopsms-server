package com.smsserver.server;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

public class ContactsService {
	private ContentResolver content;
	private Map<String, SMSContact> contacts;
	
	public ContactsService(ContentResolver content) {
		this.content = content;
		this.contacts = new HashMap<String, SMSContact>();
		this.load();
	}
	
	public void load() {
		String id, name, image_uri;
		Cursor cursor = content.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		
		if (cursor.getCount() > 0) {
			// First get the contact ids
			while (cursor.moveToNext()) {
				id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
				name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
				image_uri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
				
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
					
					// If contact was already added, add a new phone number for that contact.
					if (contacts.containsKey(name)) {
						contacts.get(name).getAddresses().add(number);
					} else {
						SMSContact newContact = new SMSContact(name);
						newContact.getAddresses().add(number);
						contacts.put(name, newContact);
					}
					Log.i("sms:contacts", ""+name + " :: " + number);
				}
				phoneCursor.close();
				
			}
			cursor.close();
		}
	}
	
	public SMSContact findByNumber(String phoneNumber) {
		SMSContact foundContact = null;
		
		if (phoneNumber != null) {
			Log.i("sms", "Getting contact name for: " + phoneNumber);
			phoneNumber = phoneNumber.replaceAll("-|\\s|\\(|\\)|\\+1", "");
			
			for (Map.Entry<String, SMSContact> entry : contacts.entrySet()) {
				
				SMSContact currentContact = entry.getValue();
				
				for (int i = 0; i < currentContact.getAddresses().size(); i++) {
					
					if (currentContact.getAddresses().get(i).equals(phoneNumber)) {
						foundContact = entry.getValue();
						//Log.i("sms:contactLookup:found", foundName);
						break;
					}
				}
			}
		}
		
		return foundContact;
	}
	
	public Map<String, SMSContact> getContacts() {
		return this.contacts;
	}
}

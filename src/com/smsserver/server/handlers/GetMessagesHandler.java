package com.smsserver.server.handlers;

import java.util.ArrayList;
import java.util.List;
import org.java_websocket.WebSocket;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smsserver.server.ContactsService;
import com.smsserver.server.SMSContact;
import com.smsserver.server.SMSMessage;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class GetMessagesHandler extends VoidMessageHandler {

	private ContactsService contacts;
	
	public GetMessagesHandler(ContentResolver content, ContactsService contacts) {
		super(content);
		this.contacts = contacts;
	}
	
	@Override
	public void handleMessage(WebSocket conn) {
		List<SMSMessage> smsList = new ArrayList<SMSMessage>();
		
		Cursor cursor = content.query(Uri.parse("content://sms/"), null, null, null, null);
		int smsCount = cursor.getCount();
		Log.i("sms:columns", "columns");
		for (int i = 0; i < cursor.getColumnCount(); i++) {
			Log.i("sms:columns", cursor.getColumnName(i).toString());
		}
		if (cursor.moveToFirst()) {
			for (int i = 0; i < smsCount; i++) {
				smsList.add(cursorToSms(cursor));
				cursor.moveToNext();
			}
		}
		cursor.close();
		
		Gson gson = new Gson();
		String messages = gson.toJson(smsList, new TypeToken<ArrayList<SMSMessage>>(){}.getType());
		
		conn.send("getMessages:" + messages);
	}
	
	/*
	 * Converts SMS data from Cursor to a SmsMessage object
	 */
	private SMSMessage cursorToSms(Cursor cursor) {
		SMSMessage sms = new SMSMessage();
		
		sms.setId(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
		sms.setThreadId(cursor.getString(cursor.getColumnIndexOrThrow("thread_id")));
		sms.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
		
		SMSContact contact = contacts.findByNumber(sms.getAddress());
		if (contact != null) {
			sms.setName(contact.getName());
		} else {
			sms.setName("");
		}
		
		sms.setMessage(cursor.getString(cursor.getColumnIndexOrThrow("body")));
		sms.setTime(cursor.getString(cursor.getColumnIndexOrThrow("date")));
		sms.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
		sms.setRead(cursor.getString(cursor.getColumnIndexOrThrow("read")));
		sms.setProtocol(cursor.getString(cursor.getColumnIndexOrThrow("protocol")));
		
		return sms;
	}
}

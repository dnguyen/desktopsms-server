package com.smsserver;

import java.util.Map;

import org.java_websocket.WebSocket;

import com.google.gson.Gson;
import com.smsserver.server.ContactsService;
import com.smsserver.server.SMSContact;
import com.smsserver.server.SMSMessage;
import com.smsserver.server.SMSServer;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

public class SMSObserver extends ContentObserver {

	private ContentResolver content;
	private ContactsService contacts;
	
	// Keep track of the last message id that was sent/received 
	// so we don't send doubles to the client.
	private String lastMessageId = "";
	
	public SMSObserver(Handler handler, ContentResolver content, ContactsService contacts) {
		super(handler);
		this.content = content;
		this.contacts = contacts;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		Cursor cursor = content.query(Uri.parse("content://sms/"), null, null, null, null);
		if (cursor.moveToFirst()) {
			String smsType = cursor.getString(cursor.getColumnIndexOrThrow("type"));
			String smsBody = cursor.getString(cursor.getColumnIndexOrThrow("body"));
			String smsId = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
			
			if (!lastMessageId.equals(smsId)) {
				
				if (smsType.equals("2")) {
					Log.i("sms:service", "new outbound SMS: " + smsBody);
				} else {
					Log.i("sms:service", "new inbound SMS: " + smsBody);
				}
				
				SMSMessage sms = new SMSMessage();
				
				sms.setId(smsId);
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
				Log.i("sms:service", "protocol: " + sms.getProtocol());
				Gson gson = new Gson();
				String smsDataJson = gson.toJson(sms);
				
				for(Map.Entry<Integer, WebSocket> socket : SMSServer.sockets.entrySet()) {
					socket.getValue().send("incomingSMS:" + smsDataJson);
				}
				
				lastMessageId = smsId;
			}
		}
	}
	
}

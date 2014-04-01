package com.smsserver.server.handlers;

import org.java_websocket.WebSocket;

import com.google.gson.Gson;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class UpdateSMSStatusHandler extends DataMessageHandler {
	
	
	public UpdateSMSStatusHandler(ContentResolver content) {
		super(content);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleMessage(WebSocket conn, String jsonString) {
		Gson gson = new Gson();
		String[] unreadMessageIds = gson.fromJson(jsonString, String[].class);
		for (int i = 0; i < unreadMessageIds.length; i++) {
			Log.i("sms", unreadMessageIds[i]);
		}
		/*Cursor cursor = content.query(Uri.parse("content://sms/inbox"), null, null, null, null);
		while (cursor.moveToNext()) {
			
		}*/
	}

}

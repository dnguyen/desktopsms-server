package com.smsserver.server.handlers;

import org.java_websocket.WebSocket;

import android.content.ContentResolver;

public abstract class DataMessageHandler {
	ContentResolver content;
	
	public DataMessageHandler(ContentResolver content) {
		this.content = content;
	}
	
	public abstract void handleMessage(WebSocket conn, String jsonString);
}

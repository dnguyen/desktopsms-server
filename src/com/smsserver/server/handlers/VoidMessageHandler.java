package com.smsserver.server.handlers;

import org.java_websocket.WebSocket;

import android.content.ContentResolver;

public abstract class VoidMessageHandler {
	ContentResolver content;
	
	public VoidMessageHandler(ContentResolver content) {
		this.content = content;
	}
	
	public abstract void handleMessage(WebSocket conn);
}

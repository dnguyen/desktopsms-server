package com.smsserver.server.handlers;

import org.java_websocket.WebSocket;

public interface DataMessageHandler {
	public void handleMessage(WebSocket conn, String jsonString);
}

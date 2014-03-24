package com.smsserver.server;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.FrameBuilder;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smsserver.server.handlers.DataMessageHandler;
import com.smsserver.server.handlers.GetMessagesHandler;
import com.smsserver.server.handlers.VoidMessageHandler;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class SMSServer extends WebSocketServer {
	ContentResolver content;
	ContactsService contacts;
	int counter = 0;
	
	private Map<String, VoidMessageHandler> voidMessageHandlers;
	private Map<String, DataMessageHandler> dataMessageHandlers;
	
	public SMSServer(int port, Draft d) throws UnknownHostException {
		super(new InetSocketAddress(port), Collections.singletonList(d));
	}
	
	public SMSServer(InetSocketAddress address, Draft d, ContentResolver content) {
		super(address, Collections.singletonList(d));
		this.content = content;
		this.dataMessageHandlers = new HashMap<String, DataMessageHandler>();
		this.voidMessageHandlers = new HashMap<String, VoidMessageHandler>();
		this.contacts = new ContactsService(content);
		setupHandlers();
	}

	/**
	 * Map our message headers to our handlers
	 */
	private void setupHandlers() {
		this.voidMessageHandlers.put("getMessages", new GetMessagesHandler(this.content, this.contacts));
	}
	
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		counter++;
		Log.i("sms", "///////////Opened connection number" + counter);
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		Log.i("sms", "closed");
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		Log.i("sms", "Error:");
		ex.printStackTrace();
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		
		// Just going to keep it simple and not worry about sending binary data. We'll
		// send data as a header and json string -> {header}:{json string here}
		String[] messageSplit = message.split(":", 2);
		String messageHeader = "";
		String messageJson = "";
		
		// Check if message contains any json data to work with.
		// If there is data, handle the message with a DataMessageHandler
		// If there's no data, handle the message with a VoidMessageHandler.
		if (messageSplit.length > 1) {
			messageHeader = messageSplit[0];
			messageJson = messageSplit[1];
			
			if (this.dataMessageHandlers.containsKey(messageHeader)) {
				Log.i("sms:onMessage:dataMessage", messageHeader);
				Log.i("sms:onMessage:dataMessage:data", messageJson);
				this.dataMessageHandlers.get(messageHeader).handleMessage(conn, messageJson);
			}
		} else {
			messageHeader = message;
			if (this.voidMessageHandlers.containsKey(messageHeader)) {
				Log.i("sms:onMessage:voidMessage", messageHeader);
				this.voidMessageHandlers.get(messageHeader).handleMessage(conn);
			}
		}
	}

	@Override
	public void onMessage(WebSocket conn, ByteBuffer blob) {
		Log.i("sms:onMessageBlog", "Buffer Capacity: " + blob.capacity());
		conn.send(blob);
	}

	public void onWebsocketMessageFragment( WebSocket conn, Framedata frame ) {
		FrameBuilder builder = (FrameBuilder) frame;
		builder.setTransferemasked(false);
		conn.sendFrame(frame);
	}	
}

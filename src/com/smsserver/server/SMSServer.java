package com.smsserver.server;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.framing.FrameBuilder;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.smsserver.server.handlers.DataMessageHandler;
import com.smsserver.server.handlers.GetMessagesHandler;
import com.smsserver.server.handlers.SendSMSHandler;
import com.smsserver.server.handlers.UpdateSMSStatusHandler;
import com.smsserver.server.handlers.VoidMessageHandler;

import android.content.ContentResolver;
import android.util.Log;

public class SMSServer extends WebSocketServer {
	// Static is bad...can't see any other way around it.
	// SmsReceiver needs access to the sockets to send incoming sms messages
	public static Map<Integer, WebSocket> sockets;
	ContentResolver content;
	ContactsService contacts;
	
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
		this.sockets = new HashMap<Integer, WebSocket>();
    	
		setupHandlers();
	}

	public void close() {
		Log.i("sms", "Closing SMSServer");
	}
	
	/**
	 * Map our message headers to our handlers
	 */
	private void setupHandlers() {
		this.voidMessageHandlers.put("getMessages", new GetMessagesHandler(this.content, this.contacts));
		
		this.dataMessageHandlers.put("sendSMS", new SendSMSHandler(this.content));
		this.dataMessageHandlers.put("updateUnread", new UpdateSMSStatusHandler(this.content));
	}
	
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		this.sockets.put(conn.hashCode(), conn);
		Log.i("sms:socket", "Opened connection number for " + conn.hashCode());
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		Log.i("sms:socket", "Closed socket for " + conn.hashCode());
		this.sockets.remove(conn.hashCode());
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		Log.i("sms", "Error:" + ex.getMessage());
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
		Log.i("sms:onMessageBlob", "Buffer Capacity: " + blob.capacity());
	}

	public void onWebsocketMessageFragment( WebSocket conn, Framedata frame ) {
		FrameBuilder builder = (FrameBuilder) frame;
		builder.setTransferemasked(false);
		conn.sendFrame(frame);
	}	
}

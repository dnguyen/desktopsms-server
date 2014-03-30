package com.smsserver.server.handlers;

import org.java_websocket.WebSocket;

import com.google.gson.Gson;
import com.smsserver.server.SendMessage;

import android.util.Log;

public class SendSMSHandler implements DataMessageHandler {

	@Override
	public void handleMessage(WebSocket conn, String jsonString) {
		Gson gson = new Gson();
		SendMessage message = gson.fromJson(jsonString, SendMessage.class);
		Log.i("sms", "Send message: " + message.getAddress() + " - " + message.getMessage());
		//smsManager.sendTextMessage(message.getAddress(), null, message.getMessage(), null, null);
		
		conn.send("confirmSendSMS:");
	}

}

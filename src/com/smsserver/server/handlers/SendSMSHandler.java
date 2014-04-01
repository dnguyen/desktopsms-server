package com.smsserver.server.handlers;

import org.java_websocket.WebSocket;

import com.google.gson.Gson;
import com.smsserver.server.SendMessage;

import android.content.ContentResolver;
import android.telephony.SmsManager;
import android.util.Log;

public class SendSMSHandler extends DataMessageHandler {

	public SendSMSHandler(ContentResolver content) {
		super(content);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleMessage(WebSocket conn, String jsonString) {
		SmsManager smsManager = SmsManager.getDefault();
		Gson gson = new Gson();
		SendMessage message = gson.fromJson(jsonString, SendMessage.class);
		Log.i("sms", "Send message: " + message.getAddress() + " - " + message.getMessage());
		smsManager.sendTextMessage(message.getAddress(), null, message.getMessage(), null, null);
		
		conn.send("confirmSendSMS:");
	}

}

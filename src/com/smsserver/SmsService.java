package com.smsserver;

import java.net.InetSocketAddress;

import org.java_websocket.drafts.Draft_17;

import com.smsserver.server.ContactsService;
import com.smsserver.server.SMSServer;
import com.smsserver.utilities.Utils;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class SmsService extends Service {
	
	private SMSServer smsServer;
	private ContactsService contacts;
	private SMSObserver smsObserver;
	
	@Override
	public void onCreate() {
		Log.i("sms:service", "Create smsservice");
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("sms:service", "Start smsservice");
		
		contacts = new ContactsService(this.getContentResolver());
		
		smsServer = new SMSServer( new InetSocketAddress(Utils.getIPAddress(true), 9003), new Draft_17(), this.getContentResolver());
		smsServer.start();
	
		smsObserver = new SMSObserver(new Handler(), this.getContentResolver(), contacts);
		getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, smsObserver);
		
		return Service.START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		
		return null;
	}
	
	@Override
	public void onDestroy() {
		this.getContentResolver().unregisterContentObserver(this.smsObserver);
		super.onDestroy();
	}

}

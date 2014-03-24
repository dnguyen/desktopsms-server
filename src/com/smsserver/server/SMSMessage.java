package com.smsserver.server;

public class SMSMessage {

	private String id;
	private String thread_id;
	private String address;
	private String name;
	private String message;
	private String time;
	private String folder;
	private String msgtype;
	
	public SMSMessage() {
		
	}
	
	public SMSMessage(String id, String address, String msg, String time, String folder, String type) {
		this.id = id;
		this.address = address;
		this.message = msg;
		this.time = time;
		this.folder = folder;
		this.msgtype = type;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getFolder() {
		return folder;
	}
	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getThreadId() {
		return thread_id;
	}

	public void setThreadId(String thread_id) {
		this.thread_id = thread_id;
	}

	public String getType() {
		return msgtype;
	}

	public void setType(String type) {
		this.msgtype = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

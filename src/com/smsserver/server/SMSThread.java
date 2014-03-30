package com.smsserver.server;

import java.util.List;

public class SMSThread {
	private String id;
	private List<SMSMessage> messages;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<SMSMessage> getMessages() {
		return messages;
	}
	public void setMessages(List<SMSMessage> messages) {
		this.messages = messages;
	}
}

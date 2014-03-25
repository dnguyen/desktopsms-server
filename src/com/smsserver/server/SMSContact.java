package com.smsserver.server;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public class SMSContact {
	private String name;
	private List<String> addresses;
	private Bitmap image;
	
	public SMSContact(String name) {
		this.name = name;
		this.addresses = new ArrayList<String>();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getAddresses() {
		return addresses;
	}
	public void setAddresses(List<String> addresses) {
		this.addresses = addresses;
	}
	public Bitmap getImage() {
		return image;
	}
	public void setImage(Bitmap image) {
		this.image = image;
	}
}

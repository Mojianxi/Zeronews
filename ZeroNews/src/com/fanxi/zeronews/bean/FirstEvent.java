package com.fanxi.zeronews.bean;

import android.graphics.Bitmap;
import android.os.Message;

public class FirstEvent {
	private Message msg;
   private String test;
   private Bitmap bitmap;
	public Bitmap getBitmap() {
	return bitmap;
}
public void setBitmap(Bitmap bitmap) {
	this.bitmap = bitmap;
}
	public FirstEvent(String test) {
	this.test = test;
}
	public FirstEvent(Message msg) {
		super();
		this.msg = msg;
	}
	public Message getMsg() {
		return msg;
	}
	public String getTest() {
		return test;
	}
	public FirstEvent(Bitmap bitmap) {
		super();
		this.bitmap = bitmap;
	}
	public FirstEvent(String test, Bitmap bitmap) {
		super();
		this.test = test;
		this.bitmap = bitmap;
	}
}

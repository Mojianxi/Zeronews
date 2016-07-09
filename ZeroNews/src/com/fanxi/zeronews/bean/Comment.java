package com.fanxi.zeronews.bean;
import java.util.ArrayList;
public class Comment {
	public String user;
	public String comment;
	public String time;
	public Comment(String user, String comment, String time) {
		super();
		this.user = user;
		this.comment = comment;
		this.time = time;
	}
}

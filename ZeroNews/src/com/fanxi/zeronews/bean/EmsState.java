package com.fanxi.zeronews.bean;

import java.util.ArrayList;

public class EmsState {
	public String msg;
	public Data result;
	public String status;
	public class Data{
		public String deliverystatus;
		public String issign;
		public ArrayList<State> list;
		public String number;
		public String type;
		@Override
		public String toString() {
			return "Data [deliverystatus=" + deliverystatus + ", issign="
					+ issign + ", list=" + list + ", number=" + number
					+ ", type=" + type + "]";
		}
	}
	public class State{
		public String status;
		public String time;
		@Override
		public String toString() {
			return "State [status=" + status + ", time=" + time + "]";
		}
	}
	@Override
	public String toString() {
		return "EmsState [msg=" + msg + ", result=" + result + ", status="
				+ status + "]";
	}
}

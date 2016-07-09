package com.fanxi.zeronews.bean;

import java.util.ArrayList;
public class EmsResults {
	public String status;
	public String msg;
	public ArrayList<Ems> result;
	public class Ems implements Comparable<Ems>{
		public Ems(String name) {
			super();
			this.name = name;
			pinyin=PinyinUtils.getPinyin(name);
		}
		public String pinyin;
		public String name;
		public String type;
//		public String letter;
		public String tel;
		public String number;
		@Override
		public String toString() {
			return "Ems [name=" + name + ", tel=" + tel + ", number=" + number
					+ "]";
		}
		@Override
		public int compareTo(Ems another) {
			if(pinyin!=null)return pinyin.compareTo(another.pinyin);
			return 0;
		}
	}
	@Override
	public String toString() {
		return "EmsResults [result=" + result + "]";
	}
}

package com.fanxi.zeronews.bean;

import java.util.ArrayList;

/**
 * 语音识别数据对象
 * 
 * @author Kevin http://www.tuling123.com/openapi/api?key=879a6cb3afb84dbf4fc84a1df2ab7319&info=打电话
 * 
 */
public class VoiceBean {
	public int bg;
	public int ed;
	public boolean ls;
	public int sn;
	public ArrayList<WsBean> ws;
	public class WsBean {
		public int bg;
		public ArrayList<CwBean> cw;
	}
	public class CwBean {
		public String sc;
		public String w;
	}
}

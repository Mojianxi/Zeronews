package com.fanxi.zeronews.bean;
/**
 * 保存问答语句的对象
 * 
 * @author Kevin
 * 
 */
public class TalkBean {
	public String text;// 问答语句
	public boolean isAsker;// 表示是否是提问者, 否则是回答者
	public TalkBean(String text, boolean isAsker) {
		super();
		this.text = text;
		this.isAsker = isAsker;
	}
}

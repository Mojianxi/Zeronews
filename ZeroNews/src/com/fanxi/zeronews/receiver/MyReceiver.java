package com.fanxi.zeronews.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import com.fanxi.zeronews.activity.NewsDetailActivity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

public class MyReceiver extends BroadcastReceiver{
	private static final String TAG = "PushReceiver";
	private String url;
	private String source;
	@Override
	public void onReceive(Context context, Intent intent){
		 Bundle bundle = intent.getExtras();
	        Log.d(TAG, "onReceive - " + intent.getAction());

	        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
	        }else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
	            System.out.println("收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
	            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
	        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
	            System.out.println("收到了通知");
	            // 在这里可以做些统计，或者做些其他工作
	        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
	            System.out.println("用户点击打开了通知");
	            // 在这里可以自己写代码去定义用户点击后的行为
	            String content = bundle.getString(JPushInterface.EXTRA_EXTRA);
	          
	        	   try {
						JSONObject jsonObject=new JSONObject(content);
						url = jsonObject.getString("url");
						source = jsonObject.getString("source");
					} catch (JSONException e) {
						e.printStackTrace();
					}
		            Intent intent2=new Intent(context, NewsDetailActivity.class);
		            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent2.putExtra("url", url);
					intent2.putExtra("source", source);
		           if(source!=null&&!"".equals(source)){
		        	   context.startActivity(intent2);
		           }
	        } else {
	            Log.d(TAG, "Unhandled intent - " + intent.getAction());
	  }
	}

}

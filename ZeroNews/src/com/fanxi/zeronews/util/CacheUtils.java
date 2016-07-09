package com.fanxi.zeronews.util;

import android.content.Context;

public class CacheUtils {
	/**
	 * 设置缓存
	 * @param key 地址url
	 * @param value 值json 
	 * 
	 */
	public static void setCache(String key,String value,Context context){
		PrefUtils.setString(context, key, value);
	}
	/**
	 * 获取缓存
	 * @param key
	 * @param context
	 * @return
	 */
	public static String getCache(String key,Context context){
		return PrefUtils.getString(context, key, null);
	}
}

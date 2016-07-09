/**
 * 
 */
package com.fanxi.zeronews.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fanxi.zeronews.bean.Constant;

/**
 * @author Adobe
 * 
 */
public class HttpUtil {
	private static final String BASE_URL = "http://image.baidu.com/channel/"
			+ "listjson?pn=0&rn=" + Constant.PAGE_SIZE + "&tag1="
			+ Constant.tag1 + "&ftags="
			+ "&sorttype=0&ie=utf8&oe=utf-8&image_id=&tag2=";

	// http://image.baidu.com/channel/listjson?pn=0&rn=15&tag1=%E7%BE%8E%E5%A5%B3&ftags=&sorttype=0&ie=utf8&oe=utf-8&image_id=&tag2=小清新&
	/**
	 * 获取网址内容
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String getContent(String url) throws Exception {
		StringBuilder sb = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpParams httpParams = client.getParams();
		// 设置网络超时参数
		HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
		HttpConnectionParams.setSoTimeout(httpParams, 6000);
		HttpResponse response = client.execute(new HttpGet(BASE_URL + url));
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					entity.getContent(), "UTF-8"), 8192);
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			reader.close();
		}
		return sb.toString();
	}
}

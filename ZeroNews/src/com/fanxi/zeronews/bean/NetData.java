package com.fanxi.zeronews.bean;
import java.util.ArrayList;
public class NetData {
	public String code;
	public String msg;
	//结果
	public Result result;
	public class Result{
		//数据
		public DataBody showapi_res_body;
		public int showapi_res_code;
		public String showapi_res_error;
		@Override
		public String toString() {
			return "Result [showapi_res_body=" + showapi_res_body
					+ ", showapi_res_code=" + showapi_res_code
					+ ", showapi_res_error=" + showapi_res_error + "]";
		}
	}
	public class DataBody{
		public PagerBean pagebean;
		public int ret_code;
		@Override
		public String toString() {
			return "DataBody [pagebean=" + pagebean + ", ret_code=" + ret_code
					+ "]";
		}
	}
	public class PagerBean{
		public int allNum;
		public int allPages;
		//数据列表
		public ArrayList<NewsData> contentlist;
		public int currentPage;
		public int maxResult;
		@Override
		public String toString() {
			return "PagerBean [allNum=" + allNum + ", allPages=" + allPages
					+ ", contentlist=" + contentlist + ", currentPage="
					+ currentPage + ", maxResult=" + maxResult + "]";
		}
	}
	public class NewsData{
		//描述绍列表
		public ArrayList<Object> allList;
		public String channelId;
		public String channelName;
		public String desc;
		//图片列表
		public ArrayList<ImageList> imageurls;
		public String link;
		public String pubDate;
		public String source;
		public String title;
		public int type;
		@Override
		public String toString() {
			return "NewsData [allList=" + allList + ", channelId=" + channelId
					+ ", channelName=" + channelName + ", desc=" + desc
					+ ", imageurls=" + imageurls + ", link=" + link
					+ ", pubDate=" + pubDate + ", source=" + source
					+ ", title=" + title + "]";
		}
	}
	//	public class DescList{
//		public String desc1;
//		public int height;
//		public String url;
//		public int width;
//		@Override
//		public String toString() {
//			return "DescList [desc1=" + desc1 + ", height=" + height + ", url="
//					+ url + ", width=" + width + "]";
//		}
//	}
	public class ImageList{
		public int height;
		public String url;
		public int width;
		@Override
		public String toString() {
			return "ImageList [height=" + height + ", url=" + url + ", width="
					+ width + "]";
		}
	}
	@Override
	public String toString() {
		return "NetData [code=" + code + ", msg=" + msg + ", result=" + result
				+ "]";
	}
}

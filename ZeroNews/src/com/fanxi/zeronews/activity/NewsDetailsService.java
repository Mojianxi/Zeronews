package com.fanxi.zeronews.activity;

import android.text.TextUtils;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

/**
 * Created by Fanxi on 2016/6/1.
 */
public class NewsDetailsService {

	private static Element content;
	private static Document document;
	private static Element child;
	private static Element child1;
	private static Element child2;
	private static Element child3;
	private static Elements body;
	private static String data;

	public static String getNewsDetails(String url) {
		document = null;
		try {
			document = Jsoup.connect(url).timeout(3000).get();
			if (!TextUtils.isEmpty(url)) {
				child = document.child(0);
				child1 = NewsDetailsService.child
						.child(NewsDetailsService.child.children().size() - 1);
				child2 = child1.child(0);
				// child2.remove();
				child3 = child1.child(1);
				body = child.getElementsByTag("body");
				Element element = body.get(0);
				Element element1 = element.child(0);
				// element1.remove();//不是虎嗅网和人民网删除
				Element element2 = element.child(1);
				// System.out.println("element2---"
				// +element2.children().size()+"==="+element2);
				// element2.remove();//不是虎嗅网和人民网删除
				Element element3 = element.child(2);
				// System.out.println("element3---"+element3.children().size()+"==="+element3);
				// element3.remove();
				Element element4 = element.child(4);
				System.out.println("element3---" + element4.children().size()
						+ "===" + element4);
				data = body.toString();
				if (data == null || "".equals(data)) {
					return "连接失败";
				}
			}
			// document = Jsoup.connect("http://www.jb51.net")
			// .data("query", "Java")
			// .userAgent("Mozilla")
			// .cookie("auth", "token")
			// .timeout(3000)
			// .post();
			// content = document.getElementById("content");
			// List<DataNode> dataNodes = content.dataNodes();
			// for(DataNode node:dataNodes){
			// String nodeName= node.nodeName();
			// System.out.println("Html数据节点"+nodeName);
			// }
		} catch (IOException e) {
			e.printStackTrace();
		}
//		 String data = "<body>" +
//		 "<center><h2 style='font-size:16px;'>" + body.toString() +
//		 "</h2></center>";
//		 data = data + "<hr size='1' />";
		return data;
	}
}

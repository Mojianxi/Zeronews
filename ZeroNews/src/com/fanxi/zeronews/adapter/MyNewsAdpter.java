package com.fanxi.zeronews.adapter;

import java.security.MessageDigest;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanxi.zeronews.R;
import com.fanxi.zeronews.bean.NetData;
import com.fanxi.zeronews.util.PrefUtils;
import com.fanxi.zeronews.util.StringMD5;
import com.fanxi.zeronews.util.UiUtils;
import com.lidroid.xutils.BitmapUtils;

public class MyNewsAdpter extends BaseAdapter{

    private String ids=null;
    ArrayList<NetData.NewsData> newsList;
    Context context;
	private BitmapUtils bitmapUtils;
	boolean isFirst;
	MessageDigest md;
	boolean isEdit;
    public MyNewsAdpter(boolean isEdit,MessageDigest md,boolean isFirst,Context context,ArrayList<NetData.NewsData> list) {
    	bitmapUtils = new BitmapUtils(UiUtils.getContext());
    	this.newsList=list;
    	this.context=context;
    	this.isFirst=isFirst;
    	this.md=md;
    	this.isEdit=isEdit;
    }
    @Override
    public int getCount() {
        return newsList.size();
    }
    @Override
    public Object getItem(int i) {
        return newsList.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        NetData.NewsData newsData1 = newsList.get(i);
        String date = newsData1.pubDate;
        int type = getItemViewType(i);
        String readId1=newsList.get(i).title;
        String readId=StringMD5.MD5(readId1, md);
       if(!isFirst){
           ids = PrefUtils.getString(context, "read_ids", "");
       }
        ViewHolder1 holder1 = null;
        ViewHolder2 holder2 = null;
        ViewHolder3 holder3 = null;
        ViewHolder4 holder4 = null;
        String url = null;
        ArrayList<NetData.ImageList> imageurls = new ArrayList<NetData.ImageList>();
        if (convertView == null) {
            switch (type) {
                case 0:
                    convertView = View.inflate(context, R.layout.list_news_item1, null);
                    holder1 = new ViewHolder1();
                    holder1.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                    holder1.iv1 = (ImageView) convertView.findViewById(R.id.iv1);
                    holder1.tv_src = (TextView) convertView.findViewById(R.id.tv_src);
                    holder1.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                    holder1.delete=(ImageView) convertView.findViewById(R.id.tv_delete);
                    if(isEdit){
                    	holder1.delete.setVisibility(View.VISIBLE);
                    }else{
                    	holder1.delete.setVisibility(View.GONE);
                    }
                    convertView.setTag(holder1);
                    break;
                case 1:
                    convertView = View.inflate(context, R.layout.list_news_item2, null);
                    holder2 = new ViewHolder2();
                    holder2.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                    holder2.iv1 = (ImageView) convertView.findViewById(R.id.iv1);
                    holder2.iv2 = (ImageView) convertView.findViewById(R.id.iv2);
                    holder2.iv3 = (ImageView) convertView.findViewById(R.id.iv3);
                    holder2.tv_src = (TextView) convertView.findViewById(R.id.tv_src);
                    holder2.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                    holder2.delete=(ImageView) convertView.findViewById(R.id.tv_delete);
                    if(isEdit){
                    	holder2.delete.setVisibility(View.VISIBLE);
                    }else{
                    	holder2.delete.setVisibility(View.GONE);
                    }
                    convertView.setTag(holder2);
                    break;
                case 2:
                    convertView = View.inflate(context, R.layout.list_news_item3, null);
                    holder3 = new ViewHolder3();
                    holder3.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                    holder3.iv = (ImageView) convertView.findViewById(R.id.iv);
                    holder3.tv_src = (TextView) convertView.findViewById(R.id.tv_src);
                    holder3.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                    holder3.delete=(ImageView) convertView.findViewById(R.id.tv_delete);
                    if(isEdit){
                    	holder3.delete.setVisibility(View.VISIBLE);
                    }else{
                    	holder3.delete.setVisibility(View.GONE);
                    }
                    convertView.setTag(holder3);
                    break;
                case 3:
                    convertView = View.inflate(context, R.layout.list_news_item4, null);
                    holder4 = new ViewHolder4();
                    holder4.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                    holder4.tv_src = (TextView) convertView.findViewById(R.id.tv_src);
                    holder4.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                    holder4.delete=(ImageView) convertView.findViewById(R.id.tv_delete);
                    if(isEdit){
                    	holder4.delete.setVisibility(View.VISIBLE);
                    }else{
                    	holder4.delete.setVisibility(View.GONE);
                    }
                    convertView.setTag(holder4);
                    break;
            }
        } else {
            switch (type) {
                case 0:
                    holder1 = (ViewHolder1) convertView.getTag();
                    if(isEdit){
                    	holder1.delete.setVisibility(View.VISIBLE);
                    }else{
                    	holder1.delete.setVisibility(View.GONE);
                    }
                    break;
                case 1:
                    holder2 = (ViewHolder2) convertView.getTag();
                    if(isEdit){
                    	holder2.delete.setVisibility(View.VISIBLE);
                    }else{
                    	holder2.delete.setVisibility(View.GONE);
                    }
                    break;
                case 2:
                    holder3 = (ViewHolder3) convertView.getTag();
                    break;
                case 3:
                    holder4 = (ViewHolder4) convertView.getTag();
                    break;
            }
        }
        switch (type){
            case 0:
                //条目1
                holder1.tv_title.setText(newsData1.title);
               if(ids!=null){
                   if(ids.contains(readId)){
                       holder1.tv_title.setTextColor(Color.GRAY);
                   }else{
                       holder1.tv_title.setTextColor(Color.BLACK);
                   }
               }
                holder1.tv_src.setText(newsData1.source);
                holder1.tv_time.setText(date);
                if (newsData1.imageurls != null && newsData1.imageurls.size() > 0) {
                    url = newsData1.imageurls.get(0).url;
                }
                if (url != null && !url.equals("")) {
                    bitmapUtils.display(holder1.iv1, url);
                } else {
                    holder1.iv1.setImageResource(R.drawable.c);
                }
                break;
            case 1:
                //条目2
                holder2.tv_title.setText(newsData1.title);
               if(ids!=null){
                   if(ids.contains(readId)){
                       holder2.tv_title.setTextColor(Color.GRAY);
                   }else{
                       holder2.tv_title.setTextColor(Color.BLACK);
                   }
               }
                holder2.tv_src.setText(newsData1.source);
                holder2.tv_time.setText(date);
                url = newsData1.imageurls.get(0).url;
                String url1 = newsData1.imageurls.get(1).url;
                String url2 = newsData1.imageurls.get(2).url;
                bitmapUtils.display(holder2.iv1, url);
                bitmapUtils.display(holder2.iv2, url1);
                bitmapUtils.display(holder2.iv3, url2);
                break;
            case 2:
                //条目3
                holder3.tv_title.setText(newsData1.title);
               if(ids!=null){
                   if(ids.contains(readId)){
                       holder3.tv_title.setTextColor(Color.GRAY);
                   }else{
                       holder3.tv_title.setTextColor(Color.BLACK);
                   }
               }
                holder3.tv_src.setText(newsData1.source);
                holder3.tv_time.setText(date);
                url = newsData1.imageurls.get(0).url;
                if (url != null && !url.equals("")) {
                    bitmapUtils.display(holder3.iv, url);
                } else {
                    holder3.iv.setImageResource(R.drawable.c);
                }
                break;
            case 3:
                //条目4
                holder4.tv_title.setText(newsData1.title);
                holder4.tv_src.setText(newsData1.source);
                holder4.tv_time.setText(date);
               if(ids!=null){
                   if(ids.contains(readId)){
                       holder4.tv_title.setTextColor(Color.GRAY);
                   }else{
                       holder4.tv_title.setTextColor(Color.BLACK);
                   }
               }
                break;
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        ArrayList<NetData.ImageList> imageurls = new ArrayList<NetData.ImageList>();
        imageurls = newsList.get(position).imageurls;
        if (imageurls.size() > 0) {
            if (imageurls.size() < 3) {
                return 0;
            } else if (imageurls.size() == 3) {
                return 1;
            } else {
                return 2;
            }
        } else {
            return 3;
        }
    }
    @Override
    public int getViewTypeCount() {
        return 4;
    }
    class ViewHolder2 {
    	ImageView delete;
        TextView tv_title;
        ImageView iv1;
        ImageView iv2;
        ImageView iv3;
        TextView tv_src;
        TextView tv_time;
    }

    class ViewHolder3 {
    	ImageView delete;
        TextView tv_title;
        ImageView iv;
        TextView tv_src;
        TextView tv_time;
    }

    class ViewHolder4 {
    	ImageView delete;
        TextView tv_title;
        TextView tv_src;
        TextView tv_time;
    }
    class ViewHolder1 {
    	ImageView delete;
        TextView tv_title;
        ImageView iv1;
        TextView tv_src;
        TextView tv_time;
    }

}

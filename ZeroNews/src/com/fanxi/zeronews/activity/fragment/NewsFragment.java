package com.fanxi.zeronews.activity.fragment;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.fanxi.zeronews.R;
import com.fanxi.zeronews.activity.BaseActivity;
import com.fanxi.zeronews.activity.NewsDetailActivity;
import com.fanxi.zeronews.adapter.MyNewsAdpter;
import com.fanxi.zeronews.bean.NetData;
import com.fanxi.zeronews.util.CacheUtils;
import com.fanxi.zeronews.util.PrefUtils;
import com.fanxi.zeronews.util.StringMD5;
import com.fanxi.zeronews.util.UiUtils;
import com.fanxi.zeronews.view.RefreshListView;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
/**
 * Created by Fanxi on 2016/5/24.
 */
public class NewsFragment extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener {
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			adapter.notifyDataSetChanged();
			Toast.makeText(mActivity, "点击", 0).show();
		};
	};
    private String title;
    private String titleId;
    private ProgressBar pb_loading;
    private TextView tv_state1;
    private LinearLayout rl_error;
    private int STATE_REFRESHING = 0;
    private int STATE_NETEOOR = 1;
    private int STATE_SECUCESS = 2;
    private int CURRENT_STATE = STATE_REFRESHING;
    private boolean isLoading=true;
    private boolean isSecusees=false;
    private boolean isError=false;
    boolean isFirst=true;
    private ArrayList<NetData.NewsData> contentlist=new ArrayList<NetData.NewsData>();
    private Button btn_reload;
    public void setTitleId(String titleId) {
        this.titleId = titleId;
    }
    private Activity mActivity;
    ArrayList<NetData.NewsData> dataArrayList;
    private RefreshListView lv_news_list;
    private NetData netData = new NetData();
    private ArrayList<NetData.NewsData> newsList;
    private MyNewsAdpter adapter;
    int i = 1;
    private MessageDigest md;
    private boolean moreIstrue=true;
	private BitmapUtils bitmapUtils;
	private String msg;
    public void setTitle(String title){
        this.title = title;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }
    boolean isEdit=false;
	private RelativeLayout rl_loading;
	private TextView tv_loading;
	private ImageView iv_loading;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(mActivity, R.layout.fragment_news, null);
        lv_news_list = (RefreshListView) view.findViewById(R.id.lv_news_list);
        tv_state1 = (TextView) view.findViewById(R.id.tv_state1);
        rl_error = (LinearLayout) view.findViewById(R.id.rl_error);
        rl_loading = (RelativeLayout) view.findViewById(R.id.rl_loading);
        rl_loading.setVisibility(View.VISIBLE);
        iv_loading = (ImageView) view.findViewById(R.id.iv_loading);
        tv_loading = (TextView) view.findViewById(R.id.tv_loading);
        Animation shake = AnimationUtils.loadAnimation(mActivity, R.anim.shake_y);
//		Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.button_shake);
		shake.reset();
		iv_loading.startAnimation(shake);
		tv_loading.startAnimation(shake);
//        Button btn=(Button) view.findViewById(R.id.btn);
//        btn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				isEdit=true;
//				handler.sendEmptyMessage(0);
//			}
//		});
//        btn_reload = (Button) view.findViewById(R.id.btn_reload);
//        initState();
//        getDataFromNet("1");
        lv_news_list.setOnRefreshListener(new RefreshListView.RefreshListener() {
            @Override
            public void onRefresh() {
            	getDataFromNet("1");
            }
            @Override
            public String onLoadMore(String s){
                ++i;
                String page = String.valueOf(i);
                getDataFromNet(page);
                   synchronized (contentlist){
                       if(contentlist!=null&&moreIstrue||contentlist.size()!=0&&moreIstrue){
                           moreIstrue=false;
                           return "正在加载更多...";
                       }else{
                           return "没有更多数据了";
                       }
                   }
            }
        });
        try {
            md = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        lv_news_list.setOnItemClickListener(this);
//        btn_reload.setOnClickListener(this);
        String cache = CacheUtils.getCache("https://way.jd.com/showapi/search_news"+title,UiUtils.getContext());
        lv_news_list.setRefreshing();// 显示下拉刷新控件
       if (!TextUtils.isEmpty(cache)) {
           parseData(cache, false);
       }
       if(BaseActivity.mTheme==R.style.RedTheme){
    	  lv_news_list.setBackgroundResource(R.color.window_bg_red);
       }else if(BaseActivity.mTheme==R.style.AppBaseTheme){
			lv_news_list.setBackgroundResource(R.color.bg_nomal);
		}else if(BaseActivity.mTheme==R.style.DefaultTheme){
			lv_news_list.setBackgroundResource(R.color.bg_default);
		}else if(BaseActivity.mTheme==R.style.PinkTheme){
			lv_news_list.setBackgroundResource(R.color.window_bg_pink);
		}else if(BaseActivity.mTheme==R.style.PurpleTheme){
			lv_news_list.setBackgroundResource(R.color.window_bg_purple);
		}else if(BaseActivity.mTheme==R.style.DeepPurpleTheme){
			lv_news_list.setBackgroundResource(R.color.window_bg_deep_purple);
		}else if(BaseActivity.mTheme==R.style.IndigoTheme){
			lv_news_list.setBackgroundResource(R.color.window_bg_indigo);
		}else if(BaseActivity.mTheme==R.style.BlueTheme){
			lv_news_list.setBackgroundResource(R.color.window_bg_blue);
		}else if(BaseActivity.mTheme==R.style.LightBlueTheme){
			lv_news_list.setBackgroundResource(R.color.window_bg_light_blue);
		}else if(BaseActivity.mTheme==R.style.CyanTheme){
			lv_news_list.setBackgroundResource(R.color.window_bg_cyan);
		}else if(BaseActivity.mTheme==R.style.TealTheme){
			lv_news_list.setBackgroundResource(R.color.window_bg_teal);
		}else if(BaseActivity.mTheme==R.style.GreenTheme){
			lv_news_list.setBackgroundResource(R.color.window_bg_green);
		}else if(BaseActivity.mTheme==R.style.LightGreenTheme){
			lv_news_list.setBackgroundResource(R.color.window_bg_light_green);
		}else if(BaseActivity.mTheme==R.style.LimeTheme){
			lv_news_list.setBackgroundResource(R.color.window_bg_lime);
		}else if(BaseActivity.mTheme==R.style.YellowTheme){
			lv_news_list.setBackgroundResource(R.color.window_bg_yellow);
		}else if(BaseActivity.mTheme==R.style.AmberTheme){
			lv_news_list.setBackgroundResource(R.color.window_bg_amber);
		}else if(BaseActivity.mTheme==R.style.OrangeTheme){
			lv_news_list.setBackgroundResource(R.color.window_bg_orange);
		}else if(BaseActivity.mTheme==R.style.DeepOrangeTheme){
			lv_news_list.setBackgroundResource(R.color.window_bg_deep_orange);
		}else if(BaseActivity.mTheme==R.style.BrownTheme){
			lv_news_list.setBackgroundResource(R.color.window_bg_brown);
		}else if(BaseActivity.mTheme==R.style.GreyTheme){
			lv_news_list.setBackgroundResource(R.color.window_bg_grey);
		}else if(BaseActivity.mTheme==R.style.BlueGreyTheme){
			lv_news_list.setBackgroundResource(R.color.window_bg_blue_grey);
		}else{
			lv_news_list.setBackgroundResource(R.color.white);
		}
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDataFromNet("1");
    }
    /**
     * 初始化数据的方法
     */
    @Override
    public void initData() {
        /**
         * 加载缓存代码
         */
        //-----------------------
        getDataFromNet("1");
        initState();
    }
    private void initState(){
       if(isError){
    	   rl_error.setVisibility(View.VISIBLE);
       }
    }
    /**
     * 请求网络数据
     */
    private void getDataFromNet(final String page) {
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("channelId", titleId);
        params.addQueryStringParameter("channelName", title + "焦点");
        params.addQueryStringParameter("title", "");
        params.addQueryStringParameter("page", page);
        params.addQueryStringParameter("appkey", "903a989ddaf0a12626ae2b8d7a0daac5");
        httpUtils.send(HttpRequest.HttpMethod.GET, "https://way.jd.com/showapi/search_news", params, new RequestCallBack<String>() {
            @Override
            public void onFailure(HttpException arg0, String arg1){
                isError=true;
                isSecusees=false;
                rl_error.setVisibility(View.VISIBLE);
                CURRENT_STATE = STATE_NETEOOR;
                lv_news_list.onRefreshComplete(false);
            }
            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                isError=false;
                isLoading=true;
                isSecusees=false;
                rl_error.setVisibility(View.GONE);
                CURRENT_STATE = STATE_REFRESHING;
            }
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
            	//简单解决xutils5.0出现的问题
            	//修改Xutils的   com.lidroid.xutils.util.core.KeyExpiryMap<K, V>类得以彻底解决
//            	bitmapUtils.configDiskCacheEnabled(true);
//                bitmapUtils.configMemoryCacheEnabled(false);
                isError=false;
                isLoading=false;
                isSecusees=true;
                String result = responseInfo.result;
                JSONObject jsonObject;
        		try {
        			jsonObject = new JSONObject(result);
        			msg = jsonObject.getString("msg");
        		} catch (JSONException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
//                System.out.println("下拉刷新获取到的数据-----"+result);
                int currentPage = Integer.parseInt(page);
                if(rl_error!=null){
                	rl_error.setVisibility(View.GONE);
                }
                if (currentPage == 1) {
                    System.out.println("第一页数据");
//                    if("ok".equals(msg)){
                    	parseData(result, false);
//                    }
                } else {
                   if("ok".equals(msg)){
                	   parseData(result, true);
                   }
//					CacheUtils.setCache("https://way.jd.com/showapi/search_news"+page, result, mActivity);
                }
                /**
                 * 设置缓存代码
                 */
                CacheUtils.setCache( "https://way.jd.com/showapi/search_news"+title, result,UiUtils.getContext());
                lv_news_list.onRefreshComplete(true);
            }
        });
    }
    /**
     * 解析网络数据
     *
     * @param result
     */
    protected void parseData(String result, boolean isMore) {
        Gson gson = new Gson();
        netData = gson.fromJson(result, NetData.class);
//		System.out.println("解析的数据---"+netData.result);
        rl_loading.setVisibility(View.GONE);
        if (!isMore) {
            newsList = netData.result.showapi_res_body.pagebean.contentlist;
//			System.out.println("&&&&&获取到的数据"+newsList);
            if (newsList != null) {
                adapter = new MyNewsAdpter(isEdit,md,isFirst,mActivity,newsList);
                lv_news_list.setAdapter(adapter);
            }
        } else {
            contentlist = netData.result.showapi_res_body.pagebean.contentlist;
            if (contentlist != null) {
                moreIstrue=true;
                newsList.addAll(contentlist);
            }else{

            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
        isFirst=false;
        String ids = PrefUtils.getString(mActivity, "read_ids", "");
        String readId1 = newsList.get(position).title;
        String readId = StringMD5.MD5(readId1, md);
        if (!ids.contains(readId)) {
            ids = ids + readId + ",";
            PrefUtils.setString(mActivity, "read_ids", ids);
        }
        Intent intent = new Intent(mActivity, NewsDetailActivity.class);
        intent.putExtra("url", newsList.get(position).link);
        if(position==0){
        	intent.putExtra("title","0");
        }else{
        	intent.putExtra("title",newsList.get(position).title);
        }
        intent.putExtra("source",newsList.get(position).source);
        startActivity(intent);
        mActivity.overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
    }
    @Override
    public void onClick(View view) {
    }
//    class MyNewsAdpter extends BaseAdapter{}
   
  
}

package com.fanxi.zeronews.activity.pager;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.fanxi.zeronews.R;
import com.fanxi.zeronews.activity.BaseActivity;
import com.fanxi.zeronews.activity.FullActivity;
import com.fanxi.zeronews.activity.MainActivity;
import com.fanxi.zeronews.application.BaseApplication;
import com.fanxi.zeronews.bean.ShitingBean;
import com.fanxi.zeronews.util.CommonUtil;
import com.fanxi.zeronews.util.ServerURL;
import com.fanxi.zeronews.util.SharedPreferencesUtil;
import com.fanxi.zeronews.util.ShiTingUrl;
import com.fanxi.zeronews.util.UiUtils;
import com.fanxi.zeronews.view.PullToRefreshBase;
import com.fanxi.zeronews.view.PullToRefreshBase.OnRefreshListener;
import com.fanxi.zeronews.view.PullToRefreshListView;
import com.fanxi.zeronews.view.VideoSuperPlayer;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
public class VedioPager extends BasePager {
	private PullToRefreshListView mListView;
	private boolean isPlaying;
	private int indexPostion = -1;
	private MAdapter mAdapter;
	private HttpUtils httpUtils;
	private HttpHandler<String> httpHandler;
	private List<ShitingBean.V9LG4B3A0Entity> list = new ArrayList<ShitingBean.V9LG4B3A0Entity>();

	public VedioPager(Activity mActivity, FragmentManager fm) {
		super(mActivity, fm);
	}
	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.shiting_frament, null);
		httpUtils = new HttpUtils();
		 initPullTorefresh(view);
		 if(BaseActivity.mTheme==R.style.RedTheme){
			 mListView.setBackgroundResource(R.color.window_bg_red);
	       }else if(BaseActivity.mTheme==R.style.NomalTheme){
	    	   mListView.setBackgroundResource(R.color.nomal_tab);
			}else if(BaseActivity.mTheme==R.style.AppBaseTheme){
				mListView.setBackgroundResource(R.color.bg_nomal);
			}else if(BaseActivity.mTheme==R.style.DefaultTheme){
				mListView.setBackgroundResource(R.color.bg_default);
			}else if(BaseActivity.mTheme==R.style.PinkTheme){
				mListView.setBackgroundResource(R.color.window_bg_pink);
			}else if(BaseActivity.mTheme==R.style.PurpleTheme){
				mListView.setBackgroundResource(R.color.window_bg_purple);
			}else if(BaseActivity.mTheme==R.style.DeepPurpleTheme){
				mListView.setBackgroundResource(R.color.window_bg_deep_purple);
			}else if(BaseActivity.mTheme==R.style.IndigoTheme){
				mListView.setBackgroundResource(R.color.window_bg_indigo);
			}else if(BaseActivity.mTheme==R.style.BlueTheme){
				mListView.setBackgroundResource(R.color.window_bg_blue);
			}else if(BaseActivity.mTheme==R.style.LightBlueTheme){
				mListView.setBackgroundResource(R.color.window_bg_light_blue);
			}else if(BaseActivity.mTheme==R.style.CyanTheme){
				mListView.setBackgroundResource(R.color.window_bg_cyan);
			}else if(BaseActivity.mTheme==R.style.TealTheme){
				mListView.setBackgroundResource(R.color.window_bg_teal);
			}else if(BaseActivity.mTheme==R.style.GreenTheme){
				mListView.setBackgroundResource(R.color.window_bg_green);
			}else if(BaseActivity.mTheme==R.style.LightGreenTheme){
				mListView.setBackgroundResource(R.color.window_bg_light_green);
			}else if(BaseActivity.mTheme==R.style.LimeTheme){
				mListView.setBackgroundResource(R.color.window_bg_lime);
			}else if(BaseActivity.mTheme==R.style.YellowTheme){
				mListView.setBackgroundResource(R.color.window_bg_yellow);
			}else if(BaseActivity.mTheme==R.style.AmberTheme){
				mListView.setBackgroundResource(R.color.window_bg_amber);
			}else if(BaseActivity.mTheme==R.style.OrangeTheme){
				mListView.setBackgroundResource(R.color.window_bg_orange);
			}else if(BaseActivity.mTheme==R.style.DeepOrangeTheme){
				mListView.setBackgroundResource(R.color.window_bg_deep_orange);
			}else if(BaseActivity.mTheme==R.style.BrownTheme){
				mListView.setBackgroundResource(R.color.window_bg_brown);
			}else if(BaseActivity.mTheme==R.style.GreyTheme){
				mListView.setBackgroundResource(R.color.window_bg_grey);
			}else if(BaseActivity.mTheme==R.style.BlueGreyTheme){
				mListView.setBackgroundResource(R.color.window_bg_blue_grey);
			}else{
				mListView.setBackgroundResource(R.color.nomal_tab);
			}
		// TextView tv=new TextView(mActivity);
		// tv.setText("影视资讯");
		// tv.setTextSize(25);
		// tv.setGravity(Gravity.CENTER);
		return view;
	}
	@Override
	public void initData() {
		 String result = SharedPreferencesUtil.getData(UiUtils.getContext(), ServerURL.shiTingUrl, "");  //共享参数缓存  首先从缓存中获取数据,
	        if (!TextUtils.isEmpty(result)) {//如果缓存有数据,直接Gson解析
	            paserData(result, false);
	        }
	        getData(ServerURL.shiTingUrl, false);//如果无缓存再去请求网络
		super.initData();
	}
	private void initPullTorefresh(View view) {
        mListView = (PullToRefreshListView) view.findViewById(R.id.shiting_refresh);
        initData();
        mListView.setPullLoadEnabled(true);  //上拉加载，屏蔽
        mListView.setPullRefreshEnabled(true);
        //   mListView.setScrollLoadEnabled(true); //设置滚动加载可用
        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData(ServerURL.shiTingUrl, false);
                String stringDate = CommonUtil.getStringDate();
                mListView.setLastUpdatedLabel(stringDate);
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                ShiTingUrl url = new ShiTingUrl();
                url.setStratPage(url.getStratPage() + 20);
                String urlfen = url.getShiTingUrl();
                getData(urlfen, true);
            }
        });
        mListView.getRefreshableView().setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if ((indexPostion < mListView.getRefreshableView().getFirstVisiblePosition() || indexPostion
                        > mListView.getRefreshableView()
                        .getLastVisiblePosition()) && isPlaying) {
                    indexPostion = -1;
                    isPlaying = false;
                    mAdapter.notifyDataSetChanged();
                    BaseApplication.setMediaPlayerNull();
                }
            }
        });
    }
	/**
	 * 视屏列表适配器
	 */
	String title;
	String mp4_url;
	class MAdapter extends BaseAdapter {
		private Context context;
		private List<ShitingBean.V9LG4B3A0Entity> list;
		private BitmapUtils bitmapUtils;

		public List<ShitingBean.V9LG4B3A0Entity> getList() {
			return list;
		}

		public void setList(List<ShitingBean.V9LG4B3A0Entity> list) {
			this.list = list;
			notifyDataSetChanged();
		}
		public MAdapter(Context context, List<ShitingBean.V9LG4B3A0Entity> list) {
			this.context = context;
			this.list = list;
			bitmapUtils = new BitmapUtils(mActivity);
			bitmapUtils.configDefaultLoadingImage(R.drawable.night_biz_media_bg);
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			GameVideoViewHolder holder = null;
			if (v == null) {
				holder = new GameVideoViewHolder();
				v = LayoutInflater.from(mActivity).inflate(
						R.layout.shiting_list_item, parent, false);
				holder.shiting_icon = (ImageView) v
						.findViewById(R.id.shiting_icon);
				holder.title = (TextView) v.findViewById(R.id.title);
				holder.description = (TextView) v
						.findViewById(R.id.description);
				holder.length = (TextView) v.findViewById(R.id.length);
				holder.iv_share = (ImageView) v.findViewById(R.id.iv_share);
				holder.playCount = (TextView) v.findViewById(R.id.playCount);
				holder.replyCount = (TextView) v.findViewById(R.id.replyCount);
				holder.mVideoViewLayout = (VideoSuperPlayer) v
						.findViewById(R.id.video);
				holder.mPlayBtnView = (ImageView) v.findViewById(R.id.play_btn);
				v.setTag(holder);
			} else {
				holder = (GameVideoViewHolder) v.getTag();
			}
			title = list.get(position).getTitle();

			mp4_url = list.get(position).getMp4_url();
			String cover = list.get(position).getCover();
			// 预加载缩略图
			bitmapUtils.display(holder.shiting_icon, cover);
			// 显示数据
			holder.title.setText(list.get(position).getTitle());// 标题
			holder.description.setText(list.get(position).getDescription());// 描述
			// TODO:时间格式转换
			long time = list.get(position).getLength();
			int a = (int) (time / 60);
			int b = (int) (time % 60);
			if (b < 10) {
				holder.length.setText("0" + a + ":" + "0" + b);// 时长
			} else {
				holder.length.setText("0" + a + ":" + b);// 时长
			}
			Log.e("MMM", time + "");
			holder.playCount.setText(list.get(position).getPlayCount() + "");// 观看次数
			holder.replyCount.setText(list.get(position).getReplyCount() + "");// 跟帖数
			// 播放监听
			holder.mPlayBtnView.setOnClickListener(new MyOnclick(mp4_url,
					holder.mVideoViewLayout, position));
			holder.mVideoViewLayout
					.setVideoPlayCallback(new MyVideoPlayCallback(
							holder.mPlayBtnView, holder.mVideoViewLayout));

			// TODO: 2015/11/19 分享
			holder.iv_share.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// ShareUtils.shareContent(mActivity, title, mp4_url);
				}
			});
			if (indexPostion == position) {
				holder.mVideoViewLayout.setVisibility(View.VISIBLE);
			} else {
				holder.mVideoViewLayout.setVisibility(View.GONE);
				holder.mVideoViewLayout.close();
			}
			return v;
		}
		class MyVideoPlayCallback implements
				VideoSuperPlayer.VideoPlayCallbackImpl {
			ImageView mPlayBtnView;
			VideoSuperPlayer mSuperVideoPlayer;
			public MyVideoPlayCallback(ImageView mPlayBtnView,
					VideoSuperPlayer mSuperVideoPlayer) {
				this.mPlayBtnView = mPlayBtnView;
				this.mSuperVideoPlayer = mSuperVideoPlayer;
			}
			@Override
			public void onCloseVideo() {
				isPlaying = false;
				indexPostion = -1;
				mSuperVideoPlayer.close();
				BaseApplication.setMediaPlayerNull();
				mPlayBtnView.setVisibility(View.VISIBLE);
				mSuperVideoPlayer.setVisibility(View.GONE);
			}
			@Override
			public void onSwitchPageType(){
				if (((Activity) context).getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
					Intent intent = new Intent(new Intent(context,
							FullActivity.class));
					context.startActivity(intent);
				}
			}
			@Override
			public void onPlayFinish() {

			}
		}
		class MyOnclick implements View.OnClickListener {
			String url;
			VideoSuperPlayer mSuperVideoPlayer;
			int position;
			public MyOnclick(String url, VideoSuperPlayer mSuperVideoPlayer,
					int position) {
				this.url = url;
				this.position = position;
				this.mSuperVideoPlayer = mSuperVideoPlayer;
			}
			@Override
			public void onClick(View v) {
				BaseApplication.setMediaPlayerNull();
				indexPostion = position;
				isPlaying = true;
				mSuperVideoPlayer.setVisibility(View.VISIBLE);
				mSuperVideoPlayer.loadAndPlay(BaseApplication.getMediaPlayer(),
						url, 0, false);
				notifyDataSetChanged();
			}
		}
		class GameVideoViewHolder {
			private VideoSuperPlayer mVideoViewLayout;
			private ImageView mPlayBtnView;
			private ImageView shiting_icon;
			private ImageView iv_share;
			public TextView title;
			public TextView description;
			public TextView length;
			public TextView playCount;
			public TextView replyCount;

		}
	}
	 //从网络获取数据
    private void getData(final String url, final boolean isfenye) {
        if (CommonUtil.isNetWork(mActivity)) {
            if (!url.equals("")) {
                httpUtils = new HttpUtils();
                httpHandler = httpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {
                	 @Override
                     public void onSuccess(ResponseInfo<String> responseInfo) {
                         SharedPreferencesUtil.saveData(mActivity, url, responseInfo.result);
                         paserData(responseInfo.result, isfenye);//Gson解析数据
                     }
                     @Override
                     public void onFailure(HttpException e, String s) {
                         Toast.makeText(mActivity, "数据请求失败", Toast.LENGTH_SHORT).show();
                     }
                });
            }
        } else {
            Toast.makeText(mActivity, "请检查网络连接......", Toast.LENGTH_SHORT).show();
        }
    }
    boolean isfenye;
    //解析数据并添加到集合
    private void paserData(String result, boolean isfenye) {
        if (!isfenye&&list!=null) {
            list.clear();
        }
        ShitingBean shitingBean = new Gson().fromJson(result, ShitingBean.class);
        if (mAdapter == null) {
            mListView.getRefreshableView().addHeaderView(View.inflate(mActivity, R.layout.shiting_frament, null));
            mAdapter = new MAdapter(mActivity, shitingBean.V9LG4B3A0);
            mListView.getRefreshableView().setAdapter(mAdapter);
        } else {
            list.addAll(shitingBean.V9LG4B3A0);
            mAdapter.setList(list);
        }
        mListView.onPullDownRefreshComplete();
        mListView.onPullUpRefreshComplete();
    }
}

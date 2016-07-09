package com.fanxi.zeronews.activity.pager;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.fanxi.zeronews.R;
import com.fanxi.zeronews.activity.BaiduMapActivity;
import com.fanxi.zeronews.activity.BaseActivity;
import com.fanxi.zeronews.activity.ChatActivity;
import com.fanxi.zeronews.activity.EmsQueryActivity;
import com.fanxi.zeronews.activity.HcpSearchActivity;
import com.fanxi.zeronews.activity.QrCodeActivity;
import com.libs.zxing.CaptureActivity;

public class ServerPager extends BasePager implements OnPageChangeListener,
		OnItemClickListener {
	private int[] ids = { R.drawable.a, R.drawable.b, R.drawable.c,
			R.drawable.d, R.drawable.e };
	private int serverIds[] = { R.drawable.icon_express, R.drawable.icon_train,
			R.drawable.decode, R.drawable.game, R.drawable.scenery,
			R.drawable.qr_code, R.drawable.drug, R.drawable.periphery1,
			R.drawable.render };
	private String[] serverName = {// 百度apistore(二维码联图网)
	// "快递100", "火车票查询(聚合)", "航班动态(聚合)", "轻松5秒钟", "热门景点", "二维码扫描",
	// "药品查询","周边检索", "翻译"
	"快递查询", "火车票查询", "二维码生成", "语音小助手", "热门景点", "二维码扫描", "药品查询", "周边检索", "翻译" };
	// 一语倾心api研究,道有道api研究,多米广告,畅思广告，阿里妈妈营销,微卡卡
	// private String[] serverName={//百度apistore
	// "热门景点","今日油价","药品查询","快递上门","快递100","浏览器上网","百度糯米","二维码扫描(联图网)"
	// ,"5秒轻游戏","外语立刻说","火车票查询(聚合)","翻译","航班动态(聚合)","挂号网","艺龙酒店预定",
	// };
	// private String[] serverName1={//阿凡达数据
	// "药品查询","周边检索(看看地图可否实现)","测距","热点检索","音乐搜索","今日油价","药房药店"
	// };
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			vp_top.setCurrentItem(vp_top.getCurrentItem() + 1);
			handler.sendEmptyMessageDelayed(0, 3000);
		};
	};
	private ViewPager vp_top;
	private LinearLayout ll_circle;
	private TextView tv_desc;
	private GridView gv;
	private int downY;
	private Intent intent;
	private String[] descs;
	public ServerPager(Activity mActivity, FragmentManager fm) {
		super(mActivity, fm);
	}
	@Override
	public View initView() {
		// TextView tv=new TextView(mActivity);
		// tv.setText("服务");
		// tv.setTextSize(25);
		// tv.setGravity(Gravity.CENTER);
		View view = View.inflate(mActivity, R.layout.pager_server, null);
		gv = (GridView) view.findViewById(R.id.gv);
		vp_top = (ViewPager) view.findViewById(R.id.vp_top);
		ll_circle = (LinearLayout) view.findViewById(R.id.ll_circle);
		tv_desc = (TextView) view.findViewById(R.id.tv_desc);
		return view;
	}

	@Override
	public void initData() {
		descs = new String[] { "巩俐不低俗,我就不能低俗", "朴树又回来了，再唱经典老歌引百万粉丝同唱",
				"揭秘北京电影如何升级", "乐视Tv版大放送", "热血吊丝的反杀" };
		initCircle();
		tv_desc.setText(descs[0]);
		vp_top.setAdapter(new MyPagerAdapter());
		vp_top.setOnPageChangeListener(this);
		int currentValue = Integer.MAX_VALUE / 2;
		int value = currentValue % ids.length;
		vp_top.setCurrentItem(currentValue - value);
		handler.sendEmptyMessageDelayed(0, 3000);
		gv.setAdapter(new MyGridPagerAdpter());
		gv.setOnItemClickListener(this);
	}
	/**
	 * 顶部数据适配器
	 * 
	 * @author Fanxi
	 * 
	 */
	class MyPagerAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView imageView = new ImageView(mActivity);
			imageView.setImageResource(ids[position % ids.length]);
			imageView.setScaleType(ScaleType.FIT_XY);
			container.addView(imageView);
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

	/**
	 * 底部数据适配器
	 * 
	 * @author Fanxi
	 * 
	 */
	class MyGridPagerAdpter extends BaseAdapter {
		public MyGridPagerAdpter() {
		}

		@Override
		public int getCount() {
			return serverName.length;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View view = View.inflate(mActivity, R.layout.server_item, null);
			ImageView imageView = (ImageView) view.findViewById(R.id.iv);
			imageView.setImageResource(serverIds[arg0]);
			TextView tv = (TextView) view.findViewById(R.id.tv);
			tv.setText(serverName[arg0]);
			return view;
		}
	}

	/**
	 * 初始化圆点的方法
	 */
	private void initCircle() {
		for (int i = 0; i < descs.length; i++) {
			View view = new View(mActivity);
			view.setEnabled(false);
			LayoutParams params = new LayoutParams(8, 8);
			if (i != 0) {
				params.leftMargin = 5;
			}
			view.setLayoutParams(params);
			view.setBackgroundResource(R.drawable.mycircle_selector);
			ll_circle.addView(view);
		}
		ll_circle.getChildAt(0).setEnabled(true);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		tv_desc.setText(descs[arg0 % ids.length]);
		for (int i = 0; i < ll_circle.getChildCount(); i++) {
			ll_circle.getChildAt(i).setEnabled(i == arg0 % ids.length);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 0:
			intent = new Intent(mActivity, EmsQueryActivity.class);
			mActivity.startActivity(intent);
			break;
		case 1:
			intent = new Intent(mActivity, HcpSearchActivity.class);
			mActivity.startActivity(intent);
			break;
		case 2:
			intent = new Intent(mActivity, QrCodeActivity.class);
			mActivity.startActivity(intent);
			break;
		case 3:
			intent = new Intent(mActivity, ChatActivity.class);
			mActivity.startActivity(intent);
			break;
		case 5:
			intent = new Intent(mActivity, CaptureActivity.class);
			mActivity.startActivity(intent);
			break;
		case 7:
			intent =new Intent(mActivity,BaiduMapActivity.class);
			mActivity.startActivity(intent);
			break;
		default:
			break;
		}
	}
}

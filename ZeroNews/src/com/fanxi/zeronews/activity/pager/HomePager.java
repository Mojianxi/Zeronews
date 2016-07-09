package com.fanxi.zeronews.activity.pager;
import java.util.ArrayList;

import com.fanxi.zeronews.R;
import com.fanxi.zeronews.activity.BaseActivity;
import com.fanxi.zeronews.util.PrefUtils;
import com.fanxi.zeronews.view.NoScrollViewPager;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
public class HomePager {
	public View mRootView;
	public Activity mActivity;
	private NoScrollViewPager vp_content;
	private RadioGroup rg_group;
	private ArrayList<BasePager> pagerList;
	private FragmentManager fm;
	private RadioButton rb_news;
	private RadioButton rb_video;
	private RadioButton rb_service;
	public HomePager(Activity mActivity, FragmentManager fm) {
		super();
		this.mActivity = mActivity;
		mRootView=initView();
		this.fm=fm;
		initData();
	}
	public View initView(){
		View view=View.inflate(mActivity, R.layout.pager_home, null);
		vp_content = (NoScrollViewPager) view.findViewById(R.id.vp_content);
		rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
		rb_news = (RadioButton) view.findViewById(R.id.rb_news);
		rb_video = (RadioButton) view.findViewById(R.id.rb_video);
		rb_service = (RadioButton) view.findViewById(R.id.rb_service);
//		TextView tv=new TextView(mActivity);
//		tv.setText("主页");
//		tv.setTextSize(25);
//		tv.setGravity(Gravity.CENTER);
		if(BaseActivity.mTheme==R.style.RedTheme){
			rg_group.setBackgroundResource(R.color.navigation_red);
		}else if(BaseActivity.mTheme==R.style.NomalTheme){
			rg_group.setBackgroundResource(R.color.nomal_tab);
		}else if(BaseActivity.mTheme==R.style.AppBaseTheme){
			rg_group.setBackgroundResource(R.color.navigation_nomal);
		}else if(BaseActivity.mTheme==R.style.DefaultTheme){
			rg_group.setBackgroundResource(R.color.navigation_default);
		}else if(BaseActivity.mTheme==R.style.PinkTheme){
			rg_group.setBackgroundResource(R.color.navigation_pink);
		}else if(BaseActivity.mTheme==R.style.PurpleTheme){
			rg_group.setBackgroundResource(R.color.navigation_purple);
		}else if(BaseActivity.mTheme==R.style.DeepPurpleTheme){
			rg_group.setBackgroundResource(R.color.navigation_deep_purple);
		}else if(BaseActivity.mTheme==R.style.IndigoTheme){
			rg_group.setBackgroundResource(R.color.navigation_indigo);
		}else if(BaseActivity.mTheme==R.style.BlueTheme){
			rg_group.setBackgroundResource(R.color.navigation_blue);
		}else if(BaseActivity.mTheme==R.style.LightBlueTheme){
			rg_group.setBackgroundResource(R.color.navigation_light_blue);
		}else if(BaseActivity.mTheme==R.style.CyanTheme){
			rg_group.setBackgroundResource(R.color.navigation_cyan);
		}else if(BaseActivity.mTheme==R.style.TealTheme){
			rg_group.setBackgroundResource(R.color.navigation_teal);
		}else if(BaseActivity.mTheme==R.style.GreenTheme){
			rg_group.setBackgroundResource(R.color.navigation_green);
		}else if(BaseActivity.mTheme==R.style.LightGreenTheme){
			rg_group.setBackgroundResource(R.color.navigation_light_green);
		}else if(BaseActivity.mTheme==R.style.LimeTheme){
			rg_group.setBackgroundResource(R.color.navigation_lime);
		}else if(BaseActivity.mTheme==R.style.YellowTheme){
			rg_group.setBackgroundResource(R.color.navigation_yellow);
		}else if(BaseActivity.mTheme==R.style.AmberTheme){
			rg_group.setBackgroundResource(R.color.navigation_amber);
		}else if(BaseActivity.mTheme==R.style.OrangeTheme){
			rg_group.setBackgroundResource(R.color.navigation_orange);
		}else if(BaseActivity.mTheme==R.style.DeepOrangeTheme){
			rg_group.setBackgroundResource(R.color.navigation_deep_orange);
		}else if(BaseActivity.mTheme==R.style.BrownTheme){
			rg_group.setBackgroundResource(R.color.navigation_brown);
		}else if(BaseActivity.mTheme==R.style.GreyTheme){
			rg_group.setBackgroundResource(R.color.navigation_grey);
		}else if(BaseActivity.mTheme==R.style.BlueGreyTheme){
			rg_group.setBackgroundResource(R.color.navigation_blue_grey);
		}else{
			rg_group.setBackgroundResource(R.color.nomal_tab);
		}
		return view;
	}
	public void initData(){
		pagerList=new ArrayList<BasePager>();
		pagerList.add(new NewsPager(mActivity,fm));
		pagerList.add(new VedioPager(mActivity,fm));
		ServerPager serverPager = new ServerPager(mActivity,fm);
		pagerList.add(serverPager);
		serverPager.initData();
		vp_content.setAdapter(new MyAdpter());
		rg_group.check(R.id.rb_news);
		rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_news:
					vp_content.setCurrentItem(0, false);
					break;
				case R.id.rb_video:
					vp_content.setCurrentItem(1, false);
					break;
				case R.id.rb_service:
					vp_content.setCurrentItem(2, false);
					break;
				}
			}
		});
	}
	class MyAdpter extends PagerAdapter{
		@Override
		public int getCount() {
			return pagerList.size();
		}
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0==arg1;
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position){
			BasePager pager=pagerList.get(position);
			container.addView(pager.mRootView);
			return pager.mRootView;
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}
}

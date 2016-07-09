package com.fanxi.zeronews.activity.pager;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.fanxi.zeronews.R;
import com.fanxi.zeronews.activity.BaseActivity;
import com.fanxi.zeronews.activity.fragment.BaseFragment;
import com.fanxi.zeronews.activity.fragment.FragmentFactory;
import com.fanxi.zeronews.util.UiUtils;
import com.fanxi.zeronews.view.HorizontalScrollViewPager;
import com.fanxi.zeronews.view.PagerTab;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class NewsPager extends BasePager {
	private PagerTab pager_tab;
	private HorizontalScrollViewPager horizontal_view;
	private String[] titleArrays;
	public NewsPager(Activity mActivity, FragmentManager fm) {
		super(mActivity,fm);
	}
	@Override
	public View initView() {
		View view=View.inflate(mActivity, R.layout.pager_news,null);
		pager_tab = (PagerTab) view.findViewById(R.id.pager_tab);
		horizontal_view = (HorizontalScrollViewPager) view.findViewById(R.id.horizontal_view);
		horizontal_view.setAdapter(new MyPagerAdpter(fm));
		pager_tab.setViewPager(horizontal_view);
		pager_tab.getChildAt(0).setSelected(true); 
		pager_tab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}
			@Override
			public void onPageSelected(int position) {
				ViewPropertyAnimator.animate(pager_tab.getChildAt(position)).scaleX(horizontal_view.getCurrentItem()==position?1.2f:1.0f).setDuration(200);
				ViewPropertyAnimator.animate(pager_tab.getChildAt(position)).scaleY(horizontal_view.getCurrentItem()==position?1.2f:1.0f).setDuration(200);
				BaseFragment newsFragment=FragmentFactory.createFragment(position);
				newsFragment.initData();
			}
			@Override
			public void onPageScrollStateChanged(int state){
			}
		});
		if(BaseActivity.mTheme==R.style.RedTheme){
			pager_tab.setBackgroundResource(R.color.button_normal_red);
		}else if(BaseActivity.mTheme==R.style.NomalTheme){
			pager_tab.setBackgroundResource(R.color.nomal_tab);
		}else if(BaseActivity.mTheme==R.style.AppBaseTheme){
			pager_tab.setBackgroundResource(R.color.button_normal_noaml);
		}else if(BaseActivity.mTheme==R.style.DefaultTheme){
			pager_tab.setBackgroundResource(R.color.button_normal_default);
		}else if(BaseActivity.mTheme==R.style.PinkTheme){
			pager_tab.setBackgroundResource(R.color.button_normal_pink);
		}else if(BaseActivity.mTheme==R.style.PurpleTheme){
			pager_tab.setBackgroundResource(R.color.button_normal_purple);
		}else if(BaseActivity.mTheme==R.style.DeepPurpleTheme){
			pager_tab.setBackgroundResource(R.color.button_normal_deep_purple);
		}else if(BaseActivity.mTheme==R.style.IndigoTheme){
			pager_tab.setBackgroundResource(R.color.button_normal_indigo);
		}else if(BaseActivity.mTheme==R.style.BlueTheme){
			pager_tab.setBackgroundResource(R.color.button_normal_blue);
		}else if(BaseActivity.mTheme==R.style.LightBlueTheme){
			pager_tab.setBackgroundResource(R.color.button_normal_light_blue);
		}else if(BaseActivity.mTheme==R.style.CyanTheme){
			pager_tab.setBackgroundResource(R.color.button_normal_cyan);
		}else if(BaseActivity.mTheme==R.style.TealTheme){
			pager_tab.setBackgroundResource(R.color.button_normal_teal);
		}else if(BaseActivity.mTheme==R.style.GreenTheme){
			pager_tab.setBackgroundResource(R.color.button_normal_green);
		}else if(BaseActivity.mTheme==R.style.LightGreenTheme){
			pager_tab.setBackgroundResource(R.color.button_normal_light_green);
		}else if(BaseActivity.mTheme==R.style.LimeTheme){
			pager_tab.setBackgroundResource(R.color.button_normal_lime);
		}else if(BaseActivity.mTheme==R.style.YellowTheme){
			pager_tab.setBackgroundResource(R.color.button_normal_yellow);
		}else if(BaseActivity.mTheme==R.style.AmberTheme){
			pager_tab.setBackgroundResource(R.color.button_normal_amber);
		}else if(BaseActivity.mTheme==R.style.OrangeTheme){
			pager_tab.setBackgroundResource(R.color.button_normal_orange);
		}else if(BaseActivity.mTheme==R.style.DeepOrangeTheme){
			pager_tab.setBackgroundResource(R.color.button_normal_deep_orange);
		}else if(BaseActivity.mTheme==R.style.BrownTheme){
			pager_tab.setBackgroundResource(R.color.button_normal_brown);
		}else if(BaseActivity.mTheme==R.style.GreyTheme){
			pager_tab.setBackgroundResource(R.color.button_normal_grey);
		}else if(BaseActivity.mTheme==R.style.BlueGreyTheme){
			pager_tab.setBackgroundResource(R.color.button_normal_blue_grey);
		}else{
			pager_tab.setBackgroundResource(R.color.nomal_tab);
		}
		return view;
	}
	class MyPagerAdpter extends FragmentPagerAdapter{
		public MyPagerAdpter(FragmentManager fm) {
			super(fm);
			titleArrays= UiUtils.getResources().getStringArray(R.array.titles);
		}
		@Override
		public Fragment getItem(int position) {
			BaseFragment fragment = FragmentFactory.createFragment(position);
			return fragment;
		}
		@Override
		public int getCount() {
			return titleArrays.length;
		}
		@Override
		public CharSequence getPageTitle(int position){
			return titleArrays[position];
		}
	}
}

package com.fanxi.zeronews.activity.pager;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.view.View;

public abstract class BasePager {
	public Activity mActivity;
	public View mRootView;
	public FragmentManager fm;
	public BasePager(Activity mActivity,FragmentManager fm) {
		super();
		this.fm=fm;
		this.mActivity = mActivity;
		mRootView=initView();
	}
	public abstract View initView();
	public void initData(){
	}
}

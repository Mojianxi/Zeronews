package com.fanxi.zeronews.activity.fragment;

import com.fanxi.zeronews.R;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Fanxi on 2016/5/24.
 */
public class ReaderFragment extends BaseFragment {
	private RelativeLayout rl_pb;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=View.inflate(mActivity, R.layout.fragment_planet, null);
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	@Override
	public void initData(){
		
	}
}

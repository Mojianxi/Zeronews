package com.fanxi.zeronews.activity;

import com.fanxi.zeronews.R;

import android.os.Bundle;
import android.widget.TextView;

public class HcpSearchActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_hcpsearch);
		initView();
	}
	private void initView() {
		TextView tv_titlebar=(TextView) findViewById(R.id.tv_titlebar);
		tv_titlebar.setText("火车票查询");
	}
}

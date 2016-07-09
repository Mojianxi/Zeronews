package com.fanxi.zeronews.activity;
import cn.waps.AppConnect;

import android.app.Activity;
import android.os.Bundle;
public class AppnominateActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		AppConnect.getInstance("f06cf17b1ee207a78067cb4b27aebb87", "google", this).showAppOffers(this);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppConnect.getInstance(this).close();
	}
}

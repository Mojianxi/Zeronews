package com.fanxi.zeronews.activity;

import com.fanxi.zeronews.R;
import com.fanxi.zeronews.application.BaseApplication;
import com.fanxi.zeronews.bean.FirstEvent;
import com.fanxi.zeronews.util.PrefUtils;
import com.fanxi.zeronews.util.ResUtil;
import com.fanxi.zeronews.util.UiUtils;
import de.greenrobot.event.EventBus;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.TaskStackBuilder;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class BaseActivity extends FragmentActivity {
	public boolean isLogin;
	public String user_name1;
	public Bitmap loginPic;
	BaseApplication appState;
	public String textSize;
	public static int mTheme = -1;
	public Handler baseHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// Toast.makeText(BaseActivity.this, "收到消息", 0).show();
		};
	};
	public static int Screenwidth;
	public static int Screenheight;

	@Override
	protected void onCreate(Bundle arg0) {
		if (mTheme != -1) {
			setTheme(mTheme);
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN| localLayoutParams.flags);
		textSize = PrefUtils.getString(this, "TextSize", "");
//		if("大号".equals(textSize)){
//			this.setTheme(R.style.Theme_Large);
//		}else if("特大号".equals(textSize)){
//			this.setTheme(R.style.Theme_super);
//		}else if("小号".equals(textSize)){
//			this.setTheme(R.style.Theme_Small);
//		}else{
//			this.setTheme(R.style.Theme_Medium);
//		}
		super.onCreate(arg0);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		EventBus.getDefault().register(this);
		isLogin = PrefUtils.getBoolean(UiUtils.getContext(), "isLogin", false);
		user_name1 = PrefUtils.getString(UiUtils.getContext(), "user_name", "");
		loginPic = ResUtil.getBitmap(UiUtils.getContext(), user_name1);
		appState = (BaseApplication)getApplicationContext();
		DisplayMetrics metric = new DisplayMetrics();  
		getWindowManager().getDefaultDisplay().getMetrics(metric);  
		Screenwidth = metric.widthPixels;
		Screenheight = metric.heightPixels;
//		float density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）  
//		int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）  
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	public void onEventMainThread(FirstEvent event) {
		String test = event.getTest();
		// this.setTheme(R.style.Theme_Small);
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(newBase);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// if(textSize.equals("大号")){
		// this.setTheme(R.style.Theme_Large);
		// }else if(textSize.equals("小号")){
		// this.setTheme(R.style.Theme_Small);
		// }
	}

	public Context getActivty() {
		return this;
	}
}

package com.fanxi.zeronews.activity;
import com.fanxi.zeronews.R;
import com.fanxi.zeronews.util.PrefUtils;
import com.fanxi.zeronews.util.UiUtils;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
public class SplashActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		delayEnterMainActivity(true);
//		overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
//		View view = View.inflate(this, R.layout.activity_splash, null);
//		setContentView(view);
//		ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1,
//				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//				0.5f);
//		scale.setDuration(1000);
//		scale.setFillAfter(true);
//		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
//		alphaAnimation.setDuration(2000);
//		alphaAnimation.setFillAfter(true);
//		AnimationSet set = new AnimationSet(false);
//		set.addAnimation(scale);
//		set.addAnimation(alphaAnimation);
//		view.startAnimation(set);
//		set.setAnimationListener(new Animation.AnimationListener() {
//			@Override
//			public void onAnimationStart(Animation animation) {
//			}
//
//			@Override
//			public void onAnimationRepeat(Animation animation) {
//			}
//
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				startActivity(new Intent(SplashActivity.this,
//						MainActivity.class));
//				finish();
//			}
//		});
		
	}
	/**
	 * 延时进入MainActivity
	 * @param isDelay
	 */
	private void delayEnterMainActivity(boolean isDelay){
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if(!hasEnterMain){
					hasEnterMain = true;
					startActivity(new Intent(SplashActivity.this,MainActivity.class));
					finish();
				}
			}
		}, isDelay?2000:0);
	}
	private boolean hasEnterMain = false;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			delayEnterMainActivity(false);
			break;
		}
		return super.onTouchEvent(event);
	}
}

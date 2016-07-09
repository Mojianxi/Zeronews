package com.fanxi.zeronews.util;

import android.text.GetChars;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
public class AnimUtil {
	public static int animCount = 0;//记录当前执行的动画数量
	public static void closeMenu(RelativeLayout rl,int startOffset){
		for (int i = 0; i < rl.getChildCount(); i++) {
			rl.getChildAt(i).setEnabled(false);
		}
		//pivotXValue: 0-1
		AnimationSet set=new AnimationSet(false);
		AlphaAnimation animation1=new AlphaAnimation(1.0f, 0.3f);
		animation1.setDuration(500);
		RotateAnimation animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 1);
		animation.setDuration(500);
//		animation.setFillAfter(true);//动画结束后保持当时的状态
		animation.setStartOffset(startOffset);
		animation.setAnimationListener(new MyAnimationListener());
		set.addAnimation(animation);
		set.addAnimation(animation1);
		rl.startAnimation(set);
	}
	public static void showMenu(RelativeLayout rl,int startOffset){
		for (int i = 0; i < rl.getChildCount(); i++) {
			rl.getChildAt(i).setEnabled(true);
		}
		
		RotateAnimation animation = new RotateAnimation(-170, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 1);
		animation.setDuration(500);
		animation.setFillAfter(true);//动画结束后保持当时的状态
		animation.setStartOffset(startOffset);
		animation.setAnimationListener(new MyAnimationListener());
		rl.startAnimation(animation);
	}
	static class MyAnimationListener implements AnimationListener{
		@Override
		public void onAnimationStart(Animation animation) {
			animCount++;
		}
		@Override
		public void onAnimationEnd(Animation animation) {
			animCount--;
		}
		@Override
		public void onAnimationRepeat(Animation animation) {
		}
	}
}

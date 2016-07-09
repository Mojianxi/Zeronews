package com.fanxi.zeronews.view;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ToggleButton extends View{

	private ToggleState state = ToggleState.Close;//滑动开关的状态
	private Bitmap slideBg;//滑动块的背景图片
	private Bitmap switchBg;//滑动开关的背景图片
	
	private boolean isSliding = false;//是否正在滑动
	
	/**
	 * 如果你想让你的自定义view在布局文件中使用，则使用此构造函数
	 * @param context
	 * @param attrs
	 */
	public ToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 如果你想让你的自定义view在java代码中动态创建，则使用此构造函数
	 * @param context
	 */
	public ToggleButton(Context context) {
		super(context);
	}

	/**
	 * 设置滑动开关的背景图片
	 * @param switchBackground
	 */
	public void setSwitchBackgroundResource(int switchBackground) {
		switchBg = BitmapFactory.decodeResource(getResources(), switchBackground);
	}
	
	/**
	 * 设置滑动块的背景图片
	 * @param slideButtonBackground
	 */
	public void setSlideBackgroundResource(int slideButtonBackground) {
		slideBg = BitmapFactory.decodeResource(getResources(), slideButtonBackground);
	}
	
	public enum ToggleState{
		Open,Close
	}
	/**
	 * 设置滑动开关的状态
	 * @param close
	 */
	public void setToggleState(ToggleState toggleState) {
		state = toggleState;
	}

	/**
	 * 测量自己显示在屏幕上的宽高
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(switchBg.getWidth(), switchBg.getHeight());
	}
	
	/**
	 * 绘制自己在屏幕上显示的样子
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		//1.绘制开关背景图片
		canvas.drawBitmap(switchBg, 0, 0, null);
		
		if(isSliding){
			int drawLeft = currentX - slideBg.getWidth()/2;
			if(drawLeft<0)drawLeft = 0;
			if(drawLeft>(switchBg.getWidth()-slideBg.getWidth())){
				drawLeft = switchBg.getWidth()-slideBg.getWidth();
			}
			canvas.drawBitmap(slideBg, drawLeft, 0, null);
		}else {
			//2.根据state绘制滑动块背景图片
			if(state==ToggleState.Close){
				canvas.drawBitmap(slideBg, 0, 0, null);
			}else {
				canvas.drawBitmap(slideBg, switchBg.getWidth()-slideBg.getWidth(), 0, null);
			}
		}
		
	}
	
	private int currentX;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		currentX = (int) event.getX();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isSliding = true;
			break;
		case MotionEvent.ACTION_MOVE:
			
			break;
		case MotionEvent.ACTION_UP:
			isSliding = false;
			
			//根据抬起时的currentX去设置它的state
			int centerX= switchBg.getWidth()/2 ;
			if(currentX>centerX){
				if(state==ToggleState.Close){
					state = ToggleState.Open;
					if(onToggleStateChangeListener!=null){
						onToggleStateChangeListener.onStateChange(state);
					}
				}
			}else {
				if(state==ToggleState.Open){
					state = ToggleState.Close;
					if(onToggleStateChangeListener!=null){
						onToggleStateChangeListener.onStateChange(state);
					}
				}
			}
			break;
		}
		invalidate();
		return true;
	}
	
	
	private OnToggleStateChangeListener onToggleStateChangeListener;
	public void setOnToggleStateChangeListener(OnToggleStateChangeListener onToggleStateChangeListener){
		this.onToggleStateChangeListener = onToggleStateChangeListener;
	}
	public interface OnToggleStateChangeListener{
		void onStateChange(ToggleState state);
	}
	
}

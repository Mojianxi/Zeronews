package com.fanxi.zeronews.view;


import com.fanxi.zeronews.view.DragLayout.Status;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;


/**
 * 根据当前DragLayout的状态，处理触摸事件
 * @author poplar
 *
 */
public class MyLinearLayout extends LinearLayout {

	private DragLayout mDragLayout;

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLinearLayout(Context context) {
		super(context);
	}
	
	public void setDragLayout(DragLayout mDragLayout){
		this.mDragLayout = mDragLayout;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(mDragLayout.getStatus() == Status.Close){
			return super.onInterceptTouchEvent(ev);
		}else {
			return true;
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mDragLayout.getStatus() == Status.Close){
			return super.onTouchEvent(event);
		}else {
			
			if(event.getAction() == MotionEvent.ACTION_UP){
				mDragLayout.close();
			}
			return true;
		}
	}
	
	
	
}

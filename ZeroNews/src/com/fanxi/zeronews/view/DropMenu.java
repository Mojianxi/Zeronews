package com.fanxi.zeronews.view;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.Scroller;
public class DropMenu extends FrameLayout{
	private View menuView,mainView;
	private int menuWidth = 0;
	private int menuHeight = 0;
	private Scroller scroller;
	public DropMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public DropMenu(Context context) {
		super(context);
		init();
	}
	private void init(){
		scroller = new Scroller(getContext());
	}
	/**
	 * 当1级的子view全部加载完调用，可以用初始化子view的引用
	 * 注意，这里无法获取子view的宽高
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		menuView = getChildAt(0);
		mainView = getChildAt(1);
		menuWidth = menuView.getLayoutParams().width;
		menuHeight=menuView.getLayoutParams().height;
	}
	
	/**
	 * l: 当前子view的左边在父view的坐标系中的x坐标
	 * t: 当前子view的顶边在父view的坐标系中的y坐标
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		menuView.layout(0, b, r, menuHeight+b);
		mainView.layout(0, 0, r, b);
		menuHeight=b;
	}
	
	private int downY;
	private int deltaY;
	private int newScrollY;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downY = (int) event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			int moveY = (int) event.getY();
			deltaY = (int) ( moveY- downY);
			newScrollY = getScrollY() - deltaY;
			
			if(newScrollY>menuHeight)newScrollY = menuHeight;
			Log.e("Main", "scrollX: "+getScrollY());
			if(deltaY>0&&downY<100){
				scrollTo(0,newScrollY);
			}
			downY = moveY;
			break;
		case MotionEvent.ACTION_UP:
			if(deltaY>0){
				closeMenu();
			}
			break;
		}
		return true;
	}
	
	public void closeMenu(){
		scroller.startScroll(0,getScrollY(),0, 0-getScrollY(), 400);
		menuView.destroyDrawingCache();
		menuView.setVisibility(View.GONE);
		invalidate();
	}
	
	public void openMenu(){
		menuView.setVisibility(View.VISIBLE);
		scroller.startScroll(0, getScrollY(),0,menuHeight+getScrollY(),400);
		invalidate();
	}
	/**
	 * Scroller不主动去调用这个方法
	 * 而invalidate()可以掉这个方法
	 * invalidate->draw->computeScroll
	 */
	@Override
	public void computeScroll() {
		super.computeScroll();
		if(scroller.computeScrollOffset()){//返回true,表示动画没结束
			scrollTo(0,scroller.getCurrY());
			invalidate();
		}
	}
}

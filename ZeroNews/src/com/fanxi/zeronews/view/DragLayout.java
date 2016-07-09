package com.fanxi.zeronews.view;

import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 侧拉面板
 * @author poplar
 *
 */
public class DragLayout extends FrameLayout {
	private static final String TAG = "TAG";
	private ViewDragHelper mDragHelper;
	private ViewGroup mLeftContent;
	private ViewGroup mMainContent;
	public DragLayout(Context context) {
		this(context, null);
	}
	public DragLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public DragLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// 做一些初始化的事情
		// a. 通过静态方法得到ViewDragHelper类的对象
		mDragHelper = ViewDragHelper.create(this, mCallback);
		
	}
	/**
	 * 状态枚举
	 * @author poplar
	 *
	 */
	public static enum Status {
		Close , Open , Draging
	}
	
	public interface OnDragChangeListener{
		void onClose();
		void onOpen();
		void onDraging(float percent);
	}
	private Status status = Status.Close;
	private OnDragChangeListener listener;
	
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public OnDragChangeListener getListener() {
		return listener;
	}
	public void setListener(OnDragChangeListener listener) {
		this.listener = listener;
	}
	// c. 重写回调
	ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
		// 1. 是否捕获当前子View, 返回结果是否可以被拖拽
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			Log.d(TAG, "tryCaptureView: " + child);
			return child == mMainContent || child == mLeftContent;
		}
		// 2. 当View被捕获时，调用
		@Override
		public void onViewCaptured(View capturedChild, int activePointerId) {
			super.onViewCaptured(capturedChild, activePointerId);
			Log.d(TAG, "onViewCaptured: " + capturedChild);
		}
		// 3. 设置横向拖拽的范围， 不会影响拖拽位置
		@Override
		public int getViewHorizontalDragRange(View child) {
			return mRange;
		}
		// 4. 进行移动前的位置修正，left是建议值，此时没有还进行真实的移动
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			int oldLeft = mMainContent.getLeft();
			// left = oldLeft + dx;
			Log.d(TAG, "clampViewPositionHorizontal: left: " + left + "= oldLeft: " + oldLeft + " dx: " + dx);
			if(child == mMainContent){
				left = fixLeft(left);
			}
			return left;
		}
		// 5. 当View的位置改变之后，处理要做的事情：（重绘界面、伴随动画、更新状态）
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			Log.d(TAG, "onViewPositionChanged : " + left);
			
			int newLeft = mMainContent.getLeft();
			if(changedView == mMainContent){
				newLeft = left;
			}else {
				// 如果拖拽的不是主面板，将变化量交给新的left值
				newLeft += dx;
			}
			Log.d(TAG, "newLeft : " + newLeft);
			
			// 修正值
			newLeft = fixLeft(newLeft);
			
			if(changedView == mLeftContent){
				mLeftContent.layout(0, 0, mWidth, mHeight);
				mMainContent.layout(newLeft, 0, newLeft + mWidth, 0 + mHeight); 
			}

			// 分发拖拽事件
			dispatchDragEvent(newLeft);
			invalidate();
		}
		// 6. 当View被释放时，处理要做的结束动画
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			// 判断所有的开启情况
			if(xvel == 0 && mMainContent.getLeft() > mRange / 2.0f){
				open();
			}else if (xvel > 0) {
				open();
			}else {
				close();
			}
		}
		
		// 7. 状态更新
		@Override
		public void onViewDragStateChanged(int state) {
			super.onViewDragStateChanged(state);
//	         * @see #STATE_IDLE
//	         * @see #STATE_DRAGGING
//	         * @see #STATE_SETTLING
			Log.d(TAG, "onViewDragStateChanged: " + state);
		}

	};
	
	/**
	 * 修正左边值
	 * @param left
	 * @return
	 */
	private int fixLeft(int left) {
		if(left < 0){
			return 0;
		}else if (left > mRange) {
			return mRange;
		}
		return left;
	}
	
	protected void dispatchDragEvent(int newLeft) {
		float percent = newLeft * 1.0f / mRange;
		// 0.0f -> 1.0f
		Log.d(TAG, "percent: " + percent);
		
		// 更新状态
		if(listener != null){
			listener.onDraging(percent);
		}
		Status lastStatus = status;
		status = updateStatus(percent);
		if(lastStatus != status){
			// 状态改变了
			if(listener != null){
				if(status == Status.Close){
					listener.onClose();
				}else if (status == Status.Open) {
					listener.onOpen();
				}
			}
		}
		
		// 执行动画
		animViews(percent);
	}

	private Status updateStatus(float percent) {
		if(percent == 0f){
			return Status.Close;
		}else if (percent == 1.0f) {
			return Status.Open;
		}
		return Status.Draging;
	}

	private void animViews(float percent) {
		//		> a. 左面板：缩放动画、透明度动画、平移动画
				// 缩放动画 0.5 -> 1.0 【0.5f + percent * (1 - 0.5f)】
		//		mLeftContent.setScaleX(evaluate(percent,  0.5f, 1.0f));
		//		mLeftContent.setScaleY(evaluate(percent,  0.5f, 1.0f));
				ViewHelper.setScaleX(mLeftContent, evaluate(percent,  0.5f, 1.0f));
				ViewHelper.setScaleY(mLeftContent, evaluate(percent,  0.5f, 1.0f));
				// 透明度 
		//		mLeftContent.setAlpha(evaluate(percent,  0.5f, 1.0f));
				ViewHelper.setAlpha(mLeftContent, evaluate(percent,  0.5f, 1.0f));
				
				// 平移动画  -mWidth/2 -> 0
		//		mLeftContent.setTranslationX(evaluate(percent, -mWidth/2, 0));
				ViewHelper.setTranslationX(mLeftContent, evaluate(percent, -mWidth/2, 0));
				
		//		> b. 主面板：缩放动画
				ViewHelper.setScaleX(mMainContent, evaluate(percent, 1.0f, 0.8f));
				ViewHelper.setScaleY(mMainContent, evaluate(percent, 1.0f, 0.8f));
				
		//		> c. 背景： 亮度变化
				getBackground().setColorFilter((Integer)evaluateColor(percent, Color.BLACK, Color.TRANSPARENT), Mode.SRC_OVER);
	}

	// 估值器
    public Float evaluate(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }
    /**
     * 颜色过渡器
     * @param fraction
     * @param startValue
     * @param endValue
     * @return
     */
    public Object evaluateColor(float fraction, Object startValue, Object endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int)((startA + (int)(fraction * (endA - startA))) << 24) |
                (int)((startR + (int)(fraction * (endR - startR))) << 16) |
                (int)((startG + (int)(fraction * (endG - startG))) << 8) |
                (int)((startB + (int)(fraction * (endB - startB))));
    }
	
	@Override
	public void computeScroll() {
		super.computeScroll();
		
		// 使动画"持续"执行，直到指定位置
		if(mDragHelper.continueSettling(true)){
			// true 需要重绘界面
			ViewCompat.postInvalidateOnAnimation(this);
		}
		
	}

	/**
	 * 执行关闭动画
	 */
	protected void close() {
		close(true);
	}
	public void close(boolean isSmooth){
		int finalLeft = 0;
		if(isSmooth){
			//ViewDragHelper内部封装了Scroller， 辅助平滑移动
			
			// "触发"一个平滑移动的事件.
			if(mDragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)){
				// 需要刷新界面，还未移动到指定位置。参数传this
				ViewCompat.postInvalidateOnAnimation(this);
			}
			
		}else {
			mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight); 
		}
	}
	
	/**
	 * 执行开启动画
	 */
	protected void open() {
		open(true);
	}
	public void open(boolean isSmooth){
		int finalLeft = mRange;
		if(isSmooth){
			//ViewDragHelper内部封装了Scroller， 辅助平滑移动
			
			// "触发"一个平滑移动的事件.
			if(mDragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)){
				// 需要刷新界面，还未移动到指定位置。参数传this
				ViewCompat.postInvalidateOnAnimation(this);
			}
			
		}else {
			mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight); 
		}
	}

	private int mHeight;
	private int mWidth;
	private int mRange;
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// b. 托管触摸事件
		return mDragHelper.shouldInterceptTouchEvent(ev);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// b. 处理触摸事件
		try {
			mDragHelper.processTouchEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		// 当界面填充完毕之后被调用
		
		// 对代码进行健壮性检查
		// 检查子View个数，必须大于等于2个
		if( getChildCount() < 2){
			throw new IllegalStateException("Your layout must contains 2 children at least.");
		}
		// 检查子View的类型, 必须是ViewGroup的子类
		if(!(getChildAt(0) instanceof ViewGroup && getChildAt(1) instanceof ViewGroup)){
			throw new IllegalArgumentException("Your children must be an instance of ViewGroup");
		}
		mLeftContent = (ViewGroup) getChildAt(0);
		mMainContent = (ViewGroup) getChildAt(1);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		mHeight = mMainContent.getMeasuredHeight();
		mWidth = mMainContent.getMeasuredWidth();
		
		mRange = (int) (mWidth * 0.6f);
		
	}
}

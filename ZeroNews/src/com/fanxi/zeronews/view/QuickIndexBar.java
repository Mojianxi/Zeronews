package com.fanxi.zeronews.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
public class QuickIndexBar extends View {
	private static final String[] LETTERS = new String[] { "A", "B", "C", "D",
			"E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
			"R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	private Paint paint;
	private int mCellWidth;
	private int mHeight;
	private float mCellHeight;
	private int touchIndex=-1;
	private onLetterChangeListener letterChangeListener;
	public interface onLetterChangeListener{
		void onLetterChange(String letter);
	}
	public onLetterChangeListener getLetterChangeListener() {
		return letterChangeListener;
	}
	public void setLetterChangeListener(onLetterChangeListener letterChangeListener) {
		this.letterChangeListener = letterChangeListener;
	}
	public QuickIndexBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public QuickIndexBar(Context context) {
		this(context, null);
	}
	public QuickIndexBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.WHITE);
		paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				10f, getResources().getDisplayMetrics()));
		paint.setTypeface(Typeface.DEFAULT_BOLD);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// 循环绘制字母
		for (int i = 0; i < LETTERS.length; i++) {
			String text = LETTERS[i];
			int x = (int) (mCellWidth / 2 - paint.measureText(text) / 2);
			Rect bounds = new Rect();
			paint.getTextBounds(text, 0, text.length(), bounds);
			int y = (int) (mCellHeight / 2 + bounds.height() / 2 + mCellHeight
					* i);
			paint.setColor(touchIndex == i ? Color.GRAY : Color.WHITE);
			canvas.drawText(text, x, y, paint);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mHeight = getMeasuredHeight();
		mCellHeight = mHeight / LETTERS.length;
		mCellWidth = getMeasuredWidth();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			float y=event.getY();
			int index=(int) (y/mCellHeight);
			if(index!=touchIndex){
				if(index>=0&&index<LETTERS.length){
					if(letterChangeListener!=null){
						letterChangeListener.onLetterChange(LETTERS[index]);
					}
				}
				touchIndex=index;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			int i=(int) (event.getY()/mCellHeight);
			if(i!=touchIndex){
				if(i>=0&&i<LETTERS.length){
					if(letterChangeListener!=null){
						letterChangeListener.onLetterChange(LETTERS[i]);
					}
				}
//				Toast.makeText(getContext(), LETTERS[i], 0).show();
				touchIndex = i;
			}
			break;
		case MotionEvent.ACTION_UP:
			touchIndex=-1;
			break;
		}
		invalidate();
		return true;
	}
}

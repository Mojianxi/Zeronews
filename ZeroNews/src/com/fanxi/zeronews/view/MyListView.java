package com.fanxi.zeronews.view;

/**
 * Created by Fanxi on 2016/5/31.
 */
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
public class MyListView extends ListView {
    private WindowManager wm;
    private static final String TAG = "TAG";
    private int mOriginalHeight;
    private int drawableHeight;
    private ImageView mImage;
    private int wWidth;
    private int wHeight;
    public WebView web;
    private String url;
//    private NewsDetailActivity.MyAsnycTask as;
    private int startHeight;
    private int endHeight;
    private ImageButton user;
    private int left;
    private int top;
    private int bottom;
    private int right;
    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MyListView(Context context) {
        super(context);
    }
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                   int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        //deltaY垂直方向的瞬时变化量,与力度相关,在顶部向下拉是负值,在顶部向上拉为正值
//		scrollY:竖直方向的偏移量,与移动的多少相关
//		scrollRangeY:竖直方向滑动的范围
//		maxOverScrollY:竖直最大滑动范围
//		isTouchEvent：是否是手指触摸滑动,false是惯性
        //手指拉动并且是下拉
        if(isTouchEvent && deltaY < 0){
            // 把拉动的瞬时变化量的绝对值交给Header, 就可以实现放大效果

            if(mImage.getHeight() <= drawableHeight){
                if(listerner!=null){
                    listerner.onLoading(true);
                }
                int newHeight = (int) (mImage.getHeight() + Math.abs(deltaY / 3.0f));
                if(user!=null){
                    //克隆view的width、height、margin的值生成margin对象
                    MarginLayoutParams margin=new MarginLayoutParams(user.getLayoutParams());
                    //设置新的边距
                    margin.setMargins(margin.leftMargin+left,newHeight-(wHeight/2),margin.rightMargin+right,bottom);
                    //把新的边距生成layoutParams对象
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
                    //设制view的新的位置
                    user.setLayoutParams(layoutParams);
                    user.requestLayout();
                }
                // 高度不超出图片最大高度时,才让其生效
                mImage.getLayoutParams().height = newHeight;
                mImage.requestLayout();
            }
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }
    public void setLocation(int l,int t,int r,int b){
        this.left=l;
        this.top=t;
        this.right=r;
        this.bottom=b;
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(web!=null){
            web.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }
    LoadingListerner listerner;
    public void setLoadingListerner(LoadingListerner listerner){
        this.listerner=listerner;
    }
    public interface LoadingListerner{
        void onLoading(boolean f);
    }
//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		return false;
//	}
    /**
     * 设置ImageView图片, 拿到引用
     * @param mImage
     */
    public void setParallaxImage(ImageView mImage,ImageButton user) {
        this.user=user;
        this.mImage = mImage;
        if(user!=null){
            wHeight =user.getHeight();
        }
        mOriginalHeight = mImage.getHeight(); // 160
//        drawableHeight = mImage.getDrawable().getIntrinsicHeight(); // 488
        drawableHeight=mOriginalHeight*4;
        this.url=url;
//        this.as=as;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(web!=null){
//            web.onTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int startY=(int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                // 执行回弹动画, 方式一: 属性动画\值动画
                // 从当前高度mImage.getHeight(), 执行动画到原始高度mOriginalHeight
                startHeight = mImage.getHeight();
                endHeight = mOriginalHeight;
                valueAnimator(startHeight, endHeight);
                break;
        }
        return super.onTouchEvent(ev);
    }
    private void valueAnimator(final int startHeight, final int endHeight){
        ValueAnimator mValueAnim = ValueAnimator.ofInt(1);
        mValueAnim.addUpdateListener(new AnimatorUpdateListener() {
            @SuppressLint("NewApi")
            @Override
            public void onAnimationUpdate(ValueAnimator mAnim) {
                float fraction = mAnim.getAnimatedFraction();
                Integer newHeight = evaluate(fraction, startHeight, endHeight);
                mImage.getLayoutParams().height = newHeight;
                if(user!=null){
                    //克隆view的width、height、margin的值生成margin对象
                    MarginLayoutParams margin=new MarginLayoutParams(user.getLayoutParams());
                    //设置新的边距
                    margin.setMargins(margin.leftMargin+left,newHeight-(wHeight/2),margin.rightMargin+right,bottom);
                    //把新的边距生成layoutParams对象
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
                    //设制view的新的位置
                    user.setLayoutParams(layoutParams);
                    user.requestLayout();
                }
                mImage.requestLayout();
            }
        });
        mValueAnim.setInterpolator(new OvershootInterpolator());
        mValueAnim.setDuration(500);
        mValueAnim.start();
    }
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        return (int)(startInt + fraction * (endValue - startInt));
    }
    public void setWebView(WebView web,String url){
        this.web=web;
        this.url=url;
//    	web.loadUrl(url);
    }
    public void setWindowXY(int x,int y) {
        wWidth=x;
        wHeight=y;
    }
}

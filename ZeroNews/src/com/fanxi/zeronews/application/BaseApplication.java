package com.fanxi.zeronews.application;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.SDKInitializer;
import com.fanxi.zeronews.R;
import com.fanxi.zeronews.util.PrefUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import cn.jpush.android.api.JPushInterface;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;
public class BaseApplication extends Application {
	private static Context context;
	private List<Activity> mainActivity = new ArrayList<Activity>();
	public static MediaPlayer mPlayer;
	@Override
	public void onCreate() {
		this.context = getApplicationContext();
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
		initImageLoader(getApplicationContext());
		SDKInitializer.initialize(getApplicationContext());
		super.onCreate();
	}
	public static Context getContext() {
		return context;
	}
	public List<Activity> MainActivity() {
		return mainActivity;
	}
	public void addActivity(Activity act) {
		if(act!=null&&mainActivity!=null){
			mainActivity.add(act);
		}
	}
	public void finishAll(){
		for (Activity act : mainActivity) {
			if (!act.isFinishing()) {
				act.finish();
			}
		}
		mainActivity = null;
	}
	 public static MediaPlayer getMediaPlayer() {
	        if (mPlayer == null) {
	            mPlayer = new MediaPlayer();
	        }
	        return mPlayer;
	    }

	    public static void setMediaPlayerNull() {
	        if (mPlayer != null) {
	            mPlayer.stop();
	            mPlayer.release();
	            mPlayer = null;
	        }
	    }
	    @Override
	    public void onTerminate() {
	        super.onTerminate();
	    }
	    /**
		 * 初始�?ImageLoaderConfiguration
		 * 
		 * @param context
		 */
		public static void initImageLoader(Context context) {
			/*
			 * ImageLoaderConfiguration config = new
			 * ImageLoaderConfiguration.Builder(context)
			 * .threadPriority(Thread.NORM_PRIORITY - 2)
			 * .denyCacheImageMultipleSizesInMemory()
			 * .diskCacheFileNameGenerator(new Md5FileNameGenerator())
			 * .diskCacheSize(50 * 1024 * 1024) // 50 Mb
			 * .tasksProcessingOrder(QueueProcessingType.LIFO) .writeDebugLogs() //
			 * Remove for release app .build();
			 */
			// File cacheDir = new File(saveImagePath);
			File cacheDir = StorageUtils.getCacheDirectory(context);

			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
					context)
					.threadPriority(Thread.NORM_PRIORITY - 2)
					// default
					.tasksProcessingOrder(QueueProcessingType.FIFO)
					// default
					.denyCacheImageMultipleSizesInMemory()
					.diskCache(new UnlimitedDiscCache(cacheDir))
					// 自定义缓存目�?
					.diskCacheSize(20 * 1024 * 1024).diskCacheFileCount(100)
					.diskCacheFileNameGenerator(new Md5FileNameGenerator()) // default
					.writeDebugLogs().build();

			ImageLoader.getInstance().init(config);
		}
}

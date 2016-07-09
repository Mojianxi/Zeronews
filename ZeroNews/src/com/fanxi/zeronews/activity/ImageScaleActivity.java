package com.fanxi.zeronews.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageScaleActivity extends BaseActivity {
	private Bitmap bitmap;
	private String filepath;
	private static final boolean defaultValue = false;
	/** Called when the activity is first created. */
	private ImageView imageView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		filepath = intent.getStringExtra("filePath");
		if(!(filepath != null)){
			filepath = "http://h.hiphotos.baidu.com/image/pic/item/4bed2e738bd4b31c4859e0ba85d6277f9e2ff84e.jpg";
		}
		// 定义Handler对象
		final Handler handler = new Handler() {
			@Override
			// 当有消息发送出来的时候就执行Handler的这个方法
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				imageView=new ImageView(getBaseContext());
				imageView.setKeepScreenOn(true);
				imageView.setImageBitmap(bitmap);
				setContentView(imageView);
			}
		};

		new Thread() {
			@Override
			public void run() {
				// 执行完毕后给handler发送一个空消息
				bitmap = ImageLoader.getInstance().loadImageSync(filepath);
				handler.sendEmptyMessage(0);
			}
		}.start();

	}
	private static final int DRAG = 10;
	private static final int NULL = 0;
	private static final int SCALE = 11;
	private int mode;
	private float mStartX;

}
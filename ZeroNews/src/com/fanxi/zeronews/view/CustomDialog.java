package com.fanxi.zeronews.view;

import com.fanxi.zeronews.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomDialog extends Dialog {
	private TextView tv_title;
	private TextView tv_desc;
	private Button btn_check;
	private ImageView iv_close;
	public CustomDialog(Context context) {
		super(context,R.style.CommonDialog);
		setCustomDialog();
	}
	private void setCustomDialog() {
		View view = LayoutInflater.from(getContext()).inflate(
				R.layout.check_dialog, null);
		tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_desc = (TextView) view.findViewById(R.id.tv_desc);
		btn_check = (Button) view.findViewById(R.id.btn_check);
		iv_close = (ImageView) view.findViewById(R.id.iv_close);
		super.setContentView(view);
	}
	@Override
	public void setContentView(int layoutResID) {
	}
	/**
	 * 确定监听
	 */
	public void setOnPositiveListener(View.OnClickListener listener) {
		btn_check.setOnClickListener(listener);
	}
	/**
	 * 取消键监听器
	 * 
	 * @param listener
	 */
	public void setOnNegativeListener(View.OnClickListener listener) {
		iv_close.setOnClickListener(listener);
	}
}

package com.fanxi.zeronews.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fanxi.zeronews.R;

public class FeedBackActivity extends BaseActivity {
	private String et_submit;
	private EditText et_feedback;
	private Button submit_feedback;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_feedback);
		et_feedback = (EditText) findViewById(R.id.edt_feedbackinfo);
		et_submit = et_feedback.getText().toString();
		submit_feedback = (Button) findViewById(R.id.submit_feedback);
		et_feedback.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				et_submit=s.toString();
			}
		});
		submit_feedback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if("".equals(et_submit)||et_submit==null){
					Toast.makeText(getApplicationContext(), "请输入反馈信息", 0).show();
				}else{
					finish();
					Toast.makeText(getApplicationContext(), "反馈信息已经提交", 0).show();
				}
			}
		});
	}
}

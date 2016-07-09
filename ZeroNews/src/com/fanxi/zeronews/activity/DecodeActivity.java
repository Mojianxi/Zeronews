package com.fanxi.zeronews.activity;

import java.io.BufferedReader;

import org.json.JSONException;
import org.json.JSONObject;

import com.fanxi.zeronews.R;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class DecodeActivity extends BaseActivity {
	private TextView et_only_name;
	private TextView et_only_company;
	private TextView et_only_position;
	private TextView et_only_phone;
	private TextView et_only_email;
	private TextView et_only_web1;
	private TextView et_only_add;
	private TextView et_only_note;

	@Override
	protected void onCreate(Bundle arg0){
		super.onCreate(arg0);
		setContentView(R.layout.activity_decode);
		TextView tv_titlebar=(TextView) findViewById(R.id.tv_titlebar);
		tv_titlebar.setText("本地二维码扫描");
		String decode = getIntent().getExtras().getString("decode");
		et_only_name = (TextView) findViewById(R.id.et_only_name); 
		et_only_company = (TextView) findViewById(R.id.et_only_company);
		et_only_position = (TextView) findViewById(R.id.et_only_position); 
		et_only_phone = (TextView) findViewById(R.id.et_only_phone);
		et_only_email = (TextView) findViewById(R.id.et_only_email);
		et_only_web1 = (TextView) findViewById(R.id.et_only_web1); 
		et_only_add = (TextView) findViewById(R.id.et_only_add);
		et_only_note = (TextView) findViewById(R.id.et_only_note);
		String[] split = decode.split("\n");
		for (String str:split) {
			if(!str.contains("BEGIN")&&!str.contains("VERSION")){
				if(str.contains("NAME")){
					str=str.split(":")[1];
					et_only_name.setText(str);
				}else if(str.contains("")){
					
				}
			}
		}
	}
}

package com.fanxi.zeronews.activity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fanxi.zeronews.R;
import com.fanxi.zeronews.bean.TalkBean;
import com.fanxi.zeronews.bean.VoiceBean;
import com.fanxi.zeronews.bean.VoiceBean.CwBean;
import com.fanxi.zeronews.bean.VoiceBean.WsBean;
import com.google.gson.Gson;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class ChatActivity extends BaseActivity {
	private ArrayList<TalkBean> mTalkList = new ArrayList<TalkBean>();// 保存会话数据
	private ListView lvList;
	String str="";
	String answer1="?";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		lvList = (ListView) findViewById(R.id.lv_chat);
		et_input = (EditText) findViewById(R.id.et_input);
//		Editable ea = et_input.getText();
		btn_send = (Button) findViewById(R.id.btn_send);
		et_input.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				str=s.toString();
			}
		});
		mAdapter = new TalkAdapter();
		lvList.setAdapter(mAdapter);
		// 将“12345678”替换成您申请的APPID，申请地址：http://open.voicecloud.cn 555be1f0
		SpeechUtility.createUtility(this, SpeechConstant.APPID + "=555be1f0");
		btn_send.setOnClickListener(new  OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println(str);
//				getForString(str, answer1);
				HttpUtils httpUtils=new HttpUtils();
				httpUtils.send(HttpMethod.GET, "http://www.tuling123.com/openapi/api?key=07435f0992da0c58f1097e3294237764&info="+str, new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo){
						String result=responseInfo.result;
						try {
							JSONObject jsonObject=new JSONObject(result);
							answer1=jsonObject.getString("text");
						} catch (JSONException e){
							e.printStackTrace();
						}
					}
					@Override
					public void onFailure(HttpException error, String msg) {
					}
				});
//				System.out.println("请求完的数据"+answer1);
				mTalkList.add(new TalkBean(str, true));
				mTalkList.add(new TalkBean(answer1, false));
				mAdapter.notifyDataSetChanged();
				lvList.setSelection(mAdapter.getCount());
			}
		});
	}
	private InitListener mInitListener = new InitListener() {
		@Override
		public void onInit(int arg0) {
		}
	};
	/**
	 * 开始语音识别
	 * 
	 * @param view
	 */
	public void startVoice(View view) {
		RecognizerDialog iatDialog = new RecognizerDialog(this, mInitListener);
		iatDialog.setParameter(SpeechConstant.DOMAIN, "iat");
		iatDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		iatDialog.setParameter(SpeechConstant.ACCENT, "mandarin ");
		iatDialog.setListener(recognizerDialogListener);
		iatDialog.show();
	}
	private StringBuffer mTextBuffer = new StringBuffer();
	private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {
		@Override
		public void onError(SpeechError arg0) {
		}
		String answer = "没听清";
		@Override
		public void onResult(RecognizerResult arg0, boolean isLast){
			// System.out.println(arg0.getResultString());
			String text = parseJson(arg0.getResultString());
			System.out.println("是否结束:" + isLast);
			mTextBuffer.append(text);
			if (isLast) {
				String ask = mTextBuffer.toString();
				mTextBuffer = new StringBuffer();// 清空StringBuffer的数据
				System.out.println("最终语句:" + ask);
				mTalkList.add(new TalkBean(ask, true));
//				int imageId = -1;
//				if (ask.contains("你好")) {
//					answer = "你好呀!!!";
//				} else if (ask.contains("你是谁")) {
//					answer = "我是你的小助手!";
//				} else if (ask.contains("美女")){
//					Random random = new Random();
//					int nextInt = random.nextInt(4);
//					answer = mnAnswerText[nextInt];
//					imageId = mnAnswerImage[nextInt];
//				} else if (ask.contains("天王盖地虎")) {
//					answer = "小鸡炖蘑菇";
//					imageId = R.drawable.m;
//				}
				HttpUtils httpUtils=new HttpUtils();
				httpUtils.send(HttpMethod.GET, "http://www.tuling123.com/openapi/api?key=07435f0992da0c58f1097e3294237764&info="+ask, new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String result=responseInfo.result;
						try {
							JSONObject jsonObject=new JSONObject(result);
							answer=jsonObject.getString("text");
							System.out.println("获取到的结果:"+answer);
						} catch (JSONException e){
							e.printStackTrace();
						}
					}
					@Override
					public void onFailure(HttpException error, String msg) {
					}
				});
//				getForString(ask, answer);
				mTalkList.add(new TalkBean(answer, false));
				mAdapter.notifyDataSetChanged();
				lvList.setSelection(mAdapter.getCount());
				speak(answer);
			}
		}
	};
//	http://www.tuling123.com/openapi/api?key=879a6cb3afb84dbf4fc84a1df2ab7319&info=打电话
	private void getForString(String send,String result){
		HttpUtils httpUtils=new HttpUtils();
		httpUtils.send(HttpMethod.GET, "http://www.tuling123.com/openapi/api?key=07435f0992da0c58f1097e3294237764&info="+send, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result=responseInfo.result;
				try {
					JSONObject jsonObject=new JSONObject(result);
					result=jsonObject.getString("text");
				} catch (JSONException e){
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(HttpException error, String msg) {
			}
		});
	}
	/**
	 * 机器人讲话
	 */
	private void speak(String answer) {
		// 1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
		SpeechSynthesizer mTts = SpeechSynthesizer
				.createSynthesizer(this, null);
		// 2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
		mTts.setParameter(SpeechConstant.VOICE_NAME, "vixying");// 设置发音人
		mTts.setParameter(SpeechConstant.SPEED, "50");// 设置语速
		mTts.setParameter(SpeechConstant.VOLUME, "80");// 设置音量，范围0~100
		// 设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
		// 保存在SD卡需要在AndroidManifest.xml添加写SD卡权限
		// 如果不需要保存合成音频，注释该行代码
//		mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
//		// 3.开始合成
		mTts.startSpeaking(answer, null);
	}

	private TalkAdapter mAdapter;

	private EditText et_input;

	private Button btn_send;

	/**
	 * 解析语音json数据
	 */
	private String parseJson(String json) {
		Gson gson = new Gson();
		VoiceBean bean = gson.fromJson(json, VoiceBean.class);
		ArrayList<WsBean> ws = bean.ws;
		StringBuffer sb = new StringBuffer();
		for (WsBean wsBean : ws){
			ArrayList<CwBean> cw = wsBean.cw;
			for (CwBean cwBean : cw) {
				String w = cwBean.w;
				sb.append(w);
			}
		}
		System.out.println("解析数据为:" + sb.toString());
		return sb.toString();
	}

	class TalkAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mTalkList.size();
		}

		@Override
		public TalkBean getItem(int position) {
			return mTalkList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(ChatActivity.this,
						R.layout.list_item, null);
				holder = new ViewHolder();
				holder.tvAsk = (TextView) convertView.findViewById(R.id.tv_ask);
				holder.tvAnswer = (TextView) convertView
						.findViewById(R.id.tv_answer);
				holder.llAnswer = (LinearLayout) convertView
						.findViewById(R.id.ll_answer);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			TalkBean item = getItem(position);

			if (item.isAsker) {// 是提问者
				holder.llAnswer.setVisibility(View.GONE);
				holder.tvAsk.setVisibility(View.VISIBLE);
				holder.tvAsk.setText(item.text);
			} else {// 是回答者
				holder.llAnswer.setVisibility(View.VISIBLE);
				holder.tvAsk.setVisibility(View.GONE);
				holder.tvAnswer.setText(item.text);
			}
			return convertView;
		}
	}

	public class ViewHolder {
		public TextView tvAsk;
		public TextView tvAnswer;
		public LinearLayout llAnswer;
	}
}

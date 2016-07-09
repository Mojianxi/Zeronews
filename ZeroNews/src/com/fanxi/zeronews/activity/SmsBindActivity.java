package com.fanxi.zeronews.activity;

import java.util.HashMap;
import java.util.Random;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.CommonDialog;
import cn.smssdk.gui.ContactsPage;
import cn.smssdk.gui.RegisterPage;

import com.fanxi.zeronews.R;
import com.fanxi.zeronews.bean.Constant;
import com.fanxi.zeronews.util.PrefUtils;

import de.greenrobot.event.EventBus;

public class SmsBindActivity extends BaseActivity implements Callback, OnClickListener {
	// 短信注册，随机产生头像
		private static final String[] AVATARS = {
			"http://tupian.qqjay.com/u/2011/0729/e755c434c91fed9f6f73152731788cb3.jpg",
			"http://99touxiang.com/public/upload/nvsheng/125/27-011820_433.jpg",
			"http://img1.touxiang.cn/uploads/allimg/111029/2330264224-36.png",
			"http://img1.2345.com/duoteimg/qqTxImg/2012/04/09/13339485237265.jpg",
			"http://diy.qqjay.com/u/files/2012/0523/f466c38e1c6c99ee2d6cd7746207a97a.jpg",
			"http://img1.touxiang.cn/uploads/20121224/24-054837_708.jpg",
			"http://img1.touxiang.cn/uploads/20121212/12-060125_658.jpg",
			"http://img1.touxiang.cn/uploads/20130608/08-054059_703.jpg",
			"http://diy.qqjay.com/u2/2013/0422/fadc08459b1ef5fc1ea6b5b8d22e44b4.jpg",
			"http://img1.2345.com/duoteimg/qqTxImg/2012/04/09/13339510584349.jpg",
			"http://img1.touxiang.cn/uploads/20130515/15-080722_514.jpg",
			"http://diy.qqjay.com/u2/2013/0401/4355c29b30d295b26da6f242a65bcaad.jpg"
		};
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_smsbind);
		Button btnRegist = (Button) findViewById(R.id.btn_bind_phone);
		View btnContact = findViewById(R.id.rl_contact);
		tvNum = (TextView) findViewById(R.id.tv_num);
		tvNum.setVisibility(View.GONE);
		btnRegist.setOnClickListener(this);
		btnContact.setOnClickListener(this);
		gettingFriends = false;
		initShareSdk();
	}
	/**
	 * 初始化share短信验证Sdk
	 */
	private boolean ready;
	private boolean gettingFriends;
	private Dialog pd;
	private TextView tvNum;

	private void initShareSdk() {
		SMSSDK.initSDK(this, Constant.SHARE_APPKEY, Constant.SHARE_APPSECRET,
				true);
		final Handler handler = new Handler(this);
		EventHandler eventHandler = new EventHandler() {
			@Override
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
		};
		// 注册回调接口监听
		SMSSDK.registerEventHandler(eventHandler);
		ready = true;
		// 获取新好友个数
		showDialog();
		SMSSDK.getNewFriendsCount();
		gettingFriends = true;
	}

	// shareSDK弹出加载通讯录的加载框
	private void showDialog() {
		if (pd != null && pd.isShowing()) {
			pd.dismiss();
		}
		pd = CommonDialog.ProgressDialog(this);
		pd.show();
	}
	// 提交用户信息
	private void registerUser(String country, String phone) {
		Random rnd = new Random();
		int id = Math.abs(rnd.nextInt());
		String uid = String.valueOf(id);
		String nickName = user_name1+ uid;
		String avatar = AVATARS[id % 12];
		SMSSDK.submitUserInfo(uid, nickName, avatar, country, phone);
	}
	@Override
	protected void onDestroy() {
		if(ready){
			SMSSDK.unregisterAllEventHandler();
		}
		super.onDestroy();
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(ready&&gettingFriends){
			//获取新好友个数
			showDialog();
			SMSSDK.getNewFriendsCount();
		}
	}
	@Override
	public boolean handleMessage(Message msg){
		if (pd != null && pd.isShowing()) {
			pd.dismiss();
		}
		int event = msg.arg1;
		int result = msg.arg2;
		Object data = msg.obj;
		if (event == SMSSDK.EVENT_SUBMIT_USER_INFO) {
			// 短信注册成功后，返回MainActivity,然后提示新好友
			if (result == SMSSDK.RESULT_COMPLETE) {
				Toast.makeText(this, R.string.smssdk_user_info_submited, Toast.LENGTH_SHORT).show();
				finish();
			} else {
				((Throwable) data).printStackTrace();
			}
		} else if (event == SMSSDK.EVENT_GET_NEW_FRIENDS_COUNT){
			if (result == SMSSDK.RESULT_COMPLETE) {
				refreshViewCount(data);
				gettingFriends = false;
			} else {
				((Throwable) data).printStackTrace();
			}
		}
		return false;
	}
	// 更新，新好友个数
		private void refreshViewCount(Object data){
			int newFriendsCount = 0;
			try {
				newFriendsCount = Integer.parseInt(String.valueOf(data));
			} catch (Throwable t) {
				newFriendsCount = 0;
			}
			if(newFriendsCount > 0){
				tvNum.setVisibility(View.VISIBLE);
				tvNum.setText(String.valueOf(newFriendsCount));
			}else{
				tvNum.setVisibility(View.GONE);
			}
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}
		}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_bind_phone:
			// 打开注册页面
						RegisterPage registerPage = new RegisterPage();
						registerPage.setRegisterCallback(new EventHandler() {
							public void afterEvent(int event, int result, Object data) {
								// 解析注册结果
								if (result == SMSSDK.RESULT_COMPLETE){
									@SuppressWarnings("unchecked")
									HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
									String country = (String) phoneMap.get("country");
									String phone = (String) phoneMap.get("phone");
									PrefUtils.setString(SmsBindActivity.this, "phone", phone);
									// 提交用户信息
									registerUser(country, phone);
								}
							}
						});
						registerPage.show(this);
			break;

		case R.id.rl_contact:
			tvNum.setVisibility(View.GONE);
			// 打开通信录好友列表页面
			ContactsPage contactsPage = new ContactsPage();
			contactsPage.show(this);
			break;
		}
	}
}

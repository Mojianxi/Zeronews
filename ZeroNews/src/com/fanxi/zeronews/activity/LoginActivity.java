package com.fanxi.zeronews.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.RemoteException;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import cn.jpush.a.a.ad;
import cn.jpush.android.api.JPushInterface;

import com.fanxi.zeronews.R;
import com.fanxi.zeronews.bean.FirstEvent;
import com.fanxi.zeronews.util.PrefUtils;
import com.fanxi.zeronews.util.ResUtil;
import com.fanxi.zeronews.util.UiUtils;
import com.fanxi.zeronews.util.Util;
import com.fanxi.zeronews.view.CustomDialog;
import com.fanxi.zeronews.view.MyListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.open.utils.HttpUtils.HttpStatusException;
import com.tencent.open.utils.HttpUtils.NetworkUnavailableException;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import de.greenrobot.event.EventBus;
public class LoginActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener, Callback {
	private static final String Scope = "all";
	private MyListView lv_user;
	private ImageView hduser;
	private ImageView btn_weixin;
	private ImageView btn_sina;
	private ImageView btn_qq;
	private ImageView btn_phone;
	private View userHedear;
	private Resources resources;
	private Tencent mTencent;
	private Message myMessage;
	private Bitmap user_bitmap;
	private String user_name;
	private PackageManager packageManager;
	/**
	 * 设置头像的变量
	 */
	/* 头像文件 */
	private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
	/* 请求识别码 */
	private static final int CODE_GALLERY_REQUEST = 0xa0;
	private static final int CODE_CAMERA_REQUEST = 0xa1;
	private static final int CODE_RESULT_REQUEST = 0xa2;
	// 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
	private static int output_X = UiUtils.dip2px(50);
	private static int output_Y = UiUtils.dip2px(50);
	private ImageView headImage = null;
	private static final int LOAD_MAIN = 2;
	private static final int LOAD_UPDATA = 3;
	private String downloadurl;
	PackageInfo packageInfo = null;
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				JSONObject response = (JSONObject) msg.obj;
				if (response.has("nickname")) {
					try {
						isLogin = true;
						user_name = response.getString("nickname");
						PrefUtils.setString(UiUtils.getContext(), "user_name",
								response.getString("nickname"));
						tv_username.setText(response.getString("nickname"));
						initLoginState();
					} catch (Exception e) {
					}
				}
			} else if (msg.what == 1) {
				Bitmap bitmap = (Bitmap) msg.obj;
				user_bitmap = bitmap;
				ResUtil.saveBitmap(LoginActivity.this, user_bitmap, user_name);
				imag_user.setImageBitmap(bitmap);
				imag_user.setVisibility(View.VISIBLE);
			}else if(msg.what==LOAD_MAIN){
				rl_loading.setVisibility(View.GONE);
			}else if(msg.what==LOAD_UPDATA){
				dialog();
				rl_loading.setVisibility(View.GONE);
			}else if(msg.what==4){
				adpter.notifyDataSetChanged();
			}
		};
	};
	// 弹窗
		private void dialog() {
			final CustomDialog dialog = new CustomDialog(LoginActivity.this);
			WindowManager windowManager = getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
			lp.width = (int)(display.getWidth()*0.6); //设置宽度
			dialog.getWindow().setAttributes(lp);
			dialog.setOnPositiveListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					download(downloadurl);
				}
			});
			dialog.setOnNegativeListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
//					Toast.makeText(getApplicationContext(), "取消", 0).show();
				}
			});
			dialog.show();
		}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		phone = PrefUtils.getString(LoginActivity.this, "phone", "");
		appState.addActivity(this);
		setContentView(R.layout.activity_login);
		rl_loading = (RelativeLayout) findViewById(R.id.rl_loading);
		setUser = false;
		mTencent = Tencent.createInstance("1105377287",
				this.getApplicationContext());
		resources = getResources();
		shake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);
		packageManager=getPackageManager();
		initView();
		initData();
		initPopuWindow();
	}

	private void initView() {
		lv_user = (MyListView) findViewById(R.id.lv_user);
		userHedear = View.inflate(this, R.layout.user_header, null);
		tv_username = (TextView) userHedear.findViewById(R.id.tv_username);
		hduser = (ImageView) userHedear.findViewById(R.id.herder_user);
		tv = (TextView) userHedear.findViewById(R.id.tv);
		imag_user = (ImageView) userHedear.findViewById(R.id.imag_user);
		btn_weixin = (ImageView) userHedear.findViewById(R.id.btn_weixin);
		btn_sina = (ImageView) userHedear.findViewById(R.id.btn_sina);
		btn_qq = (ImageView) userHedear.findViewById(R.id.btn_qq);
		btn_phone = (ImageView) userHedear.findViewById(R.id.btn_phone);
		if (isLogin && user_name1 != null) {
			initLoginState();
			imag_user.setImageBitmap(loginPic);
			tv_username.setText(user_name1);
		}
		// 设置字体大小的pipuView
		// inittextSizePopuWindow();
	}

	private void initData() {
		btn_weixin.setOnClickListener(this);
		btn_sina.setOnClickListener(this);
		btn_qq.setOnClickListener(this);
		btn_phone.setOnClickListener(this);
		imag_user.setOnClickListener(this);
		lv_user.addHeaderView(userHedear);
		userHedear.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						lv_user.setParallaxImage(hduser, null);
						userHedear.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});
		adpter = new MyUserAdpter();
		lv_user.setAdapter(adpter);
		lv_user.setOnItemClickListener(this);
		try {
			packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		view2 = View.inflate(LoginActivity.this,
				R.layout.item_setting, null);
		TextView tv_name1 = (TextView) view2
				.findViewById(R.id.tv_name);
		ImageView iv_arrow1 = (ImageView) view2
				.findViewById(R.id.iv_arrow);
		tv_desc1 = (TextView) view2
				.findViewById(R.id.tv_desc);
		tv_name1.setText("清除缓存");
		iv_arrow1.setVisibility(View.GONE);
		tv_desc1.setVisibility(View.VISIBLE);
		tv_desc1.setTextColor(Color.GRAY);
		tv_desc1.setTextSize(14);
		tv_desc1.setText("6.5M");
	}

	private void initLoginState() {
		tv_username.setVisibility(View.VISIBLE);
		imag_user.setVisibility(View.VISIBLE);
		btn_qq.setVisibility(View.GONE);
		btn_phone.setVisibility(View.GONE);
		btn_sina.setVisibility(View.GONE);
		btn_weixin.setVisibility(View.GONE);
		tv.setVisibility(View.GONE);
	}

	PopupWindow popupWindow;
	View view;

	private void initPopuWindow() {
		view = this.getLayoutInflater().inflate(R.layout.select_avatar_popu,
				null);
		TextView sele_photo = (TextView) view.findViewById(R.id.sele_photo);
		TextView camera = (TextView) view.findViewById(R.id.camera);
		TextView cancle = (TextView) view.findViewById(R.id.cancle);
		sele_photo.setOnClickListener(this);
		camera.setOnClickListener(this);
		cancle.setOnClickListener(this);
		popupWindow = new PopupWindow(view,
				getWindow().getWindowManager().getDefaultDisplay().getWidth()/2,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				popupWindow.dismiss();
			}
		});
	}

	public void initOpenidAndToken(JSONObject jsonObject) {
		try {
			String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
			String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
			String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
			if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
					&& !TextUtils.isEmpty(openId)) {
				mTencent.setAccessToken(token, expires);
				mTencent.setOpenId(openId);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void updateUserInfo() {
		if (mTencent != null && mTencent.isSessionValid()) {
			IUiListener listener = new IUiListener() {
				@Override
				public void onError(UiError arg0) {
					PrefUtils
							.setBoolean(UiUtils.getContext(), "isLogin", false);
				}

				@Override
				public void onComplete(final Object response) {
					Message msg = new Message();
					msg.obj = response;
					msg.what = 0;
					EventBus.getDefault().post(new FirstEvent(msg));
					mHandler.sendMessage(msg);
					new Thread() {
						public void run() {
							JSONObject json = (JSONObject) response;
							if (json.has("figureurl")) {
								Bitmap bitmap = null;
								try {
									bitmap = Util.getbitmap(json
											.getString("figureurl_qq_2"));
								} catch (Exception e) {
								}
								Message msg = new Message();
								msg.obj = bitmap;
								msg.what = 1;
								myMessage = msg;
								EventBus.getDefault().post(
										new FirstEvent(myMessage));
								mHandler.sendMessage(msg);
							}
						}
					}.start();
					PrefUtils.setBoolean(UiUtils.getContext(), "isLogin", true);
				}

				@Override
				public void onCancel() {
					PrefUtils
							.setBoolean(UiUtils.getContext(), "isLogin", false);
				}
			};
			mInfo = new UserInfo(this, mTencent.getQQToken());
			mInfo.getUserInfo(listener);
		} else {
			PrefUtils.setBoolean(UiUtils.getContext(), "isLogin", false);
			tv_username.setText("");
			tv_username.setVisibility(View.GONE);
			// iv_username.setImagVisible(false);
			imag_user.setVisibility(View.GONE);
			btn_phone.setVisibility(View.VISIBLE);
			btn_qq.setVisibility(View.VISIBLE);
			btn_sina.setVisibility(View.VISIBLE);
			btn_weixin.setVisibility(View.VISIBLE);
			tv.setVisibility(View.VISIBLE);
		}
	}
	private String getCacheSize(PackageInfo packageInfo) {
		try {
			Class<?> clazz = getClassLoader().loadClass("android.content.pm.PackageManager");
//			Class clazz=Class.forName("android.content.pm.PackageManager");
			//通过反射获取到当前的方法
			Method method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
			method.invoke(packageManager,
					packageInfo.applicationInfo.packageName,
					new MyIPackageStatsObserver());
			return formatcacheSize;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	private String formatcacheSize;
	private class MyIPackageStatsObserver extends IPackageStatsObserver.Stub {
		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			// 获取得到当前手机应用的缓存大小
			long cacheSize = pStats.cacheSize;
			formatcacheSize = Formatter.formatFileSize(getApplicationContext(), cacheSize);
		}
	}
	IUiListener loginListener = new BaseUiListener() {
		protected void doComplete(JSONObject values) {
			initOpenidAndToken(values);
			updateUserInfo();
		};
	};
	private UserInfo mInfo;
	private TextView tv;
	private ImageView imag_user;
	private TextView tv_username;
	private boolean networkAvailable;
	private Animation shake;
	private boolean setUser;
	private Bitmap bitmap12;
	private static Bitmap photo;
	private Intent intent;
	private String phone;
	private RelativeLayout rl_loading;

	private void onClickLogin() {
		if (!mTencent.isSessionValid()) {
			mTencent.login(this, "all", loginListener);
		}
	}
	public void getUserInfoInThread() {
		new Thread() {
			@Override
			public void run() {
				JSONObject json;
				try {
					json = mTencent.request(Constants.GRAPH_BASE, null,
							Constants.HTTP_GET);
					System.out.println(json);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NetworkUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpStatusException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	private TextView tv_desc;
	private MyUserAdpter adpter;
	private View view2;
	private TextView tv_desc1;
	class MyUserAdpter extends BaseAdapter implements OnCheckedChangeListener {
		private ToggleButton toggleButton;

		@Override
		public int getCount() {
			return 11;
		}
		@Override
		public Object getItem(int position) {
			return null;
		}
		@Override
		public long getItemId(int position) {
			return 0;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position == 0) {
				convertView = View.inflate(LoginActivity.this,
						R.layout.item_set_title, null);
				return convertView;
			} else if (position == 1) {
				View view = new View(LoginActivity.this);
				view.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, UiUtils.dip2px(10)));
				view.setBackgroundResource(R.color.paddongbg);
				convertView = view;
				return view;
			}
			convertView = View.inflate(LoginActivity.this,
					R.layout.item_setting, null);
			TextView tv_name = (TextView) convertView
					.findViewById(R.id.tv_name);
			ImageView iv_arrow = (ImageView) convertView
					.findViewById(R.id.iv_arrow);
			tv_desc = (TextView) convertView
					.findViewById(R.id.tv_desc);
			TextView tv_single = (TextView) convertView
					.findViewById(R.id.tv_single);
			toggleButton = (ToggleButton) convertView.findViewById(R.id.toggle);
			if (position == 2) {
				tv_single.setVisibility(View.VISIBLE);
				tv_name.setText("个性签名");
				iv_arrow.setVisibility(View.GONE);
				String signature = PrefUtils.getString(LoginActivity.this,
						"signature", "");
				if (signature != null && !"".equals(signature)) {
					tv_single.setText(signature);
				} else {
					tv_single.setText("未填写");
				}
			} else if (position == 3) {
				tv_name.setText("手机号");
				iv_arrow.setVisibility(View.GONE);
				tv_desc.setVisibility(View.VISIBLE);
				if (isLogin && phone != null && !"".equals(phone)) {
					tv_desc.setText(phone);
				}
			} else if (position == 4) {
				tv_name.setText("字体大小");
			} else if (position == 5) {
				tv_name.setText("允许消息推送");
				toggleButton.setVisibility(View.VISIBLE);
				toggleButton.setOnCheckedChangeListener(this);
				iv_arrow.setVisibility(View.GONE);
			} else if (position == 6) {
				return view2;
			} else if (position == 7) {
				tv_name.setText("软件推荐");
			} else if (position == 8) {
				tv_name.setText("版本更新");
			} else if (position == 9) {
				tv_name.setText("反馈");
			} else if (position == 10) {
				tv_name.setText("退出登录");
				iv_arrow.setVisibility(View.GONE);
				tv_desc.setVisibility(View.GONE);
				tv_name.setTextColor(Color.RED);
				tv_name.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				tv_name.setGravity(Gravity.CENTER);
				convertView = tv_name;
				if (isLogin) {
					convertView.setVisibility(View.VISIBLE);
				} else {
					convertView.setVisibility(View.GONE);
				}
			}
			return convertView;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if(isChecked){
				 JPushInterface.resumePush(getApplicationContext());//AA
			}else{
				JPushInterface.stopPush(getApplicationContext());
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long arg3) {
		switch (position) {
		case 11:// 退出登录
			PrefUtils.setBoolean(UiUtils.getContext(), "isLogin", false);
			if (mTencent != null) {
				if (networkAvailable) {
					mTencent.logout(this);
					updateUserInfo();
				}
			}
			appState.finishAll();
			break;
		case 3:// 个性签名
			intent = new Intent(this, SignatureActivity.class);
			startActivity(intent);
			break;
		case 4:// 手机号
			intent = new Intent(this, SmsBindActivity.class);
			startActivity(intent);
			break;
		case 5:// 字体大小
			inintDialog();
			break;
		case 7:
			AlertDialog.Builder builder = new AlertDialog.Builder(
					LoginActivity.this);
			builder.setTitle("提示");
			builder.setMessage("确定要清除所有缓存吗?离线内容及图片都会被清除");
			builder.setNegativeButton("取消", null);
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							UiUtils.cleanInternalCache(LoginActivity.this);
							tv_desc1.setText("0KB");
							mHandler.sendEmptyMessage(4);
						}
					});
			builder.show();
			break;
		case 8://软件推荐
			startActivity(new Intent(LoginActivity.this,AppnominateActivity.class));
			break;
		case 9://版本更新
			rl_loading.setVisibility(View.VISIBLE);
			checkVersion();
			break;
		case 10:
			startActivity(new Intent(this,FeedBackActivity.class));
			break;
		}
	}
	// 调用SDK已经封装好的接口时，例如：登录、快速支付登录、应用分享、应用邀请等接口，需传入该回调的实例。
	private class BaseUiListener implements IUiListener {
		@Override
		public void onCancel() {
		}

		protected void doComplete(JSONObject values) {
		}

		@Override
		public void onComplete(Object response) {
			doComplete((JSONObject) response);
		}

		@Override
		public void onError(UiError arg0) {
		}
	}

	/**
	 * 选择对话框 选择字体大小
	 */
	private void inintDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("字体大小");
		final String[] sex = { "小号", "中号", "大号", "特大号" };
		// 设置一个单项选择下拉框
		/**
		 * 第一个参数指定我们要显示的一组下拉单选框的数据集合 第二个参数代表索引，指定默认哪一个单选框被勾选上，1表示默认'中号' 会被勾选上
		 * 第三个参数给每一个单选项绑定一个监听器
		 */
		builder.setSingleChoiceItems(sex, -1,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						PrefUtils.setString(LoginActivity.this, "TextSize",
								sex[which]);
						// baseHandler.sendEmptyMessage(0);
						// tv_username.setTextSize(12);
						dialog.dismiss();
					}
				});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.show();
	}
//	class GameThread implements Runnable {
//		public void run() {
//			while (!Thread.currentThread().isInterrupted()) {
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					Thread.currentThread().interrupt();
//				}
//				// 使用postInvalidate可以直接在线程中更新界面
//				lv_user.postInvalidate();
//			}
//		}
//	}

	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	//
	// // mTencent.onActivityResult(requestCode, resultCode, data);
	// }
	// public void login(){
	// mTencent = Tencent.createInstance("1105370039",
	// this.getApplicationContext());
	// if (!mTencent.isSessionValid())
	// {
	// mTencent.login(this, Scope, listener);
	// }
	// }
	public int dip2px(int dp) {
		float density = resources.getDisplayMetrics().density;
		return (int) (dp * density + 0.5);
	}

	// 退出qq登录
	public void logout() {
		mTencent.logout(this);
	}

	@Override
	public void onClick(View v) {
		networkAvailable = UiUtils.isNetworkAvailable(this);
		switch (v.getId()) {
		case R.id.btn_weixin:
			break;
		case R.id.btn_sina:
			break;
		case R.id.btn_qq:
			if (networkAvailable) {
				onClickLogin();
			} else {
				v.startAnimation(shake);
				Toast.makeText(getApplicationContext(), "请联网后操作", 0).show();
			}
			break;
		case R.id.btn_phone:
			intent = new Intent(this, SmsBindActivity.class);
			startActivity(intent);
			break;
		case R.id.imag_user:
			if (!popupWindow.isShowing()) {
				popupWindow.showAtLocation(imag_user, Gravity.CENTER, 0, 0);
			}
			break;
		case R.id.sele_photo:
			choseHeadImageFromGallery();
			popupWindow.dismiss();
			// Toast.makeText(getApplicationContext(), "1", 0).show();
			break;
		case R.id.camera:
			choseHeadImageFromCameraCapture();
			popupWindow.dismiss();
			// Toast.makeText(getApplicationContext(), "2", 0).show();
			break;
		case R.id.cancle:
			popupWindow.dismiss();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		adpter.notifyDataSetChanged();
		super.onResume();
	}

	// 启动手机相机拍摄照片作为头像
	private void choseHeadImageFromCameraCapture() {
		Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 判断存储卡是否可用，存储照片文件
		if (UiUtils.isSdcardReady()) {
			intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
					.fromFile(new File(Environment
							.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
		}

		startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
	}

	// 从本地相册选取图片作为头像
	private void choseHeadImageFromGallery() {
		Intent intentFromGallery = new Intent();
		// 设置文件类型
		intentFromGallery.setType("image/*");
		intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
	}

	@Override
	public void onBackPressed() {
		// if(setUser){
		// Message message=new Message();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		// message.what=1;
		// Bitmap bitmap=photo;
		// message.obj=bitmap;
		// EventBus.getDefault().post(new FirstEvent(message));
		// }
		super.onBackPressed();
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == CODE_GALLERY_REQUEST
				|| requestCode == CODE_CAMERA_REQUEST
				|| requestCode == CODE_RESULT_REQUEST) {
			switch (requestCode) {
			case CODE_GALLERY_REQUEST:
				cropRawPhoto(intent.getData());
				break;

			case CODE_CAMERA_REQUEST:
				if (UiUtils.isSdcardReady()) {
					File tempFile = new File(
							Environment.getExternalStorageDirectory(),
							IMAGE_FILE_NAME);
					cropRawPhoto(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(getApplication(), "没有SDCard!",
							Toast.LENGTH_LONG).show();
				}

				break;
			case CODE_RESULT_REQUEST:
				if (intent != null) {
					initLoginState();
					setImageToHeadView(intent);
				}

				break;
			}
			return;
		} else {
			Tencent.onActivityResultData(requestCode, resultCode, intent,
					loginListener);
			// 用户没有进行有效的设置操作，返回
			if (resultCode == RESULT_CANCELED) {
				Toast.makeText(getApplication(), "取消", Toast.LENGTH_LONG)
						.show();
				return;
			}
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}

	/**
	 * 裁剪原始的图片
	 */
	public void cropRawPhoto(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");

		// 设置裁剪
		intent.putExtra("crop", "true");

		// aspectX , aspectY :宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX , outputY : 裁剪图片宽高
		intent.putExtra("outputX", output_X);
		intent.putExtra("outputY", output_Y);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, CODE_RESULT_REQUEST);
	}

	/**
	 * 提取保存裁剪之后的图片数据，并设置头像部分的View
	 */
	private void setImageToHeadView(Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			photo = extras.getParcelable("data");
			imag_user.setImageBitmap(photo);
			// setUser=true;
		}
	}
	private void checkVersion(){
	new Thread(){
		private String desc;
		public void run() {
			// 检查 代码执行的时间。如果时间少于2秒 补足2秒
			long startTime = System.currentTimeMillis();
			Message message=new Message();
			int currentVersion=getCurrentVersion();
			try {
				URL url=new URL("http://o8r8gy8c5.bkt.clouddn.com/update.json");
				HttpURLConnection conn=(HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(2000);
				int code=conn.getResponseCode();
				if(code==200){
					InputStream is=conn.getInputStream();
					String json = UiUtils.readStream(is);
					if(TextUtils.isEmpty(json)){
						Toast.makeText(getApplicationContext(), "服务器数据为空,请联系客服", 0).show();
//						System.out.println("服务器数据为空,请联系客服");
//						message.what=LOAD_MAIN;
					}else{
						JSONObject jsonObj = new JSONObject(json);
						downloadurl = jsonObj.getString("downloadUrl");
						int serverVersionCode = jsonObj.getInt("versionCode");
						desc = jsonObj.getString("description");						
						if(currentVersion==serverVersionCode){
							Toast.makeText(getApplicationContext(), "已经是最新版本", 0).show();
//							System.out.println("已经是最新版本进入页面");
//							message.what=LOAD_MAIN;
						}else{
//							Toast.makeText(getApplicationContext(), "有新版本弹出更新框", 0).show();
							System.out.println("有新版本弹出更新框");
							message.what=LOAD_UPDATA;
						}
					}
				}else{
//					Toast.makeText(getApplicationContext(), "服务器状态码错误,请联系客服", 0).show();
					System.out.println("服务器状态码错误,请联系客服");
//					message.what=LOAD_MAIN;
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
//				System.out.println("服务器地址找不到,请联系客服");
				Toast.makeText(getApplicationContext(), "服务器地址找不到,请联系客服", 0).show();
//				message.what=LOAD_MAIN;
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "网络错误", 0).show();
//				System.out.println("网络错误");
//				message.what=LOAD_MAIN;
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "数据解析错误", 0).show();
//				System.out.println("数据解析错误");
//				message.what=LOAD_MAIN;
			}finally{
				long endtime = System.currentTimeMillis();
				long dtime = endtime - startTime;
				if (dtime > 2000) {
					mHandler.sendMessage(message);
				} else {
					try {
						Thread.sleep(2000 - dtime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mHandler.sendMessage(message);
				}
			}
		}
	}.start();}

	/**
	 * 多线程下载新版本
	 * 
	 * @param downloadurl 
	 */
	private void download(String downloadurl) {
		// 多线程断点下载。
		HttpUtils http = new HttpUtils();
		http.download(downloadurl, "/mnt/sdcard/temp.apk",
				new RequestCallBack<File>() {
					@Override
					public void onSuccess(ResponseInfo<File> arg0) {
						System.out.println("安装 /mnt/sdcard/temp.apk");
						// <action android:name="android.intent.action.VIEW" />
						// <category
						// android:name="android.intent.category.DEFAULT" />
						// <data android:scheme="content" />
						// <data android:scheme="file" />
						// <data
						// android:mimeType="application/vnd.android.package-archive"
						// />
						Intent intent = new Intent();
						intent.setAction("android.intent.action.VIEW");
						intent.addCategory("android.intent.category.DEFAULT");

						// intent.setType("application/vnd.android.package-archive");
						// intent.setData(Uri.fromFile(new
						// File(Environment.getExternalStorageDirectory(),"temp.apk")));
						intent.setDataAndType(Uri.fromFile(new File(Environment
								.getExternalStorageDirectory(), "temp.apk")),
								"application/vnd.android.package-archive");
						startActivityForResult(intent, 0);                                                                                                                                                                                                          
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(LoginActivity.this, "下载失败", 0).show();
						System.out.println(arg1);
						arg0.printStackTrace();
//						loadMainUI();
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						super.onLoading(total, current, isUploading);
					}
				});
	}
	private int getCurrentVersion(){
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			int CurrentVersionCode = packInfo.versionCode;
			return CurrentVersionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
	// handler接口回调,sharesdk短信验证用得到
	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}

}
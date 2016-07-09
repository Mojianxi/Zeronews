package com.fanxi.zeronews.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.fanxi.zeronews.R;
import com.fanxi.zeronews.bean.Comment;
import com.fanxi.zeronews.util.AnimUtil;
import com.fanxi.zeronews.util.JsonParser;
import com.fanxi.zeronews.util.PrefUtils;
import com.fanxi.zeronews.util.ResUtil;
import com.fanxi.zeronews.util.UiUtils;
import com.fanxi.zeronews.view.MyListView;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.sunflower.FlowerCollector;
import com.tencent.open.yyb.TitleBar;
public class NewsDetailActivity extends BaseActivity implements
		OnClickListener, View.OnFocusChangeListener {
	private MyListView lv_detail;
	private ImageView mImage;
	private View headerView;
	WebView webView1 = null;
	private ImageView iv_hd;
	private int mIheight = 0;
	private Resources resources;
	ArrayList<Comment> commentList = new ArrayList<Comment>();
	private ImageView arrow_back;
	private ImageView arrow_top;
	MyWebViewClient viewClient = new MyWebViewClient();
	private View webItem;
	private WebView webView;
	private String url;
	private ProgressBar pb_loading;
	private View main;
	private String user_name;
	private EditText et_comment;
	private InputMethodManager imm;
	private LinearLayout ll_bottom;
	private RelativeLayout rl_submit;
	private boolean isSubmit = false;
	private ImageView ib_comment;
	private ImageView ib_collect;
	private ImageView ib_share;
	private ImageView ib_submit;
	private String content;
	private long currentTime;
	private String comment;
	private String data = "";
	static int webTop = 0;
	boolean scrollT = true;
	// 用HashMap存储听写结果
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	// 语音听写对象
	private SpeechRecognizer mIat;
	// 语音识别弹窗
	private RecognizerDialog mIatDialog;
	private Button btn;
	private SharedPreferences mSharedPreferences;
	// 语音合成(阅读)对象
	private SpeechSynthesizer mTts;
	// 默认发音人
	private String voicer = "xiaoyan";
	private boolean scrollFlag = false;// 标记是否滑动
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				break;
			default:
				break;
			}
		};
	};
	private MyAdpter adpter;
	private Bitmap bitmap_user;
	private ImageButton head_img;
	private RelativeLayout rl_lv;
	private TextView head_name;
	private boolean networkAvailable;
	private String title;
	private String source;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appState.addActivity(this);
		setContentView(R.layout.activity_detailnews);
		networkAvailable = UiUtils.isNetworkAvailable(this);
		url = getIntent().getStringExtra("url");
		System.out.println("----" + url);
		title = getIntent().getStringExtra("title");
		source = getIntent().getStringExtra("source");
		initView();
		//语音播报部分
		SpeechUtility.createUtility(this, SpeechConstant.APPID + "=555be1f0");
		mIat=SpeechRecognizer .createRecognizer(this, mInitListener);
		//语音合成
		mIatDialog=new RecognizerDialog(this, mInitListener);
		//初始化语音合成对象
		mTts=SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
		
		initData();
		initListerner();
		if (isLogin && user_name1 != null) {
			head_img.setImageBitmap(loginPic);
			head_name.setText(user_name1);
		} else {
			head_img.setImageResource(R.drawable.user);
			head_name.setText("未登录");
		}
		
	}

	/**
	 * 初始化布局
	 */
	private void initView() {
		headerView = View.inflate(this, R.layout.view_header, null);
		lv_detail = (MyListView) findViewById(R.id.lv_detail);
		rl_lv = (RelativeLayout) findViewById(R.id.rl_lv);
		head_img = (ImageButton) headerView.findViewById(R.id.head_img);
		head_name = (TextView) headerView.findViewById(R.id.user_name);
		arrow_back = (ImageView) findViewById(R.id.arrow_back);
		arrow_top = (ImageView) findViewById(R.id.arrow_top);
		et_comment = (EditText) findViewById(R.id.et_comment);
		ib_comment = (ImageView) findViewById(R.id.ib_comment);
		ib_collect = (ImageView) findViewById(R.id.ib_collect);
		ib_share = (ImageView) findViewById(R.id.ib_share);
		ib_submit = (ImageView) findViewById(R.id.ib_submit);
		sound_edit = (ImageView) findViewById(R.id.sound_edit);
		level1 = (RelativeLayout) findViewById(R.id.level1);
		level1.setOnClickListener(this);
		ib_comment.setOnClickListener(this);
		ib_collect.setOnClickListener(this);
		ib_share.setOnClickListener(this);
		// 让EditText组件自动弹出和键盘的代码
		// ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
		// .hideSoftInputFromWindow(et_comment.getWindowToken(),
		// InputMethodManager.RESULT_SHOWN);
		// et_comment.setInputType(InputType.TYPE_NULL); //始终不弹出软键盘
		lv_detail.addHeaderView(headerView);
		lv_detail.setOverScrollMode(View.OVER_SCROLL_NEVER);
		mImage = (ImageView) headerView.findViewById(R.id.iv);
		webItem = View.inflate(NewsDetailActivity.this, R.layout.item_webview,
				null);
		pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
		main = findViewById(R.id.main);
		// main.setVisibility(View.VISIBLE);
		// pb_loading.setVisibility(View.VISIBLE);
		webView = (WebView) webItem.findViewById(R.id.web_detail);
		WebSettings settings = webView.getSettings();
//		settings.setJavaScriptEnabled(true);
//		 webView.addJavascriptInterface(new InJavaScriptLocalObj(),"local_obj");
		settings.setLoadWithOverviewMode(true);
		settings.setUseWideViewPort(true);
		// settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		settings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		// settings.setUseWideViewPort(true);
		// settings.setPluginsEnabled(true);
		// webView.getSettings().setSupportZoom(true);
		// webView.getSettings().setJavaScriptEnabled(true);
		// webView.addJavascriptInterface(new Handler(), "handler");
		if ("大号".equals(textSize)) {
			settings.setTextSize(TextSize.LARGER);
		} else if ("特大号".equals(textSize)) {
			settings.setTextSize(TextSize.LARGEST);
		} else if ("小号".equals(textSize)) {
			settings.setTextSize(TextSize.SMALLER);
		} else {
			settings.setTextSize(TextSize.NORMAL);
		}
		if ("环球网".equals(title)) {
			settings.setTextSize(TextSize.LARGEST);
		}
		// webView.setBackgroundResource(Color.TRANSPARENT);
		webView.setWebChromeClient(new MyWebChromeClient());
		webView.setWebViewClient(new MyWebViewClient());
		// webView.loadUrl("javascript:window.handler.show(document.body.innerHTML);");
		// new MyAsnycTask().equals(url);
		// webView.loadUrl(url);
		if ("0".equals(title)) {
			webView.requestFocus();
			settings.setSupportZoom(true);
			settings.setDomStorageEnabled(true);
			webView.getSettings().setBuiltInZoomControls(true);
			settings.setDefaultZoom(ZoomDensity.CLOSE);
			settings.setTextSize(TextSize.LARGEST);
			String baseurl = "file:///android_asset/";
			html = UiUtils.readAssest(this, "index.html");
			webView.loadDataWithBaseURL(baseurl, html, "text/html", "UTF-8",null);
			html2Text = UiUtils.html2Text(html);
//			System.out.println("html数据==" + html2Text);
		} else {
//			webView.loadDataWithBaseURL(url, html, "text/html", "UTF-8",
//					null);
			webView.loadUrl(url);
			new Thread(){
				public void run() {
					html2Text = UiUtils.html2Text(NewsDetailsService.getNewsDetails(url));;
					System.out.println("网页内容"+html2Text);
				};
			}.start();
		}
		ll_bottom = (LinearLayout) findViewById(R.id.ll);
	}
	/**
	 * 初始化数据
	 */
	private void initData() {
		user_name = PrefUtils.getString(UiUtils.getContext(), "user_name", "");// 获取到用户名
		bitmap_user = ResUtil.getBitmap(UiUtils.getContext(), user_name);
		Comment comment1 = new Comment("清风微微", "赞一下", "5-31 18:06");
		Comment comment2 = new Comment("清风微微", "路过", "5-23 09:11");
		Comment comment3 = new Comment("清风微微", "顶一下", "3-21 17:16");
		Comment comment4 = new Comment("清风微微", "聚焦焦点", "01-11 11:06");
		Comment comment5 = new Comment("清风微微", "这是一条内部测试评论，不是每条评论都这么牛",
				"6-05 19:11");
		commentList.add(comment5);
		commentList.add(comment4);
		commentList.add(comment3);
		commentList.add(comment2);
		commentList.add(comment1);
		adpter = new MyAdpter();
		lv_detail.setAdapter(adpter);
		lv_detail.setWebView(webView, url);
		resources = this.getResources();
		// if(source!=null){
		// if(source.contains("新浪")||source.contains("网易")||source.contains("搜狐")||source.contains("腾讯")){
		// mImage.setBackgroundResource(R.drawable.news_bg5);
		// }else if(source!=null&&source.contains("中国")||source.contains("人民")){
		// mImage.setBackgroundResource(R.drawable.news_bg4);
		// }else
		// if(source!=null&&source.contains("techWeb")||source.contains("虎嗅网")||source.contains("天极网")||source.contains("多玩游戏")||source.contains("雷锋网")){
		// mImage.setBackgroundResource(R.drawable.news_bg3);
		// }else
		// if(source!=null&&source.contains("光明网")||source.contains("环球")||source.contains("新华")||source.contains("扬子晚报")){
		// mImage.setBackgroundResource(R.drawable.news_bg2);
		// }else{
		// mImage.setBackgroundResource(R.drawable.news_bg1);
		// }
		// }
		if (bitmap_user != null && user_name != null) {
			head_img.setImageBitmap(bitmap_user);
			head_name.setText(user_name);
		}
		// System.out.println("  url:"+url+"=="+source);
	}

	/**
	 * 初始化监听器
	 */
	private void initListerner() {
		et_comment.setOnFocusChangeListener(this);
		arrow_back.setOnClickListener(this);
		arrow_top.setOnClickListener(this);
		et_comment.setOnClickListener(this);
		ib_collect.setOnClickListener(this);
		head_img.setOnClickListener(this);
		et_comment.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				content = s.toString();
			}
		});
		lv_detail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				hiddenSubmit(v);
			}
		});
		headerView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						// 当布局填充结束之后, 此方法会被调用
						lv_detail.setParallaxImage(mImage, head_img);
						int[] leftTop = { 0, 0 };
						// 获取输入框当前的location位置
						head_img.getLocationOnScreen(leftTop);
						int left = leftTop[0];
						int top = leftTop[1];
						int bottom = top + head_img.getHeight();
						int right = left + head_img.getWidth();
						lv_detail.setLocation(left, top, right, bottom);
						headerView.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});
		 lv_detail.setOnScrollListener(new AbsListView.OnScrollListener() {
		 @Override
		 public void onScrollStateChanged(AbsListView absListView, int
		 scrollState) {
		 if(scrollState==
		 AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
		 scrollFlag=true;
		 }else{
		 scrollFlag=false;
		 }
		 }
		 @Override
		 public void onScroll(AbsListView absListView, int firstVisibleItem,
		 int i1, int i2) {
		 if (scrollFlag && lv_detail.getChildAt(0).getTop() >= 0) {
		 lv_detail.setLoadingListerner(new MyListView.LoadingListerner() {
		 @Override
		 public void onLoading(boolean s) {
		  webView.loadUrl(url);
		 }
		 });
		 } else {
		 if (lv_detail != null && lv_detail.getChildAt(0) != null && webTop <
		 lv_detail.getChildAt(0).getTop()) {
		 scrollT = true;
		 } else if (lv_detail != null && lv_detail.getChildAt(0) != null &&
		 webTop > lv_detail.getChildAt(0).getTop()) {
		 scrollT = false;
		 } else if (lv_detail != null && lv_detail.getChildAt(0) != null &&
		 webTop == lv_detail.getChildAt(0).getTop()) {
		 if (!scrollT) {
		 arrow_back.setVisibility(View.GONE);
		 } else {
		 arrow_back.setVisibility(View.VISIBLE);
		 }
		 return;
		 }
		 if (lv_detail != null && lv_detail.getChildAt(0) != null) {
		 webTop = lv_detail.getChildAt(0).getTop();
		 }
		 }
		 }});
	}

	int firstClick = 0;
	private String html2Text;
	private String html;
	private RelativeLayout level1;
	@Override
	public void onBackPressed(){
		if (firstClick == 0) {
			hiddenSubmit(et_comment);
		}
		super.onBackPressed();
	}

	/**
	 * 隐藏发布信息框
	 * 
	 * @param v
	 */
	public void hiddenSubmit(View v) {
		ib_submit.setVisibility(View.GONE);
		sound_edit.setVisibility(View.GONE);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
		ib_comment.setVisibility(View.VISIBLE);
		ib_collect.setVisibility(View.VISIBLE);
		ib_share.setVisibility(View.VISIBLE);
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, UiUtils.dip2px(30), 5);
		et_comment.setLayoutParams(params2);
		et_comment.setGravity(Gravity.CENTER_VERTICAL);
		et_comment.setPadding(UiUtils.dip2px(10), 0, 0, 0);
		et_comment.setSingleLine(true);
		et_comment.setLines(1);
		et_comment.setBackgroundResource(R.drawable.et_bg1);
		params.gravity = Gravity.CENTER;
		ll_bottom.setLayoutParams(params);
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
	/**
	 * 弹出发布信息框
	 * 
	 * @param v
	 */
	public void popSubmit(View v) {
		lv_detail.setSelection(commentList.size()-1);
		ib_comment.setVisibility(View.GONE);
		ib_collect.setVisibility(View.GONE);
		ib_share.setVisibility(View.GONE);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 0, 3);
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(0,
				LinearLayout.LayoutParams.MATCH_PARENT, 3);
		LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(0,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1);
		params2.setMargins(UiUtils.dip2px(10), UiUtils.dip2px(10),
				UiUtils.dip2px(10), UiUtils.dip2px(10));
		et_comment.setLayoutParams(params2);
		et_comment.setGravity(Gravity.TOP);
		et_comment.setPadding(UiUtils.dip2px(10), UiUtils.dip2px(10), 0, 0);
		et_comment.setSingleLine(false);
		et_comment.setLines(4);
		et_comment.setBackgroundResource(R.drawable.et_bg1);
		ib_submit.setVisibility(View.VISIBLE);
		sound_edit.setVisibility(View.VISIBLE);
		ib_submit.setLayoutParams(params3);
		sound_edit.setLayoutParams(params3);
		ll_bottom.setLayoutParams(params);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(v, InputMethodManager.RESULT_SHOWN);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
				InputMethodManager.HIDE_IMPLICIT_ONLY);
		ib_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v){
				if ("".equals(content) || content == null) {
					Toast.makeText(getApplicationContext(), "请输入内容",
							Toast.LENGTH_SHORT).show();
				} else {
					Comment comment1 = new Comment(user_name, content, ""
							+ System.currentTimeMillis());
					commentList.add(comment1);
					adpter.notifyDataSetChanged();
					et_comment.setText("");
					hiddenSubmit(v);
				}
			}
		});
		sound_edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FlowerCollector.onEvent(NewsDetailActivity.this, "iat_recognize");
				et_comment.setText(null);// 清空显示内容
				mIatResults.clear();
				//设置参数
				setParam();
				mIatDialog.setListener(mRecognizerDialogListener);
				mIatDialog.show();
			}
		});
	}
	/**
	 * 异步加载webview
	 */
	public class MyAsnycTask extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... urls) {
			data = NewsDetailsService.getNewsDetails(url);
			return data;
		}
		@Override
		protected void onPostExecute(String data) {
//			webView.loadDataWithBaseURL(url, data, "text/html", "utf-8", null);
		}
	}

	final class InJavaScriptLocalObj {
		public void showSource(String html) {
			 System.out.println("开始执行");
			 System.out.println("====>html="+html);
			 System.out.println("执行完了吗");
		}
	}

	class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url){
			view.loadUrl(url);
			return true;
		}
		@Override
		public void onPageFinished(WebView view, String url){
			pb_loading.setVisibility(View.GONE);
			main.setVisibility(View.GONE);
			webView.setVisibility(View.VISIBLE);
			view.loadUrl("javascript:window.local_obj.showSource('<head>'+"
					+ "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
			super.onPageFinished(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			pb_loading.setVisibility(View.GONE);
			view.setVisibility(View.GONE);
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
	}

	class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress != 100) {
				pb_loading.setProgress(newProgress);
			}
			super.onProgressChanged(view, newProgress);
		}
	}

	class MyAdpter extends BaseAdapter {
		@Override
		public int getCount() {
			return commentList.size() + 2;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			int type = getItemViewType(position);
			ViewHolder1 holder1 = null;
			ViewHolder2 holder2 = null;
			ViewHolder3 holder3 = null;
			// case 0:
			// convertView = webItem;
			// holder1 = new ViewHolder1();
			// holder1.webView = webView;
			// lv_detail.setWebView(holder1.webView, url);
			// convertView.setTag(holder1);
			// lv_detail.setWebView(webView, url);
			if (position == 0) {
				return webItem;
			}
			if (convertView == null) {
				switch (type) {
				// break;
				case 1:
					convertView = View.inflate(NewsDetailActivity.this,
							R.layout.comment_title, null);
					holder2 = new ViewHolder2();
					holder2.tv_title = (TextView) convertView
							.findViewById(R.id.tv_title);
					convertView.setTag(holder2);
					break;
				case 2:
					convertView = View.inflate(NewsDetailActivity.this,
							R.layout.item_comment, null);
					holder3 = new ViewHolder3();
					holder3.tv_user = (TextView) convertView
							.findViewById(R.id.tv_user);
					holder3.time = (TextView) convertView
							.findViewById(R.id.time);
					holder3.content = (TextView) convertView
							.findViewById(R.id.content);
					convertView.setTag(holder3);
					holder3.tv_user.setText(commentList.get(position - 2).user);
					holder3.content
							.setText(commentList.get(position - 2).comment);
					holder3.time.setText(commentList.get(position - 2).time);
					if (position >= 7 && bitmap_user != null) {
						Drawable left = new BitmapDrawable(bitmap_user);
						left.setBounds(0, 0, left.getMinimumWidth(),
								left.getMinimumHeight());
						holder3.tv_user.setCompoundDrawables(left, null, null,
								null);
					}
					break;
				}
			} else {
				switch (type) {
				// case 0:
				// // holder1 = (ViewHolder1) convertView.getTag();
				// // lv_detail.setWebView(holder1.webView, url);
				// return webItem;
				// break;
				case 1:
					holder2 = (ViewHolder2) convertView.getTag();
					break;
				case 2:
					holder3 = (ViewHolder3) convertView.getTag();
					holder3.tv_user.setText(commentList.get(position - 2).user);
					holder3.content
							.setText(commentList.get(position - 2).comment);
					holder3.time.setText(commentList.get(position - 2).time);
					// if(position>2&&position<7){
					// holder3.iv_user.setImageResource(R.drawable.as);
					// }
					if (position >= 7 && bitmap_user != null) {
						Drawable left = new BitmapDrawable(bitmap_user);
						left.setBounds(0, 0, left.getMinimumWidth(),
								left.getMinimumHeight());
						holder3.tv_user.setCompoundDrawables(left, null, null,
								null);
					}
					break;
				}
			}
			return convertView;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0) {
				return 0;
			} else if (position == 1) {
				return 1;
			} else {
				return 2;
			}
		}

		@Override
		public int getViewTypeCount() {
			return 3;
		}
	}

	class ViewHolder1 {
		WebView webView;
	}

	class ViewHolder2 {
		TextView tv_title;
	}

	class ViewHolder3 {
		TextView tv_user;
		TextView time;
		TextView content;
		ImageView iv_user;
	}

	class ViewHolder4 {
		ImageView webView;
	}
	private boolean startSpeak=false;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.arrow_back:
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
			finish();
			overridePendingTransition(R.anim.hyperspace_in,
					R.anim.hyperspace_out);// 压缩变小淡出
			
			// overridePendingTransition(R.anim.fade, R.anim.hold); //淡入淡出
			// overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);//放大淡出
			// overridePendingTransition(R.anim.scale_rotate,R.anim.my_alpha_action);//转动淡出
			// overridePendingTransition(R.anim.scale_translate_rotate,R.anim.my_alpha_action);//转动淡出
			break;
		case R.id.arrow_top:
			lv_detail.setSelection(0);
			break;
		case R.id.ll:
			break;
		case R.id.lv_detail:
			break;
		case R.id.et_comment:
			popSubmit(v);
			break;
		case R.id.rl_lv:
			// et_comment.setOnFocusChangeListener(this);
			break;
		case R.id.head_img:
			startActivity(new Intent(this, LoginActivity.class));
			finish();
			break;
		case R.id.ib_comment:
			lv_detail.setSelection(2);
			break;
		case R.id.ib_collect:
			ib_collect.setImageResource(R.drawable.collect_press);
			break;
		case R.id.ib_share:
			showShare();
			// 分享
			break;
		case R.id.level1:
			Toast.makeText(getApplicationContext(), "语音播报", 0).show();
//			String text=.getText().toString();
			//设置参数
			if(!startSpeak){
				setParam1();
				int code=mTts.startSpeaking(html2Text, mTtsListener);
				System.out.println(code);
				startSpeak=true;
			}else{
				mTts.pauseSpeaking();
				startSpeak=false;
			}
			break;
//		case 语音输入:
//			FlowerCollector.onEvent(NewsDetailActivity.this, "iat_recognize");
//			et_comment.setText(null);// 清空显示内容
//			mIatResults.clear();
//			//设置参数
//			setParam();
//			mIatDialog.setListener(mRecognizerDialogListener);
//			mIatDialog.show();
//			break
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			popSubmit(v);
		} else {
			hiddenSubmit(v);
		}
	}

	/**
	 * sharesdk凡曦功能实现
	 */
	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// oks.setTheme(OnekeyShareTheme.CLASSIC);//设置天蓝色的主题 //关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getString(R.string.app_name));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://smilewike.tuweia.cn/");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("Android版零点新闻最新版上线了快来体验吧");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://smilewike.tuweia.cn/");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("这篇文章不错挺一下");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://smilewike.tuweia.cn/");
		// 启动分享GUI
		oks.show(this);
	}

	private boolean isShowMenu = false;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			int startOffset = 0;
			if (!isShowMenu) {
				level1.setVisibility(View.VISIBLE);
				AnimUtil.showMenu(level1, 200);
				isShowMenu = true;
			} else {
				AnimUtil.closeMenu(level1, startOffset);
				level1.setVisibility(View.GONE);
				isShowMenu = false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	/**
	 * 初始化语音输入监听器
	 */
	private InitListener mInitListener=new InitListener() {
		@Override
		public void onInit(int code) {
			if(code!=ErrorCode.SUCCESS){
				Toast.makeText(getApplicationContext(), "初始化失败", 0).show();
			}
		}
	};
	/**
	 * 语音输入参数设置
	 */
	public void setParam(){
		mIat.setParameter(SpeechConstant.PARAMS, null);
		//设置听写引擎
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		//设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
		mIat.setParameter(SpeechConstant.LANGUAGE,"zh_cn");
		mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
		//设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		mIat.setParameter(SpeechConstant.VAD_EOS, "700");
		//设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
		mIat.setParameter(SpeechConstant.ASR_PTT, "1");
		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//		mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
//		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
	}
	/**
	 * 语音输入UI监听器
	 */
	private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
		public void onResult(RecognizerResult results, boolean isLast) {
			printResult(results);
		}

		/**
		 * 识别回调错误.
		 */
		public void onError(SpeechError error) {
//			showTip(error.getPlainDescription(true));
			Toast.makeText(getApplicationContext(), error.getPlainDescription(true), 0).show();
		}

	};
	private void printResult(RecognizerResult results) {
		String text = JsonParser.parseIatResult(results.getResultString());

		String sn = null;
		// 读取json结果中的sn字段
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mIatResults.put(sn, text);

		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mIatResults.keySet()) {
			resultBuffer.append(mIatResults.get(key));
		}

		et_comment.setText(resultBuffer.toString());
		et_comment.setSelection(et_comment.length());
	}
	/**
	 * 初始化语音合成监听。
	 */
	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS) {
        		Toast.makeText(getApplicationContext(),"初始化失败 错误码"+code, 0).show();
        	} else {
				// 初始化成功，之后可以调用startSpeaking方法
        		// 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
        		// 正确的做法是将onCreate中的startSpeaking调用移至这里
			}		
		}
	};
	/**
	 * 设置语音阅读参数
	 */
	private void setParam1(){
		mTts.setParameter(SpeechConstant.PARAMS, null);//清空参数
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
		mTts.setParameter(SpeechConstant.SPEED, "50");//设置合成语速
		mTts.setParameter(SpeechConstant.PITCH, "50");//设置合成音调
		mTts.setParameter(SpeechConstant.VOLUME, "50");//设置合成音量
		//设置音频流类型
		mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
		//设置播放合成音频打断音乐播放,默认为true
		mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//		mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//		mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.wav");
	}
	/**
	 * 合成回调监听。
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener() {
		
		@Override
		public void onSpeakBegin() {
			Toast.makeText(getApplicationContext(), "开始阅读", 0).show();
		}

		@Override
		public void onSpeakPaused() {
			Toast.makeText(getApplicationContext(), "暂停阅读", 0).show();
		}

		@Override
		public void onSpeakResumed() {
			Toast.makeText(getApplicationContext(), "停止阅读", 0).show();
		}

		@Override
		public void onBufferProgress(int percent, int beginPos, int endPos,
				String info) {
			// 合成进度
//			mPercentForBuffering = percent;
//			showTip(String.format(getString(R.string.tts_toast_format),
//					mPercentForBuffering, mPercentForPlaying));
		}

		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
//			// 播放进度
//			mPercentForPlaying = percent;
//			showTip(String.format(getString(R.string.tts_toast_format),
//					mPercentForBuffering, mPercentForPlaying));
		}

		@Override
		public void onCompleted(SpeechError error) {
			if (error == null) {
				Toast.makeText(getApplicationContext(), "阅读完毕", 0).show();
			} else if (error != null) {
				Toast.makeText(getApplicationContext(), error.getPlainDescription(true), 0).show();
			}
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			// 若使用本地能力，会话id为null
			//	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			//		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			//		Log.d(TAG, "session id =" + sid);
			//	}
		}
	};
	private ImageView sound_edit;
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 退出时释放连接
		mIat.cancel();
		mIat.destroy();
	}
}

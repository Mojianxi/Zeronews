package com.fanxi.zeronews.activity;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.fanxi.zeronews.R;
import com.fanxi.zeronews.bean.EmsResults;
import com.fanxi.zeronews.bean.EmsResults.Ems;
import com.fanxi.zeronews.bean.EmsState;
import com.fanxi.zeronews.bean.EmsState.State;
import com.fanxi.zeronews.bean.PinyinUtils;
import com.fanxi.zeronews.view.DropMenu;
import com.fanxi.zeronews.view.QuickIndexBar;
import com.fanxi.zeronews.view.QuickIndexBar.onLetterChangeListener;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class EmsQueryActivity extends BaseActivity implements OnClickListener {
	String EmsResultsListUrl = "http://api.jisuapi.com/express/type?appkey=ea21900e22d7b4f6";
	String emsStateUri = "http://api.jisuapi.com/express/query?appkey=ea21900e22d7b4f6&";
	ArrayList<Ems> EmsFirmList = new ArrayList<Ems>();
	private ListView listView;
	private TextView tv_order;
	private PopupWindow listWindow;
	private View view_quicklist;
	private QuickIndexBar quickIndex;
	private TextView tv_center;
	private ArrayList<State> stateList;
	String type = null;
	String number = null;
	String EmsName = null;
	private String status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_queryems);
		initView();
		initData();
		initListener();
	}

	/**
	 * 初始化布局
	 */
	private void initView() {
		TextView tv_titlebar = (TextView) findViewById(R.id.tv_titlebar);
		tv_titlebar.setText("快递查询");
		tv_order = (TextView) findViewById(R.id.tv_order);
		btn_query = (Button) findViewById(R.id.btn_query);
		slideMenu = (DropMenu) findViewById(R.id.slideMenu);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		tv_none = (TextView) findViewById(R.id.tv_none);
		lv_state = (ListView) findViewById(R.id.lv_state);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_testnumber = (TextView) findViewById(R.id.tv_testnumber);
		tv_number = (TextView) findViewById(R.id.tv_number);
		et_orderId = (EditText) findViewById(R.id.et_orderId);
		tv_msg = (TextView) findViewById(R.id.tv_msg);
		tv_top_slide = (TextView) findViewById(R.id.tv_top_slide);
		view_quicklist = View.inflate(this, R.layout.ems_list, null);
		listView = (ListView) view_quicklist.findViewById(R.id.lv_ems);
		quickIndex = (QuickIndexBar) view_quicklist
				.findViewById(R.id.quickIndex);
		tv_center = (TextView) view_quicklist.findViewById(R.id.tv_center);
		listView.setVerticalScrollBarEnabled(false);
	}

	/**
	 * 初始化监听器
	 */
	private void initListener() {
		tv_order.setOnClickListener(this);
		btn_query.setOnClickListener(this);
		tv_top_slide.setOnClickListener(this);
		iv_back.setOnClickListener(this);
		/**
		 * 快速索引监听
		 */
		quickIndex.setLetterChangeListener(new onLetterChangeListener() {
			@Override
			public void onLetterChange(String letter) {
				showLetter(letter);
				for (int i = 0; i < EmsFirmList.size(); i++) {
					String str = EmsFirmList.get(i).pinyin.charAt(0) + "";
					if (TextUtils.equals(letter, str)) {
						listView.setSelection(i);
						break;
					}
				}
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				tv_order.setText(EmsFirmList.get(position).name);
				tv_testnumber.setText("当前测试码\n公司:"
						+ EmsFirmList.get(position).name + "\n运单号"
						+ EmsFirmList.get(position).number);
				listWindow.dismiss();
			}
		});
	}
	/**
	 * 初始化数据
	 */
	private void initData() {
		getEmsList();
		if (isLogin) {
			slideMenu.setVisibility(View.VISIBLE);
			tv_none.setVisibility(View.GONE);
		} else {
			slideMenu.setVisibility(View.GONE);
			tv_none.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 选择快递公司的弹窗
	 */
	private void initPopuWindow() {
		if (listWindow == null) {
			listWindow = new PopupWindow(view_quicklist, tv_order.getWidth(),
					LayoutParams.MATCH_PARENT);
			listWindow.setFocusable(true);
			listWindow.setOutsideTouchable(true);// 点击外部消失
			listWindow.setBackgroundDrawable(new BitmapDrawable());
		}
		listWindow.showAsDropDown(tv_order, 0, 0);
	}

	/**
	 * 网络获取快递公司列表
	 */
	private void getEmsList() {
		httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, EmsResultsListUrl,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String result = responseInfo.result;
						parseData(result);
					}
					@Override
					public void onFailure(HttpException error, String msg) {
					}
				});
	}

	private void parseData(String result) {
		Gson gson = new Gson();
		EmsResults EmsResults = gson.fromJson(result, EmsResults.class);
		EmsFirmList = EmsResults.result;
		for (int i = 0; i < EmsFirmList.size(); i++) {
			Ems ems = EmsFirmList.get(i);
			ems.pinyin = PinyinUtils.getPinyin(ems.name);
		}
		fillAndSotr(EmsFirmList);
		MyAdapter adapter = new MyAdapter();
		listView.setAdapter(adapter);
	}

	/**
	 * 快递公司列表适配器
	 * 
	 * @author Fanxi
	 * 
	 */
	class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return EmsFirmList.size();
		}

		@Override
		public Object getItem(int position) {
			return EmsFirmList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(EmsQueryActivity.this,
						R.layout.ems_list_item, null);
			}
			ViewHolder mHolder = ViewHolder.getHolder(convertView);
			Ems ems = EmsFirmList.get(position);
			String letter = null;
			String currentLetter = ems.pinyin.charAt(0) + "";
			if (position == 0) {
				letter = currentLetter;
			} else {
				String preLetter = EmsFirmList.get(position - 1).pinyin
						.charAt(0) + "";
				if (!TextUtils.equals(preLetter, currentLetter)) {
					letter = currentLetter;
				}
			}
			mHolder.mIndex.setVisibility(letter == null ? View.GONE
					: View.VISIBLE);
			if (letter != null) {
				mHolder.mIndex.setText(letter);
			}
			mHolder.mName.setText(ems.name);
			return convertView;
		}
	}

	static class ViewHolder {
		TextView mIndex;
		TextView mName;

		public static ViewHolder getHolder(View view) {
			Object tag = view.getTag();
			if (tag != null) {
				return (ViewHolder) tag;
			} else {
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.mIndex = (TextView) view.findViewById(R.id.tv_index);
				viewHolder.mName = (TextView) view.findViewById(R.id.tv_name);
				view.setTag(viewHolder);
				return viewHolder;
			}
		}
	}

	/**
	 * 列表按字母排序
	 * 
	 * @param EmsFirmList
	 */
	private void fillAndSotr(ArrayList<Ems> EmsFirmList) {
		Collections.sort(this.EmsFirmList);
	}

	private Handler handler = new Handler();
	private Button btn_query;
	private DropMenu slideMenu;
	private HttpUtils httpUtils;
	private ListView lv_state;
	private TextView tv_top_slide;
	private TextView tv_name;
	private TextView tv_number;
	private TextView tv_msg;
	private EditText et_orderId;
	private EmsState emsState;
	private TextView tv_none;
	private ImageView iv_back;
	private TextView tv_testnumber;

	protected void showLetter(String letter) {
		tv_center.setText(letter);
		tv_center.setVisibility(View.VISIBLE);
		handler.removeCallbacksAndMessages(null);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				tv_center.setVisibility(View.GONE);
			}
		}, 2000);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tv_order:
			initPopuWindow();
			break;
		case R.id.btn_query:
			getEmsState();
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(
					et_orderId.getWindowToken(), 0);
			if ("".equals(et_orderId.getText().toString())
					|| et_orderId.getText().toString() == null
					|| "".equals(tv_order.getText().toString())
					|| tv_order.getText().toString() == null) {
				Toast.makeText(getApplicationContext(), "请正确输入公司和运单号", 0)
						.show();
				return;
			}
			if ("0".equals(status)) {
				tv_name.setText("物流公司：" + EmsName);
				tv_number.setText("运单号：" + number);
				slideMenu.openMenu();
				et_orderId.setText("");
			} else {
				if (!requestState) {
					Toast.makeText(getApplicationContext(), "请核对运单号后再次查询", 0)
							.show();
				}
			}
			break;
		case R.id.tv_top_slide:
			slideMenu.closeMenu();
			break;
		case R.id.iv_back:
			finish();
			overridePendingTransition(R.anim.push_top_in, R.anim.push_top_out);
			break;
		}
	}

	private boolean requestState;

	private void getEmsState() {
		httpUtils = new HttpUtils();
		String orderName = (String) tv_order.getText();
		for (int i = 0; i < EmsFirmList.size(); i++) {
			if (TextUtils.equals(orderName, EmsFirmList.get(i).name)) {
				type = EmsFirmList.get(i).type;
				// number = EmsFirmList.get(i).number;
				EmsName = EmsFirmList.get(i).name;
			}
		}
		number = et_orderId.getText().toString();
		httpUtils.send(HttpMethod.GET, emsStateUri + "type=" + type
				+ "&number=" + number, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				requestState = true;
				String result = responseInfo.result;
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(result);
					status = jsonObject.getString("status");
					if ("0".equals(status)) {
						parseState(result);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				requestState = false;
			}
		});
	}

	private void parseState(String result) {
		Gson gson = new Gson();
		emsState = gson.fromJson(result, EmsState.class);
		stateList = emsState.result.list;
		lv_state.setAdapter(new EmsMegAdapter());
		tv_msg.setText("最新信息：" + stateList.get(stateList.size() - 1).status);
	}

	/**
	 * 快递状态信息适配器
	 */
	class EmsMegAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return stateList.size();
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
			convertView = View.inflate(EmsQueryActivity.this,
					R.layout.ems_msg_item, null);
			TextView tv_status = (TextView) convertView
					.findViewById(R.id.tv_status);
			TextView tv_time = (TextView) convertView
					.findViewById(R.id.tv_time);
			tv_status.setText(stateList.get(position).status);
			tv_time.setText(stateList.get(position).time);
			return convertView;
		}
	}
}

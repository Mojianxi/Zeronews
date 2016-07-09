package com.fanxi.zeronews.activity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.fanxi.zeronews.R;
import com.fanxi.zeronews.baidumap.PoiOverlay;

public class BaiduMapActivity extends BaseActivity implements OnClickListener,
		OnGetPoiSearchResultListener, OnGetSuggestionResultListener {
	private MapView mapView;
	private ToggleButton btn_3d;
	private ToggleButton btn_lukuang;
	private ToggleButton btn_reli;
	private boolean isShowMenu = false;
	private ImageView iv_menu;
	private RelativeLayout rl_menu;
	private BaiduMap mBaiduMap;
	private LinearLayout ll_2d;
	private LinearLayout ll_3d;
	private ImageView iv_2d;
	private ImageView iv_3d;
	private LinearLayout ll_top;

	private PoiSearch mPoiSearch;
	private SuggestionSearch mSuggestionSearch;
	private AutoCompleteTextView et_search;
	private List<String> suggest;
	int searchType = 0; // 搜索的类型，在显示时区分
	private int loadIndex = 0;
	private ArrayAdapter<String> sugAdapter = null;
	private TextView tv_find;
	LatLng center = new LatLng(37.090356, -95.712921);// 搜索位置周边
	int radius = 500;// 搜索周边的范围
	private ImageView iv_search_near;
	private ImageView iv_next;

	// 定位相关
		LocationClient mLocClient;
		public MyLocationListenner myListener = new MyLocationListenner();
		private LocationMode mCurrentMode;
		BitmapDescriptor mCurrentMarker;
		private static final int accuracyCircleFillColor = 0xAAFFFF88;
		private static final int accuracyCircleStrokeColor = 0xAA00FF00;
		// UI相关
		OnCheckedChangeListener radioButtonListener;
		Button requestLocButton;
		boolean isFirstLoc = true; // 是否首次定位
		private Button iv_location;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_bdmap);
		mapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mapView.getMap();
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		btn_3d = (ToggleButton) findViewById(R.id.btn_3d);
		btn_lukuang = (ToggleButton) findViewById(R.id.btn_lukuang);
		iv_search_near = (ImageView) findViewById(R.id.iv_search_near);
		iv_next = (ImageView) findViewById(R.id.iv_next);
		tv_find = (TextView) findViewById(R.id.tv_find);
		btn_reli = (ToggleButton) findViewById(R.id.btn_reli);
		iv_menu = (ImageView) findViewById(R.id.iv_menu);
		rl_menu = (RelativeLayout) findViewById(R.id.rl_menu);
		ll_2d = (LinearLayout) findViewById(R.id.ll_2d);
		ll_3d = (LinearLayout) findViewById(R.id.ll_3d);
		ll_top = (LinearLayout) findViewById(R.id.ll_top);
		iv_2d = (ImageView) findViewById(R.id.iv_2d);
		iv_3d = (ImageView) findViewById(R.id.iv_3d);
		iv_location = (Button) findViewById(R.id.iv_location);
		mapView.setOnClickListener(this);
		ll_2d.setOnClickListener(this);
		ll_3d.setOnClickListener(this);
		iv_menu.setOnClickListener(this);
		btn_3d.setOnClickListener(this);
		btn_lukuang.setOnClickListener(this);
		btn_reli.setOnClickListener(this);
		iv_next.setOnClickListener(this);
		tv_find.setOnClickListener(this);
		LatLng point = new LatLng(39.963175, 116.400244);
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_marka);
		OverlayOptions option = new MarkerOptions().position(point)
				.icon(bitmap);
		// 在地图上添加Marker，并显示
		mBaiduMap.addOverlay(option);
		final int ll_top_width = ll_top.getWidth();
		mBaiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
				Point p = new Point(Screenwidth - 200, Screenheight / 2);
				mapView.setZoomControlsPosition(p);
			}
		});
		// 搜索模块
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(this);
		et_search = (AutoCompleteTextView) findViewById(R.id.et_search);
		// 自动下拉提示组件
		sugAdapter = new ArrayAdapter<String>(this,
				R.layout.my_textview_drop);
		et_search.setAdapter(sugAdapter);
		et_search.setThreshold(1);// 必须在开始指定最小值
		et_search.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cs, int start, int before,
					int count) {
				if (cs.length() <= 0) {
					return;
				}
				mSuggestionSearch
						.requestSuggestion((new SuggestionSearchOption())
								.keyword(cs.toString()).city("北京"));
			}

			@Override
			public void beforeTextChanged(CharSequence cs, int start,
					int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		// 定位相关
				mCurrentMode = LocationMode.NORMAL;
				OnClickListener btnClickListener = new OnClickListener() {
					public void onClick(View v) {
						switch (mCurrentMode) {
						case NORMAL:
//							requestLocButton.setText("跟随");
							mCurrentMode = LocationMode.FOLLOWING;
							mBaiduMap
									.setMyLocationConfigeration(new MyLocationConfiguration(
											mCurrentMode, true, mCurrentMarker));
							break;
						case COMPASS:
//							requestLocButton.setText("普通");
							System.out.println("定位开始");
							mCurrentMode = LocationMode.NORMAL;
							mBaiduMap
									.setMyLocationConfigeration(new MyLocationConfiguration(
											mCurrentMode, true, mCurrentMarker));
							System.out.println("定位结束");
							break;
						case FOLLOWING:
//							requestLocButton.setText("罗盘");
							mCurrentMode = LocationMode.COMPASS;
							mBaiduMap
									.setMyLocationConfigeration(new MyLocationConfiguration(
											mCurrentMode, true, mCurrentMarker));
							break;
						default:
							break;
						}
					}
				};
				iv_location.setOnClickListener(btnClickListener);
				// 开启定位图层
				mBaiduMap.setMyLocationEnabled(true);
				// 定位初始化
				mLocClient = new LocationClient(this);
				mLocClient.registerLocationListener(myListener);
				LocationClientOption option1 = new LocationClientOption();
				option1.setOpenGps(true); // 打开gps
				option1.setCoorType("bd09ll"); // 设置坐标类型
				option1.setScanSpan(5000);
				option1.disableCache(false);//禁止启用缓存定位
				
				mLocClient.setLocOption(option1);
				mLocClient.start();
	}
//	private void initLocation(){
//        LocationClientOption option = new LocationClientOption();
//        option.setLocationMode(LocationMode.
//);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
//        int span=1000;
//        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
//        option.setOpenGps(true);//可选，默认false,设置是否使用gps
//        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
//        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死  
//        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
//option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
//        mLocClient.setLocOption(option);
//    }
	/**
	 * 城市内搜索按钮点击事件
	 * 
	 * @param v
	 */
	public void start_serch(View v) {
		searchType = 1;
		String keystr = et_search.getText().toString();
		mPoiSearch.searchInCity((new PoiCitySearchOption()).city("北京")
				.keyword(keystr).pageNum(loadIndex));
	}
	private void showMenuAnim() {
		// Animator animation=AnimatorInflater.loadAnimator(this,
		// R.anim.mapmenu_show);
		// animation.setupStartValues();
		// iv_menu.setPivotX(0f);
		// iv_menu.setPivotY(0.0f);
		// rl_menu.invalidate();
		// animation.setTarget(rl_menu);
		// animation.start();
		AnimationSet set = new AnimationSet(false);
		ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
				0.0f);
		animation.setDuration(500);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1.0f);
		alphaAnimation.setDuration(500);
		set.addAnimation(alphaAnimation);
		set.addAnimation(animation);
		// animation.setRepeatCount(int repeatCount);//设置重复次数
		// animation.setFillAfter(boolean);//动画执行完后是否停留在执行完的状态
		// animation.setStartOffset(long startOffset);//执行前的等待时间
		// rl_menu.setAnimation(animation);
		// animation.start();
		rl_menu.startAnimation(set);
	}

	@Override
	protected void onDestroy() {
		mPoiSearch.destroy();
		mSuggestionSearch.destroy();
		  // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
		mapView.onDestroy();
		mapView = null;
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mapView.onPause();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_menu:
			if (!isShowMenu) {
				rl_menu.setFocusable(true);
				rl_menu.requestFocus();
				showMenuAnim();
				rl_menu.setVisibility(View.VISIBLE);
				isShowMenu = true;
			} else {
				rl_menu.setVisibility(View.GONE);
				isShowMenu = false;
			}
			break;
		case R.id.ll_2d:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			iv_2d.setImageResource(R.drawable.mapselected);
			iv_3d.setImageResource(R.drawable.map_show3d);
			// 空白地图,
			// 基础地图瓦片将不会被渲染。在地图类型中设置为NONE，将不会使用流量下载基础地图瓦片图层。使用场景：与瓦片图层一起使用，节省流量，提升自定义瓦片图下载速度。
			// mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
			break;
		case R.id.ll_3d:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			iv_2d.setImageResource(R.drawable.map_show2d);
			iv_3d.setImageResource(R.drawable.map02_selected);
			break;
		case R.id.btn_3d:
			break;
		case R.id.btn_lukuang:
			if (btn_lukuang.isChecked()) {
				// 开启交通图
				mBaiduMap.setTrafficEnabled(true);
			} else {
				mBaiduMap.setTrafficEnabled(false);
			}
			break;
		case R.id.btn_reli:
			if (btn_reli.isChecked()) {
				// 开启热力图
				mBaiduMap.setBaiduHeatMapEnabled(true);
			} else {
				mBaiduMap.setBaiduHeatMapEnabled(false);
			}
		case R.id.rl_menu:
			rl_menu.setVisibility(View.VISIBLE);
			break;
		case R.id.iv_next://
			loadIndex++;
			start_serch(null);
			break;
		case R.id.tv_find:// 周边搜索
			searchType = 2;
			PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption()
					.keyword(et_search.getText().toString())
					.sortType(PoiSortType.distance_from_near_to_far)
					.location(center).radius(radius).pageNum(loadIndex);
			mPoiSearch.searchNearby(nearbySearchOption);
			break;
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isShowMenu) {
			rl_menu.setVisibility(View.GONE);
			isShowMenu = false;
		}
		// 判断隐藏软键盘是否弹出
		if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
			// 隐藏软键盘
			getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void onGetSuggestionResult(SuggestionResult res) {
		 if (res == null || res.getAllSuggestions() == null) {
	            return;
	        }    
	        suggest = new ArrayList<String>();
	        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
	            if (info.key != null) {
	                suggest.add(info.key);
	            }
	        }
	        sugAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, suggest);
	        et_search.setAdapter(sugAdapter);
	        sugAdapter.notifyDataSetChanged();
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getApplicationContext(), "抱歉，未找到结果", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(getApplicationContext(), result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT)
                    .show();
        }
	}
	@Override
	public void onGetPoiIndoorResult(PoiIndoorResult arg0) {
		
	}
	
	@Override
	public void onGetPoiResult(PoiResult result) {

		 if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
	            Toast.makeText(BaiduMapActivity.this, "未找到结果", Toast.LENGTH_LONG)
	                    .show();
	            return;
	        }
		 if(result.error==SearchResult.ERRORNO.NO_ERROR){
			 mBaiduMap.clear();
			 PoiOverlay overlay=new MyPoiOverlay(mBaiduMap);
			 mBaiduMap.setOnMarkerClickListener(overlay);
			 overlay.setData(result);
			 overlay.addTopMap();
			 overlay.zoomToSpan();
			 switch (searchType) {
			case 2:
				showNearbyArea(ll, radius);
				break;
			default:
				break;
			}
			 return;
		 }
		 if(result.error== SearchResult.ERRORNO.AMBIGUOUS_KEYWORD){
			 //当输入关键字在本市没有找到,但在其它城市找到时,返回包含该关键字信息的城市列表
			 String strInfo="在";
			 for(CityInfo cityInfo:result.getSuggestCityList()){
				 strInfo+=cityInfo.city;
				 strInfo+=",";
			 }
			 strInfo+="找到结果";
			 Toast.makeText(getApplicationContext(), strInfo, Toast.LENGTH_SHORT).show();
		 }
	}
	private class MyPoiOverlay extends PoiOverlay{
		public MyPoiOverlay(BaiduMap baiduMap){
			super(baiduMap);
		}
		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poiInfo=getPoiResult().getAllPoi().get(index);
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(poiInfo.uid));
			return true;
		}
	}
	public void showNearbyArea(LatLng center,int radius){
		BitmapDescriptor centerBitmap=BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
		MarkerOptions ooMarker=new MarkerOptions().position(center).icon(centerBitmap);
		mBaiduMap.addOverlay(ooMarker);
		OverlayOptions ooCircle=new CircleOptions().fillColor( 0xCCCCCC00 ).center(center).stroke(new Stroke(5, 0xFFFF00FF)).radius(radius);
		mBaiduMap.addOverlay(ooCircle);
	}
	/**
     * 定位SDK监听函数
     */
	private LatLng ll;
    public class MyLocationListenner implements BDLocationListener {
		@Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(15.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
}

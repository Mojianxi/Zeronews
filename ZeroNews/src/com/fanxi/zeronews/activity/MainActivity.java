package com.fanxi.zeronews.activity;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.CycleInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.fanxi.zeronews.R;
import com.fanxi.zeronews.activity.fragment.FragmentFactory;
import com.fanxi.zeronews.activity.pager.HomePager;
import com.fanxi.zeronews.application.BaseApplication;
import com.fanxi.zeronews.bean.FirstEvent;
import com.fanxi.zeronews.util.UiUtils;
import com.fanxi.zeronews.view.DragLayout;
import com.fanxi.zeronews.view.DragLayout.OnDragChangeListener;
import com.fanxi.zeronews.view.MyLinearLayout;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
public class MainActivity extends BaseActivity implements OnClickListener{
	public DragLayout dragLayout;
	private ImageView iv_head;
	private ListView lv_main;
	public MyLinearLayout ll_main;
	public FrameLayout fl_main;
	private ImageView iv_user;
	private Message msg;
	private String name;
	private static String nickname;
	private Bitmap userBitmap;
	private static Bitmap bitmap;
	private TextView tv;
	private static FirstEvent firstEvent;
	public static final int NONE_THEME = 0;
	public static final int APPBASE_THEME = 1;
	public static final int DEFAULT_THEME = 2;
	public static final int RED_THEME = 3;
	public static final int PINK_THEME = 4;
	public static final int PURPLE_THEME = 5;
	public static final int DEEP_PURPLE_THEME = 6;
	public static final int INDIGO_THEME = 7;
	public static final int BLUE_THEME = 8;
	public static final int LIGHT_BLUE_THEME = 9;
	public static final int CYAN_THEME = 10;
	public static final int TEAL_THEME = 11;
	public static final int GREEN_THEME = 12;
	public static final int LIGHT_THEME = 13;
	public static final int LIME_THEME = 14;
	public static final int YELLOW_THEME = 15;
	public static final int AMBER_THEME = 16;
	public static final int ORANGE_THEME = 17;
	public static final int DEEP_ORANGE_THEME = 18;
	public static final int BROWN_THEME = 19;
	public static final int GREY_THEME = 20;
	public static final int BLUE_GREY_THEME = 21;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		appState = (BaseApplication)getApplicationContext();
		setContentView(R.layout.activity_main);
		dragLayout = (DragLayout) findViewById(R.id.dl);
		ll_menu = (LinearLayout) findViewById(R.id.ll_menu);
		ll_main = (MyLinearLayout) findViewById(R.id.ll_main);
		fl_main = (FrameLayout) findViewById(R.id.fl_main);
		iv_head = (ImageView) findViewById(R.id.iv_head);
		rl_title = (RelativeLayout) findViewById(R.id.rl_title);
		iv_user = (ImageView) findViewById(R.id.iv_user);
		tv = (TextView) findViewById(R.id.tv_name);
		lv_left = (ListView) findViewById(R.id.lv_left);
		FragmentManager fm = getSupportFragmentManager();
		lv_left.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.style_names)));
		HomePager homePager=new HomePager(this,fm);
		fl_main.addView(homePager.mRootView);
//		lv_main.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,Cheeses.sCheeseStrings));
		dragLayout.setListener(dragChangeListener);
		ll_main.setDragLayout(dragLayout);
		iv_head.setOnClickListener(this);
		iv_user.setOnClickListener(this);
		if(isLogin&&user_name1!=null){
			tv.setText(user_name1);
			iv_head.setImageBitmap(UiUtils.toRoundBitmap(loginPic));
			iv_user.setImageBitmap(UiUtils.toRoundBitmap(loginPic));
		}
		appState.addActivity(this);
		lv_left.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				
				setTheme(position);
				reload();
			}
		});
		if(mTheme==R.style.DefaultTheme){
			rl_title.setBackgroundResource(R.color.action_default);
			lv_left.setBackgroundResource(R.color.bg_default);
		}else if(mTheme==R.style.RedTheme){
			rl_title.setBackgroundResource(R.color.action_red);
			lv_left.setBackgroundResource(R.color.bg_red);
			dragLayout.setBackgroundResource(R.color.window_bg_red);
		}else if(BaseActivity.mTheme==R.style.NomalTheme){
			rl_title.setBackgroundResource(R.drawable.bg);
			lv_left.setBackgroundResource(R.color.bg_done);
			dragLayout.setBackgroundResource(R.color.bg_done);
		}else if(BaseActivity.mTheme==R.style.AppBaseTheme){
			rl_title.setBackgroundResource(R.color.action_nomal);
			lv_left.setBackgroundResource(R.color.bg_nomal);
			dragLayout.setBackgroundResource(R.color.bg_default);
		}else if(BaseActivity.mTheme==R.style.DefaultTheme){
			rl_title.setBackgroundResource(R.color.bg_default);
			lv_left.setBackgroundResource(R.color.control_normal_default);
			dragLayout.setBackgroundResource(R.color.button_normal_default);
		}else if(BaseActivity.mTheme==R.style.PinkTheme){
			rl_title.setBackgroundResource(R.color.action_pink);
			lv_left.setBackgroundResource(R.color.bg_pink);
			dragLayout.setBackgroundResource(R.color.window_bg_pink);
		}else if(BaseActivity.mTheme==R.style.PurpleTheme){
			rl_title.setBackgroundResource(R.color.action_purple);
			lv_left.setBackgroundResource(R.color.bg_purple);
			dragLayout.setBackgroundResource(R.color.window_bg_purple);
		}else if(BaseActivity.mTheme==R.style.DeepPurpleTheme){
			rl_title.setBackgroundResource(R.color.action_deep_purple);
			lv_left.setBackgroundResource(R.color.bg_deep_purple);
			dragLayout.setBackgroundResource(R.color.window_bg_deep_purple);
		}else if(BaseActivity.mTheme==R.style.IndigoTheme){
			rl_title.setBackgroundResource(R.color.action_indigo);
			lv_left.setBackgroundResource(R.color.bg_indigo);
			dragLayout.setBackgroundResource(R.color.window_bg_indigo);
		}else if(BaseActivity.mTheme==R.style.BlueTheme){
			rl_title.setBackgroundResource(R.color.action_blue);
			lv_left.setBackgroundResource(R.color.bg_blue);
			dragLayout.setBackgroundResource(R.color.window_bg_blue);
		}else if(BaseActivity.mTheme==R.style.LightBlueTheme){
			rl_title.setBackgroundResource(R.color.action_light_blue);
			lv_left.setBackgroundResource(R.color.bg_light_blue);
			dragLayout.setBackgroundResource(R.color.window_bg_light_blue);
		}else if(BaseActivity.mTheme==R.style.CyanTheme){
			rl_title.setBackgroundResource(R.color.action_cyan);
			lv_left.setBackgroundResource(R.color.bg_cyan);
			dragLayout.setBackgroundResource(R.color.window_bg_cyan);
		}else if(BaseActivity.mTheme==R.style.TealTheme){
			rl_title.setBackgroundResource(R.color.action_teal);
			lv_left.setBackgroundResource(R.color.bg_teal);
			dragLayout.setBackgroundResource(R.color.window_bg_teal);
		}else if(BaseActivity.mTheme==R.style.GreenTheme){
			rl_title.setBackgroundResource(R.color.action_green);
			lv_left.setBackgroundResource(R.color.bg_green);
			dragLayout.setBackgroundResource(R.color.window_bg_green);
		}else if(BaseActivity.mTheme==R.style.LightGreenTheme){
			rl_title.setBackgroundResource(R.color.action_light_green);
			lv_left.setBackgroundResource(R.color.bg_light_green);
			dragLayout.setBackgroundResource(R.color.window_bg_light_green);
		}else if(BaseActivity.mTheme==R.style.LimeTheme){
			rl_title.setBackgroundResource(R.color.action_lime);
			lv_left.setBackgroundResource(R.color.bg_lime);
			dragLayout.setBackgroundResource(R.color.window_bg_lime);
		}else if(BaseActivity.mTheme==R.style.YellowTheme){
			rl_title.setBackgroundResource(R.color.action_yellow);
			lv_left.setBackgroundResource(R.color.bg_yellow);
			dragLayout.setBackgroundResource(R.color.window_bg_yellow);
		}else if(BaseActivity.mTheme==R.style.AmberTheme){
			rl_title.setBackgroundResource(R.color.action_amber);
			lv_left.setBackgroundResource(R.color.bg_amber);
			dragLayout.setBackgroundResource(R.color.window_bg_amber);
		}else if(BaseActivity.mTheme==R.style.OrangeTheme){
			rl_title.setBackgroundResource(R.color.action_orange);
			lv_left.setBackgroundResource(R.color.button_normal_orange);
			dragLayout.setBackgroundResource(R.color.window_bg_orange);
		}else if(BaseActivity.mTheme==R.style.DeepOrangeTheme){
			rl_title.setBackgroundResource(R.color.action_deep_orange);
			lv_left.setBackgroundResource(R.color.bg_deep_orange);
			dragLayout.setBackgroundResource(R.color.window_bg_deep_orange);
		}else if(BaseActivity.mTheme==R.style.BrownTheme){
			rl_title.setBackgroundResource(R.color.action_brown);
			lv_left.setBackgroundResource(R.color.bg_brown);
			dragLayout.setBackgroundResource(R.color.window_bg_brown);
		}else if(BaseActivity.mTheme==R.style.GreyTheme){
			rl_title.setBackgroundResource(R.color.action_grey);
			lv_left.setBackgroundResource(R.color.bg_grey);
			dragLayout.setBackgroundResource(R.color.window_bg_grey);
		}else if(BaseActivity.mTheme==R.style.BlueGreyTheme){
			rl_title.setBackgroundResource(R.color.action_blue_grey);
			lv_left.setBackgroundResource(R.color.bg_blue_grey);
			dragLayout.setBackgroundResource(R.color.window_bg_blue_grey);
		}else{
			rl_title.setBackgroundResource(R.color.action_done);
			lv_left.setBackgroundResource(R.color.bg_done);
			dragLayout.setBackgroundResource(R.drawable.bg);
		}
	}
	public OnDragChangeListener dragChangeListener=new OnDragChangeListener() {
		@Override
		public void onOpen() {
//			Random random = new Random();
//			lv_left.smoothScrollToPosition(random.nextInt(50));
			dragLayout.open(true);
		}
		@Override
		public void onDraging(float percent) {
			ViewHelper.setAlpha(iv_head, 1 - percent);
		}
		
		@Override
		public void onClose() {
			ObjectAnimator mAnim = ObjectAnimator.ofFloat(iv_head, "translationX", 15f);
			mAnim.setInterpolator(new CycleInterpolator(4));
			mAnim.setDuration(500);
			mAnim.start();
		}
	};
	protected void reload(){
		BaseActivity activity=(BaseActivity) getActivty();
        Intent intent =activity.getIntent();
        activity.overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.finish();
//        appState.finishAll();
        FragmentFactory.clear();
        activity.overridePendingTransition(0, 0);
        startActivity(intent);
   }
	public void setTheme(int index){
		switch (index) {
		case NONE_THEME:
			BaseActivity.mTheme=R.style.NomalTheme;
			break;
		case APPBASE_THEME:
			BaseActivity.mTheme=R.style.AppBaseTheme;
			break;
		case DEFAULT_THEME:
			BaseActivity.mTheme=R.style.DefaultTheme;
			break;
		case RED_THEME:
			BaseActivity.mTheme=R.style.RedTheme;
			break;
		case PINK_THEME:
			BaseActivity.mTheme=R.style.PinkTheme;
			break;
		case PURPLE_THEME:
			BaseActivity.mTheme=R.style.PurpleTheme;
			break;
		case DEEP_PURPLE_THEME:
			BaseActivity.mTheme=R.style.DeepPurpleTheme;
			break;
		case INDIGO_THEME:
			BaseActivity.mTheme=R.style.IndigoTheme;
			break;
		case BLUE_THEME:
			BaseActivity.mTheme=R.style.BlueTheme;
			break;
		case LIGHT_BLUE_THEME:
			BaseActivity.mTheme=R.style.LightBlueTheme;
			break;
		case CYAN_THEME:
			BaseActivity.mTheme=R.style.CyanTheme;
			break;
		case TEAL_THEME:
			BaseActivity.mTheme=R.style.TealTheme;
			break;
		case GREEN_THEME:
			BaseActivity.mTheme=R.style.GreenTheme;
			break;
		case LIGHT_THEME:
			BaseActivity.mTheme=R.style.LightGreenTheme;
			break;
		case LIME_THEME:
			BaseActivity.mTheme=R.style.LimeTheme;
			break;
		case YELLOW_THEME:
			BaseActivity.mTheme=R.style.YellowTheme;
			break;
		case AMBER_THEME:
			BaseActivity.mTheme=R.style.AmberTheme;
			break;
		case ORANGE_THEME:
			BaseActivity.mTheme=R.style.OrangeTheme;
			break;
		case DEEP_ORANGE_THEME:
			BaseActivity.mTheme=R.style.DeepOrangeTheme;
			break;
		case BROWN_THEME:
			BaseActivity.mTheme=R.style.BrownTheme;
			break;
		case GREY_THEME:
			BaseActivity.mTheme=R.style.GreyTheme;
			break;
		case BLUE_GREY_THEME:
			BaseActivity.mTheme=R.style.BlueGreyTheme;
			break;
		}
	}
	public void onEventMainThread(FirstEvent event){
		Message msg=event.getMsg();
		if(msg.what==0){
			JSONObject response=(JSONObject) msg.obj;
			if(response.has("nickname")){
				try{
					nickname = response.getString("nickname");
					name = nickname;
					tv.setText(name);
				}catch (Exception e) {
				}
			}
		}else if(msg.what==1){
			bitmap=(Bitmap) msg.obj;
//			userBitmap=bitmap;
			Bitmap roundBitmap = UiUtils.toRoundBitmap(bitmap);
			iv_user.setImageBitmap(roundBitmap);
			iv_head.setImageBitmap(roundBitmap);
			listerner=new firstEventChangedListerner() {
				@Override
				public FirstEvent setfirstEvent() {
					FirstEvent event=new FirstEvent(bitmap);
					return event;
				}
			};
		}
	}
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////		if(requestCode==0){
////			String stringExtra = data.getStringExtra("head");
////			if(stringExtra!=null){
////				Toast.makeText(getApplicationContext(), "更换头像", 0).show();
////				iv_head.setImageBitmap(UiUtils.toRoundBitmap(ResUtil.getBitmap(UiUtils.getContext(), user_name1)));
////			}
////		}
//		super.onActivityResult(requestCode, resultCode, data);
//	}
	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.iv_head:
				dragLayout.open(true);
				break;
			case R.id.iv_user:
				startActivityForResult(new Intent(this,LoginActivity.class), 0);
				dragLayout.close(true);
				this.finish();
//				overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
				break;
		}
	}
	public static FirstEvent getFirstEvent(){
		if(listerner!=null){
			FirstEvent setfirstEvent = listerner.setfirstEvent();
			return setfirstEvent;
		}
		return null;
	}
	static firstEventChangedListerner listerner;
	private ListView lv_left;
	private RelativeLayout rl_title;
	private LinearLayout ll_menu;
	public  interface firstEventChangedListerner{
		FirstEvent setfirstEvent();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}

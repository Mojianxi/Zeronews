package com.fanxi.zeronews.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.fanxi.zeronews.R;
import com.fanxi.zeronews.application.BaseApplication;
import com.fanxi.zeronews.view.VideoMediaController;
import com.fanxi.zeronews.view.VideoSuperPlayer;



/**
 * Created by Administrator on 2015/11/16.
 */
public class FullActivity extends BaseActivity {
    VideoSuperPlayer video;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 横屏
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.full_activity);
        video = (VideoSuperPlayer) findViewById(R.id.textureview);
        video.loadAndPlay(BaseApplication.getMediaPlayer(), "url", 0, true);
        video.setPageType(VideoMediaController.PageType.EXPAND);
        video.setVideoPlayCallback(new VideoSuperPlayer.VideoPlayCallbackImpl(){
            @Override
            public void onSwitchPageType() {
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    finish();
                    video.stopPlay();
                }
            }
            @Override
            public void onPlayFinish() {
            }
            @Override
            public void onCloseVideo() {
            }
        });
    }
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    	finish();
    	video.stopPlay();
    }
}

package com.bqt.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.liveview.ViEAndroidGLES20;
import com.example.liveview.ViERenderer;
import com.example.liveview.livestream;

public class LiveActivity extends Activity {
	private ViEAndroidGLES20 mSurfaceView;
	private LinearLayout root;
	private String mVideoUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题栏
		initViews();
		setContentView(root);
	}

	private void initViews() {
		// 视频比率 4:3 
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int width = displayMetrics.widthPixels;
		int h = width * 3 / 4;
		if (h % 2 != 0) h += 1;
		root = new LinearLayout(this);
		root.setOrientation(LinearLayout.VERTICAL);
		mSurfaceView = ViERenderer.CreateRenderer(this, true, null);
		mSurfaceView.setLayoutParams(new FrameLayout.LayoutParams(width, h));//具有父控件的View设置setLayoutParams才有效！
		root.addView(mSurfaceView);
		TextView textView = new TextView(this);
		root.addView(textView);
		Intent intent = getIntent();
		if (intent != null && intent.getStringExtra("anchorId") != null) {
			mVideoUrl = String.format("app/%s?k=1092dc67c9402014144fc19181974172&t=540fb568", intent.getStringExtra("anchorId"));
			textView.setText("视频地址：" + mVideoUrl);
			//livestream.setPlayerStateHandler(mHandler);
			livestream.init(mVideoUrl, mSurfaceView);
			startVideo();
		}
	}

	public void startVideo() {
		try {
			Log.v("vvvv", "startPlay");
			livestream.setVideoPath(mVideoUrl);
			livestream.setSurfaceView(mSurfaceView);
			livestream.playVideo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopVideo(boolean isFreeHandler) {
		try {
			Log.v("vvvv", "stopPlay");
			livestream.stopVideo();
			if (isFreeHandler) {
				livestream.setPlayerStateHandler(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void freeVideo() {
		try {
			livestream.setPlayerStateHandler(null);
			livestream.free();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mSurfaceView != null) mSurfaceView.onPause();
		stopVideo(true);
	}
}
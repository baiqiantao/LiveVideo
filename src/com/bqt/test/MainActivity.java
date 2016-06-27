package com.bqt.test;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bqt.test.AsyncHttpHelper.OnHttpListener;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends Activity {
	public static final String SERVER_URL = "api.95xiu.com";//"tapi.95xiu.com"
	public static final String RQ_LIVE = "/show/anchor_list_v3.php";
	private List<LiveBean> mList;
	private GridView gridView;
	private MyBaseAdapter adapter;
	private DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题栏
		ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(this).build());
		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)//将图片缓存到内存和硬盘中
				.showImageOnLoading(R.drawable.live_icon_default).showImageOnFail(R.drawable.live_icon_default)//加载中、加载错误时显示的图片
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		setContentView(R.layout.layout_main);
		mList = new ArrayList<LiveBean>();
		loadingData();
		gridView = (GridView) findViewById(R.id.gv);
		adapter = new MyBaseAdapter();
		gridView.setAdapter(adapter);
	}

	private void loadingData() {
		RequestParams requestParams = new RequestParams();
		requestParams.put("page_index", "0");//非必须
		requestParams.put("version", "1");//非必须
		AsyncHttpHelper.get(SERVER_URL,RQ_LIVE, requestParams, new OnHttpListener<JSONObject>() {//http://api.95xiu.com/show/anchor_list_v3.php
			@Override
			public void onHttpListener(boolean httpSuccessed, JSONObject obj) {
				if (httpSuccessed) {
					Log.i("bqt", obj.toString());
					if (obj.optInt("result") == 1) {
						JSONArray arr = obj.optJSONArray("user_info");
						if (arr != null && arr.length() > 0) {
							for (int i = 0; i < arr.length(); i++) {
								JSONObject jObject = arr.optJSONObject(i);
								mList.add(new LiveBean(jObject.optString("anchor_image"), jObject.optString("nickname"), jObject.optString("live_num")//
										, jObject.optString("uid")));
							}
							Log.i("bqt", "数据个数：" + mList.size());
						}
						adapter.notifyDataSetChanged();
					}
				}
			}
		});
	}

	public class MyBaseAdapter extends BaseAdapter {
		private ViewHolder mViewHolder;

		@Override
		public int getCount() {
			return mList == null ? 0 : mList.size();
		}

		@Override
		public Object getItem(int position) {
			return mList == null ? null : mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView != null) mViewHolder = (ViewHolder) convertView.getTag();
			else {
				convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item, null);
				mViewHolder = new ViewHolder();
				mViewHolder.iv_head = (ImageView) convertView.findViewById(R.id.iv_anchor_img);
				mViewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_anchor_name);
				mViewHolder.tv_audience_num = (TextView) convertView.findViewById(R.id.tv_audience_num);
				mViewHolder.iv_rankingLev = (ImageView) convertView.findViewById(R.id.iv_rankingLev);
				mViewHolder.iv_moods = (ImageView) convertView.findViewById(R.id.iv_moods);
				mViewHolder.iv_coverage = (ImageView) convertView.findViewById(R.id.iv_coverage);
				convertView.setTag(mViewHolder);
			}
			if (mList != null) {
				ImageLoader.getInstance().displayImage(mList.get(position).anchor_image, mViewHolder.iv_head, options);
				mViewHolder.tv_name.setText(mList.get(position).nickname);
				mViewHolder.tv_audience_num.setText(mList.get(position).live_num);
				if (position == 0) mViewHolder.iv_rankingLev.setVisibility(View.VISIBLE);
				else mViewHolder.iv_rankingLev.setVisibility(View.INVISIBLE);
				if (position == 0 || position == 1 || position == 3 || position == 6) mViewHolder.iv_moods.setVisibility(View.VISIBLE);
				else mViewHolder.iv_moods.setVisibility(View.INVISIBLE);
				mViewHolder.iv_coverage.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MainActivity.this, LiveActivity.class);
						intent.putExtra("anchorId", mList.get(position).anchorId);
						Log.i("bqt", "主播ID：" + mList.get(position).anchorId);
						startActivity(intent);
					}
				});
			}
			return convertView;
		}
	}

	public static class ViewHolder {
		public ImageView iv_head;//主播头像
		public TextView tv_name;//主播名字
		public TextView tv_audience_num;//直播间人数
		public ImageView iv_rankingLev;//排名
		public ImageView iv_moods;//人气等标签
		public ImageView iv_coverage;//背景框
	}
}

class LiveBean {
	public String anchor_image;
	public String nickname;
	public String live_num;
	public String anchorId;

	public LiveBean(String anchor_image, String nickname, String live_num, String anchorId) {
		super();
		this.anchor_image = anchor_image;
		this.nickname = nickname;
		this.live_num = live_num;
		this.anchorId = anchorId;
	}
}
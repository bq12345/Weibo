package com.bq.weibo;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bq.adapter.WeiboAdapter;
import com.bq.models.ErrorInfo;
import com.bq.models.Status;
import com.bq.models.StatusList;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.utils.LogUtil;

public class HomeActivity extends Activity {
	private Oauth2AccessToken mAccessToken;
	private StatusesAPI mStatusesAPI;
	private ListView lv;
	private WeiboAdapter adapter;
	private LinearLayout progress;
	private TextView tv_title;
	private Long maxId = 0L;
	private Button loadMoreButton;
	private boolean refershing = false;

	private void setupView(StatusList statuses) {

		adapter = new WeiboAdapter(HomeActivity.this, statuses);
		lv.setAdapter(adapter);
		lv.setCacheColorHint(0);
	}

	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		lv = (ListView) findViewById(R.id.lv);
		tv_title = (TextView) findViewById(R.id.txt_wb_title);
		progress = (LinearLayout) findViewById(R.id.layout_progress);
		View loadMoreView = getLayoutInflater().inflate(R.layout.load_more,
				null);
		tv_title.setText("微博");
		loadMoreButton = (Button) loadMoreView
				.findViewById(R.id.loadMoreButton);
		lv.addFooterView(loadMoreView);
		lv.setFadingEdgeLength(0);
		progress.setVisibility(View.VISIBLE);
		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		mStatusesAPI = new StatusesAPI(mAccessToken);
		mStatusesAPI.friendsTimeline(0L, 0L, 10, 1, false, 0, false, mListener);
		loadMoreButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!refershing) {
					progress.setVisibility(View.VISIBLE);
					refershing = true;
					loadMoreButton.setText("正在加载，请稍候...");
					mStatusesAPI.friendsTimeline(maxId, 0L, 10, 1, false, 0,
							false, mAddListener);
				}
			}
		});
	}

	public void writeHandler(View v) {
		Intent intent = new Intent(this, SendActivity.class);
		startActivity(intent);
	}

	public void refershHandler(View v) {
		if (!refershing) {
			refershing = true;
			progress.setVisibility(View.VISIBLE);
			mStatusesAPI.friendsTimeline(0L, 0L, 10, 1, false, 0, false,
					mListener);
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		mStatusesAPI.friendsTimeline(0L, 0L, 10, 1, false, 0, false, mListener);
	}

	/**
	 * 微博 OpenAPI 回调接口。
	 */
	private RequestListener mListener = new RequestListener() {
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				if (response.startsWith("{\"statuses\"")) {
					// 调用 StatusList#parse 解析字符串成微博列表对象
					StatusList statuses = StatusList.parse(response);
					if (statuses != null && statuses.total_number > 0) {
						// Toast.makeText(HomeActivity.this,"获取微博信息流成功, 条数: " +
						// statuses.statusList.size(),Toast.LENGTH_SHORT).show();
						setupView(statuses);
						maxId = Long.parseLong(statuses.statusList
								.get(statuses.statusList.size() - 1).id);
						refershing = false;
						progress.setVisibility(View.GONE);

					}
				} else {
					Toast.makeText(HomeActivity.this, response,
							Toast.LENGTH_LONG).show();
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			LogUtil.e("Error", e.getMessage());
			ErrorInfo info = ErrorInfo.parse(e.getMessage());
			Toast.makeText(HomeActivity.this, info.toString(),
					Toast.LENGTH_LONG).show();
		}
	};

	/**
	 * 微博 OpenAPI 加载更多回调接口。
	 */
	private RequestListener mAddListener = new RequestListener() {
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				if (response.startsWith("{\"statuses\"")) {
					// 调用 StatusList#parse 解析字符串成微博列表对象
					StatusList statuses = StatusList.parse(response);
					if (statuses != null && statuses.total_number > 0) {
						ArrayList<Status> list = statuses.statusList;
						int num = list.size();
						for (int i = 0; i < num; i++)
							adapter.addItem(list.get(i));
						adapter.notifyDataSetChanged();
						loadMoreButton.setText("加载更多");
						maxId = Long.parseLong(list.get(num - 1).id);
						refershing = false;
						progress.setVisibility(View.GONE);

					}
				} else {
					Toast.makeText(HomeActivity.this, response,
							Toast.LENGTH_LONG).show();
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			LogUtil.e("Error", e.getMessage());
			ErrorInfo info = ErrorInfo.parse(e.getMessage());
			Toast.makeText(HomeActivity.this, info.toString(),
					Toast.LENGTH_LONG).show();
		}
	};
}

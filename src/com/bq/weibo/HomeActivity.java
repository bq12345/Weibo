package com.bq.weibo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.Toast;

import com.bq.adapter.WeiboAdapter;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.utils.LogUtil;

public class HomeActivity extends Activity {
	private Oauth2AccessToken mAccessToken;
	private StatusesAPI mStatusesAPI;
	private ListView lv;
	private WeiboAdapter adapter;

	private void setupView(StatusList statuses) {
		lv = (ListView) findViewById(R.id.lv);
		adapter = new WeiboAdapter(HomeActivity.this, statuses);
		lv.setAdapter(adapter);
		lv.setCacheColorHint(0);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		mStatusesAPI = new StatusesAPI(mAccessToken);
		mStatusesAPI.friendsTimeline(0L, 0L, 10, 1, false, 0, false, mListener);
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
						Toast.makeText(HomeActivity.this,
								"获取微博信息流成功, 条数: " + statuses.statusList.size(),
								Toast.LENGTH_SHORT).show();
						setupView(statuses);

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

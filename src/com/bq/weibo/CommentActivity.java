package com.bq.weibo;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bq.adapter.CommentAdapter;
import com.bq.models.Comment;
import com.bq.models.CommentList;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.utils.LogUtil;

public class CommentActivity extends Activity {
	private Oauth2AccessToken mAccessToken;
	private CommentsAPI mCommentsAPI;
	private TextView input;
	private long id;
	private String text;
	private ListView lv;
	private CommentAdapter adapter;
	private LinearLayout progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.comment);

		lv = (ListView) findViewById(R.id.lv);
		TextView tv = (TextView) findViewById(R.id.text);
		TextView user = (TextView) findViewById(R.id.comment_user);
		TextView date = (TextView) findViewById(R.id.comment_date);
		progress = (LinearLayout) findViewById(R.id.comment_layout_progress);
		Intent intent = getIntent();

		text = intent.getStringExtra("text");
		id = Long.parseLong(intent.getStringExtra("id"));

		tv.setText(text);
		user.setText(intent.getStringExtra("user"));
		date.setText(intent.getStringExtra("date"));
		input = (TextView) findViewById(R.id.comment_input);
		int comments = getIntent().getIntExtra("comments", 0);
		// 获取当前已保存过的 Token
		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		// 获取微博评论信息接口
		mCommentsAPI = new CommentsAPI(mAccessToken);
		if (comments > 0) {
			progress.setVisibility(View.VISIBLE);
			mCommentsAPI.show(id, 0L, 0L, 50, 1, 0, listener);
		}
	}

	private void setupView(CommentList list) {
		adapter = new CommentAdapter(CommentActivity.this, list);
		lv.setAdapter(adapter);
		lv.setCacheColorHint(0);
	}

	public void clickHandler(View v) {
		mCommentsAPI.create(input.getText().toString(), id, false, mListener);
	}

	public void cancelHandler(View v) {
		finish();
	}

	private RequestListener listener = new RequestListener() {
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				CommentList comments = CommentList.parse(response);
				if (comments != null && comments.total_number > 0) {
					setupView(comments);
					progress.setVisibility(View.GONE);
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			LogUtil.e("Error", e.getMessage());
		}
	};
	/**
	 * 微博 OpenAPI 回调接口。
	 */
	private RequestListener mListener = new RequestListener() {
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(response);
					Comment comment = Comment.parse(jsonObject);
					Toast.makeText(CommentActivity.this,
							"评论: " + comment.text + "\n推送成功",
							Toast.LENGTH_SHORT).show();
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(CommentActivity.this, "评论推送失败",
							Toast.LENGTH_SHORT).show();
				}
				CommentActivity.this.finish();
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			LogUtil.e("Error", e.getMessage());
		}
	};

}

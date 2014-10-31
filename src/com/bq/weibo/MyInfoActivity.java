package com.bq.weibo;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bq.models.ErrorInfo;
import com.bq.models.User;
import com.bq.util.ReadImage;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.LogoutAPI;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.utils.LogUtil;

/**
 * 个人信息面板
 * 
 * @author bq
 */
public class MyInfoActivity extends Activity {

	private Oauth2AccessToken mAccessToken;
	private UsersAPI mUsersAPI;
	private ImageView icon;
	private TextView user_name;
	private TextView description;
	private TextView followers_count;
	private TextView friends_count;
	private TextView statuses_count;
	private TextView logout;
	private TextView token;
	private LogOutRequestListener mLogoutRequestListener = new LogOutRequestListener();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);
		icon = (ImageView) findViewById(R.id.info_icon);
		user_name = (TextView) findViewById(R.id.info_user);
		description = (TextView) findViewById(R.id.info_description);
		friends_count = (TextView) findViewById(R.id.friends_count);
		followers_count = (TextView) findViewById(R.id.followers_count);
		statuses_count = (TextView) findViewById(R.id.statuses_count);
		logout = (TextView) findViewById(R.id.info_logout);
		token = (TextView) findViewById(R.id.info_token);
		logout.setText("注销并退出");
		mAccessToken = AccessTokenKeeper.readAccessToken(this);

		if (mAccessToken.isSessionValid()) {
			updateTokenView(true);
			mUsersAPI = new UsersAPI(mAccessToken);
			long uid = Long.parseLong(mAccessToken.getUid());
			mUsersAPI.show(uid, mListener);

		}
	}

	/**
	 * 根据数据结构初始化Activity
	 */
	private void setupView(User user) {
		icon.setImageBitmap(ReadImage.getImage(user.profile_image_url));
		user_name.setText(user.screen_name);
		description.setText(user.description);
		followers_count.setText("粉丝数:" + user.followers_count);
		friends_count.setText("关注数:" + user.friends_count);
		statuses_count.setText("微博数:" + user.statuses_count);

	}

	/**
	 * 微博 OpenAPI 回调接口填充数据。
	 */
	private RequestListener mListener = new RequestListener() {
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				User user = User.parse(response);
				if (user != null) {
					setupView(user);
				} else {
					Toast.makeText(MyInfoActivity.this, response,
							Toast.LENGTH_LONG).show();
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			LogUtil.e("Error", e.getMessage());
			ErrorInfo info = ErrorInfo.parse(e.getMessage());
			Toast.makeText(MyInfoActivity.this, info.toString(),
					Toast.LENGTH_LONG).show();
		}
	};

	/** 处理点击事件 */
	public void clickHandler(View v) {
		if (mAccessToken != null && mAccessToken.isSessionValid()) {
			new LogoutAPI(mAccessToken).logout(mLogoutRequestListener);
			finish();
		} else {
			token.setText(R.string.weibosdk_demo_logout_failed_1);
		}
	}

	/**
	 * 显示当前 Token 信息。
	 * 
	 * @param hasExisted
	 *            配置文件中是否已存在 token 信息并且合法
	 */
	private void updateTokenView(boolean hasExisted) {
		String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA)
				.format(new java.util.Date(mAccessToken.getExpiresTime()));
		String format = getString(R.string.weibosdk_demo_token_to_string_format_1);
		String message = String.format(format, mAccessToken.getToken(), date);
		if (hasExisted) {
			message = getString(R.string.weibosdk_demo_token_has_existed)
					+ "\n" + message;
		}
		token.setText(message);
	}

	/**
	 * 注销按钮的监听器，接收注销处理结果。（API请求结果的监听器）
	 */
	private class LogOutRequestListener implements RequestListener {
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				try {
					JSONObject obj = new JSONObject(response);
					String value = obj.getString("result");

					if ("true".equalsIgnoreCase(value)) {
						AccessTokenKeeper.clear(MyInfoActivity.this);

						token.setText(R.string.weibosdk_demo_logout_success);
						mAccessToken = null;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			token.setText(R.string.weibosdk_demo_logout_failed);
		}
	}
}

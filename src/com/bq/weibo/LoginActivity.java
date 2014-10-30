package com.bq.weibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;

public class LoginActivity extends Activity {
	private WeiboAuth mWeiboAuth;
	private Oauth2AccessToken mAccessToken;
	Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		intent = new Intent(this, MainActivity.class);
		mWeiboAuth = new WeiboAuth(this, Constants.APP_KEY,
				Constants.REDIRECT_URL, Constants.SCOPE);
		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		if (mAccessToken.isSessionValid()) {
			finish();
			startActivity(intent);
		} else {
			Toast.makeText(this, "未获得授权", Toast.LENGTH_SHORT).show();
		}
	}

	public void clickHandler(View v) {
		mWeiboAuth.anthorize(new AuthListener());
	}

	class AuthListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			// 从 Bundle 中解析 Token
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (mAccessToken.isSessionValid()) {
				// 显示 Token
				finish();
				startActivity(intent);

				// 保存 Token 到 SharedPreferences
				AccessTokenKeeper.writeAccessToken(LoginActivity.this,
						mAccessToken);
				Toast.makeText(LoginActivity.this,
						R.string.weibosdk_demo_toast_auth_success,
						Toast.LENGTH_SHORT).show();
			} else {
				// 以下几种情况，您会收到 Code：
				// 1. 当您未在平台上注册的应用程序的包名与签名时；
				// 2. 当您注册的应用程序包名与签名不正确时；
				// 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
				String code = values.getString("code");
				String message = getString(R.string.weibosdk_demo_toast_auth_failed);
				if (!TextUtils.isEmpty(code)) {
					message = message + "\nObtained the code: " + code;
				}
				Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG)
						.show();
			}
		}

		@Override
		public void onCancel() {
			Toast.makeText(LoginActivity.this,
					R.string.weibosdk_demo_toast_auth_canceled,
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(LoginActivity.this,
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}
	}
}

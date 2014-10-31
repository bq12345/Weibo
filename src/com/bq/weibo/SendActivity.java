package com.bq.weibo;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bq.models.ErrorInfo;
import com.bq.models.Status;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.utils.LogUtil;

public class SendActivity extends Activity {
	private EditText input;
	private Oauth2AccessToken mAccessToken;
	private StatusesAPI mStatusesAPI;
	private ImageView imageView;
	private Bitmap bitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send);
		input = (EditText) findViewById(R.id.send_input);
		imageView = (ImageView) findViewById(R.id.image_view);
		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		mStatusesAPI = new StatusesAPI(mAccessToken);
	}

	public void imageHandler(View v) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Uri uri = data.getData();
			Log.e("uri", uri.toString());
			ContentResolver cr = this.getContentResolver();
			try {
				bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
				/* 将Bitmap设定到ImageView */
				imageView.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				Log.e("Exception", e.getMessage(), e);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void clickHandler(View v) {
		String content = input.getText().toString();
		if (mAccessToken != null && mAccessToken.isSessionValid()
				&& !content.equals("")) {
			if (null != bitmap) {
				mStatusesAPI.upload(content, bitmap, null, null, mListener);
			} else {
				mStatusesAPI.update(content, null, null, mListener);
			}
			input.setText("");
		} else {
			Toast.makeText(this, "推送微博失败,请稍后再试", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 微博 OpenAPI 回调接口。
	 */
	private RequestListener mListener = new RequestListener() {
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				LogUtil.i("Error", response);
				if (response.startsWith("{\"created_at\"")) {
					Status status = Status.parse(response);
					Toast.makeText(SendActivity.this,
							"微博推送成功, 微博 id = " + status.id, Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(SendActivity.this, response,
							Toast.LENGTH_LONG).show();
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			LogUtil.e("Error", e.getMessage());
			ErrorInfo info = ErrorInfo.parse(e.getMessage());
			Toast.makeText(SendActivity.this, info.toString(),
					Toast.LENGTH_LONG).show();
		}
	};

}

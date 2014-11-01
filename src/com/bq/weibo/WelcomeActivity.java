package com.bq.weibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.bq.weibo.R;

/**
 * @author bq
 * @version 1.0
 */
public class WelcomeActivity extends Activity {
	private ImageView weibo_logo;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置全屏、无标题栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.welcome);

		Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
		startService(intent);

		weibo_logo = (ImageView) findViewById(R.id.weibo_logo);

		AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
		animation.setDuration(3000);
		animation.setAnimationListener(new AnimationListener() {
			// 动画结束操作
			public void onAnimationEnd(Animation arg0) {
				Intent intent = new Intent(WelcomeActivity.this,
						LoginActivity.class);
				startActivity(intent);
				finish();
			}

			public void onAnimationRepeat(Animation arg0) {

			}

			public void onAnimationStart(Animation arg0) {

			}

		});
		weibo_logo.setAnimation(animation);
	}
}

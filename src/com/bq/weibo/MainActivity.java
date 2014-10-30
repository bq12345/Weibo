package com.bq.weibo;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements
		OnCheckedChangeListener {
	private RadioGroup mainTab;
	private TabHost tabhost;
	private Intent iHome;
	private Intent iInfo;
	private Intent iMore;
	private Oauth2AccessToken mAccessToken;
	Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		intent = new Intent(this, LoginActivity.class);
		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		if (!mAccessToken.isSessionValid()) {
			finish();
			startActivity(intent);
		}
		mainTab = (RadioGroup) findViewById(R.id.main_tab);
		mainTab.setOnCheckedChangeListener(this);
		tabhost = getTabHost();

		iHome = new Intent(this, HomeActivity.class);
		tabhost.addTab(tabhost
				.newTabSpec("iHome")
				.setIndicator(getResources().getString(R.string.main_home),
						getResources().getDrawable(R.drawable.icon_1_n))
				.setContent(iHome));

		iInfo = new Intent(this, MyInfoActivity.class);
		tabhost.addTab(tabhost
				.newTabSpec("iInfo")
				.setIndicator(getResources().getString(R.string.main_my_info),
						getResources().getDrawable(R.drawable.icon_3_n))
				.setContent(iInfo));

		iMore = new Intent(this, SendActivity.class);
		tabhost.addTab(tabhost
				.newTabSpec("iMore")
				.setIndicator(getResources().getString(R.string.send),
						getResources().getDrawable(R.drawable.icon_5_n))
				.setContent(iMore));
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radio_button0:
			this.tabhost.setCurrentTabByTag("iHome");
			break;
		case R.id.radio_button2:
			this.tabhost.setCurrentTabByTag("iInfo");
			break;
		case R.id.radio_button4:
			this.tabhost.setCurrentTabByTag("iMore");
			break;
		}
	}

}

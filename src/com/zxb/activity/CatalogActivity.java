package com.zxb.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.zxb.R;
import com.zxb.util.FileUtil;
import com.zxb.view.TabView;

// 主界面
@SuppressWarnings("deprecation")
public class CatalogActivity extends TabActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_catalog);// 设置TabHost使用的布局文件

		TabHost tabHost = getTabHost();

		TabView view = null;

		// 刷卡
		view = new TabView(this, R.drawable.icon_n_0, R.drawable.icon_s_0);
		view.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.icon_s_0));

		TabSpec recentContactSpec = tabHost.newTabSpec("RecentContact");
		recentContactSpec.setIndicator(view);
		Intent recentContactIntent = new Intent(this, InputMoneyActivity.class);
		recentContactSpec.setContent(recentContactIntent);

		// 流水
		view = new TabView(this, R.drawable.icon_n_1, R.drawable.icon_s_1);
		view.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.icon_s_1));

		TabSpec contactBookSpec = tabHost.newTabSpec("ContactBook");
		contactBookSpec.setIndicator(view);
		Intent contactBookIntent = new Intent(this, TransferListActivity.class);
		contactBookSpec.setContent(contactBookIntent);

		// 商户
		view = new TabView(this, R.drawable.icon_n_2, R.drawable.icon_s_2);
		view.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.icon_s_2));

		TabSpec smsMessageSpec = tabHost.newTabSpec("SmsMessage");
		smsMessageSpec.setIndicator(view);
		Intent smsMessageIntent = new Intent(this, MerchantActivity.class);
		smsMessageSpec.setContent(smsMessageIntent);

		// 工具
		view = new TabView(this, R.drawable.icon_n_3, R.drawable.icon_s_3);
		view.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.icon_s_3));

		TabSpec settingSpec = tabHost.newTabSpec("Setting");
		settingSpec.setIndicator(view);
		Intent settingIntent = new Intent(this, ToolsActivity.class);
		settingSpec.setContent(settingIntent);

		tabHost.addTab(recentContactSpec);
		tabHost.addTab(contactBookSpec);
		tabHost.addTab(smsMessageSpec);
		tabHost.addTab(settingSpec);

		tabHost.setCurrentTab(0);

		new DeleteFileTask().execute();
	}

	// 删除缓存的附件
	class DeleteFileTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			try {
				FileUtil.deleteFiles();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	
}
package com.zxb.activity;

import com.zxb.R;
import com.zxb.client.ApplicationEnvironment;
import com.zxb.client.Constants;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;


public class RateInstructionActivity extends BaseActivity implements OnClickListener {
	private WebView mWebView;
	private Handler mHandler = new Handler();

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rateinstruction);

		Button btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		mWebView = (WebView) this.findViewById(R.id.webview);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(new Object() {
			@SuppressWarnings("unused")
			public void clickOnAndroid() {
				mHandler.post(new Runnable() {
					public void run() {
						mWebView.loadUrl("javascript:wave()");
					}
				});
			}
		}, "demo");

		String url = Constants.ip+"300134.tran?EPOSFLG=1&PHONENUMBER=" + ApplicationEnvironment.getInstance().getPreferences(this).getString(Constants.kUSERNAME, "");
		mWebView.loadUrl(url);

	}

	protected void homeAction() {
		finish();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_back:
			this.finish();
			break;

		default:
			break;
		}

	}

}

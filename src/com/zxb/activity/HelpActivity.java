package com.zxb.activity;


import com.zxb.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

public class HelpActivity extends BaseActivity implements OnClickListener {
	private WebView mWebView;
    private Handler mHandler = new Handler();       
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		Button btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		mWebView = (WebView)this.findViewById(R.id.webview);
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
        
        mWebView.loadUrl("file:///android_asset/help.html");       
		
	}
	
	protected void homeAction(){
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

package com.zxb.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.zxb.R;

// 关于系统
public class AboutSystemActivity extends BaseActivity implements OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_system);

		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);

		Button btn_access = (Button) this.findViewById(R.id.btn_access);
		btn_access.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_access:
			Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://www.people2000.net/"));
			startActivity(viewIntent);
			break;
		default:
			break;
		}

	}

}

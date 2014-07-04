package com.zxb.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.zxb.R;

public class TradeSuccessActivity extends BaseActivity implements OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_success);

		TextView tv_tips = (TextView) findViewById(R.id.tv_tips);
		String tips = this.getIntent().getStringExtra("tips");
		if(tips != null){
			tv_tips.setText(tips);
		}
		Button btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			TradeSuccessActivity.this.setResult(RESULT_OK);
			this.finish();
			
			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		TradeSuccessActivity.this.setResult(RESULT_OK);
		this.finish();
	}
	
	

}

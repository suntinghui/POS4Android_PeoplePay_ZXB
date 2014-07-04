package com.zxb.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.zxb.R;

public class TradeFaiureActivity extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_failure);

		Button btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
		
		TextView messageText = (TextView) this.findViewById(R.id.tv_msg);
		messageText.setText(this.getIntent().getStringExtra("MESSAGE"));
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			TradeFaiureActivity.this.setResult(RESULT_CANCELED);
			this.finish();
			
			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		TradeFaiureActivity.this.setResult(RESULT_CANCELED);
		this.finish();
	}

}

package com.zxb.activity;

import java.util.HashMap;

import com.zxb.R;
import com.zxb.client.ApplicationEnvironment;
import com.zxb.client.Constants;
import com.zxb.client.TransferRequestTag;
import com.zxb.network.LKAsyncHttpResponseHandler;
import com.zxb.network.LKHttpRequest;
import com.zxb.network.LKHttpRequestQueue;
import com.zxb.network.LKHttpRequestQueueDone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// 提现
public class WithdrawalCashActivity extends BaseActivity implements OnClickListener {
	String PAYAMT;
	String PAYTYPE;
	String PAYDATE;
	TextView tv_amount;
	EditText et_pwd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_withdrawal_cash);

		PAYAMT = this.getIntent().getStringExtra("PAYAMT");
		PAYTYPE = this.getIntent().getStringExtra("PAYTYPE");
		PAYDATE = this.getIntent().getStringExtra("PAYDATE");
		
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		if(PAYTYPE.equals("1")){
			tv_title.setText("快速提现");
		}else{
			tv_title.setText("普通提现");
		}
		
		Button btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		Button btn_confirm = (Button) findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);

		tv_amount = (TextView) findViewById(R.id.tv_amount);
		tv_amount.setText("￥" + PAYAMT);
		et_pwd = (EditText) findViewById(R.id.et_pwd);

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_confirm:
			if (checkValue()) {
				drawMoneyAction();
			}

			break;
		default:
			break;
		}

	}

	private Boolean checkValue() {
		if (et_pwd.getText().length() == 0) {
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	// 提现
	private void drawMoneyAction() {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("TRANCODE", "199025");
		tempMap.put("PHONENUMBER", ApplicationEnvironment.getInstance().getPreferences(this).getString(Constants.kUSERNAME, ""));
		tempMap.put("PAYAMT", PAYAMT);
		tempMap.put("PAYTYPE", PAYTYPE); // 1 快速提现 2 普通提现
		tempMap.put("PASSWORD", et_pwd.getText().toString());
		tempMap.put("PAYDATE", PAYDATE);
		LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.DrawMoney, tempMap, getDrawMoneyHandler());

		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue("正在获取数据请稍候...", new LKHttpRequestQueueDone() {

			@Override
			public void onComplete() {
				super.onComplete();

			}
		});
	}

	private LKAsyncHttpResponseHandler getDrawMoneyHandler() {
		return new LKAsyncHttpResponseHandler() {

			@Override
			public void successAction(Object obj) {
				HashMap<String, String> map = (HashMap<String, String>) obj;
				if (map.get("RSPCOD") != null && map.get("RSPCOD").equals("00")) {
					Intent intent = new Intent(WithdrawalCashActivity.this, TradeSuccessActivity.class);
					intent.putExtra("tips", "提现成功");
					startActivityForResult(intent, 101);

				} else {
					Toast.makeText(WithdrawalCashActivity.this, map.get("RSPMSG"), Toast.LENGTH_SHORT).show();
				}
			}

		};
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			this.setResult(RESULT_OK);
			this.finish();
		}
	}
}

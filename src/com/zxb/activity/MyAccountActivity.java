package com.zxb.activity;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zxb.R;
import com.zxb.client.ApplicationEnvironment;
import com.zxb.client.Constants;
import com.zxb.client.TransferRequestTag;
import com.zxb.network.LKAsyncHttpResponseHandler;
import com.zxb.network.LKHttpRequest;
import com.zxb.network.LKHttpRequestQueue;
import com.zxb.network.LKHttpRequestQueueDone;
import com.zxb.util.DateUtil;

// 我的账户
public class MyAccountActivity extends BaseActivity implements OnClickListener {
	TextView tv_balance;
	EditText et_amount;
	String amount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_account);

		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);

		Button btn_nomal = (Button) this.findViewById(R.id.btn_nomal);
		btn_nomal.setOnClickListener(this);
		Button btn_fast = (Button) this.findViewById(R.id.btn_fast);
		btn_fast.setOnClickListener(this);

		tv_balance = (TextView) this.findViewById(R.id.tv_balance);
		et_amount = (EditText) findViewById(R.id.et_amount);

		myAccount();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_nomal:
			if (checkValue()) {

				String PAYDATE = DateUtil.getSystemDate2();

				Intent intent_n = new Intent(MyAccountActivity.this, WithdrawalCashActivity.class);
				intent_n.putExtra("PAYAMT", et_amount.getText().toString());
				intent_n.putExtra("PAYTYPE", "2");
				intent_n.putExtra("PAYDATE", PAYDATE);
				startActivity(intent_n);
			}

			break;
		case R.id.btn_fast:
			if (checkValue()) {
				String PAYDATE = DateUtil.getSystemDate2();

				Intent intent_n = new Intent(MyAccountActivity.this, WithdrawalCashActivity.class);
				intent_n.putExtra("PAYAMT", et_amount.getText().toString());
				intent_n.putExtra("PAYTYPE", "1");
				intent_n.putExtra("PAYDATE", PAYDATE);
				startActivity(intent_n);
			}

			break;
		
		case R.id.btn_back:
			this.finish();
			break;
		default:
			break;
		}

	}

	private Boolean checkValue() {
		if (et_amount.getText().length() == 0) {
			Toast.makeText(this, "提现金额不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(amount == null || "".equals(amount) || Double.valueOf(amount) == 0.00){
			Toast.makeText(this, "账户余额不足", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			this.finish();
		}

	}

	// 我的账户
	private void myAccount() {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("TRANCODE", "199026");
		tempMap.put("PHONENUMBER", ApplicationEnvironment.getInstance().getPreferences(this).getString(Constants.kUSERNAME, ""));
		LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.MyAccount, tempMap, getMyAccountHandler());

		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue("正在获取数据请稍候...", new LKHttpRequestQueueDone() {

			@Override
			public void onComplete() {
				super.onComplete();

			}
		});
	}

	private LKAsyncHttpResponseHandler getMyAccountHandler() {
		return new LKAsyncHttpResponseHandler() {

			@Override
			public void successAction(Object obj) {
				HashMap<String, String> map = (HashMap<String, String>) obj;
				if (map.get("RSPCOD") != null && map.get("RSPCOD").equals("00")) {
					amount =  map.get("CASHACBAL");
					tv_balance.setText("￥" + map.get("CASHACBAL"));

				} else {
					Toast.makeText(MyAccountActivity.this, map.get("RSPMSG"), Toast.LENGTH_SHORT).show();
				}
			}

		};
	}
}

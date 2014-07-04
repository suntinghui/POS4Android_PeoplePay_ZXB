package com.zxb.activity;

import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zxb.R;
import com.zxb.client.TransferRequestTag;
import com.zxb.network.LKAsyncHttpResponseHandler;
import com.zxb.network.LKHttpRequest;
import com.zxb.network.LKHttpRequestQueue;
import com.zxb.network.LKHttpRequestQueueDone;

// 设置密码
public class SetPwdActivity extends BaseActivity implements OnClickListener {
	private EditText et_new_pwd;
	private EditText et_confirm_pwd;
	private String PHONENUMBER = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_pwd);

		PHONENUMBER = this.getIntent().getStringExtra("PHONENUMBER");

		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);

		Button btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);

		et_new_pwd = (EditText) findViewById(R.id.et_new_pwd);
		et_confirm_pwd = (EditText) findViewById(R.id.et_confirm_pwd);

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_confirm:
			if (checkValue()) {
				setAction();
			}
			break;
		default:
			break;
		}

	}

	private Boolean checkValue() {

		if (et_new_pwd.getText().toString().length() == 0) {
			Toast.makeText(this, "新密码不能为空", 2).show();
			return false;
		}
		if (et_confirm_pwd.getText().toString().length() == 0) {
			Toast.makeText(this, "确认密码不能为空", 2).show();
			return false;
		}

		if (!et_new_pwd.getText().toString()
				.equals(et_confirm_pwd.getText().toString())) {
			Toast.makeText(this, "密码输入不一致，请重新输入", 2).show();
			return false;
		}

		return true;
	}

	// 忘记密码
	private void setAction() {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("TRANCODE", "199004");
		tempMap.put("PHONENUMBER ", PHONENUMBER);
		tempMap.put("PASSWORDNEW ", et_confirm_pwd.getText().toString());

		LKHttpRequest req1 = new LKHttpRequest(
				TransferRequestTag.ForgetLoginPwd, tempMap, forgetPwdHandler());

		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue("正在提交...",
				new LKHttpRequestQueueDone() {

					@Override
					public void onComplete() {
						super.onComplete();

					}

				});
	}

	private LKAsyncHttpResponseHandler forgetPwdHandler() {
		return new LKAsyncHttpResponseHandler() {

			@SuppressWarnings("rawtypes")
			@Override
			public void successAction(Object obj) {
				if (obj instanceof HashMap) {
					if (((HashMap) obj).get("RSPCOD").toString()
							.equals("00")) {
						Toast.makeText(getApplicationContext(),
								((HashMap) obj).get("RSPMSG").toString(),
								Toast.LENGTH_SHORT).show();
						
						SetPwdActivity.this.setResult(5);
						SetPwdActivity.this.finish();
						
					} else if (((HashMap) obj).get("RSPMSG").toString() != null
							&& ((HashMap) obj).get("RSPMSG").toString()
									.length() != 0) {
						Toast.makeText(getApplicationContext(),
								((HashMap) obj).get("RSPMSG").toString(),
								Toast.LENGTH_SHORT).show();
					}
				} else {
				}

			}

		};
	}
}

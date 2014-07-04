package com.zxb.activity;

import java.util.HashMap;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zxb.R;
import com.zxb.client.ApplicationEnvironment;
import com.zxb.client.Constants;
import com.zxb.client.TransferRequestTag;
import com.zxb.network.LKAsyncHttpResponseHandler;
import com.zxb.network.LKHttpRequest;
import com.zxb.network.LKHttpRequestQueue;
import com.zxb.network.LKHttpRequestQueueDone;
import com.zxb.view.LKAlertDialog;

// 忘记密码
public class ForgetPwdActivity extends BaseActivity implements OnClickListener {
	EditText et_phone;
	EditText et_security_code;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_pwd);

		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		Button btn_next = (Button) this.findViewById(R.id.btn_next);
		btn_next.setOnClickListener(this);
		Button btn_securitycode = (Button) findViewById(R.id.btn_securitycode);
		btn_securitycode.setOnClickListener(this);
		et_phone = (EditText) findViewById(R.id.et_account);
		et_security_code = (EditText) findViewById(R.id.et_securitycode);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_next:
			if (checkValue()) {
				checkSMS();
				
			}

			break;
		case R.id.btn_securitycode:
			if (et_phone.getText().toString().length() == 0) {
				Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
			} else if (et_phone.getText().toString().length() != 11) {
				Toast.makeText(this, "手机号不合法", Toast.LENGTH_SHORT).show();
			} else {
				sendSMS();
			}

			break;
		case R.id.btn_back:
			this.finish();
			break;
		default:
			break;
		}

	}

	// 发送短信
	private void sendSMS() {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("TRANCODE", "199018");
		tempMap.put("PHONENUMBER", et_phone.getText().toString().trim());
		tempMap.put("TOPHONENUMBER ", et_phone.getText().toString().trim());
		tempMap.put("TYPE", "100002"); // 100001－注册 100002－忘记密码

		LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.SmsSend,
				tempMap, sendSMSHandler());

		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue(
				"正在获取短信验证码...", new LKHttpRequestQueueDone() {

					@Override
					public void onComplete() {
						super.onComplete();

					}

				});
	}

	private LKAsyncHttpResponseHandler sendSMSHandler() {
		return new LKAsyncHttpResponseHandler() {

			@SuppressWarnings("rawtypes")
			@Override
			public void successAction(Object obj) {
				if (obj instanceof HashMap) {
					if (((HashMap) obj).get("RSPCOD").toString().equals("00")) {
						Toast.makeText(getApplicationContext(),
								"短信发送成功，请注意查收！", Toast.LENGTH_SHORT).show();
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

	// 短信码验证
		private void checkSMS() {
			HashMap<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("TRANCODE", "199019");
			tempMap.put("PHONENUMBER", et_phone.getText().toString().trim());
			tempMap.put("CHECKCODE", et_security_code.getText().toString()); // 100001－注册 100002－忘记密码

			LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.SmsCheck,
					tempMap, checkSMSHandler());

			new LKHttpRequestQueue().addHttpRequest(req1).executeQueue(
					"正在验证短信验证码...", new LKHttpRequestQueueDone() {

						@Override
						public void onComplete() {
							super.onComplete();

						}

					});
		}

		private LKAsyncHttpResponseHandler checkSMSHandler() {
			return new LKAsyncHttpResponseHandler() {

				@SuppressWarnings("rawtypes")
				@Override
				public void successAction(Object obj) {
					if (obj instanceof HashMap) {
						if (((HashMap) obj).get("RSPCOD").toString().equals("00")) {
							Intent intent = new Intent(ForgetPwdActivity.this, SetPwdActivity.class);
							intent.putExtra("PHONENUMBER", et_phone.getText().toString());
							startActivityForResult(intent, 0);

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
		
	public Boolean checkValue() {

		if (et_phone.getText().length() == 0) {
			Toast.makeText(this, "请输入手机号!", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (et_security_code.getText().length() == 0) {
			Toast.makeText(this, "请输入验证码!", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 5){
			this.finish();
		}
		
	}
	
	
}

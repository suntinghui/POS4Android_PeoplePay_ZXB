package com.zxb.activity;

import java.util.HashMap;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zxb.R;
import com.zxb.client.ApplicationEnvironment;
import com.zxb.client.TransferRequestTag;
import com.zxb.network.LKAsyncHttpResponseHandler;
import com.zxb.network.LKHttpRequest;
import com.zxb.network.LKHttpRequestQueue;
import com.zxb.network.LKHttpRequestQueueDone;
import com.zxb.view.LKAlertDialog;

// 注册
public class RegisterActivity extends BaseActivity implements OnClickListener {
	EditText et_phone;
	EditText et_security_code;
	EditText et_pwd;
	EditText et_pwd_confirm;
	private Boolean isSelected = false;
	private Button btn_select;
	private LKAlertDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		Button btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
		Button btn_securitycode = (Button) findViewById(R.id.btn_securitycode);
		btn_securitycode.setOnClickListener(this);
		Button btn_deal = (Button) findViewById(R.id.btn_deal);
		btn_deal.setOnClickListener(this);
		btn_select = (Button) findViewById(R.id.btn_select);
		btn_select.setOnClickListener(this);
		et_phone = (EditText) findViewById(R.id.et_account);
		et_security_code = (EditText) findViewById(R.id.et_securitycode);
		et_pwd = (EditText) findViewById(R.id.et_pwd);
		et_pwd_confirm = (EditText) findViewById(R.id.et_pwd_confirm);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
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
		case R.id.btn_select:
			isSelected = !isSelected;
			if (isSelected) {
				btn_select.setBackgroundResource(R.drawable.select_button_s);
			} else {
				btn_select.setBackgroundResource(R.drawable.select_button_n);
			}
			
			break;
		case R.id.btn_deal:
			dealAction();
			break;
		default:
			break;
		}

	}

	private void dealAction() {
		dialog = new LKAlertDialog(this);
		dialog.setTitle("服务协议");
		dialog.setMessage(ApplicationEnvironment.getInstance().getApplication().getResources().getString(R.string.deal));
		dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();

			}
		});
		dialog.create().show();
		dialog.mSetCancelable(true);
	}

	// 发送短信
	private void sendSMS() {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("TRANCODE", "199018");
		tempMap.put("PHONENUMBER", et_phone.getText().toString().trim());
		tempMap.put("TOPHONENUMBER ", et_phone.getText().toString().trim());
		tempMap.put("TYPE", "100002"); // 100001－注册 100002－忘记密码

		LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.SmsSend, tempMap, sendSMSHandler());

		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue("正在获取短信验证码...", new LKHttpRequestQueueDone() {

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
						Toast.makeText(getApplicationContext(), "短信发送成功，请注意查收！", Toast.LENGTH_SHORT).show();
					} else if (((HashMap) obj).get("RSPMSG").toString() != null && ((HashMap) obj).get("RSPMSG").toString().length() != 0) {
						Toast.makeText(getApplicationContext(), ((HashMap) obj).get("RSPMSG").toString(), Toast.LENGTH_SHORT).show();
					}
				} else {
				}

			}

		};
	}

	// 注册
	private void registerAction() {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("TRANCODE", "199001");
		tempMap.put("PHONENUMBER", et_phone.getText().toString());
		tempMap.put("PASSWORD", et_pwd.getText().toString());
		tempMap.put("CPASSWORD", et_pwd_confirm.getText().toString());
		tempMap.put("MSCODE", et_security_code.getText().toString());

		LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.Register, tempMap, getRegisterHandler());

		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue("正在注册，请稍候...", new LKHttpRequestQueueDone() {

			@Override
			public void onComplete() {
				super.onComplete();

			}
		});
	}

	private LKAsyncHttpResponseHandler getRegisterHandler() {
		return new LKAsyncHttpResponseHandler() {

			@Override
			public void successAction(Object obj) {
				if (obj instanceof HashMap) {
					if (((HashMap) obj).get("RSPCOD").toString().equals("00")) {
						
						Toast.makeText(getApplicationContext(), ((HashMap) obj).get("RSPMSG").toString(), Toast.LENGTH_SHORT).show();
					} else if (((HashMap) obj).get("RSPMSG").toString() != null && ((HashMap) obj).get("RSPMSG").toString().length() != 0) {
						Toast.makeText(getApplicationContext(), ((HashMap) obj).get("RSPMSG").toString(), Toast.LENGTH_SHORT).show();
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

		if (!isSelected) {
			Toast.makeText(this, "请勾选同意用户注册协议", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (et_pwd.length() == 0) {
			Toast.makeText(this, "请输入登录密码!", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (et_pwd_confirm.length() == 0) {
			Toast.makeText(this, "请输入确认密码!", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (!et_pwd_confirm.getText().toString().equals(et_pwd.getText().toString())) {
			Toast.makeText(this, "密码两次输入不正确!", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	// 短信码验证
	private void checkSMS() {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("TRANCODE", "199019");
		tempMap.put("PHONENUMBER", et_phone.getText().toString().trim());
		tempMap.put("CHECKCODE", et_security_code.getText().toString()); // 100001－注册
																			// 100002－忘记密码

		LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.SmsCheck, tempMap, checkSMSHandler());

		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue("正在验证短信验证码...", new LKHttpRequestQueueDone() {

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
						registerAction();

					} else if (((HashMap) obj).get("RSPMSG").toString() != null && ((HashMap) obj).get("RSPMSG").toString().length() != 0) {
						Toast.makeText(getApplicationContext(), ((HashMap) obj).get("RSPMSG").toString(), Toast.LENGTH_SHORT).show();
					}
				} else {
				}

			}

		};
	}

}

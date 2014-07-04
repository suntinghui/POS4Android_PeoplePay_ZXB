package com.zxb.activity;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.zxb.R;
import com.zxb.client.TransferRequestTag;
import com.zxb.network.LKAsyncHttpResponseHandler;
import com.zxb.network.LKHttpRequest;
import com.zxb.network.LKHttpRequestQueue;
import com.zxb.network.LKHttpRequestQueueDone;

public class ConsumeSuccessActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener {

	private CheckBox checkBox = null;
	private EditText phoneNumEdit = null;
	private Button btn_confirm = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_consume_success);

		checkBox = (CheckBox) this.findViewById(R.id.checkBox);
		checkBox.setOnCheckedChangeListener(this);

		phoneNumEdit = (EditText) this.findViewById(R.id.phoneText);

		btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			if (checkBox.isChecked()) {
				String phoneNum = phoneNumEdit.getText().toString();
				if (phoneNum.equals("")) {
					Toast.makeText(ConsumeSuccessActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
					return;
				} else if (phoneNum.length() != 11) {
					Toast.makeText(ConsumeSuccessActivity.this, "手机号不合法", Toast.LENGTH_SHORT).show();
					return;
				} else {
					this.sendTicket();
				}

			} else {
				ConsumeSuccessActivity.this.setResult(RESULT_OK);
				this.finish();
			}

			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
	}

	@Override
	public void onCheckedChanged(CompoundButton button, boolean flag) {
		switch (button.getId()) {
		case R.id.checkBox:
			phoneNumEdit.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
			btn_confirm.setText(flag?"发送小票":"完    成");
			break;
		}
	}
	
	private void sendTicket(){
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("TRANCODE", "199037");
		tempMap.put("PHONENUMBER", phoneNumEdit.getText().toString().trim());
		tempMap.put("LOGNO", getIntent().getStringExtra("LOGNO"));

		LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.SendTicket, tempMap, getTicketHandler());

		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue("正在发送小票请稍候...", new LKHttpRequestQueueDone() {

			@Override
			public void onComplete() {
				super.onComplete();
			}
		});
	}
	
	private LKAsyncHttpResponseHandler getTicketHandler() {
		return new LKAsyncHttpResponseHandler() {

			@Override
			public void successAction(Object obj) {
				HashMap<String, String> map = (HashMap<String, String>) obj;
				String respCode = map.get("RSPCOD");
				if (respCode.equals("00")){
					Toast.makeText(ConsumeSuccessActivity.this, "交易小票发送成功，请查收", Toast.LENGTH_SHORT).show();
					ConsumeSuccessActivity.this.setResult(RESULT_OK);
					ConsumeSuccessActivity.this.finish();
					
				} else {
					Toast.makeText(ConsumeSuccessActivity.this, map.get("RSPMSG"), Toast.LENGTH_SHORT).show();
				}
			}
		};
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
//		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK){
			this.setResult(RESULT_OK);
			this.finish();
		}
	}

}

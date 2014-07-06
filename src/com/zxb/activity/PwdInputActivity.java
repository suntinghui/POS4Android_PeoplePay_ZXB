package com.zxb.activity;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.fncat.xswipe.controller.ErrorCode;
import com.zxb.R;
import com.zxb.client.AppDataCenter;
import com.zxb.client.TransferRequestTag;
import com.zxb.network.LKAsyncHttpResponseHandler;
import com.zxb.network.LKHttpRequest;
import com.zxb.network.LKHttpRequestQueue;
import com.zxb.network.LKHttpRequestQueueDone;
import com.zxb.qpos.ThreadCalcMac;
import com.zxb.qpos.ThreadEncPwd;
import com.zxb.util.ByteUtil;
import com.zxb.view.PasswordWithLabelView;

public class PwdInputActivity extends BaseActivity implements OnClickListener {
	
	private PasswordWithLabelView et_pwd = null;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pwd_input);
		
		intent = this.getIntent();

		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		Button btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);

		et_pwd = (PasswordWithLabelView) this.findViewById(R.id.et_pwd);
		et_pwd.setHintWithLabel("密码", "请输入密码");

	}

	private boolean checkValue() {
		if (et_pwd.getText().length() != 6) {
			Toast.makeText(this, "密码应为6位", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}
	
	private void gotoTradeFailureActivity(String msg) {
		if (msg == null || msg.trim().equals("")) {
			msg = "交易失败";
		}

		Intent intent = new Intent(this, TradeFaiureActivity.class);
		intent.putExtra("MESSAGE", msg);
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		this.finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			this.setResult(RESULT_OK);
			this.finish();

			break;
			
		case R.id.btn_confirm:
			if (checkValue()) {
				try {
					encPwd();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			break;

		default:
			break;
		}

	}
	
	private void encPwd(){
		new ThreadEncPwd(encPwdHandler, this, et_pwd.getPwd()).start();
	}
	
	private Handler encPwdHandler =  new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ErrorCode.SUCCESS:
				new ThreadCalcMac(calcMacHandler, PwdInputActivity.this, getExtraString()).start();;
				break;
			}
		}
	};
	
	private String getExtraString() {
		StringBuffer sb = new StringBuffer();
		sb.append(intent.getStringExtra("TRANCODE"));
		sb.append(intent.getStringExtra("CTXNAT"));
		sb.append(intent.getStringExtra("TSEQNO"));
		sb.append(intent.getStringExtra("TTXNTM"));
		sb.append(intent.getStringExtra("TTXNDT"));
		sb.append(intent.getStringExtra("PID")); 

		return sb.toString();
	}
	
	private Handler calcMacHandler =  new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ErrorCode.SUCCESS:
				transfer();
				break;
			}
		}
	};

	private void transfer() {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("TRANCODE", intent.getStringExtra("TRANCODE"));
		tempMap.put("PHONENUMBER", intent.getStringExtra("PHONENUMBER")); // 手机号
		tempMap.put("TERMINALNUMBER", intent.getStringExtra("TID")); // 终端号
		tempMap.put("PCSIM", intent.getStringExtra("PCSIM"));
		tempMap.put("TRACK", ByteUtil.byte2hex(AppDataCenter.encTrack));
		tempMap.put("TSEQNO", intent.getStringExtra("TSEQNO")); // 终端流水号
		tempMap.put("CTXNAT", intent.getStringExtra("CTXNAT")); // 消费金额
		tempMap.put("TPINBLK", AppDataCenter.PIN_Block[0]); // 支付密码
		tempMap.put("CRDNO", intent.getStringExtra("CRDNO")); // 卡号
		tempMap.put("CHECKX", intent.getStringExtra("CHECKX")); // 横坐标
		tempMap.put("CHECKY", intent.getStringExtra("CHECKY")); // 纵坐标
		tempMap.put("TTXNTM", intent.getStringExtra("TTXNTM")); // 交易时间
		tempMap.put("TTXNDT", intent.getStringExtra("TTXNDT")); // 交易日期
		tempMap.put("PSAMCARDNO", intent.getStringExtra("PID")); // PSAM卡号 "UN201410000046"
		tempMap.put("MAC", AppDataCenter.MAC[0].substring(0, 8)); // MAC

		LKHttpRequest req = new LKHttpRequest(TransferRequestTag.Consume, tempMap, transferHandler());

		new LKHttpRequestQueue().addHttpRequest(req).executeQueue("正在交易请稍候...", new LKHttpRequestQueueDone() {

			@Override
			public void onComplete() {
				super.onComplete();

			}

		});
	}

	private LKAsyncHttpResponseHandler transferHandler() {
		return new LKAsyncHttpResponseHandler() {

			@Override
			public void successAction(Object obj) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) obj;

				if (map.get("RSPCOD").equals("00")) {
					Intent intent0 = new Intent(PwdInputActivity.this, HandSignActivity.class);
					intent0.putExtra("AMOUNT", intent.getStringExtra("CTXNAT"));
					intent0.putExtra("LOGNO", map.get("LOGNO"));
					startActivityForResult(intent0, 0);

				} else {
					gotoTradeFailureActivity(map.get("RSPMSG"));
				}

			}

		};
	}


}

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
import com.zxb.util.StringUtil;
import com.zxb.view.PasswordWithLabelView;

public class PwdInputActivity extends BaseActivity implements OnClickListener {

	private PasswordWithLabelView et_pwd = null;
	private Intent intent;
	private int type = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pwd_input);

		intent = this.getIntent();
		type = intent.getIntExtra("TYPE", 0);

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

	private void encPwd() {
		new ThreadEncPwd(encPwdHandler, this, et_pwd.getPwd()).start();
	}

	private Handler encPwdHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ErrorCode.SUCCESS:
				String extraStr = "";
				
				if (type == TransferRequestTag.Consume) {
					extraStr = getExtraStringCunsum();
				} else if (type == TransferRequestTag.PhoneRecharge) {
					extraStr = getExtraStringPhone();
				} else if (type == TransferRequestTag.CardCard) {
					extraStr = getExtraStringCard();
				} else if (type == TransferRequestTag.CreditCard) {
					extraStr = getExtraStringCredit();
				}
				new ThreadCalcMac(calcMacHandler, PwdInputActivity.this, extraStr).start();
				break;
			}
		}
	};

	// 消费
	private String getExtraStringCunsum() {
		StringBuffer sb = new StringBuffer();
		sb.append(intent.getStringExtra("TRANCODE"));
		sb.append(intent.getStringExtra("CTXNAT"));
		sb.append(intent.getStringExtra("TSEQNO"));
		sb.append(intent.getStringExtra("TTXNTM"));
		sb.append(intent.getStringExtra("TTXNDT"));
		sb.append(intent.getStringExtra("PID"));

		return sb.toString();
	}

	// 手机充值
	private String getExtraStringPhone() {
		StringBuffer sb = new StringBuffer();
		sb.append(intent.getStringExtra("TRANCODE"));
		sb.append(intent.getStringExtra("TXNAMT_B"));
		sb.append(intent.getStringExtra("TSeqNo_B"));
		sb.append(intent.getStringExtra("TTxnTm_B"));
		sb.append(intent.getStringExtra("TTxnDt_B"));
		sb.append(intent.getStringExtra("PID"));

		return sb.toString();
	}

	// 卡卡转账
	private String getExtraStringCard() {
		StringBuffer sb = new StringBuffer();
		sb.append(intent.getStringExtra("TRANCODE"));
		sb.append(intent.getStringExtra("TXNAMT_B"));
		sb.append(intent.getStringExtra("TSeqNo_B"));
		sb.append(intent.getStringExtra("TTxnTm_B"));
		sb.append(intent.getStringExtra("TTxnDt_B"));
		sb.append(intent.getStringExtra("PID"));

		return sb.toString();
	}

	// 信用卡还款
	private String getExtraStringCredit() {
		StringBuffer sb = new StringBuffer();
		sb.append(intent.getStringExtra("TRANCODE"));
		sb.append(intent.getStringExtra("TXNAMT_B"));
		sb.append(intent.getStringExtra("TSeqNo_B"));
		sb.append(intent.getStringExtra("TTxnTm_B"));
		sb.append(intent.getStringExtra("TTxnDt_B"));
		sb.append(intent.getStringExtra("PID"));

		return sb.toString();
	}

	private Handler calcMacHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ErrorCode.SUCCESS:
				if (type == TransferRequestTag.Consume) {
					transferConsum();
				} else if (type == TransferRequestTag.PhoneRecharge) {
					transferPhone();
				} else if (type == TransferRequestTag.CardCard) {
					transferCard();
				} else if (type == TransferRequestTag.CreditCard) {
					transferCredit();
				}
				
				break;
			}
		}
	};

	//消费
	private void transferConsum() {
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
		tempMap.put("PSAMCARDNO", intent.getStringExtra("PID")); // PSAM卡号
																	// "UN201410000046"
		tempMap.put("MAC", AppDataCenter.MAC[0].substring(0, 8)); // MAC

		LKHttpRequest req = new LKHttpRequest(TransferRequestTag.Consume, tempMap, transferHandlerConsum());

		new LKHttpRequestQueue().addHttpRequest(req).executeQueue("正在交易请稍候...", new LKHttpRequestQueueDone() {

			@Override
			public void onComplete() {
				super.onComplete();

			}

		});
	}

	private LKAsyncHttpResponseHandler transferHandlerConsum() {
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
	
	//手机充值
	private void transferPhone() {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("TRANCODE", intent.getStringExtra("TRANCODE"));
		tempMap.put("SELLTEL_B", intent.getStringExtra("SELLTEL_B")); // 消费撤销唯一凭证
		
		tempMap.put("phoneNumber_B", intent.getStringExtra("phoneNumber_B"));// 接收信息手机号
		tempMap.put("Track2_B", ByteUtil.byte2hex(AppDataCenter.encTrack));
		tempMap.put("CRDNOJLN_B", AppDataCenter.PIN_Block[0]); // 支付密码
		tempMap.put("TXNAMT_B", intent.getStringExtra("TXNAMT_B"));
		tempMap.put("TSeqNo_B", intent.getStringExtra("TSeqNo_B"));
		tempMap.put("TTxnTm_B", intent.getStringExtra("TTxnTm_B"));
		tempMap.put("TTxnDt_B", intent.getStringExtra("TTxnDt_B"));
		
		tempMap.put("POSTYPE_B", intent.getStringExtra("POSTYPE_B"));
		tempMap.put("CHECKX_B", intent.getStringExtra("CHECKX_B"));
		tempMap.put("CHECKY_B", intent.getStringExtra("CHECKY_B"));
		tempMap.put("TERMINALNUMBER_B", intent.getStringExtra("PID")); // PSAM卡号
		tempMap.put("MAC_B", AppDataCenter.MAC[0].substring(0, 8)); // MAC

		LKHttpRequest req = new LKHttpRequest(TransferRequestTag.PhoneRecharge, tempMap, transferHandlerPhone());

		new LKHttpRequestQueue().addHttpRequest(req).executeQueue("正在交易，请稍候...", new LKHttpRequestQueueDone() {

			@Override
			public void onComplete() {
				super.onComplete();

			}

		});
	}

	private LKAsyncHttpResponseHandler transferHandlerPhone() {
		return new LKAsyncHttpResponseHandler() {

			@Override
			public void successAction(Object obj) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) obj;
				if (map.get("RSPCOD") != null && map.get("RSPCOD").equals("00")) {
					Intent intent = new Intent(PwdInputActivity.this, TradeSuccessActivity.class);
					intent.putExtra("tips", "充值成功");
					startActivityForResult(intent, 102);

				} else {
					gotoTradeFailureActivity(map.get("RSPMSG"));
				}
			}

		};
	}
	
	//卡卡转账

	private void transferCard() {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("TRANCODE", intent.getStringExtra("TRANCODE"));
		tempMap.put("SELLTEL_B", intent.getStringExtra("SELLTEL_B")); // 消费撤销唯一凭证
		tempMap.put("CRDNO1_B", intent.getStringExtra("CARDNO1_B"));// 信用卡卡号
		
		tempMap.put("INCARDNAM_B", intent.getStringExtra("INCARDNAM_B"));
		tempMap.put("OUTCARDNAM_B", intent.getStringExtra("OUTCARDNAM_B"));
		tempMap.put("OUT_IDTYP_B", intent.getStringExtra("OUT_IDTYP_B"));
		tempMap.put("OUT_IDTYPNAM_B", intent.getStringExtra("OUT_IDTYPNAM_B"));
		tempMap.put("OUT_IDCARD_B", intent.getStringExtra("OUT_IDCARD_B"));
		
		tempMap.put("phoneNumber_B", intent.getStringExtra("phoneNumber_B"));// 接收信息手机号
		tempMap.put("Track2_B", ByteUtil.byte2hex(AppDataCenter.encTrack));
		tempMap.put("CRDNOJLN_B", AppDataCenter.PIN_Block[0]); // 支付密码
		tempMap.put("TXNAMT_B", intent.getStringExtra("TXNAMT_B"));
		tempMap.put("TSeqNo_B", intent.getStringExtra("TSeqNo_B"));
		tempMap.put("TTxnTm_B", intent.getStringExtra("TTxnTm_B"));
		tempMap.put("TTxnDt_B", intent.getStringExtra("TTxnDt_B"));
		
		tempMap.put("POSTYPE_B", intent.getStringExtra("POSTYPE_B"));
		tempMap.put("RAND_B", "");
		tempMap.put("CHECKX_B", intent.getStringExtra("CHECKX_B"));
		tempMap.put("CHECKY_B", intent.getStringExtra("CHECKY_B"));
		tempMap.put("TERMINALNUMBER_B", intent.getStringExtra("PID")); // PSAM卡号
		tempMap.put("MAC_B", AppDataCenter.MAC[0].substring(0, 8)); // MAC

		LKHttpRequest req = new LKHttpRequest(TransferRequestTag.CardCard, tempMap, transferHandlerCard());

		new LKHttpRequestQueue().addHttpRequest(req).executeQueue("正在交易，请稍候...", new LKHttpRequestQueueDone() {

			@Override
			public void onComplete() {
				super.onComplete();

			}

		});
	}

	private LKAsyncHttpResponseHandler transferHandlerCard() {
		return new LKAsyncHttpResponseHandler() {

			@Override
			public void successAction(Object obj) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) obj;
				if (map.get("RSPCOD").equals("00")) {
					Intent intent = new Intent(PwdInputActivity.this, TradeSuccessActivity.class);
					intent.putExtra("tips", "转账成功");
					startActivityForResult(intent, 101);

				} else {
					gotoTradeFailureActivity(map.get("RSPMSG"));
				}
			}

		};
	}

	// 信用卡还款
	private void transferCredit() {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("TRANCODE", intent.getStringExtra("TRANCODE"));
		tempMap.put("SELLTEL_B", intent.getStringExtra("SELLTEL_B")); // 消费撤销唯一凭证
		tempMap.put("CRDNO1_B", intent.getStringExtra("CARDNO1_B"));// 信用卡卡号
		
		tempMap.put("phoneNumber_B", intent.getStringExtra("phoneNumber_B"));// 接收信息手机号
		tempMap.put("Track2_B", ByteUtil.byte2hex(AppDataCenter.encTrack));
		tempMap.put("CRDNOJLN_B", AppDataCenter.PIN_Block[0]); // 支付密码
		tempMap.put("TXNAMT_B", intent.getStringExtra("TXNAMT_B"));
		tempMap.put("TSeqNo_B", intent.getStringExtra("TSeqNo_B"));
		tempMap.put("TTxnTm_B", intent.getStringExtra("TTxnTm_B"));
		tempMap.put("TTxnDt_B", intent.getStringExtra("TTxnDt_B"));
		
		tempMap.put("POSTYPE_B", intent.getStringExtra("POSTYPE_B"));
		tempMap.put("CHECKX_B", intent.getStringExtra("CHECKX_B"));
		tempMap.put("CHECKY_B", intent.getStringExtra("CHECKY_B"));
		tempMap.put("TERMINALNUMBER_B", intent.getStringExtra("PID")); // PSAM卡号
		tempMap.put("MAC_B", AppDataCenter.MAC[0].substring(0, 8)); // MAC

		LKHttpRequest req = new LKHttpRequest(TransferRequestTag.CreditCard, tempMap, transferHandlerCredit());

		new LKHttpRequestQueue().addHttpRequest(req).executeQueue("正在交易，请稍候...", new LKHttpRequestQueueDone() {

			@Override
			public void onComplete() {
				super.onComplete();

			}

		});
	}

	private LKAsyncHttpResponseHandler transferHandlerCredit() {
		return new LKAsyncHttpResponseHandler() {

			@Override
			public void successAction(Object obj) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) obj;
				if (map.get("RSPCOD") != null && map.get("RSPCOD").equals("00")) {
					Intent intent = new Intent(PwdInputActivity.this, TradeSuccessActivity.class);
					intent.putExtra("tips", "还款成功");
					startActivityForResult(intent, 100);

				} else {
					gotoTradeFailureActivity(map.get("RSPMSG"));
				}
			}

		};
	}
}

package com.zxb.activity;

import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fncat.xswipe.controller.ErrorCode;
import com.zxb.R;
import com.zxb.client.AppDataCenter;
import com.zxb.client.ApplicationEnvironment;
import com.zxb.client.Constants;
import com.zxb.client.TransferRequestTag;
import com.zxb.network.LKAsyncHttpResponseHandler;
import com.zxb.network.LKHttpRequest;
import com.zxb.network.LKHttpRequestQueue;
import com.zxb.network.LKHttpRequestQueueDone;
import com.zxb.qpos.ThreadCalcMac;
import com.zxb.qpos.ThreadCancel;
import com.zxb.qpos.ThreadDeviceID;
import com.zxb.qpos.ThreadEncPwd;
import com.zxb.qpos.ThreadSwip;
import com.zxb.qpos.ThreadUpDataKey;
import com.zxb.qpos.ZXBPOS;
import com.zxb.util.ByteUtil;
import com.zxb.util.DateUtil;
import com.zxb.util.StringUtil;

public class SearchAndSwipeActivity extends BaseActivity implements OnClickListener {

	private Button backBtn = null;
	private TextView titleView = null;

	private Intent intent = null;
	private ImageView iv_device;
	private RelativeLayout layout_swip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_search_swipe);
		
		intent = this.getIntent();

		this.registerReceiver(mQPOSUpdateReceiver, makeUpdateIntentFilter());

		backBtn = (Button) this.findViewById(R.id.btn_back);
		backBtn.setOnClickListener(this);
		
		layout_swip = (RelativeLayout) findViewById(R.id.layout_swip);
		layout_swip.setVisibility(View.GONE);

		titleView = (TextView) this.findViewById(R.id.titleView);
		titleView.setText("请稍候");

		ImageView iv_card = (ImageView) findViewById(R.id.iv_card);
		Animation myAnimation0 = AnimationUtils.loadAnimation(SearchAndSwipeActivity.this, R.anim.swip_card_anim);
		LinearInterpolator lir0 = new LinearInterpolator();
		myAnimation0.setInterpolator(lir0);
		iv_card.startAnimation(myAnimation0);
		
		iv_device = (ImageView) findViewById(R.id.iv_device);
		iv_device.setImageResource(R.drawable.ip_shuaka_pos_s);
		
		doAction();
	}

	private void doAction() {
		String preDate = ApplicationEnvironment.getInstance().getPreferences(this).getString(Constants.kPRESIGNDATE, "0101");
		String nowDate = DateUtil.getSystemMonthDay();

		if (!preDate.equals(nowDate)) {
			titleView.setText("正在签到");
			layout_swip.setVisibility(View.GONE);
			this.showDialog(BaseActivity.PROGRESS_DIALOG_NO_CANCEL, "正在签到，请稍候...");
			
			new Sign().doAction();

		} else {
			titleView.setText("请稍候");
			layout_swip.setVisibility(View.GONE);
			this.showDialog(BaseActivity.PROGRESS_DIALOG_NO_CANCEL, "正在启动刷卡器...");
			
			
			int type = intent.getIntExtra("TYPE", 0);

			if (type == TransferRequestTag.Consume) {
				new ConsumeAction().doAction();

			} else if (type == TransferRequestTag.PhoneRecharge) {
				new PhoneRechargeAction().doAction();
				
			} else if (type == TransferRequestTag.CardCard) {
				new CardCardAction().doAction();
				
			} else if (type == TransferRequestTag.CreditCard) {
				new CreditCardAction().doAction();
			}
		}
	}

	@Override
	public void onBackPressed() {

		this.backAction(null);
	}

	public void backAction(View view) {
		new ThreadCancel(null, this).start();

		this.finish();
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.btn_back) {
			this.backAction(null);
		}
	}

	public BroadcastReceiver mQPOSUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (Constants.ACTION_ZXB_STARTSWIP.equals(action)) {

			} else if (Constants.ACTION_ZXB_SUCCESS.equals(action)){
				SearchAndSwipeActivity.this.showDialog(BaseActivity.PROGRESS_DIALOG_NO_CANCEL, "刷卡成功，正在解析磁道信息");
				
			} else if (Constants.ACTION_ZXB_SWIPFINISHED.equals(action)) {
				// 刷卡成功后启动输入密码
				SearchAndSwipeActivity.this.intent.setClass(SearchAndSwipeActivity.this, PwdInputActivity.class);
				BaseActivity.getTopActivity().startActivityForResult(SearchAndSwipeActivity.this.intent, 0);
			}
		}
	};

	public IntentFilter makeUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.ACTION_ZXB_STARTSWIP);
		intentFilter.addAction(Constants.ACTION_ZXB_SUCCESS);
		intentFilter.addAction(Constants.ACTION_ZXB_SWIPFINISHED);
		return intentFilter;
	}

	private void gotoTradeFailureActivity(String msg) {
		if (msg == null || msg.trim().equals("")) {
			msg = "交易失败";
		}

		Intent intent = new Intent(this, TradeFaiureActivity.class);
		intent.putExtra("MESSAGE", msg);
		startActivityForResult(intent, 0);
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////

	// 签到
	class Sign {

		private String tid = "";
		private String pid = "";

		public void doAction() {
			new ThreadDeviceID(getDeviceIDHandler, SearchAndSwipeActivity.this).start();
		}

		private Handler getDeviceIDHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case ErrorCode.SUCCESS:
					ZXBPOS.checkBattery();
					
					tid = ByteUtil.byte2hex(AppDataCenter.deviceId);
					pid = tid;

					signAction();

					break;

				default:
					Toast.makeText(SearchAndSwipeActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
					SearchAndSwipeActivity.this.finish();
					break;
				}
			}
		};

		private void signAction() {
			HashMap<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("TRANCODE", "199020");
			tempMap.put("PHONENUMBER", ApplicationEnvironment.getInstance().getPreferences(SearchAndSwipeActivity.this).getString(Constants.kUSERNAME, "")); // 手机号
			tempMap.put("TERMINALNUMBER", tid);// tid
			tempMap.put("PSAMCARDNO", pid);//
			tempMap.put("TERMINALSERIANO", AppDataCenter.getTraceAuditNum());
			LKHttpRequest req = new LKHttpRequest(TransferRequestTag.SignIn, tempMap, signHandler());

			new LKHttpRequestQueue().addHttpRequest(req).executeQueue(null, new LKHttpRequestQueueDone() {

				@Override
				public void onComplete() {
					super.onComplete();
				}
			});

		}

		private LKAsyncHttpResponseHandler signHandler() {
			return new LKAsyncHttpResponseHandler() {

				@Override
				public void successAction(Object obj) {
					@SuppressWarnings("unchecked")
					HashMap<String, String> map = (HashMap<String, String>) obj;
					if (map.get("RSPCOD").equals("00")) { // 签到成功
						String desKey = map.get("ENCRYPTKEY");
						String pinKey = map.get("PINKEY");
						String macKey = map.get("MACKEY");

						new ThreadUpDataKey(mHandler, SearchAndSwipeActivity.this, desKey + pinKey + macKey).start();

					} else { // 签到失败
						gotoTradeFailureActivity(map.get("RSPMSG"));
					}

				}
			};
		}

		private Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case ErrorCode.SUCCESS:
					Editor editor = ApplicationEnvironment.getInstance().getPreferences(SearchAndSwipeActivity.this).edit();
					editor.putString(Constants.kPRESIGNDATE, DateUtil.getSystemMonthDay());
					editor.commit();

					SearchAndSwipeActivity.this.doAction();

					break;

				default:
					gotoTradeFailureActivity((String) msg.obj);
					break;
				}
			}
		};

	}

	// 消费
	class ConsumeAction {

		private String tid = "";
		private String pid = "";

		public void doAction() {
			new ThreadDeviceID(getDeviceIDHandler, SearchAndSwipeActivity.this).start();
		}

		private Handler getDeviceIDHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case ErrorCode.SUCCESS:
					ZXBPOS.checkBattery();
					
					tid = ByteUtil.byte2hex(AppDataCenter.deviceId);
					pid = tid;
					
					intent.putExtra("TID", tid);
					intent.putExtra("PID", pid);

					new ThreadSwip(swipeHandler, SearchAndSwipeActivity.this).start();

					break;
				}
			}
		};
		
		private Handler swipeHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case ErrorCode.SUCCESS:
					titleView.setText("请刷卡");
					layout_swip.setVisibility(View.VISIBLE);
					SearchAndSwipeActivity.this.hideDialog(PROGRESS_DIALOG);

					break;

				default:
					Toast.makeText(SearchAndSwipeActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
					SearchAndSwipeActivity.this.finish();
					break;
				}
			}
		};
		
	}

	// 手机充值
	class PhoneRechargeAction {
		private String tid = "";
		private String pid = "";

		public void doAction() {
			new ThreadDeviceID(getDeviceIDHandler, SearchAndSwipeActivity.this).start();
		}

		private Handler getDeviceIDHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case ErrorCode.SUCCESS:
					HashMap<String, String> map = (HashMap<String, String>) msg.obj;
					tid = map.get("TID");
					pid = map.get("PID");

					HashMap<String, String> intentMap = (HashMap<String, String>) intent.getSerializableExtra("map");
					String amountStr = StringUtil.String2AmountFloat4QPOS(intentMap.get("TXNAMT_B")) + "";

					// TODO
					//new ThreadSwip_SixPass(swipeHandler, SearchAndSwipeActivity.this, amountStr, getExtraString()).start();

					break;
				}
			}
		};

		private Handler swipeHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case ErrorCode.SUCCESS:
					HashMap<String, String> map = (HashMap<String, String>) msg.obj;

					transfer(map);

					break;

				default:
					Toast.makeText(SearchAndSwipeActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
					SearchAndSwipeActivity.this.finish();
					break;
				}
			}
		};

		private String getExtraString() {
			HashMap<String, String> intentMap = (HashMap<String, String>) intent.getSerializableExtra("map");
			StringBuffer sb = new StringBuffer();
			sb.append(intentMap.get("TRANCODE"));
			sb.append(intentMap.get("TXNAMT_B"));
			sb.append(intentMap.get("TSeqNo_B"));
			sb.append(intentMap.get("TTxnTm_B"));
			sb.append(intentMap.get("TTxnDt_B"));
			sb.append(StringUtil.asciiToHex(pid.replace("UN", ""))); // UN201410000046->201410000046

			return sb.toString();
		}

		private void transfer(HashMap<String, String> map) {
			HashMap<String, String> intentMap = (HashMap<String, String>) intent.getSerializableExtra("map");
			HashMap<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("TRANCODE", intentMap.get("TRANCODE"));
			tempMap.put("SELLTEL_B", intentMap.get("SELLTEL_B")); // 消费撤销唯一凭证
			
			tempMap.put("phoneNumber_B", intentMap.get("phoneNumber_B"));// 接收信息手机号
			tempMap.put("Track2_B", map.get("CARD"));
			tempMap.put("CRDNOJLN_B", map.get("PIN")); // 支付密码???
			tempMap.put("TXNAMT_B", intentMap.get("TXNAMT_B"));
			tempMap.put("TSeqNo_B", intentMap.get("TSeqNo_B"));
			tempMap.put("TTxnTm_B", intentMap.get("TTxnTm_B"));
			tempMap.put("TTxnDt_B", intentMap.get("TTxnDt_B"));
			
			tempMap.put("POSTYPE_B", intentMap.get("POSTYPE_B"));
//			tempMap.put("RAND_B", "");
			tempMap.put("CHECKX_B", intentMap.get("CHECKX_B"));
			tempMap.put("CHECKY_B", intentMap.get("CHECKY_B"));
			tempMap.put("TERMINALNUMBER_B", pid);
			tempMap.put("MAC_B", map.get("MAC")); // MAC

			LKHttpRequest req = new LKHttpRequest(TransferRequestTag.PhoneRecharge, tempMap, transferHandler());

			new LKHttpRequestQueue().addHttpRequest(req).executeQueue("正在交易，请稍候...", new LKHttpRequestQueueDone() {

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
					if (map.get("RSPCOD") != null && map.get("RSPCOD").equals("00")) {
						Intent intent = new Intent(SearchAndSwipeActivity.this, TradeSuccessActivity.class);
						intent.putExtra("tips", "充值成功");
						startActivityForResult(intent, 102);

					} else {
						gotoTradeFailureActivity(map.get("RSPMSG"));
					}
				}

			};
		}

	}

	// 卡卡转账
	class CardCardAction {
		private String tid = "";
		private String pid = "";

		public void doAction() {
			new ThreadDeviceID(getDeviceIDHandler, SearchAndSwipeActivity.this).start();
		}

		private Handler getDeviceIDHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case ErrorCode.SUCCESS:
					HashMap<String, String> map = (HashMap<String, String>) msg.obj;
					tid = map.get("TID");
					pid = map.get("PID");
					HashMap<String, String> intentMap = (HashMap<String, String>) intent.getSerializableExtra("map");
					String amountStr = StringUtil.String2AmountFloat4QPOS(intentMap.get("TXNAMT_B")) + "";

					// TODO
					//new ThreadSwip_SixPass(swipeHandler, SearchAndSwipeActivity.this, amountStr, getExtraString()).start();

					break;
				}
			}
		};

		private Handler swipeHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case ErrorCode.SUCCESS:
					HashMap<String, String> map = (HashMap<String, String>) msg.obj;

					transfer(map);

					break;

				default:
					Toast.makeText(SearchAndSwipeActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
					SearchAndSwipeActivity.this.finish();
					break;
				}
			}
		};

		private String getExtraString() {
			HashMap<String, String> intentMap = (HashMap<String, String>) intent.getSerializableExtra("map");
			StringBuffer sb = new StringBuffer();
			sb.append(intentMap.get("TRANCODE"));
			sb.append(intentMap.get("TXNAMT_B"));
			sb.append(intentMap.get("TSeqNo_B"));
			sb.append(intentMap.get("TTxnTm_B"));
			sb.append(intentMap.get("TTxnDt_B"));
			sb.append(StringUtil.asciiToHex(pid.replace("UN", ""))); // UN201410000046->201410000046

			return sb.toString();
		}

		private void transfer(HashMap<String, String> map) {
			HashMap<String, String> intentMap = (HashMap<String, String>) intent.getSerializableExtra("map");
			HashMap<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("TRANCODE", intentMap.get("TRANCODE"));
			tempMap.put("SELLTEL_B", intentMap.get("SELLTEL_B")); // 消费撤销唯一凭证
			tempMap.put("CRDNO1_B", intentMap.get("CARDNO1_B"));// 信用卡卡号
			
			tempMap.put("INCARDNAM_B", intentMap.get("INCARDNAM_B"));
			tempMap.put("OUTCARDNAM_B", intentMap.get("OUTCARDNAM_B"));
			tempMap.put("OUT_IDTYP_B", intentMap.get("OUT_IDTYP_B"));
			tempMap.put("OUT_IDTYPNAM_B", intentMap.get("OUT_IDTYPNAM_B"));
			tempMap.put("OUT_IDCARD_B", intentMap.get("OUT_IDCARD_B"));
			
			tempMap.put("phoneNumber_B", intentMap.get("phoneNumber_B"));// 接收信息手机号
			tempMap.put("Track2_B", map.get("CARD"));
			tempMap.put("CRDNOJLN_B", map.get("PIN")); // 支付密码???
			tempMap.put("TXNAMT_B", intentMap.get("TXNAMT_B"));
			tempMap.put("TSeqNo_B", intentMap.get("TSeqNo_B"));
			tempMap.put("TTxnTm_B", intentMap.get("TTxnTm_B"));
			tempMap.put("TTxnDt_B", intentMap.get("TTxnDt_B"));
			
			tempMap.put("POSTYPE_B", intentMap.get("POSTYPE_B"));
			tempMap.put("RAND_B", "");
			tempMap.put("CHECKX_B", intentMap.get("CHECKX_B"));
			tempMap.put("CHECKY_B", intentMap.get("CHECKY_B"));
			tempMap.put("TERMINALNUMBER_B", pid); // PSAM卡号 "UN201410000046"
			tempMap.put("MAC_B", map.get("MAC")); // MAC

			LKHttpRequest req = new LKHttpRequest(TransferRequestTag.CardCard, tempMap, transferHandler());

			new LKHttpRequestQueue().addHttpRequest(req).executeQueue("正在交易，请稍候...", new LKHttpRequestQueueDone() {

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
						Intent intent = new Intent(SearchAndSwipeActivity.this, TradeSuccessActivity.class);
						intent.putExtra("tips", "转账成功");
						startActivityForResult(intent, 101);

					} else {
						gotoTradeFailureActivity(map.get("RSPMSG"));
					}
				}

			};
		}

	}

	// 信用卡还款
	class CreditCardAction {
		private String tid = "";
		private String pid = "";

		public void doAction() {
			new ThreadDeviceID(getDeviceIDHandler, SearchAndSwipeActivity.this).start();
		}

		private Handler getDeviceIDHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case ErrorCode.SUCCESS:
					HashMap<String, String> map = (HashMap<String, String>) msg.obj;
					tid = map.get("TID");
					pid = map.get("PID");

					HashMap<String, String> intentMap = (HashMap<String, String>) intent.getSerializableExtra("map");
					String amountStr = StringUtil.String2AmountFloat4QPOS(intentMap.get("TXNAMT_B")) + "";

					// TODO
					//new ThreadSwip_SixPass(swipeHandler, SearchAndSwipeActivity.this, amountStr, getExtraString()).start();

					break;
				}
			}
		};

		private Handler swipeHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case ErrorCode.SUCCESS:
					HashMap<String, String> map = (HashMap<String, String>) msg.obj;

					transfer(map);

					break;

				default:
					Toast.makeText(SearchAndSwipeActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
					SearchAndSwipeActivity.this.finish();
					break;
				}
			}
		};

		private String getExtraString() {
			HashMap<String, String> intentMap = (HashMap<String, String>) intent.getSerializableExtra("map");
			StringBuffer sb = new StringBuffer();
			sb.append(intentMap.get("TRANCODE"));
			sb.append(intentMap.get("TXNAMT_B"));
			sb.append(intentMap.get("TSeqNo_B"));
			sb.append(intentMap.get("TTxnTm_B"));
			sb.append(intentMap.get("TTxnDt_B"));
			sb.append(StringUtil.asciiToHex(pid.replace("UN", ""))); // UN201410000046->201410000046

			return sb.toString();
		}

		private void transfer(HashMap<String, String> map) {
			HashMap<String, String> intentMap = (HashMap<String, String>) intent.getSerializableExtra("map");
			HashMap<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("TRANCODE", intentMap.get("TRANCODE"));
			tempMap.put("SELLTEL_B", intentMap.get("SELLTEL_B")); // 消费撤销唯一凭证
			tempMap.put("CRDNO1_B", intentMap.get("CARDNO1_B"));// 信用卡卡号
			
			tempMap.put("phoneNumber_B", intentMap.get("phoneNumber_B"));// 接收信息手机号
			tempMap.put("Track2_B", map.get("CARD"));
			tempMap.put("CRDNOJLN_B", map.get("PIN")); // 支付密码???
			tempMap.put("TXNAMT_B", intentMap.get("TXNAMT_B"));
			tempMap.put("TSeqNo_B", intentMap.get("TSeqNo_B"));
			tempMap.put("TTxnTm_B", intentMap.get("TTxnTm_B"));
			tempMap.put("TTxnDt_B", intentMap.get("TTxnDt_B"));
			
			tempMap.put("POSTYPE_B", intentMap.get("POSTYPE_B"));
			tempMap.put("CHECKX_B", intentMap.get("CHECKX_B"));
			tempMap.put("CHECKY_B", intentMap.get("CHECKY_B"));
			tempMap.put("TERMINALNUMBER_B", pid);
			tempMap.put("MAC_B", map.get("MAC")); // MAC

			LKHttpRequest req = new LKHttpRequest(TransferRequestTag.CreditCard, tempMap, transferHandler());

			new LKHttpRequestQueue().addHttpRequest(req).executeQueue("正在交易，请稍候...", new LKHttpRequestQueueDone() {

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
					if (map.get("RSPCOD") != null && map.get("RSPCOD").equals("00")) {
						Intent intent = new Intent(SearchAndSwipeActivity.this, TradeSuccessActivity.class);
						intent.putExtra("tips", "还款成功");
						startActivityForResult(intent, 100);

					} else {
						gotoTradeFailureActivity(map.get("RSPMSG"));
					}
				}

			};
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		this.finish();
	}
}

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
import android.widget.TextView;
import android.widget.Toast;

import com.zxb.R;
import com.zxb.client.AppDataCenter;
import com.zxb.client.ApplicationEnvironment;
import com.zxb.client.Constants;
import com.zxb.client.TransferRequestTag;
import com.zxb.model.TradeModel;
import com.zxb.network.LKAsyncHttpResponseHandler;
import com.zxb.network.LKHttpRequest;
import com.zxb.network.LKHttpRequestQueue;
import com.zxb.network.LKHttpRequestQueueDone;
import com.zxb.util.DateUtil;
import com.zxb.util.StringUtil;

// 流水详情
public class TransferDetailActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener {
	
	private TradeModel model = null;
	private CheckBox checkBox = null;
	private EditText phoneNumEdit = null;
	private Button btn_revoke;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_transfer_detail);

		model = (TradeModel) this.getIntent().getSerializableExtra("model");

		TextView tv_status = (TextView) findViewById(R.id.tv_status);
		tv_status.setText(model.getStatus());

		checkBox = (CheckBox) this.findViewById(R.id.checkBox);
		checkBox.setOnCheckedChangeListener(this);
		phoneNumEdit = (EditText) this.findViewById(R.id.phoneText);
		phoneNumEdit.setText(ApplicationEnvironment.getInstance().getPreferences(this).getString(Constants.kUSERNAME, ""));
		
		TextView tv_money_before = (TextView) findViewById(R.id.tv_money_before);
		TextView tv_money_after = (TextView) findViewById(R.id.tv_money_after);
		String tmp = StringUtil.String2SymbolAmount(model.getTxnamt()).substring(1);
		tv_money_before.setText(tmp.substring(0, tmp.length()-3));
		tv_money_after.setText(tmp.substring(tmp.length()-3));
		
		TextView tv_date = (TextView) findViewById(R.id.tv_date);
		tv_date.setText(StringUtil.dateStringFormate(model.getSysDate()));

		TextView tv_account = (TextView) findViewById(R.id.tv_account);
		tv_account.setText(StringUtil.formatCardId(StringUtil.formatAccountNo(model.getCardNo())));

		TextView tv_merchant_name = (TextView) findViewById(R.id.tv_merchant_name);
		tv_merchant_name.setText(model.getMerName());

		TextView tv_flow_num = (TextView) findViewById(R.id.tv_flow_num);
		tv_flow_num.setText(model.getLogNo());

		btn_revoke = (Button) findViewById(R.id.btn_revoke);
		btn_revoke.setOnClickListener(this);

//		if (model.getTxncd().equals("0200000000") && model.getTxnsts().equalsIgnoreCase("S") && model.getLogDate().equalsIgnoreCase(DateUtil.formatDate2(new Date()))) {
//			btn_revoke.setVisibility(View.VISIBLE);
//		} else {
//			btn_revoke.setVisibility(View.GONE);
//		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_revoke:
//			revokeTransfer();
			if (checkBox.isChecked()) {
				String phoneNum = phoneNumEdit.getText().toString();
				if (phoneNum.equals("")) {
					Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
					return;
				} else if (phoneNum.length() != 11) {
					Toast.makeText(this, "手机号不合法", Toast.LENGTH_SHORT).show();
					return;
				} else {
					this.sendTicket();
				}

			} else {
				this.finish();
			}
			break;

		default:
			break;
		}
	}

	public void backAction(View view) {
		this.setResult(RESULT_CANCELED);
		this.finish();
	}
	
	private void sendTicket(){
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("TRANCODE", "199037");
		tempMap.put("PHONENUMBER", phoneNumEdit.getText().toString().trim());
		tempMap.put("LOGNO", model.getLogNo());

		LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.SendTicket, tempMap, getTicketHandler());

		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue("正在处理请稍候...", new LKHttpRequestQueueDone() {

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
					Toast.makeText(TransferDetailActivity.this, "交易小票发送成功，请查收", Toast.LENGTH_SHORT).show();
					
					TransferDetailActivity.this.finish();
					
				} else {
					Toast.makeText(TransferDetailActivity.this, map.get("RSPMSG"), Toast.LENGTH_SHORT).show();
				}
			}
		};
	}
	
	private void revokeTransfer(){
		Intent intent = new Intent(this, SearchAndSwipeActivity.class);
		
		intent.putExtra("TYPE", TransferRequestTag.ConsumeCancel);
		intent.putExtra("TRANCODE", "199006");
		intent.putExtra("LOGNO", model.getLogNo()); // 流水号，撤销唯一凭证
		intent.putExtra("PHONENUMBER", ApplicationEnvironment.getInstance().getPreferences(this).getString(Constants.kUSERNAME, ""));
		intent.putExtra("PCSIM", "获取不到");
		intent.putExtra("TSEQNO", AppDataCenter.getTraceAuditNum());
		intent.putExtra("CTXNAT", model.getTxnamt());
		intent.putExtra("CRDNO", "");
		intent.putExtra("CHECKX", "0.0");
		intent.putExtra("APPTOKEN", "APPTOKEN");
		intent.putExtra("TTXNTM", DateUtil.getSystemTime());
		intent.putExtra("TTXNDT", DateUtil.getSystemMonthDay());
		
		startActivityForResult(intent, 0);
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		switch (arg0.getId()) {
		case R.id.checkBox:
			phoneNumEdit.setVisibility(arg1 ? View.VISIBLE : View.INVISIBLE);
			btn_revoke.setText(arg1?"发送小票":"完    成");
			break;
		}
		
	}

}

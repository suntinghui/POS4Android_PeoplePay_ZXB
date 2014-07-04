package com.zxb.activity;

import java.util.HashMap;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zxb.R;
import com.zxb.client.AppDataCenter;
import com.zxb.client.ApplicationEnvironment;
import com.zxb.client.Constants;
import com.zxb.client.TransferRequestTag;
import com.zxb.util.DateUtil;
import com.zxb.util.StringUtil;
import com.zxb.view.LKAlertDialog;

// 信用卡转账
public class CreditCardActivity extends BaseActivity implements OnClickListener {
	private EditText et_card_num;
	private EditText et_amount;
	private EditText et_phone;
	private int positon = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creditcard);

		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		Button btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
		
		et_card_num = (EditText) findViewById(R.id.et_card_num);
		et_amount = (EditText) findViewById(R.id.et_amount);
		et_phone = (EditText) findViewById(R.id.et_phone);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			if (checkValue()) {
				LKAlertDialog dialog = new LKAlertDialog(this);
				dialog.setTitle("提示");
				dialog.setMessage("信用卡卡号		" +et_card_num.getText().toString()+"\n还款金额		"+et_amount.getText().toString()+"元");
				dialog.setCancelable(false);
				dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
						creditCardAction();
					}
				});
				dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialog.create().show();
				
			}
			break;
		case R.id.btn_back:
			this.finish();
			break;
		default:
			break;
		}

	}

	private void creditCardAction(){
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("TRANCODE", "708102");
		map.put("SELLTEL_B", ApplicationEnvironment.getInstance().getPreferences(this).getString(Constants.kUSERNAME, ""));
		map.put("CARDNO1_B", et_card_num.getText().toString());//接收转账卡号
		
		map.put("phoneNumber_B", et_phone.getText().toString());//接收信息手机号
		map.put("TXNAMT_B", StringUtil.amount2String(String.format("%1$.2f", Double.valueOf(et_amount.getText().toString()))));//交易金额
		map.put("POSTYPE_B", "1");//POSTYPE_B   1 普通刷卡器 2 小刷卡器
		map.put("CHECKX_B", "0.0");//当前经度
		map.put("CHECKY_B", "0.0");//当前纬度
		
		map.put("TSeqNo_B", AppDataCenter.getTraceAuditNum());
		map.put("TTxnTm_B", DateUtil.getSystemTime());
		map.put("TTxnDt_B", DateUtil.getSystemMonthDay());
		
		Intent intent = new Intent(CreditCardActivity.this, SearchAndSwipeActivity.class);
		intent.putExtra("TYPE", TransferRequestTag.CreditCard);
		intent.putExtra("map", map);
		startActivityForResult(intent, 100);
	}

	public Boolean checkValue() {
		if(et_card_num.getText().length() == 0){
			Toast.makeText(this, "卡号不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(et_amount.getText().length() == 0){
			Toast.makeText(this, "金额不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(et_phone.getText().length() == 0){
			Toast.makeText(this, "手机号码不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(resultCode == RESULT_OK){
			finish();
		}else if(resultCode == RESULT_CANCELED){
			
		}
	}
}

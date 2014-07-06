package com.zxb.activity;

import java.util.HashMap;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.zxb.R;
import com.zxb.client.AppDataCenter;
import com.zxb.client.ApplicationEnvironment;
import com.zxb.client.Constants;
import com.zxb.client.TransferRequestTag;
import com.zxb.util.DateUtil;
import com.zxb.util.StringUtil;
import com.zxb.view.LKAlertDialog;

public class MobileChargeActivity extends BaseActivity implements OnClickListener, OnItemSelectedListener {

	private EditText et_phone;

	private Spinner spinner = null;

	private String[] amount = { "50", "100", "200", "300" };
	private String currentAmount = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_recharge);

		Button btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		Button btn_confirm = (Button) findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
		Button btn_phone = (Button) findViewById(R.id.btn_phone);
		btn_phone.setOnClickListener(this);

		currentAmount = amount[0];
		et_phone = (EditText) findViewById(R.id.et_phone);

		spinner = (Spinner) findViewById(R.id.spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.amount, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		currentAmount = amount[arg2];
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 110 && null != data) {
			Bundle bundle = data.getExtras();
			String str = bundle.getString("phoneNumber");
			if (null != str) {
				et_phone.setText(str);
			}
		}
		if(requestCode == 100){
			if(resultCode == RESULT_OK){
				finish();
			}else if(resultCode == RESULT_CANCELED){
				
			}
		}

	}

	private boolean checkValue() {
		if ("".equals(et_phone.getText().toString())) {
			Toast.makeText(this, "请输入要充值手机号码", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_confirm:
			if (checkValue()) {
				LKAlertDialog dialog = new LKAlertDialog(this);
				dialog.setTitle("提示");
				dialog.setMessage("手机号		" +et_phone.getText().toString()+"\n充值金额		"+currentAmount+"元");
				dialog.setCancelable(false);
				dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
						rechargeAction();
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
		case R.id.btn_phone:
			Intent intent_p = new Intent(MobileChargeActivity.this, ContactsActivity.class);
			startActivityForResult(intent_p, 110);
			break;
		}
	}

	// 充值
	private void rechargeAction(){
		Intent intent = new Intent(this, SearchAndSwipeActivity.class);
		
		intent.putExtra("TRANCODE", "708103");
		intent.putExtra("SELLTEL_B", ApplicationEnvironment.getInstance().getPreferences(this).getString(Constants.kUSERNAME, ""));
		
		intent.putExtra("phoneNumber_B", et_phone.getText().toString());//接收信息手机号
		intent.putExtra("TXNAMT_B", StringUtil.amount2String(String.format("%1$.2f", Double.valueOf(currentAmount))));//交易金额
		intent.putExtra("POSTYPE_B", "1");//POSTYPE_B   1 普通刷卡器 2 小刷卡器
		intent.putExtra("CHECKX_B", "0.0");//当前经度
		intent.putExtra("CHECKY_B", "0.0");//当前纬度
		
		intent.putExtra("TSeqNo_B", AppDataCenter.getTraceAuditNum());
		intent.putExtra("TTxnTm_B", DateUtil.getSystemTime());
		intent.putExtra("TTxnDt_B", DateUtil.getSystemMonthDay());
		
		intent.putExtra("TYPE", TransferRequestTag.PhoneRecharge);
		
		startActivityForResult(intent, 100);
	}

}
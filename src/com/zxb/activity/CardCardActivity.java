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

// 卡卡转账
public class CardCardActivity extends BaseActivity implements OnClickListener {
	private EditText et_in_name;
	private EditText et_out_name;
	private EditText et_papers_num;
	private Spinner spinner;
	private EditText et_card_num;
	private EditText et_amount;
	private EditText et_phone;
	private String[] scope = {"身份证","军官证","护照","回乡证","台胞证","警官证","士兵证","其他证件类型"};
	private String[] scopeId = {"01","02","03","04","05","06","07","99"};
	private int positon = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cardcard);

		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		Button btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
		
		et_in_name = (EditText) findViewById(R.id.et_in_name);
		et_out_name = (EditText) findViewById(R.id.et_out_name);
		et_papers_num = (EditText) findViewById(R.id.et_papers_num);
		et_card_num = (EditText) findViewById(R.id.et_card_num);
		et_amount = (EditText) findViewById(R.id.et_amount);
		et_phone = (EditText) findViewById(R.id.et_phone);
		
		Spinner s = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.papers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(
                new OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                    	positon = position;
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			
			if (checkValue()) {
				
				LKAlertDialog dialog = new LKAlertDialog(this);
				dialog.setTitle("提示");
				dialog.setMessage("收款银行卡卡号\n"+et_card_num.getText().toString()+"\n转账金额		"+et_amount.getText().toString()+"元");
				dialog.setCancelable(false);
				dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
						cardCardAction();
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

	private void cardCardAction(){
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("TRANCODE", "708101");
		map.put("SELLTEL_B", ApplicationEnvironment.getInstance().getPreferences(this).getString(Constants.kUSERNAME, ""));
		map.put("CARDNO1_B", et_card_num.getText().toString());//接收转账卡号
		map.put("INCARDNAM_B", et_in_name.getText().toString());
		map.put("OUTCARDNAM_B", et_out_name.getText().toString());
		map.put("OUT_IDTYP_B", scopeId[positon]);
		map.put("OUT_IDTYPNAM_B", scope[positon]);
		map.put("OUT_IDCARD_B", et_papers_num.getText().toString());
		
		map.put("phoneNumber_B", et_phone.getText().toString());//接收信息手机号
		map.put("TXNAMT_B", StringUtil.amount2String(String.format("%1$.2f", Double.valueOf(et_amount.getText().toString()))));//交易金额
		map.put("POSTYPE_B", "1");//POSTYPE_B   1 普通刷卡器 2 小刷卡器
		map.put("CHECKX_B", "0.0");//当前经度
		map.put("CHECKY_B", "0.0");//当前纬度
		
		map.put("TSeqNo_B", AppDataCenter.getTraceAuditNum());
		map.put("TTxnTm_B", DateUtil.getSystemTime());
		map.put("TTxnDt_B", DateUtil.getSystemMonthDay());
		
		Intent intent = new Intent(CardCardActivity.this, SearchAndSwipeActivity.class);
		intent.putExtra("TYPE", TransferRequestTag.CardCard);
		intent.putExtra("map", map);
		startActivityForResult(intent, 101);
	}

	public Boolean checkValue() {
		if(et_in_name.getText().length() == 0){
			Toast.makeText(this, "转入卡卡主姓名不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(et_out_name.getText().length() == 0){
			Toast.makeText(this, "转出卡卡主姓名不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(et_papers_num.getText().length() == 0){
			Toast.makeText(this, "证件号不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(et_amount.getText().length() == 0){
			Toast.makeText(this, "金额不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(et_card_num.getText().length() == 0){
			Toast.makeText(this, "收款卡号不能为空！", Toast.LENGTH_SHORT).show();
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

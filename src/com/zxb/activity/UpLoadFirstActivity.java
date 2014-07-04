package com.zxb.activity;

import java.util.HashMap;

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
import com.zxb.client.TransferRequestTag;
import com.zxb.network.LKAsyncHttpResponseHandler;
import com.zxb.network.LKHttpRequest;
import com.zxb.network.LKHttpRequestQueue;
import com.zxb.network.LKHttpRequestQueueDone;
import com.zxb.util.StringUtil;

// 基本信息
public class UpLoadFirstActivity extends BaseActivity implements OnClickListener {
	private EditText et_name;
	private EditText et_id;
	private EditText et_merchant_name;
	private EditText et_address;
	private EditText et_serial;
	private String[] scope = {"服装","3c家电","美容化妆、健身养身","品牌直销","办公用品印刷","家居建材家具","商业服务、成人教育","生活服务","箱包皮具服饰","食品饮料烟酒零售","文化体育休闲玩意","杂货超市","餐饮娱乐、休闲度假",
			"汽车、自行车","珠宝工艺、古董花鸟","彩票充值票务旅游","药店及医疗服务","物流、租赁","公益类"};
	private int positon = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_first);

		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		Button btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
		
		et_name = (EditText) findViewById(R.id.et_apply_name);
		et_id = (EditText) findViewById(R.id.et_id);
		et_merchant_name = (EditText) findViewById(R.id.et_merchant_name);
		et_address = (EditText) findViewById(R.id.et_address);
		et_serial = (EditText) findViewById(R.id.et_serial);
		
		Spinner s = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.scope, android.R.layout.simple_spinner_item);
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
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("USERNAME", et_name.getText().toString());
				map.put("IDNUMBER", et_id.getText().toString());
				map.put("MERNAME", et_merchant_name.getText().toString());
				map.put("SCOBUS", scope[positon]);
				map.put("MERADDRESS", et_address.getText().toString());
				map.put("TERMID", et_serial.getText().toString());
				
				Intent intent = new Intent(UpLoadFirstActivity.this, UpLoadSecondActivity.class);
				intent.putExtra("map", map);
				startActivity(intent);
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
					if (((HashMap) obj).get("RSPCOD").toString()
							.equals("000000")) {
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


	public Boolean checkValue() {
		if(et_name.getText().length() == 0){
			Toast.makeText(this, "姓名不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(et_id.getText().length() == 0){
			Toast.makeText(this, "身份证不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(et_merchant_name.getText().length() == 0){
			Toast.makeText(this, "商户名称不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(et_address.getText().length() == 0){
			Toast.makeText(this, "地址不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(et_serial.getText().length() == 0){
			Toast.makeText(this, "机器序列号不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(!StringUtil.checkIdCard(et_id.getText().toString())){
			Toast.makeText(this, "身份证号码不合法！", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			finish();
		}
	}
}

package com.zxb.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
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
import com.zxb.client.TransferRequestTag;
import com.zxb.model.Bank;
import com.zxb.model.CityModel;
import com.zxb.model.Province;
import com.zxb.network.LKAsyncHttpResponseHandler;
import com.zxb.network.LKHttpRequest;
import com.zxb.network.LKHttpRequestQueue;
import com.zxb.network.LKHttpRequestQueueDone;

// 账户信息
public class UpLoadSecondActivity extends BaseActivity implements OnClickListener {
	private EditText et_name;
	private EditText et_account;
	private Spinner spinner1, spinner2, spinner3;
	private Province currentProvince;
	private CityModel currentCity;
	private Bank currentBank;
	private ArrayList<Province> provices;
	private ArrayList<CityModel> cities;
	private ArrayList<Bank> banks;
	private ArrayList<Bank> branchBanks;
	private Button btn_bank_branch;

	private String currentBankBankName;
	private String currentBankBankCode;

	private HashMap<String, String> fromForeMap;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_second);

		fromForeMap = (HashMap<String, String>) this.getIntent().getSerializableExtra("map");
		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		Button btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);

		et_name = (EditText) findViewById(R.id.et_name);
		et_account = (EditText) findViewById(R.id.et_account);

		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner2 = (Spinner) findViewById(R.id.spinner2);
		spinner3 = (Spinner) findViewById(R.id.spinner3);
		
		btn_bank_branch = (Button) findViewById(R.id.btn_bank_branch);
		btn_bank_branch.setOnClickListener(this);
		btn_bank_branch.setFocusable(true);
		btn_bank_branch.setFocusableInTouchMode(true);
		btn_bank_branch.requestFocus();

		getProvinceName();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			if (checkValue()) {
				fromForeMap.put("BANKUSERNAME", et_name.getText().toString());
				fromForeMap.put("BANKAREA", currentCity.getCode() + "");
				fromForeMap.put("BIGBANKCOD", currentBank.getCode() + "");
				fromForeMap.put("BIGBANKNAM", currentBank.getName());
				fromForeMap.put("BANKCOD", currentBankBankCode);
				fromForeMap.put("BANKNAM", currentBankBankName);
				fromForeMap.put("BANKACCOUNT", et_account.getText().toString());

				Intent intent = new Intent(UpLoadSecondActivity.this, UploadImagesActivity.class);
				intent.putExtra("map", fromForeMap);
				startActivity(intent);
			}

			break;
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_bank_branch:
			Intent intent_b = new Intent(UpLoadSecondActivity.this, BankBranchActivity.class);
			intent_b.putExtra("list", branchBanks);
			startActivityForResult(intent_b, 0);
			break;
		default:
			break;
		}

	}

	public Boolean checkValue() {
		if (et_name.getText().length() == 0) {
			Toast.makeText(this, "开户行不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (et_account.getText().length() == 0) {
			Toast.makeText(this, "银行账号不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (currentBankBankCode == null || currentBankBankCode.length() == 0) {
			Toast.makeText(this, "支行不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	class BankAdapter implements OnItemSelectedListener {

		/**
		 * (non-Javadoc)
		 * 
		 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView,
		 *      android.view.View, int, long)
		 */
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			currentBank = banks.get(position);
			getBranchBank(currentCity.getCode() + "", currentBank.getCode() + "");
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
		 */
		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}

	}

	public void onProvinChange(int position) {
		currentProvince = provices.get(position);
		String provinceCode = currentProvince.getCode() + "";
		// 获取城市
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("TRANCODE", "199032");
		tempMap.put("PARCOD", provinceCode);
		LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.GetCityName, tempMap, getCityNameHandler());

		// 获取银行
		HashMap<String, Object> tempMap1 = new HashMap<String, Object>();
		tempMap1.put("TRANCODE", "199035");
		tempMap1.put("PARCOD", provinceCode);
		LKHttpRequest req2 = new LKHttpRequest(TransferRequestTag.GetBank, tempMap1, getBankHandler());
		new LKHttpRequestQueue().addHttpRequest(req1, req2).executeQueue(null, new LKHttpRequestQueueDone() {
			@Override
			public void onComplete() {
				super.onComplete();
			}

		});

	}

	class ProvinceAdapter implements OnItemSelectedListener {

		/**
		 * (non-Javadoc)
		 * 
		 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView,
		 *      android.view.View, int, long)
		 */
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			onProvinChange(position);
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
		 */
		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}

	}

	final class CityAdapter extends ProvinceAdapter {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			currentCity = cities.get(position);
		}
	}

	protected void dialog(String message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message).setCancelable(false).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();

	}

	// 获取省份名称
	private void getProvinceName() {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("TRANCODE", "199031");

		LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.GetProvinceName, tempMap, getProvinceNameHandler());

		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue("正在获取省份信息...", new LKHttpRequestQueueDone() {

			@Override
			public void onComplete() {
				super.onComplete();

			}
		});
	}

	private LKAsyncHttpResponseHandler getProvinceNameHandler() {
		return new LKAsyncHttpResponseHandler() {

			@SuppressWarnings("unchecked")
			@Override
			public void successAction(Object obj) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> recieveMap = (HashMap<String, Object>) obj;
				if ("00".equals(recieveMap.get("RSPCOD"))) {
					provices = (ArrayList<Province>) recieveMap.get("list");
					if (provices != null && provices.size() != 0) {
						ArrayAdapter<Province> provinceAdapter = new ArrayAdapter<Province>(UpLoadSecondActivity.this, R.layout.simple_spinner_item, android.R.id.text1, provices);
						spinner1.setAdapter(provinceAdapter);
						spinner1.setOnItemSelectedListener(new ProvinceAdapter());
					}

				}

			}

		};
	}

	private LKAsyncHttpResponseHandler getCityNameHandler() {
		return new LKAsyncHttpResponseHandler() {

			@SuppressWarnings("unchecked")
			@Override
			public void successAction(Object obj) {
				HashMap<String, Object> recieveMap = (HashMap<String, Object>) obj;
				if ("00".equals(recieveMap.get("RSPCOD"))) {
					cities = (ArrayList<CityModel>) recieveMap.get("list");
					if (cities != null && cities.size() != 0) {
						spinner2.setOnItemSelectedListener(new CityAdapter());

						ArrayAdapter<CityModel> cityAdapter = new ArrayAdapter<CityModel>(UpLoadSecondActivity.this, R.layout.simple_spinner_item, android.R.id.text1, cities);
						spinner2.setAdapter(cityAdapter);
					}

				}
			}
		};
	}

	private LKAsyncHttpResponseHandler getBankHandler() {
		return new LKAsyncHttpResponseHandler() {

			@Override
			public void successAction(Object obj) {
				HashMap<String, Object> recieveMap = (HashMap<String, Object>) obj;
				if ("00".equals(recieveMap.get("RSPCOD"))) {
					banks = (ArrayList<Bank>) recieveMap.get("list");
					if (banks != null && banks.size() != 0) {
						spinner3.setOnItemSelectedListener(new BankAdapter());

						ArrayAdapter<Bank> bankAdapter = new ArrayAdapter<Bank>(UpLoadSecondActivity.this, R.layout.simple_spinner_item, android.R.id.text1, banks);
						spinner3.setAdapter(bankAdapter);
					}
				}
			}
		};
	}

	// 获取支行
	private void getBranchBank(String cityCode, String bankCode) {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("TRANCODE", "199034");
		tempMap.put("CITYCOD", cityCode);
		tempMap.put("BBANKCOD", bankCode);

		LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.GetBankBranch, tempMap, getBranchHandler());

		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue("正在获取支行信息...", new LKHttpRequestQueueDone() {

			@Override
			public void onComplete() {
				super.onComplete();

			}
		});
	}

	private LKAsyncHttpResponseHandler getBranchHandler() {
		return new LKAsyncHttpResponseHandler() {

			@SuppressWarnings("unchecked")
			@Override
			public void successAction(Object obj) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> recieveMap = (HashMap<String, Object>) obj;
				if ("00".equals(recieveMap.get("RSPCOD"))) {
					branchBanks = (ArrayList<Bank>) recieveMap.get("list");
					if (branchBanks != null && branchBanks.size() != 0) {
						currentBankBankName = ((Bank) (branchBanks.get(0))).getName();
						currentBankBankCode = ((Bank) (branchBanks.get(0))).getCode() + "";
						btn_bank_branch.setText(currentBankBankName);
					}
				}

			}

		};
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			this.setResult(RESULT_OK);
			finish();
		}else if (resultCode == 5) {
			currentBankBankCode = data.getStringExtra("bankbranchid");
			currentBankBankName = data.getStringExtra("bankbranchname");
			btn_bank_branch.setText(currentBankBankName);
		} 
	}
}

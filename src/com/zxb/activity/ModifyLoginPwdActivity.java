package com.zxb.activity;

import java.util.HashMap;

import com.zxb.R;
import com.zxb.client.ApplicationEnvironment;
import com.zxb.client.Constants;
import com.zxb.client.TransferRequestTag;
import com.zxb.network.LKAsyncHttpResponseHandler;
import com.zxb.network.LKHttpRequest;
import com.zxb.network.LKHttpRequestQueue;
import com.zxb.network.LKHttpRequestQueueDone;
import com.zxb.view.LKAlertDialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


// 修改登录密码
public class ModifyLoginPwdActivity extends BaseActivity implements OnClickListener{
	private EditText et_old_pwd;
	private EditText et_new_pwd;
	private EditText et_confirm_pwd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_login_pwd);
		
		Button btn_back = (Button)this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		
		Button btn_confirm = (Button)this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
		
		et_old_pwd = (EditText) findViewById(R.id.et_old_pwd);
		et_new_pwd = (EditText) findViewById(R.id.et_new_pwd);
		et_confirm_pwd = (EditText) findViewById(R.id.et_confirm_pwd);
		
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_confirm:
			if(checkValue()){
				modifyAction();				
			}
			break;
		default:
			break;
		}
		
	}

	private void modifyAction(){
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("TRANCODE", "199003");
		tempMap.put("PHONENUMBER", ApplicationEnvironment.getInstance().getPreferences(this).getString(Constants.kUSERNAME, ""));
		tempMap.put("PASSWORD", et_old_pwd.getText().toString());
		tempMap.put("PASSWORDNEW", et_confirm_pwd.getText().toString());
		
		LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.ModifyLoginPwd, tempMap, getModifyHandler());
		
		new LKHttpRequestQueue().addHttpRequest(req1)
		.executeQueue("正在修改请稍候...", new LKHttpRequestQueueDone(){

			@Override
			public void onComplete() {
				super.onComplete();
				
			}
			
		});	
	}
	
	private LKAsyncHttpResponseHandler getModifyHandler(){
		 return new LKAsyncHttpResponseHandler(){
			 
			@Override
			public void successAction(Object obj) {
				Log.e("success:", obj.toString());
				
				if (obj instanceof HashMap){
					Log.e("success:", obj.toString());
					if(((HashMap<?, ?>) obj).get("RSPCOD").toString().equals("00")){
						LKAlertDialog dialog1 = new LKAlertDialog(ModifyLoginPwdActivity.this);
						dialog1.setTitle("提示");
						dialog1.setMessage("密码修改成功");
						dialog1.setCancelable(false);
						dialog1.setPositiveButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.dismiss();
								ModifyLoginPwdActivity.this.finish();
							}
						});
						
						dialog1.create().show();
					}else if(((HashMap) obj).get("RSPMSG").toString() != null && ((HashMap) obj).get("RSPMSG").toString().length() != 0){
						Toast.makeText(getApplicationContext(), ((HashMap) obj).get("RSPMSG").toString(),
							     Toast.LENGTH_SHORT).show();
					}
				} else {
				}
				
			}

		};
		}

	private Boolean checkValue(){
		
		if(et_old_pwd.getText().toString().length() == 0){
			Toast.makeText(this,"原密码不能为空", 2).show();
			return false;
		}
		if(et_new_pwd.getText().toString().length() == 0){
			Toast.makeText(this,"新密码不能为空", 2).show();
			return false;
		}
		if(et_confirm_pwd.getText().toString().length() == 0){
			Toast.makeText(this,"确认密码不能为空", 2).show();
			return false;
		}
		
		if(!et_new_pwd.getText().toString().equals(et_confirm_pwd.getText().toString())){
			Toast.makeText(this,"密码输入不一致，请重新输入", 2).show();
			return false;
		}
		
		return true;
	}
}

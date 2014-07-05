package com.zxb.activity;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.zxb.R;
import com.zxb.view.PasswordWithLabelView;


public class PwdInputActivity extends BaseActivity implements OnClickListener {
	private PasswordWithLabelView et_pwd = null;
	private String field4 = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pwd_input);

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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			this.finish();

			break;
		case R.id.btn_confirm:
			if (checkValue()) {
				
				try {


				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			break;

		default:
			break;
		}

	}
	
	private void getBalance(){
		try{
			
	        
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

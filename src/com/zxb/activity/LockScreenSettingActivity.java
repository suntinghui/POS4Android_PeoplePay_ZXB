package com.zxb.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zxb.R;
import com.zxb.client.ApplicationEnvironment;
import com.zxb.client.Constants;
import com.zxb.client.TransferRequestTag;
import com.zxb.network.LKAsyncHttpResponseHandler;
import com.zxb.network.LKHttpRequest;
import com.zxb.network.LKHttpRequestQueue;
import com.zxb.network.LKHttpRequestQueueDone;
import com.zxb.util.BitmapUtil;
import com.zxb.view.CircularImage;
import com.zxb.view.GestureLockView;
import com.zxb.view.GestureLockView.OnGestureFinishListener;
import com.zxb.view.LKAlertDialog;

// 锁屏 设置
@SuppressLint("ResourceAsColor")
public class LockScreenSettingActivity extends Activity implements
		OnClickListener {
	private TextView tv_tips;
	private GestureLockView gv;
	private int drawCount = 0;
	private String firstKey = "";
	private String secondKey = "";
	private LinearLayout layout_lock;
	
	private CircularImage ibtn_head;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_screen_setting);

		
		ibtn_head = (CircularImage) findViewById(R.id.ibtn_head);
		ibtn_head.setOnClickListener(this);
		
		layout_lock = (LinearLayout) findViewById(R.id.layout_lock);

		tv_tips = (TextView) findViewById(R.id.tv_tips);
		tv_tips.setText("请绘制新手势");
		
		tv_tips = (TextView) findViewById(R.id.tv_tips);
		gv = (GestureLockView) findViewById(R.id.gv);
		gv.isSetting = true;
		gv.setOnGestureFinishListener(new OnGestureFinishListener() {
			@Override
			public void OnGestureFinish(boolean success) {
				if (drawCount++ == 0) {
					firstKey = gv.getCurrentKey();
					tv_tips.setText("请再次绘制新手势");
				} else {
					secondKey = gv.getCurrentKey();
					if (firstKey.equals(secondKey)) {
						// 手势设置成功
						SharedPreferences pre = ApplicationEnvironment
								.getInstance().getPreferences();
						Editor editor = pre.edit();
						editor.putString(Constants.kLOCKKEY, secondKey);
						editor.putBoolean(Constants.kGESTRUECLOSE, true);
						editor.commit();
						gv.setKey(secondKey);
						tv_tips.setText("修改成功");
						tv_tips.setTextColor(LockScreenSettingActivity.this
								.getResources().getColor(R.color.white));

						// 启动超时退出服务
						Intent intent = new Intent(BaseActivity
								.getTopActivity(), TimeoutService.class);
						BaseActivity.getTopActivity().startService(intent);

						LKAlertDialog dialog1 = new LKAlertDialog(LockScreenSettingActivity.this);
						dialog1.setTitle("提示");
						dialog1.setMessage("手势设置成功");
						dialog1.setCancelable(false);
						dialog1.setPositiveButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.dismiss();
								Intent it = new Intent();  
				                it.putExtra("isOpen", true);  
				                setResult(Activity.RESULT_OK, it); 
								LockScreenSettingActivity.this.finish();
							}
						});
						dialog1.create().show();
						
					} else {
						// 手势设置失败
						tv_tips.setText("与上一次绘制不一致，请重新绘制");
						tv_tips.setTextColor(LockScreenSettingActivity.this
								.getResources().getColor(R.color.red));
						Animation shakeAnim = AnimationUtils.loadAnimation(
								LockScreenSettingActivity.this, R.anim.shake_x);
						shakeAnim.setDuration(700);
						tv_tips.startAnimation(shakeAnim);
					}
				}

			}
		});
		
		
		getDownLoadHead();
	}

	@Override
	public void onClick(View arg0) {

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			Intent it = new Intent();  
            it.putExtra("isOpen", ApplicationEnvironment.getInstance().getPreferences(this).getBoolean(Constants.kGESTRUECLOSE, false));  
            setResult(Activity.RESULT_OK, it); 
			LockScreenSettingActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	// 下载图像
	private void getDownLoadHead() {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("PHONENUMBER", ApplicationEnvironment.getInstance()
				.getPreferences(this).getString(Constants.kUSERNAME, ""));

		LKHttpRequest req1 = new LKHttpRequest(
				TransferRequestTag.GetDownLoadHead, tempMap,
				getDownLoadHeadHandler());

		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue(null,
				new LKHttpRequestQueueDone() {

					@Override
					public void onComplete() {
						super.onComplete();
					}
				});
	}

	private LKAsyncHttpResponseHandler getDownLoadHeadHandler() {
		return new LKAsyncHttpResponseHandler() {

			@Override
			public void successAction(Object obj) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> map = (HashMap<String, Object>) obj;
				String RSPCOD = (String) map.get("RSPCOD");
				String RSPMSG = (String) map.get("RSPMSG");

				if (RSPCOD.equals("000000")) {

					if ((String) map.get("HEADIMG") != null) {
						ibtn_head.setImageBitmap(BitmapUtil
								.convertStringToBitmap((String) map
										.get("HEADIMG")));
					}
				} else {
					Toast.makeText(LockScreenSettingActivity.this, RSPMSG,
							Toast.LENGTH_SHORT).show();
				}

			}

		};
	}
}

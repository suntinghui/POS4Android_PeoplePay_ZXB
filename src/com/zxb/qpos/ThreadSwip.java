package com.zxb.qpos;

import java.util.HashMap;

import com.fncat.xswipe.controller.ErrorCode;
import com.zxb.client.AppDataCenter;
import com.zxb.client.Constants;
import com.zxb.util.StringUtil;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * 刷卡+6位密码
 * 
 * @author Fncat
 * 
 */
public class ThreadSwip extends Thread {
	private Handler mHandler;
	private Context mContext;

	public ThreadSwip(Handler mHandler, Context mContext) {
		this.mHandler = mHandler;
		this.mContext = mContext;
	}

	@Override
	public void run() {
		Log.e("---", "启动刷卡。。。");
		
		int r = ZXBPOS.getPOSManage().requestSwipeCard(AppDataCenter.encryptFactor, AppDataCenter.Adc);
		
		ZXBPOS.handSwipData(mHandler, r);
	}
	
}

package com.zxb.qpos;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.zxb.client.AppDataCenter;

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
		
		ZXBPOS.handData(mHandler, r);
	}
	
}

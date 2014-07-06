package com.zxb.qpos;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.fncat.xswipe.controller.ErrorCode;
import com.zxb.client.AppDataCenter;

/**
 * 刷卡+6位密码
 * 
 * @author Fncat
 * 
 */
public class ThreadDeviceIdAndSwip extends Thread {
	private Handler mHandler;
	private Context mContext;

	public ThreadDeviceIdAndSwip(Handler mHandler, Context mContext) {
		this.mHandler = mHandler;
		this.mContext = mContext;
	}

	@Override
	public void run() {
		int r1 = ZXBPOS.getPOSManage().getDeviceInfo(AppDataCenter.Adc, AppDataCenter.DevTypes, AppDataCenter.deviceId, AppDataCenter.version);
		if (r1 == ErrorCode.SUCCESS){
			Log.e("---", "启动刷卡。。。");
			int r2 = ZXBPOS.getPOSManage().requestSwipeCard(AppDataCenter.encryptFactor, AppDataCenter.Adc);
			
			ZXBPOS.handData(mHandler, r2);
		} else {
			ZXBPOS.handData(mHandler, r1);
		}
		
	}
	
}

package com.zxb.qpos;

import android.content.Context;
import android.os.Handler;

import com.zxb.client.AppDataCenter;

/**
 * 获取设备终端号
 * 
 * @author Fncat
 * 
 */
public class ThreadDeviceID extends Thread {
	private Handler mHandler;
	private Context mContext;

	public ThreadDeviceID(Handler mHandler, Context mContext) {
		this.mHandler = mHandler;
		this.mContext = mContext;
	}

	@Override
	public void run() {
		int r = ZXBPOS.getPOSManage().getDeviceInfo(AppDataCenter.Adc, AppDataCenter.DevTypes, AppDataCenter.deviceId, AppDataCenter.version);
		ZXBPOS.handData(mHandler, r);
	}

}

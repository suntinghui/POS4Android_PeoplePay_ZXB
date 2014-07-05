package com.zxb.qpos;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.zxb.activity.BaseActivity;
import com.zxb.client.AppDataCenter;
import com.zxb.util.ByteUtil;

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
		
		/*
		Log.e("---", AppDataCenter.Adc[0]+"---"+AppDataCenter.Adc[1]);
		Log.e("---", AppDataCenter.DevTypes[0]);
		Log.e("---", ByteUtil.byte2hex(AppDataCenter.deviceId));
		Log.e("---", ByteUtil.byte2hex(AppDataCenter.version));
		*/
		
		ZXBPOS.handData(mHandler, r);
	}

}

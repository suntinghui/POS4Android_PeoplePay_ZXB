package com.zxb.qpos;

import com.fncat.xswipe.controller.ErrorCode;
import com.zxb.client.AppDataCenter;
import com.zxb.util.ByteUtil;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * 密码加密
 * 
 * @author Fncat
 * 
 */
public class ThreadCalcMac extends Thread {
	private Handler mHandler;
	private Context mContext;
	private String macStr;

	public ThreadCalcMac(Handler mHandler, Context mContext, String macStr) {
		this.mHandler = mHandler;
		this.mContext = mContext;
		this.macStr = macStr;
	}

	@Override
	public void run() {
		try {

			Log.e("***", "计算mac...");

			int r = ZXBPOS.getPOSManage().GetMAC(macStr, AppDataCenter.MAC);

			if (r == ErrorCode.SUCCESS) {
				Log.e("***", "计算mac成功...");

				Log.e("***", "mac:" + AppDataCenter.MAC[0]);
			} else {
				Log.e("***", "计算mac失败...");
			}

			ZXBPOS.handData(mHandler, r);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

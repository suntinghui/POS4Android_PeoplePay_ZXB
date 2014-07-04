package com.zxb.qpos;

import com.fncat.xswipe.controller.ErrorCode;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * 更新密钥
 * 
 * @author Fncat
 * 
 */
public class ThreadUpDataKey extends Thread {
	private Handler mHandler;
	private Context mContext;
	private String keyStr;

	public ThreadUpDataKey(Handler mHandler, Context mContext, String keyStr) {
		this.mHandler = mHandler;
		this.mContext = mContext;
		this.keyStr = keyStr;
	}

	@Override
	public void run() {
		
		int r = ZXBPOS.getPOSManage().Change_KEY(keyStr);
		
		if (r == ErrorCode.SUCCESS){
			Log.e("===", "签到成功。。。");
		} else {
			Log.e("===", "签到失败。。。");
		}
		
		
		ZXBPOS.handData(mHandler, r);
	}

}

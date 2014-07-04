package com.zxb.qpos;

import android.content.Context;
import android.os.Handler;

/**
 * 打开刷卡器
 * 
 * @author Fncat
 * 
 */
public class ThreadOpen extends Thread {
	private Handler mHandler;
	private Context mContext;

	public ThreadOpen(Handler mHandler, Context mContext) {
		this.mHandler = mHandler;
		this.mContext = mContext;
	}

	@Override
	public void run() {
		try {
			
			ZXBPOS.getPOSManage().open();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

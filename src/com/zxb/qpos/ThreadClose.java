package com.zxb.qpos;

import android.content.Context;
import android.os.Handler;

/**
 * 关闭刷卡器
 * 
 * @author Fncat
 * 
 */
public class ThreadClose extends Thread {
	private Handler mHandler;
	private Context mContext;

	public ThreadClose(Handler mHandler, Context mContext) {
		this.mHandler = mHandler;
		this.mContext = mContext;
	}

	@Override
	public void run() {
		try {
			
			ZXBPOS.getPOSManage().Destroy();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

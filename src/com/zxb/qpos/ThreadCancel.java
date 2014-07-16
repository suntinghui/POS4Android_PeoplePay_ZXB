package com.zxb.qpos;

import cn.com.fmsh.util.FM_Bytes;
import android.content.Context;
import android.os.Handler;

/**
 * 关闭刷卡器
 * 
 * @author Fncat
 * 
 */
public class ThreadCancel extends Thread {
	private Handler mHandler;
	private Context mContext;

	public ThreadCancel(Handler mHandler, Context mContext) {
		this.mHandler = mHandler;
		this.mContext = mContext;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(500);
			
			int resultCode = ZXBPOS.getPOSManage().SwiperCardCancel();
			
			ZXBPOS.handData(mHandler, resultCode);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

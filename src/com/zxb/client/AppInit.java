package com.zxb.client;

import android.app.Application;
import android.content.Context;

import com.zxb.qpos.ZXBPOS;

public class AppInit extends Application {

	public static Context context;

	@Override
	public void onCreate() {
		context = getApplicationContext();
		super.onCreate();
		
		ZXBPOS.getPOSManage();
	}

}

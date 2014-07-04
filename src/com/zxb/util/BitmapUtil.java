package com.zxb.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.Base64;

public class BitmapUtil {

	/**
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	
	public static Bitmap convertToBitmap(int w, int h) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 设置为ture只获取图片大小
		opts.inJustDecodeBounds = true;
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
		// 返回为空
		BitmapFactory.decodeFile("/sdcard/head.png", opts);
		int width = opts.outWidth;
		int height = opts.outHeight;
		float scaleWidth = 0.f, scaleHeight = 0.f;
		if (width > w || height > h) {
			// 缩放
			scaleWidth = ((float) width) / w;
			scaleHeight = ((float) height) / h;
		}
		opts.inJustDecodeBounds = false;
		float scale = Math.max(scaleWidth, scaleHeight);
		opts.inSampleSize = (int) scale;
		WeakReference<Bitmap> weak = new WeakReference<Bitmap>(
				BitmapFactory.decodeFile("/sdcard/head.png", opts));
		return Bitmap.createScaledBitmap(weak.get(), w, h, true);
	}
	
	
	public static void saveMyBitmap(Bitmap mBitmap){
		  File f = new File("/sdcard/head.png");
		  try {
		   f.createNewFile();
		  } catch (IOException e) {
		   // TODO Auto-generated catch block
		  }
		  FileOutputStream fOut = null;
		  try {
		   fOut = new FileOutputStream(f);
		  } catch (FileNotFoundException e) {
		   e.printStackTrace();
		  }
		  mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		  try {
		   fOut.flush();
		  } catch (IOException e) {
		   e.printStackTrace();
		  }
		  try {
		   fOut.close();
		  } catch (IOException e) {
		   e.printStackTrace();
		  }
		 }
	
	public static Bitmap convertStringToBitmap(String st)
	{
		// OutputStream out;
		Bitmap bitmap = null;
		try
		{
			// out = new FileOutputStream("/sdcard/aa.jpg");
			byte[] bitmapArray;
			bitmapArray = Base64.decode(st, Base64.DEFAULT);
			bitmap =
					BitmapFactory.decodeByteArray(bitmapArray, 0,
							bitmapArray.length);
			// bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			return bitmap;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	public static String bitmaptoBase64(Bitmap bitmap) {

		// 将Bitmap转换成字符串
		String string = null;
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, bStream);
		
		try {
			bStream.flush();
			bStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] bytes = bStream.toByteArray();
		string = Base64.encodeToString(bytes, Base64.DEFAULT);
		return string;

	}
}
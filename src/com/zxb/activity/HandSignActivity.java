package com.zxb.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zxb.R;
import com.zxb.client.TransferRequestTag;
import com.zxb.network.LKAsyncHttpResponseHandler;
import com.zxb.network.LKHttpRequest;
import com.zxb.network.LKHttpRequestQueue;
import com.zxb.network.LKHttpRequestQueueDone;
import com.zxb.util.StringUtil;

public class HandSignActivity extends BaseActivity implements OnClickListener {

	private TextView amountText = null;
	private Bitmap bitmap = null;

	private Button okButton = null;
	private Button clearButton = null;
	private LinearLayout paintLayout = null;

	private PaintView paintView = null;

	private boolean hasSign = false; // 简单判断用户是否有签名

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_handsign);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置成全屏模式
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 强制为横屏

		amountText = (TextView) this.findViewById(R.id.amount);
		amountText.setText(StringUtil.String2SymbolAmount(getIntent().getStringExtra("AMOUNT")));

		okButton = (Button) this.findViewById(R.id.okButton);
		okButton.setOnClickListener(this);
		clearButton = (Button) this.findViewById(R.id.clearButton);
		clearButton.setOnClickListener(this);

		paintLayout = (LinearLayout) this.findViewById(R.id.paintLayout);
	}

	// 当activity显示出来的时候执行此方法
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (hasFocus && null == paintView) {
			// 首先创建一个新的位图
			bitmap = Bitmap.createBitmap(paintLayout.getWidth(), paintLayout.getHeight(), Config.ARGB_8888);

			paintView = new PaintView(this);
			paintLayout.addView(paintView, new LayoutParams(paintLayout.getWidth(), paintLayout.getHeight()));
			paintView.requestFocus();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.okButton:
			if (hasSign) {
				new UploadSignImageTask().execute();

			} else {
				Toast.makeText(this, "您还没有签名，请先完成签名", Toast.LENGTH_SHORT).show();
			}

			break;

		case R.id.clearButton:
			if (hasSign) {
				paintView.clear();
			}

			break;
		}
	}

	class PaintView extends View {
		private Paint paint;
		private Canvas cacheCanvas;
		private Path path;

		public PaintView(Context context) {
			super(context);

			initView();
		}

		private void initView() {
			// 首先打印水印
			// drawMD5OnView();
			this.setBackgroundDrawable(new BitmapDrawable(bitmap));

			// 初始化画笔，准备签名
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStrokeWidth(5);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.BLACK);
			path = new Path();
			cacheCanvas = new Canvas(bitmap);

		}

		public void clear() {
			hasSign = false;

			if (cacheCanvas != null) {
				paintLayout.removeAllViews();

				// 首先创建一个新的位图
				bitmap = Bitmap.createBitmap(paintLayout.getWidth(), paintLayout.getHeight(), Config.ARGB_8888);

				paintView = new PaintView(HandSignActivity.this);
				paintLayout.addView(paintView, new LayoutParams(paintLayout.getWidth(), paintLayout.getHeight()));
				paintLayout.invalidate();
			}
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawBitmap(bitmap, 0, 0, null);
			canvas.drawPath(path, paint);
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {

			int curW = bitmap != null ? bitmap.getWidth() : 0;
			int curH = bitmap != null ? bitmap.getHeight() : 0;
			if (curW >= w && curH >= h) {
				return;
			}

			if (curW < w)
				curW = w;
			if (curH < h)
				curH = h;

			Bitmap newBitmap = Bitmap.createBitmap(curW, curH, Bitmap.Config.ARGB_8888);
			Canvas newCanvas = new Canvas();
			newCanvas.setBitmap(newBitmap);
			if (bitmap != null) {
				newCanvas.drawBitmap(bitmap, 0, 0, null);
			}
			bitmap = newBitmap;
			cacheCanvas = newCanvas;
		}

		private float cur_x, cur_y;

		@Override
		public boolean onTouchEvent(MotionEvent event) {

			float x = event.getX();
			float y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				cur_x = x;
				cur_y = y;
				path.moveTo(cur_x, cur_y);
				break;
			}

			case MotionEvent.ACTION_MOVE: {
				// 简单认为用户移动了手指即有签名
				hasSign = true;

				path.quadTo(cur_x, cur_y, x, y);
				cur_x = x;
				cur_y = y;
				break;
			}

			case MotionEvent.ACTION_UP: {
				cacheCanvas.drawPath(path, paint);
				path.reset();
				break;
			}
			}

			invalidate();

			return true;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// 此处写处理的事件
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	// 上传签购单
	class UploadSignImageTask extends AsyncTask{
		
		String photoStr = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			HandSignActivity.this.showDialog(BaseActivity.PROGRESS_DIALOG, "正在解析签名请稍候...");
		}
		
		@Override
		protected Object doInBackground(Object... arg0) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);// (0 - 100)压缩文件
			photoStr = StringUtil.bytes2HexString(getBitmapByte(bitmap));
			
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			
			HashMap<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("TRANCODE", "199010");
			tempMap.put("LOGNO", getIntent().getStringExtra("LOGNO"));
			tempMap.put("ELESIGNA", photoStr);

			LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.UploadSignImage, tempMap, getUploadImageHandler());

			new LKHttpRequestQueue().addHttpRequest(req1).executeQueue("正在上传图片请稍候...", new LKHttpRequestQueueDone() {

				@Override
				public void onComplete() {
					super.onComplete();

				}
			});
		}

	}

	private LKAsyncHttpResponseHandler getUploadImageHandler() {
		return new LKAsyncHttpResponseHandler() {

			@Override
			public void successAction(Object obj) {

				HashMap<String, String> respMap = (HashMap<String, String>) obj;
				if ("00".equals(respMap.get("RSPCOD"))) {

					Intent intent = new Intent(HandSignActivity.this, ConsumeSuccessActivity.class);
					intent.putExtra("LOGNO", getIntent().getStringExtra("LOGNO"));
					startActivityForResult(intent, 0);

				} else {
					Toast.makeText(HandSignActivity.this, respMap.get("RSPMSG"), Toast.LENGTH_SHORT).show();
				}
			}

		};
	}

	public byte[] getBitmapByte(Bitmap bitmap) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(resultCode == RESULT_OK){
			this.setResult(RESULT_OK);
			this.finish();
		}
	}
}

package com.zxb.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.zxb.R;
import com.zxb.client.ApplicationEnvironment;
import com.zxb.client.Constants;
import com.zxb.client.TransferRequestTag;
import com.zxb.network.LKAsyncHttpResponseHandler;
import com.zxb.network.LKHttpRequest;
import com.zxb.network.LKHttpRequestQueue;
import com.zxb.network.LKHttpRequestQueueDone;

// 上传头像
public class UploadImagesActivity extends BaseActivity implements OnClickListener {
	public static final String IMAGE_UNSPECIFIED = "image/*";

	private File sdcardTempFile;

	private byte[] mContent;
	private Bitmap myBitmap;
	private String mImagePath;

	private AlertDialog dialog;
	private int index = 1;

	private ImageView iv_one;
	private ImageView iv_two;
	private ImageView iv_three;
	private ImageView iv_four;

	private String str_one;
	private String str_two;
	private String str_three;
	private String str_four;

	private HashMap<String, String> fromForeMap;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_images);

		fromForeMap = (HashMap<String, String>) this.getIntent().getSerializableExtra("map");
		sdcardTempFile = new File("/mnt/sdcard/", "tmp_pic_" + SystemClock.currentThreadTimeMillis() + ".jpg");

		iv_one = (ImageView) findViewById(R.id.iv_one);
		iv_one.setOnClickListener(this);
		iv_two = (ImageView) findViewById(R.id.iv_two);
		iv_two.setOnClickListener(this);
		iv_three = (ImageView) findViewById(R.id.iv_three);
		iv_three.setOnClickListener(this);
		iv_four = (ImageView) findViewById(R.id.iv_four);
		iv_four.setOnClickListener(this);
		Button btn_next = (Button) findViewById(R.id.btn_next);
		btn_next.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.iv_one:
			index = 1;
			showDialog();
			break;
		case R.id.iv_two:
			index = 2;
			showDialog();
			break;
		case R.id.iv_three:
			index = 3;
			showDialog();
			break;
		case R.id.iv_four:
			index = 4;
			showDialog();
			break;
		case R.id.btn_next:
			if (chechValue()) {

				HashMap<String, Object> tempMap = new HashMap<String, Object>();
				tempMap.put("TRANCODE", "199030");
				tempMap.put("PHONENUMBER", ApplicationEnvironment.getInstance().getPreferences(this).getString(Constants.kUSERNAME, ""));
				tempMap.put("USERNAME", fromForeMap.get("USERNAME"));
				tempMap.put("IDNUMBER", fromForeMap.get("IDNUMBER"));
				tempMap.put("MERNAME", fromForeMap.get("MERNAME"));
				tempMap.put("SCOBUS", fromForeMap.get("SCOBUS"));
				tempMap.put("MERADDRESS", fromForeMap.get("MERADDRESS"));
				tempMap.put("TERMID", fromForeMap.get("TERMID"));
				tempMap.put("BANKUSERNAME", fromForeMap.get("BANKUSERNAME"));
				tempMap.put("BANKAREA", fromForeMap.get("BANKAREA"));
				tempMap.put("BIGBANKCOD", fromForeMap.get("BIGBANKCOD"));
				tempMap.put("BIGBANKNAM", fromForeMap.get("BIGBANKNAM"));
				tempMap.put("BANKCOD", fromForeMap.get("BANKCOD"));
				tempMap.put("BANKNAM", fromForeMap.get("BANKNAM"));
				tempMap.put("BANKACCOUNT", fromForeMap.get("BANKACCOUNT"));

				tempMap.put("MYPIC", str_one);
				tempMap.put("IDPICURL", str_two);
				tempMap.put("CARDPIC2", str_three);
				tempMap.put("CARDPIC", str_four);
				LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.Authentication, tempMap, getAuthenticationHandler());

				new LKHttpRequestQueue().addHttpRequest(req1).executeQueue("正在获取数据请稍候...", new LKHttpRequestQueueDone() {

					@Override
					public void onComplete() {
						super.onComplete();

					}
				});

			}

			break;

		default:
			break;
		}

	}

	private Boolean chechValue() {
		if (str_one == null || str_one.length() == 0) {
			Toast.makeText(UploadImagesActivity.this, "身份证正面照片不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (str_two == null || str_two.length() == 0) {
			Toast.makeText(UploadImagesActivity.this, "身份证反面照片不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (str_three == null || str_three.length() == 0) {
			Toast.makeText(UploadImagesActivity.this, "收款银行卡照片不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (str_four == null || str_four.length() == 0) {
			Toast.makeText(UploadImagesActivity.this, "申请人手持身份证照片不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private void showDialog() {
		if (dialog == null) {
			dialog = new AlertDialog.Builder(this).setItems(new String[] { "相机", "相册" }, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == 0) {
						String status = Environment.getExternalStorageState();
						if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
							Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							try {
								sdcardTempFile.createNewFile();
								i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(sdcardTempFile));// 将拍摄的照片信息存到capturefile中
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							startActivityForResult(i, 100);// 用户点击了从照相机获取
						} else {
							showToast("没有SD卡");
						}

					} else {

						Intent intent = new Intent(Intent.ACTION_PICK, null);
						intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
						startActivityForResult(intent, 101);

					}
				}
			}).create();
		}
		if (!dialog.isShowing()) {
			dialog.show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 100 || null != data) {

			mImagePath = sdcardTempFile.getAbsolutePath();
			File file = new File(mImagePath);
			myBitmap = decodeFile(file);

		}
		if (requestCode == 101 || null != data ) {

			try {
				ContentResolver resolver = getContentResolver();
				Uri originalUri = data.getData();// 取数据

				mContent = readStream(resolver.openInputStream(Uri.parse(originalUri.toString())));
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inJustDecodeBounds = true;
				opt.inDither = false;
				opt.inPurgeable = true;
				opt.inSampleSize = 3;
				// opt.inTempStorage = new byte[12 * 1024];
				opt.inJustDecodeBounds = false;
				// 将字节数组转换为ImageView可调用的Bitmap对象
				myBitmap = getPicFromBytes(mContent, opt);

				mImagePath = getPath(originalUri);
			} catch (Exception e) {
				System.out.print(e.getMessage());
			}

		}

		switch (index) {// MYPIC、IDPIC、IDPIC2、CARDPIC
		case 1:
			iv_one.setImageBitmap(myBitmap);
			getUpLoadImage("IDPIC");
			break;
		case 2:
			iv_two.setImageBitmap(myBitmap);
			getUpLoadImage("IDPIC2");
			break;
		case 3:
			iv_three.setImageBitmap(myBitmap);
			getUpLoadImage("CARDPIC");
			break;
		case 4:
			iv_four.setImageBitmap(myBitmap);
			getUpLoadImage("MYPIC");
			break;
		default:
			break;
		}
	}

	private String getPath(Uri originalUri) {
		String[] imgs = { MediaStore.Images.Media.DATA };// 将图片URI转换成存储路径
		Cursor cursor = this.managedQuery(originalUri, imgs, null, null, null);
		int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(index);
	}

	// 相机调用的方法getPicFromBytes，将字节数组转换为ImageView可调用的Bitmap对象
	public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
		if (bytes != null)
			if (opts != null)
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
			else
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return null;
	}

	// 相机调用readStream，将图片内容解析成字节数组
	public static byte[] readStream(InputStream inStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;
	}

	/**
	 * 按正方形裁切图片
	 */
	public static Bitmap ImageCrop(Bitmap bitmap) {
		int w = bitmap.getWidth(); // 得到图片的宽，高
		int h = bitmap.getHeight();

		int wh = w > h ? h : w;// 裁切后所取的正方形区域边长

		int retX = w > h ? (w - h) / 2 : 0;// 基于原图，取正方形左上角x坐标
		int retY = w > h ? 0 : (h - w) / 2;

		// 下面这句是关键
		return Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
	}

	// 上传图片
	private void getUpLoadImage(String type) {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("TRANCODE", "199021");
		tempMap.put("PHONENUMBER", ApplicationEnvironment.getInstance().getPreferences(this).getString(Constants.kUSERNAME, ""));
		tempMap.put("FILETYPE", type); // MYPIC、IDPIC、IDPIC2、CARDPIC
		tempMap.put("PHOTOS", imgToBase64(mImagePath));// bitmap_zoom
														// imgToBase64(mImagePath)
		LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.UpLoadImage, tempMap, getUpLoadImageHandler());

		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue("正在获取数据请稍候...", new LKHttpRequestQueueDone() {

			@Override
			public void onComplete() {
				super.onComplete();

			}
		});
	}

	private LKAsyncHttpResponseHandler getUpLoadImageHandler() {
		return new LKAsyncHttpResponseHandler() {

			@Override
			public void successAction(Object obj) {
				HashMap<String, String> respMap = (HashMap<String, String>) obj;
				if ("00".equals(respMap.get("RSPCOD"))) {
					if (respMap.get("RSPMSG") != null && respMap.get("RSPMSG").length() != 0) {
						String showStr = "";
						Toast.makeText(UploadImagesActivity.this, "图片上传成功", Toast.LENGTH_SHORT).show();
						switch (index) {
						case 1:
							str_one = respMap.get("FILENAME");
							iv_one.setImageBitmap(myBitmap);
							showStr = "身份证正面照片上传成功";
							break;
						case 2:
							str_two = respMap.get("FILENAME");
							iv_two.setImageBitmap(myBitmap);
							showStr = "身份证反面照片上传成功";
							break;
						case 3:
							str_three = respMap.get("FILENAME");
							iv_three.setImageBitmap(myBitmap);
							showStr = "收款银行卡照片上传成功";
							break;
						case 4:
							str_four = respMap.get("FILENAME");
							iv_four.setImageBitmap(myBitmap);
							showStr = "申请人手持身份证照片上传成功";
							break;

						default:
							break;
						}
					}
				}
			}

		};
	}

	public String imgToBase64(String imgPath) {
		WindowManager windowManager0 = getWindowManager();
		Display display = windowManager0.getDefaultDisplay();
		int height = display.getHeight();
		int width = display.getWidth();

		Bitmap bitmap = null;
		if (imgPath != null && imgPath.length() > 0) {
			FileDescriptor fd;
			try {
				fd = new FileInputStream(imgPath).getFD();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				// BitmapFactory.decodeFile(imgFile, options);
				BitmapFactory.decodeFileDescriptor(fd, null, options);

				int bmpheight = options.outHeight;
				int bmpWidth = options.outWidth;
				int inSampleSize = bmpheight / height > bmpWidth / width ? bmpheight / height : bmpWidth / width;

				if (inSampleSize > 1)
					if (inSampleSize == 2) {// 小米3
						options.inSampleSize = inSampleSize * 6;// 设置缩放比例
					} else if (inSampleSize == 3) {// 华为C8220
						if (height > 800) {
							options.inSampleSize = inSampleSize * 4;
						} else {
							options.inSampleSize = inSampleSize * 2;// 设置缩放比例
						}
					} else if (inSampleSize == 4) {
						options.inSampleSize = inSampleSize * 2;
					} else if (inSampleSize == 5) {
						options.inSampleSize = inSampleSize * 2;
					} else {
						options.inSampleSize = inSampleSize * 2;
					}
				// 这里一定要将其设置回false，因为之前我们将其设置成了true
				// 设置inJustDecodeBounds为true后，decodeFile并不分配空间，即，BitmapFactory解码出来的Bitmap为Null,但可计算出原始图片的长度和宽度
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeFile(imgPath, options);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		if (bitmap == null) {
			return null;
		}
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

			out.flush();
			out.close();

			byte[] imgBytes = out.toByteArray();
			return Base64.encodeToString(imgBytes, Base64.DEFAULT);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		} finally {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	// 实名认证

	private LKAsyncHttpResponseHandler getAuthenticationHandler() {
		return new LKAsyncHttpResponseHandler() {

			@Override
			public void successAction(Object obj) {
				HashMap<String, String> respMap = (HashMap<String, String>) obj;
				if ("00".equals(respMap.get("RSPCOD"))) {
					Toast.makeText(UploadImagesActivity.this, "实名认证成功！", Toast.LENGTH_SHORT).show();
					UploadImagesActivity.this.setResult(RESULT_OK);
					finish();
				} else {
					Toast.makeText(UploadImagesActivity.this, respMap.get("RSPMSG"), Toast.LENGTH_SHORT).show();
				}
			}

		};
	}

	/*
	 * 压缩图片，避免内存不足报错
	 */
	private Bitmap decodeFile(File f) {
		Bitmap b = null;
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;

			FileInputStream fis = new FileInputStream(f);
			BitmapFactory.decodeStream(fis, null, o);
			fis.close();

			int scale = 1;
			if (o.outHeight > 100 || o.outWidth > 100) {
				scale = (int) Math.pow(2, (int) Math.round(Math.log(100 / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			fis = new FileInputStream(f);
			b = BitmapFactory.decodeStream(fis, null, o2);
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}
}

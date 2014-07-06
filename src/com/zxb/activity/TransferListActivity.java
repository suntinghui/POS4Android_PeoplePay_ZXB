package com.zxb.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zxb.R;
import com.zxb.client.ApplicationEnvironment;
import com.zxb.client.Constants;
import com.zxb.client.TransferRequestTag;
import com.zxb.model.CashModel;
import com.zxb.model.TradeModel;
import com.zxb.network.LKAsyncHttpResponseHandler;
import com.zxb.network.LKHttpRequest;
import com.zxb.network.LKHttpRequestQueue;
import com.zxb.network.LKHttpRequestQueueDone;
import com.zxb.view.CashAdapter;
import com.zxb.view.DateSlider;
import com.zxb.view.LKAlertDialog;
import com.zxb.view.MonthYearDateSlider;
import com.zxb.view.TransferAdapter;

// 流水
public class TransferListActivity extends BaseActivity implements OnClickListener, OnItemClickListener, OnScrollListener {

	static final int MONTHYEARDATESELECTOR_ID = 4;

	private static final int INDEX_CONTENT = 0;
	private static final int INDEX_PATH = 1;

	protected LayoutInflater mLayoutInflater;

	private View mParentContent, mParentPath;
	private TextView mTvContent, mTvPath;
	private View mLineContent, mLinePath;

	private int mTextColorSelected;
	private int mTextColorUnselected;

	private ViewPager mViewPager;
	private ListView mContentLv, mCashLv;
	private TransferAdapter mContentAdapter;
	private CashAdapter mCashAdapter;

	private ArrayList<TradeModel> arrayTransfer = new ArrayList<TradeModel>();
	private ArrayList<CashModel> arrayCash = new ArrayList<CashModel>();

	private TextView tv_totalnum;
	private TextView tv_totalmoney;

	private long exitTimeMillis = 0;
	private Button btn_refresh;
	private Button btn_date;

	private ImageView iv_nodata;

	private Boolean isCurrentList = true; // true: 交易流水 false:现金流水

	private String totalAmountTransfer;
	private String totalAmountCash = "￥0.0";
	private String totalNumCash = "0";
	private String totalNumTransfer;

	private long totalAmount = 0L;

	private View moreView; // 加载更多页面
	private int lastItem;
	private int count;

	private int totalPage = 0;
	private int currentPage = 0;

	private int currentDelete = 0;

	private boolean clickFlag = false;
	private String dateStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_transfer_list);

		mLayoutInflater = LayoutInflater.from(this);

		Resources res = getResources();
		mTextColorSelected = res.getColor(R.color.tab_text_selected);
		mTextColorUnselected = res.getColor(R.color.tab_text_unselected);

		iv_nodata = (ImageView) this.findViewById(R.id.iv_nodata);

		btn_refresh = (Button) findViewById(R.id.btn_refresh);
		btn_refresh.setOnClickListener(this);
		

		btn_date = (Button) findViewById(R.id.btn_date);
		btn_date.setOnClickListener(this);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM");
		dateStr=sdf.format(new java.util.Date());
		btn_date.setText(dateStr);
		btn_date.setVisibility(View.GONE);
		
		tv_totalnum = (TextView) findViewById(R.id.tv_totalnum);
		tv_totalmoney = (TextView) findViewById(R.id.tv_totalmoney);

		findViews();

		setupViews();

		mContentAdapter = new TransferAdapter(TransferListActivity.this, 0, arrayTransfer);
		mContentLv.setAdapter(mContentAdapter);

		mCashAdapter = new CashAdapter(TransferListActivity.this, 0, arrayCash);
		mCashLv.addFooterView(moreView);
		mCashLv.setAdapter(mCashAdapter);

		queryHistory();
	}

	protected void findViews() {

		mParentContent = findViewById(R.id.vg_content);
		mParentPath = findViewById(R.id.vg_path);

		mTvContent = (TextView) findViewById(R.id.tv_content);
		mTvPath = (TextView) findViewById(R.id.tv_path);

		mLineContent = findViewById(R.id.v_content_line_selected);
		mLinePath = findViewById(R.id.v_path_line_selected);

		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		mContentLv = (ListView) mLayoutInflater.inflate(R.layout.layout_list_view, null);
		mCashLv = (ListView) mLayoutInflater.inflate(R.layout.layout_list_view, null);
		mContentLv.setOnItemClickListener(this);
		mCashLv.setOnItemClickListener(this);
		mCashLv.setOnScrollListener(this);

		moreView = getLayoutInflater().inflate(R.layout.load, null);
	}

	protected void setupViews() {

		mParentContent.setOnClickListener(this);
		mParentPath.setOnClickListener(this);

		List<View> list = new ArrayList<View>();
		list.add(mContentLv);
		list.add(mCashLv);
		mViewPager.setAdapter(new MyPagerAdapter(list));
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	private void switchTo(int activeIdx) {

		if (activeIdx == INDEX_CONTENT) {
			btn_date.setVisibility(View.GONE);
			isCurrentList = true;
			if (totalAmount == 0) {
				iv_nodata.setVisibility(View.VISIBLE);
			} else {
				iv_nodata.setVisibility(View.GONE);
			}

			tv_totalnum.setText(totalNumTransfer);
			tv_totalmoney.setText(totalAmountTransfer);
			mTvContent.setTextColor(mTextColorSelected);
			mTvPath.setTextColor(mTextColorUnselected);
			mLineContent.setVisibility(View.VISIBLE);
			mLinePath.setVisibility(View.GONE);

			mContentAdapter.setData(arrayTransfer);
			mContentAdapter.notifyDataSetChanged();
			if (arrayTransfer == null && arrayTransfer.size() == 0) {
				queryHistory();
			}

		} else if (activeIdx == INDEX_PATH) {
			btn_date.setVisibility(View.VISIBLE);
			isCurrentList = false;
			iv_nodata.setVisibility(View.GONE);
			tv_totalnum.setText(totalNumCash);
			tv_totalmoney.setText(totalAmountCash);
			mTvPath.setTextColor(mTextColorSelected);
			mTvContent.setTextColor(mTextColorUnselected);
			mLinePath.setVisibility(View.VISIBLE);
			mLineContent.setVisibility(View.GONE);
			mCashAdapter.setData(arrayCash);
			mCashAdapter.notifyDataSetChanged();
			if (arrayCash == null || arrayCash.size() == 0) {
				queryCashFlow();
			}

		}
		mViewPager.setCurrentItem(activeIdx);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_back:
			this.finish();
			break;

		case R.id.btn_refresh:
			Animation myAnimation = AnimationUtils.loadAnimation(this, R.anim.refresh_anim);
			LinearInterpolator lir = new LinearInterpolator();
			myAnimation.setInterpolator(lir);
			btn_refresh.startAnimation(myAnimation);
			if (isCurrentList) {
				queryHistory();
			} else {
				arrayCash.clear();
				currentPage = 0;
				queryCashFlow();
			}

			break;
		case R.id.btn_date:
			showDialog(MONTHYEARDATESELECTOR_ID);
			break;
		case R.id.vg_content:
			clickFlag = true;
			// 交易流水
			switchTo(INDEX_CONTENT);
			break;

		case R.id.vg_path:
			clickFlag = true;
			// 现金流水
			switchTo(INDEX_PATH);
			break;
		default:
			break;
		}
	}

	// 查询交易明细
	private void queryHistory() {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("TRANCODE", "199008");
		tempMap.put("PHONENUMBER", ApplicationEnvironment.getInstance().getPreferences(TransferListActivity.this).getString(Constants.kUSERNAME, ""));

		LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.FlowQuery, tempMap, queryTransferHandler());

		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue("正在请求数据...", new LKHttpRequestQueueDone() {

			@Override
			public void onComplete() {
				super.onComplete();
			}

		});
	}

	private LKAsyncHttpResponseHandler queryTransferHandler() {
		return new LKAsyncHttpResponseHandler() {

			@SuppressWarnings("rawtypes")
			@Override
			public void successAction(Object obj) {
				arrayTransfer.clear();
				btn_refresh.clearAnimation();
				if (((HashMap) obj).get("RSPCOD").toString().equals("00")) {
					totalAmount = 0L;
					arrayTransfer.addAll((ArrayList<TradeModel>) ((HashMap) obj).get("list"));
					for (int i = 0; i < arrayTransfer.size(); i++) {
						TradeModel model = arrayTransfer.get(i);
						if (model.getTxncd().equalsIgnoreCase("0200200000")) {

						} else {
							totalAmount += Long.parseLong(model.getTxnamt());
						}

					}

					if (arrayTransfer.size() == 0) {
						totalAmountTransfer = "￥0.0";
						totalNumTransfer = "0";
						iv_nodata.setVisibility(View.VISIBLE);
					} else {
						totalAmountTransfer = "￥" + totalAmount / 100.00f;
						totalNumTransfer = arrayTransfer.size() + "";
						iv_nodata.setVisibility(View.GONE);
					}
					tv_totalmoney.setText(totalAmountTransfer);
					tv_totalnum.setText(totalNumTransfer);
					mContentAdapter.notifyDataSetChanged();

				} else if (((HashMap) obj).get("RSPMSG").toString() != null && ((HashMap) obj).get("RSPMSG").toString().length() != 0) {
					Toast.makeText(getApplicationContext(), ((HashMap) obj).get("RSPMSG").toString(), Toast.LENGTH_SHORT).show();
				}

			}

		};

	}

	// 查询现金流水
	private void queryCashFlow() {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("TRANCODE", "200003");
		tempMap.put("PHONENUMBER", ApplicationEnvironment.getInstance().getPreferences(TransferListActivity.this).getString(Constants.kUSERNAME, ""));
		tempMap.put("operationId", "getTransaction");
		tempMap.put("pageIndex", currentPage + "");
		tempMap.put("pageSize", Constants.kPAGESIZE);
		tempMap.put("dateStr", dateStr);

		LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.GetCashCharge, tempMap, queryCashHandler());

		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue("正在请求数据...", new LKHttpRequestQueueDone() {

			@Override
			public void onComplete() {
				super.onComplete();
			}

		});
	}

	private LKAsyncHttpResponseHandler queryCashHandler() {
		return new LKAsyncHttpResponseHandler() {

			@SuppressWarnings("rawtypes")
			@Override
			public void successAction(Object obj) {
				btn_refresh.clearAnimation();
				if (((HashMap) obj).get("RSPCOD").toString().equals("000000")) {
					int totalNum = Integer.valueOf((String) ((HashMap) obj).get("TOTALROWNUMS"));
					if (totalNum == 0) {
						iv_nodata.setVisibility(View.VISIBLE);
						mCashAdapter.notifyDataSetChanged();
						tv_totalmoney.setText("￥0.0");
						tv_totalnum.setText("0");
						totalAmountCash = "￥0.0";
						totalNumCash = "0";
						return;
					}
					totalPage = Integer.valueOf((String) ((HashMap) obj).get("TOTALPAGE"));
					ArrayList<CashModel> tmpArray = (ArrayList<CashModel>) ((HashMap) obj).get("list");
					if (tmpArray == null) {
						iv_nodata.setVisibility(View.VISIBLE);
						mCashAdapter.notifyDataSetChanged();
						tv_totalmoney.setText("￥0.0");
						tv_totalnum.setText("0");
					} else {
						for (int i = 0; i < tmpArray.size(); i++) {
							arrayCash.add(tmpArray.get(i));
						}

						count = arrayCash.size();

						if (arrayCash.size() == 0) {
							totalAmountCash = "￥0.0";
							totalNumCash = "0";
							iv_nodata.setVisibility(View.VISIBLE);
						} else {
							totalAmountCash = "￥" + (String) ((HashMap) obj).get("TOTALTRANSAMT");
							totalNumCash = totalNum + "";
							iv_nodata.setVisibility(View.GONE);
						}
						tv_totalmoney.setText(totalAmountCash);
						tv_totalnum.setText(totalNumCash);
						mCashAdapter.notifyDataSetChanged();
						moreView.setVisibility(View.GONE);
					}

				} else if (((HashMap) obj).get("RSPMSG").toString() != null && ((HashMap) obj).get("RSPMSG").toString().length() != 0) {
					Toast.makeText(getApplicationContext(), ((HashMap) obj).get("RSPMSG").toString(), Toast.LENGTH_SHORT).show();
				}

			}

		};

	}

	// 查询小票
	public void queryTicket(int position) {
		TradeModel model = arrayTransfer.get(position);
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("TRANCODE", "199036");
		tempMap.put("PHONENUMBER", ApplicationEnvironment.getInstance().getPreferences(TransferListActivity.this).getString(Constants.kUSERNAME, ""));
		tempMap.put("LOGNO", model.getLogNo());

		LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.CheckTicket, tempMap, queryTicketHandler());

		new LKHttpRequestQueue().addHttpRequest(req1).executeQueue("正在请求数据...", new LKHttpRequestQueueDone() {

			@Override
			public void onComplete() {
				super.onComplete();
			}

		});
	}

	private LKAsyncHttpResponseHandler queryTicketHandler() {
		return new LKAsyncHttpResponseHandler() {

			@SuppressWarnings("rawtypes")
			@Override
			public void successAction(Object obj) {
				if (((HashMap) obj).get("RSPCOD").toString().equals("000000")) {
					int totalNum = Integer.valueOf((String) ((HashMap) obj).get("TOTALROWNUMS"));
					if (totalNum == 0) {
						iv_nodata.setVisibility(View.VISIBLE);
						mCashAdapter.notifyDataSetChanged();
						tv_totalmoney.setText("￥0.0");
						tv_totalnum.setText("0");
						return;
					}
					totalPage = Integer.valueOf((String) ((HashMap) obj).get("TOTALPAGE"));
					ArrayList<CashModel> tmpArray = (ArrayList<CashModel>) ((HashMap) obj).get("list");
					if (tmpArray == null) {
						iv_nodata.setVisibility(View.VISIBLE);
						mCashAdapter.notifyDataSetChanged();
						tv_totalmoney.setText("￥0.0");
						tv_totalnum.setText("0");
					} else {
						for (int i = 0; i < tmpArray.size(); i++) {
							arrayCash.add(tmpArray.get(i));
						}

						count = arrayCash.size();

						if (arrayCash.size() == 0) {
							totalAmountCash = "￥0.0";
							totalNumCash = "0";
							iv_nodata.setVisibility(View.VISIBLE);
						} else {
							totalAmountCash = "￥" + (String) ((HashMap) obj).get("TOTALTRANSAMT");
							totalNumCash = totalNum + "";
							iv_nodata.setVisibility(View.GONE);
						}
						tv_totalmoney.setText(totalAmountCash);
						tv_totalnum.setText(totalNumCash);
						mCashAdapter.notifyDataSetChanged();
						moreView.setVisibility(View.GONE);
					}

				} else if (((HashMap) obj).get("RSPMSG").toString() != null && ((HashMap) obj).get("RSPMSG").toString().length() != 0) {
					Toast.makeText(getApplicationContext(), ((HashMap) obj).get("RSPMSG").toString(), Toast.LENGTH_SHORT).show();
				}

			}

		};

	}

	// 删除现金记账
	public void deleteCashItem(final String index) {
		currentDelete = Integer.valueOf(index);

		Log.i("delete tag:", currentDelete + "");
		LKAlertDialog dialog = new LKAlertDialog(this);
		dialog.setTitle("提示");
		dialog.setMessage("确定删除当前记账");
		dialog.setCancelable(false);
		dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();

				String index0 = arrayCash.get(Integer.valueOf(index)).getTransId();
				HashMap<String, Object> tempMap = new HashMap<String, Object>();
				tempMap.put("transId", index0);

				LKHttpRequest req1 = new LKHttpRequest(TransferRequestTag.CashDelete, tempMap, deletCashHandler());

				new LKHttpRequestQueue().addHttpRequest(req1).executeQueue("正在请求数据...", new LKHttpRequestQueueDone() {

					@Override
					public void onComplete() {
						super.onComplete();
					}

				});
			}
		});
		dialog.create().show();

	}

	private LKAsyncHttpResponseHandler deletCashHandler() {
		return new LKAsyncHttpResponseHandler() {

			@SuppressWarnings("rawtypes")
			@Override
			public void successAction(Object obj) {
				if (((HashMap) obj).get("RSPCOD").toString().equals("000000")) {
					currentPage = 0;
					arrayCash.clear();
					queryCashFlow();

				} else if (((HashMap) obj).get("RSPMSG").toString() != null && ((HashMap) obj).get("RSPMSG").toString().length() != 0) {
					Toast.makeText(getApplicationContext(), ((HashMap) obj).get("RSPMSG").toString(), Toast.LENGTH_SHORT).show();
					if (arrayCash.size() == 0) {
						iv_nodata.setVisibility(View.VISIBLE);
					} else {
						iv_nodata.setVisibility(View.GONE);
					}
				}

			}

		};

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			queryHistory();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTimeMillis) > 2000) {
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTimeMillis = System.currentTimeMillis();
			} else {
				ArrayList<BaseActivity> list = BaseActivity.getAllActiveActivity();
				for (BaseActivity activity : list) {
					activity.finish();
				}

				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (isCurrentList) {// 交易列表
			Intent intent = new Intent(TransferListActivity.this, TransferDetailActivity.class);
			intent.putExtra("model", arrayTransfer.get(arg2));
			startActivityForResult(intent, 0);
		} else {// 现金列表

		}

	}

	private class MyPagerAdapter extends PagerAdapter {
		List<View> mViews = null;

		public MyPagerAdapter(List<View> views) {
			mViews = views;
		}

		@Override
		public int getCount() {
			return mViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			View view = mViews.get(arg1);
			mViewPager.addView(view, 0);
			return mViews.get(arg1);
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			mViewPager.removeView(mViews.get(position));
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}

	private class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int index) {
			if (!clickFlag) {
				switchTo(index);
			}

			clickFlag = false;
		}

	}

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

		lastItem = arg1 + arg2 - 1; // 减1是因为上面加了个addFooterView

	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		// 下拉到空闲是，且最后一个item的数等于数据的总数时，进行更新
		if (lastItem == count && arg1 == this.SCROLL_STATE_IDLE) {
			moreView.setVisibility(arg0.VISIBLE);

			if (currentPage < totalPage - 1) {
				currentPage++;
				queryCashFlow();
			} else {
				moreView.setVisibility(View.GONE);
				Toast.makeText(this, "已经是最后一页数据", Toast.LENGTH_SHORT).show();
			}

		}

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

	}

	@Override
    protected Dialog onCreateDialog(int id) {
		final Calendar c = Calendar.getInstance();
    	switch (id) {
        case MONTHYEARDATESELECTOR_ID:
            return new MonthYearDateSlider(this,mMonthYearSetListener,c);
            
        case PROGRESS_DIALOG:
			this.showProgressDialog(true);
			break;

		case MODAL_DIALOG:
			this.showAlertDialog();
			break;
        }
    	return super.onCreateDialog(id);
//        return null;
    }
	
	 private DateSlider.OnDateSetListener mMonthYearSetListener =
		        new DateSlider.OnDateSetListener() {
		            public void onDateSet(DateSlider view, Calendar selectedDate) {
		            	String month = String.format("%02d", selectedDate.get(Calendar.MONTH)+1);
		                dateStr = selectedDate.get(Calendar.YEAR)+"-"+month;
		                btn_date.setText(dateStr);
		                currentPage = 0;
		                arrayCash.clear();
		                queryCashFlow();
		            }
		    };
}

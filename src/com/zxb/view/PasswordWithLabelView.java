package com.zxb.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.zxb.R;
import com.zxb.activity.BaseActivity;

public class PasswordWithLabelView extends LinearLayout implements TextWatcher, OnFocusChangeListener, OnTouchListener {

	private LinearLayout rootLayout		= null;
	private TextView textView			= null;
	private EditText editText			= null;
	
	private PopupWindow popup			= null;
	private Context context				= null;
	
	private InputMethodManager imm		= null;
	
	public PasswordWithLabelView(Context context) {
		super(context);
		
		init(context);
	}

	public PasswordWithLabelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init(context);
	}
	
	public String getPwd(){
		return editText.getText().toString();
	}
	private void init(Context context){
		this.context = context;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.password_with_label, this);
		
		rootLayout = (LinearLayout) this.findViewById(R.id.rootLayout);
		textView = (TextView) this.findViewById(R.id.label);
		editText = (EditText) this.findViewById(R.id.text);
		
		editText.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(6)});
		editText.setInputType(InputType.TYPE_CLASS_NUMBER); 
		editText.setFocusable(true);
		editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
		
		editText.addTextChangedListener(this);
		editText.setOnFocusChangeListener(this);
		editText.setOnTouchListener(this);
	}
	

	@Override
	public void afterTextChanged(Editable editable) {
//		pwd = editable.toString();
//		if (pwd.length() == 6){
//			try {
////				encryptPassword(pwd);
////				calcMD5Password(pwd);
//    			pwd = "";
//    			
//			} catch (Exception e) {
//				getEditText().setText("");
//			}
//		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}
	
	// 点击打密码输入框时弹出自定义软键盘
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN){
			this.showPopup();
		}
		return false;
	}

	// 主要是用于有的输入法选择下一项时弹出自定义软键盘
	// 但是也要防止刚进入界面时，界面中密码框第一个获得焦点时弹出软键盘，设置其为只有系统键盘弹出时才弹出自定义软键盘
	// isActive()并不是判断软键盘是否已弹出，而是是否处于活动状态，即此控件是否能调用软键盘
	/*
	 * if(getWindow().getAttributes().softInputMode==WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED){
		 //隐藏软键盘
		 getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		 getWindow().getAttributes().softInputMode=WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED;
		}
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO 测试值为290.或是在showPopup中调用强制显示软键盘。。。
		//Log.e("++", ""+BaseActivity.getTopActivity().getWindow().getAttributes().softInputMode);
		
		if (hasFocus && BaseActivity.getTopActivity().getWindow().getAttributes().softInputMode != 290){
			this.showPopup();
			
		} else {
			this.hidePopup();
		}
	}
	
	private void showPopup(){
		if (null != popup && popup.isShowing()){
			return;
		}
		
		RandomPWDKeyboardView keyboardView = new RandomPWDKeyboardView(context);
		popup = new PopupWindow(keyboardView,LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT, true);
		// 如果没有设置此项目，setOutsideTouchable setFocusable将不起作用。而且必须在setOutsideTouchable setFocusable前面设置
		// 请放心，设置 BackgroundDrawable 并不会改变你在配置文件中设置的背景颜色或图像。
		popup.setBackgroundDrawable(new BitmapDrawable()); 
		popup.setFocusable(false); // 必须为false。否则系统键盘会遮挡自定义键盘
		popup.setTouchable(true); // 必须为true。否则自定义键盘不起作用无法点击，会使背后的系统键盘响应点击事件。
		popup.setOutsideTouchable(false);
		popup.setOnDismissListener(new OnDismissListener(){
			@Override
			public void onDismiss() {
				// 可能有重复调用，但是一定要保证关闭自定义软键盘后一定关闭了系统键盘
				closeSystemKeyBoard();
			}
			
		});
		
		// 非常非常重要的一步，完美解决返回按纽让系统键盘与自定义键盘同时消失！！！
		this.editText.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK){
					popup.dismiss();
				}
				return false;
			}
			
		});
		
		// 必须设置的属性
		keyboardView.setEditText(editText);
		keyboardView.setPopup(popup);
		
		popup.showAtLocation(this, Gravity.BOTTOM, 0, 0);
	}
	
	// 隐藏系统自定义软键盘
	private void hidePopup(){
		if (null != popup && popup.isShowing()){
			// 保证关闭自定义软键盘的时候一定关闭了系统键盘。不过使用系统键盘也没有关系，也能保证加密完成交易。
			closeSystemKeyBoard();
			popup.dismiss();
		}
	}
	
	// 关闭系统输入法
	private void closeSystemKeyBoard(){
		 this.getIMM().hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
	
	private InputMethodManager getIMM(){
		if (null == imm){
			imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		
		return imm;
	}
	
	public EditText getEditText(){
		return this.editText;
	}
	
	public TextView getTextView(){
		return this.textView;
	}
	
	public void setHint(String hint){
		editText.setHint(hint);
	}
	
	public void setLabel(String label){
		textView.setText(label);
	}
	
	public void setHintWithLabel(String label, String hint){
		textView.setText(label);
		editText.setHint(hint);
	}
	
	public void setText(String text){
		editText.setText(text);
	}
	
	public String getText(){
		return editText.getText().toString();
	}
	
	public void setRootLayoutBg(int resid){
		this.rootLayout.setBackgroundResource(resid);
	}
	
	public void setHint(){
		rootLayout.setVisibility(View.INVISIBLE);
	}
	
	public void setInputType(int type){
		editText.setInputType(type);
	}

}

package com.zxb.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zxb.R;

public class CalculatorActivity extends BaseActivity {

	String str = "";
	EditText et;
	int c = 0, flag = 0;
	double b = 0.0, g = 0.0, f = 0.0;
	View vi;

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, "退出");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	// 计算方法
	public double calculater() {
		switch (c) {
		case 0:
			f = g;
			break;
		case 1:
			f = b + g;
			break;
		case 2:
			f = b - g;
			break;
		case 3:
			f = b * g;
			break;
		case 4:
			f = b / g;
			break;
		}

		b = f;
		c = 0;

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calculator);

		// 获得按键
		final Button number[] = new Button[10];
		final Button fuhao[] = new Button[11];

		fuhao[0] = (Button) findViewById(R.id.button01);
		fuhao[1] = (Button) findViewById(R.id.button02);
		fuhao[2] = (Button) findViewById(R.id.button03);
		fuhao[3] = (Button) findViewById(R.id.button04);
		fuhao[4] = (Button) findViewById(R.id.button05);
		fuhao[5] = (Button) findViewById(R.id.button06);
		fuhao[6] = (Button) findViewById(R.id.buttonce);
		fuhao[7] = (Button) findViewById(R.id.buttonc);
		fuhao[8] = (Button) findViewById(R.id.zheng);
		fuhao[9] = (Button) findViewById(R.id.kaifang);
		fuhao[10] = (Button) findViewById(R.id.pingfang);

		number[0] = (Button) findViewById(R.id.button0);
		number[1] = (Button) findViewById(R.id.button1);
		number[2] = (Button) findViewById(R.id.button2);
		number[3] = (Button) findViewById(R.id.button3);
		number[4] = (Button) findViewById(R.id.button4);
		number[5] = (Button) findViewById(R.id.button5);
		number[6] = (Button) findViewById(R.id.button6);
		number[7] = (Button) findViewById(R.id.button7);
		number[8] = (Button) findViewById(R.id.button8);
		number[9] = (Button) findViewById(R.id.button9);

		et = (EditText) findViewById(R.id.textView1);

		Button btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CalculatorActivity.this.finish();

			}

		});

		Button btn_complete = (Button) findViewById(R.id.btn_complete);
		btn_complete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (et.getText().toString().equalsIgnoreCase("Infinity")) {
					Toast.makeText(CalculatorActivity.this, "输入金额不合法", Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent(CalculatorActivity.this, InputMoneyActivity.class);
					intent.putExtra("amount", et.getText().toString().length() == 0 ? "0" : et.getText().toString());
					setResult(RESULT_OK, intent);
					CalculatorActivity.this.finish();
				}

			}
		});
		fuhao[6].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (str.length() == 1 || str.length() == 0) {
					et.setText("0");
					str = "";

				} else {
					str = str.toString().substring(0, str.length() - 1);
					et.setText(str);
				}
				vi = v;
			}
		});

		fuhao[7].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				str = "";
				et.setText("0");
				vi = v;
			}
		});

		fuhao[8].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (vi != fuhao[5] && str != "") {
					char ch = str.charAt(0);
					if (ch == '-')
						str = str.replace("-", "");
					else
						str = "-" + str;
					et.setText(str);
				}
			}
		});

		fuhao[9].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (str != "") {
					double a = Double.parseDouble(str);
					str = Math.sqrt(a) + "";
					et.setText(str);
				}
			}
		});

		fuhao[10].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (str != "") {
					double a = Double.parseDouble(str);
					str = "" + a * a;
					et.setText(str);
				}
			}
		});

		// 设定数字按键
		number[0].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (et.getText().length() == 9)
					return;

				if (flag == 1) {
					str = "";
					str += 0;
					et.setText(str);
					flag = 0;
				} else {
					char ch1[];
					ch1 = str.toCharArray();
					if (!(ch1.length == 1 && ch1[0] == '0')) {
						str += 0;
						et.setText(str);
					}

				}
				vi = v;
			}
		});

		number[1].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (et.getText().length() == 9)
					return;
				if (flag == 1) {
					str = "";
					str += 1;
					et.setText(str);
					flag = 0;
				} else {
					str += 1;
					et.setText(str);
				}
				vi = v;
			}
		});

		number[2].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (et.getText().length() == 9)
					return;
				if (flag == 1) {
					str = "";
					str += 2;
					et.setText(str);
					flag = 0;
				} else {
					str += 2;
					et.setText(str);
				}
				vi = v;
			}
		});

		number[3].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (et.getText().length() == 9)
					return;
				if (flag == 1) {
					str = "";
					str += 3;
					et.setText(str);
					flag = 0;
				} else {
					str += 3;
					et.setText(str);
				}
				vi = v;
			}
		});

		number[4].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (et.getText().length() == 9)
					return;
				if (flag == 1) {
					str = "";
					str += 4;
					et.setText(str);
					flag = 0;
				} else {
					str += 4;
					et.setText(str);
				}
				vi = v;
			}
		});

		number[5].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (et.getText().length() == 9)
					return;
				if (flag == 1) {
					str = "";
					str += 5;
					et.setText(str);
					flag = 0;
				} else {
					str += 5;
					et.setText(str);
				}
				vi = v;
			}
		});

		number[6].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (et.getText().length() == 9)
					return;
				if (flag == 1) {
					str = "";
					str += 6;
					et.setText(str);
					flag = 0;
				} else {
					str += 6;
					et.setText(str);
				}
				vi = v;
			}
		});

		number[7].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (et.getText().length() == 9)
					return;
				if (flag == 1) {
					str = "";
					str += 7;
					et.setText(str);
					flag = 0;
				} else {
					str += 7;
					et.setText(str);
				}
				vi = v;
			}
		});

		number[8].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (et.getText().length() == 9)
					return;
				if (flag == 1) {
					str = "";
					str += 8;
					et.setText(str);
					flag = 0;
				} else {
					str += 8;
					et.setText(str);
				}
				vi = v;
			}
		});

		number[9].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (et.getText().length() == 9)
					return;
				if (flag == 1) {
					str = "";
					str += 9;
					et.setText(str);
					flag = 0;
				} else {
					str += 9;
					et.setText(str);
				}
				vi = v;
			}
		});

		// 设定符号键

		// 加
		fuhao[0].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (str != "") {
					if (vi == fuhao[0] || vi == fuhao[1] || vi == fuhao[2] || vi == fuhao[3]) {
						c = 1;
					}

					else {
						g = Double.parseDouble(str);
						calculater();
						str = "" + f;
						et.setText(str);
						c = 1;
						flag = 1;
						vi = v;
					}
				}
			}
		});

		// 减
		fuhao[1].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (str != "") {
					if (vi == fuhao[0] || vi == fuhao[1] || vi == fuhao[2] || vi == fuhao[3]) {
						c = 2;
					}

					else {
						g = Double.parseDouble(str);
						calculater();
						str = "" + f;
						et.setText(str);
						c = 2;
						flag = 1;
						vi = v;
					}
				}
			}
		});

		// 乘
		fuhao[2].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (str != "") {
					if (vi == fuhao[0] || vi == fuhao[1] || vi == fuhao[2] || vi == fuhao[3]) {
						c = 3;
					}

					else {
						g = Double.parseDouble(str);
						calculater();
						str = "" + f;
						et.setText(str);
						c = 3;
						flag = 1;
						vi = v;
					}
				}
			}
		});

		// 除
		fuhao[3].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (str != "") {
					if (vi == fuhao[0] || vi == fuhao[1] || vi == fuhao[2] || vi == fuhao[3]) {
						c = 4;
					}

					else {
						g = Double.parseDouble(str);
						calculater();
						str = "" + f;
						et.setText(str);
						c = 4;
						flag = 1;
						vi = v;
					}
				}
			}
		});

		// 等号
		fuhao[4].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (str != "" && vi != fuhao[0] && vi != fuhao[1] && vi != fuhao[2] && vi != fuhao[3]) {
					g = Double.parseDouble(str);
					calculater();
					str = "" + f;
					et.setText(str);
					flag = 1;
					vi = v;

				}
			}
		});

		// 小数点
		fuhao[5].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (str == "") {
					str += ".";
					et.setText(str);
				} else {
					char ch1[];
					int x = 0;
					ch1 = str.toCharArray();
					for (int i = 0; i < ch1.length; i++)
						if (ch1[i] == '.')
							x++;
					if (x == 0) {
						str += ".";
						et.setText(str);
					}
				}

			}
		});
	}

}
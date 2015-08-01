package com.example.activity;

import com.example.GameActivity.R;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MockDialog extends Activity {
	private int mType;
	private int nowButton = 0;
	private int[] imageDrawableId;
	private int[] buttonId;
	private Button[] button = new Button[6];
	private String[] name;
	private ImageView imageView;
	public Button bn;
	public TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 去掉Activity上面的状态栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		Bundle bundle = this.getIntent().getExtras();
		mType = bundle.getInt("Type");

		if (mType == GameActivity.ABOUT || mType == GameActivity.ABOUTANDHELP) {
			this.showAboutTest();
		} else if (mType == GameActivity.HELP) {
			this.showHelpTest();
		}
	}

	private void showAboutTest() {
		setContentView(R.layout.mock);
		TextView textView = (TextView) findViewById(R.id.text);
		textView.setTextSize(22f);
		textView.setText("\n\n   本游戏为休闲娱乐类手机游戏，本游戏操作简单，画面美观，"
				+ "具有很强的交互性以及简单易行性。\n\n    任何人都可以在很短时间内熟悉它的游戏规则，因此适合各个水平的用户使用 。\n\n"
				+ "    本游戏是由齐鲁工业大学信息学院DK小组基于2D游戏引擎Andengine"
				+ "开发的棋类游戏，根据民间小游戏历经数个日日夜夜制作而成。\n\n");
		textView.setBackgroundColor(Color.rgb(150, 150, 150));
		Button bn = (Button) findViewById(R.id.bnk);
		bn.setTextSize(35f);
		if (mType == GameActivity.ABOUT)
			bn.setText("退出");
		bn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mType == GameActivity.ABOUT)
					finish();
				else
					showHelpTest();
			}
		});
	}

	private void showHelpTest() {
		setContentView(R.layout.mock_help);
		imageDrawableId = new int[] { R.drawable.mock0, R.drawable.mock1,
				R.drawable.mock2, R.drawable.mock3_1, R.drawable.mock3_2,
				R.drawable.mock3_3, };
		buttonId = new int[] { R.id.bn0, R.id.bn1, R.id.bn2, R.id.bn3,
				R.id.bn4, R.id.bn5, };
		name = new String[] { "CHESS", "TWO", "FOUR", "NINE", "NINE", "NINE",

		};
		textView = (TextView) findViewById(R.id.text);
		textView.setTextSize(22f);
		textView.setText(name[0]);

		imageView = (ImageView) findViewById(R.id.image);
		imageView.setImageResource(imageDrawableId[0]);

		for (int i = 0; i < button.length; i++) {
			button[i] = (Button) findViewById(buttonId[i]);
			button[i].setTextSize(35f);
			button[i].setOnClickListener(new buttonOnClickListener(i));
		}
		button[0].setEnabled(false);
		bn = (Button) findViewById(R.id.bn);
		bn.setTextSize(35f);
		bn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (nowButton) {
				case 0:
					bn.setText("NEXT");
					button[nowButton].setEnabled(true);
					nowButton++;
					imageView.setImageResource(imageDrawableId[nowButton]);
					textView.setText(name[nowButton]);
					button[nowButton].setEnabled(false);
					break;
				case 1:
					bn.setText("NEXT");
					button[nowButton].setEnabled(true);
					nowButton++;
					imageView.setImageResource(imageDrawableId[nowButton]);
					textView.setText(name[nowButton]);
					button[nowButton].setEnabled(false);
					break;
				case 2:
					bn.setText("NEXT");
					button[nowButton].setEnabled(true);
					nowButton++;
					imageView.setImageResource(imageDrawableId[nowButton]);
					textView.setText(name[nowButton]);
					button[nowButton].setEnabled(false);
					break;
				case 3:
					bn.setText("NEXT");
					button[nowButton].setEnabled(true);
					nowButton++;
					imageView.setImageResource(imageDrawableId[nowButton]);
					textView.setText(name[nowButton]);
					button[nowButton].setEnabled(false);
					break;
				case 4:
					bn.setText("退出");
					button[nowButton].setEnabled(true);
					nowButton++;
					imageView.setImageResource(imageDrawableId[nowButton]);
					textView.setText(name[nowButton]);
					button[nowButton].setEnabled(false);
					break;
				case 5:
					nowButton = 0;
					finish();
					break;
				default:
					break;
				}
			}
		});

	}

	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {

		if (pKeyCode == KeyEvent.KEYCODE_BACK
				&& pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			finish();
			return true;
		}

		return super.onKeyDown(pKeyCode, pEvent);
	}

	class buttonOnClickListener implements OnClickListener {
		private int ID;

		public buttonOnClickListener(int i) {
			this.ID = i;
		}

		@Override
		public void onClick(View v) {
			bn.setText("NEXT");
			button[nowButton].setEnabled(true);
			nowButton = ID;
			imageView.setImageResource(imageDrawableId[nowButton]);
			textView.setText(name[nowButton]);
			button[nowButton].setEnabled(false);
			if (nowButton == 5) {
				bn.setText("确定");
			}

		}

	}

}

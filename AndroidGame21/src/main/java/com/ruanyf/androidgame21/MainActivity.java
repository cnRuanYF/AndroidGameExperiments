package com.ruanyf.androidgame21;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Feng on 2017/11/02.
 */
public class MainActivity extends Activity {

	private String[] options = {"显示调试信息", "直接开始战斗"};
	private boolean[] checkedOptions = {false, false};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// AppCompat主题无默认全屏主题，故在此设置
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// 选择是否调试
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("运行参数");
		builder.setCancelable(false);
		builder.setMultiChoiceItems(options, checkedOptions, new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				checkedOptions[which] = isChecked;
			}
		});
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setContentView(new GameView(MainActivity.this, checkedOptions[0], checkedOptions[1]));
			}
		});
		builder.show();

	}
}

package com.ruanyf.androidgame26;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * Created by Feng on 2017/11/15.
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 保持亮屏
		setContentView(new GameView(this));
	}
}

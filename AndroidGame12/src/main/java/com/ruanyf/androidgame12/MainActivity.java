package com.ruanyf.androidgame12;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Feng on 2017/9/30.
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new GameView(this));
	}
}

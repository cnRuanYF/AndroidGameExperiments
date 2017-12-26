package com.ruanyf.androidgame14;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Feng on 2017/10/18.
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new GameView(this));
	}
}

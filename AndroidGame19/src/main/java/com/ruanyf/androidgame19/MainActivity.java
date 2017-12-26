package com.ruanyf.androidgame19;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Feng on 2017/11/01.
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new GameView(this));
	}
}

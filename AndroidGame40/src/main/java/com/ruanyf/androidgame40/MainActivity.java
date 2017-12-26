package com.ruanyf.androidgame40;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;

/**
 * Created by Feng on 2017/11/16.
 * Update on 2017/12/21.
 */
public class MainActivity extends Activity {

	private CCDirector ccDirector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CCGLSurfaceView ccglSurfaceView = new CCGLSurfaceView(this);
		setContentView(ccglSurfaceView);

		ccDirector = CCDirector.sharedDirector();
		ccDirector.attachInView(ccglSurfaceView);
		ccDirector.setDisplayFPS(true);

		// 屏幕适配
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int designWidth = 480;
		float screenScale = (float) dm.widthPixels / designWidth;
		int designHeight = (int) (dm.heightPixels / screenScale);
		ccDirector.setScreenSize(designWidth, designHeight); // 设置设计屏幕尺寸

		CCScene ccScene = CCScene.node();
		ccScene.addChild(new MenuLayer());
		ccDirector.runWithScene(ccScene);
	}

	@Override
	protected void onPause() {
		super.onPause();
		ccDirector.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		ccDirector.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ccDirector.end();
	}

}

package com.ruanyf.androidgame28;

import android.app.Activity;
import android.os.Bundle;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;

/**
 * Created by Feng on 2017/11/16.
 */
public class MainActivity extends Activity {
	private CCDirector mCcDirector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CCGLSurfaceView mCcglSurfaceView = new CCGLSurfaceView(this);
		setContentView(mCcglSurfaceView);
		mCcDirector = CCDirector.sharedDirector();
		mCcDirector.attachInView(mCcglSurfaceView);
		mCcDirector.setDisplayFPS(true);
		mCcDirector.setScreenSize(800, 480); // 设置设计屏幕尺寸，实际屏幕会自动适配
		CCScene mCcScene = CCScene.node();
		mCcScene.addChild(new LogoLayer());
		mCcDirector.runWithScene(mCcScene);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mCcDirector.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mCcDirector.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCcDirector.end();
	}

}

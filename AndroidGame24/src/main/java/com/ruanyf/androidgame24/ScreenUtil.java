package com.ruanyf.androidgame24;

import android.util.Log;

/**
 * Created by Feng on 2017/11/9.
 */
public enum ScreenUtil {
	INSTANCE;

	public static final int DESIGN_SCREEN_REF = 480;
	private float screenScale;
	private int screenWidth, screenHeight;

	/**
	 * 设置实际屏幕尺寸，自动计算缩放及设计尺寸
	 *
	 * @param width  实际宽度（像素）
	 * @param height 实际高度（像素）
	 */
	public void setActualScreenSize(int width, int height) {
		/*
		 * 由于现在的设备屏幕比例从4:3~16:10不等，
		 * 以固定的设计尺寸进行拉伸会造成比例不正确，
		 * 我的解决方案是参考较短的边长获取缩放比例，
		 * 根据缩放比例确定较长的边长，以适应实际比例
		 */
		if (width > height) {
			// 横屏的情形，以高度为参照
			screenScale = (float) height / DESIGN_SCREEN_REF;
			screenWidth = (int) (width / screenScale);
			screenHeight = DESIGN_SCREEN_REF;
		} else {
			screenScale = width / DESIGN_SCREEN_REF;
			screenWidth = DESIGN_SCREEN_REF;
			screenHeight = (int) (height / screenScale);
		}
		Log.d("GUtil", "actualScreenSize: " + width + " * " + height);
		Log.d("GUtil", "designScreenSize: " + screenWidth + " * " + screenHeight);
		Log.d("GUtil", "screenScale: " + screenScale);

	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public float getScreenScale() {
		return screenScale;
	}

}

package com.ruanyf.androidgame12;

import android.graphics.Bitmap;

/**
 * Created by Feng on 2017/10/12.
 */
class Blast extends Sprite {

	public Blast(Bitmap bitmap, int width, int height) {
		super(bitmap, width, height);
	}

	/**
	 * 逻辑操作
	 */
	@Override
	public void doLogic() {
		if (isVisible()) {
			nextFrame();
			if (getFrameIndex() == 0) { // 若回到第0帧说明已播放完1次
				setVisible(false); // 播放完一次即隐藏
			}
		}
	}
}
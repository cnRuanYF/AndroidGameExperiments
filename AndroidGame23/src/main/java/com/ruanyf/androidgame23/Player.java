package com.ruanyf.androidgame23;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.List;

/**
 * Created by Feng on 2017/11/9.
 */

public class Player extends Sprite {

	private boolean isMirror, isRun, isJump, isDead;
	private long frameCount;

	/**
	 * 使用多张(单帧)图片构建Sprite
	 */
	public Player(List<Bitmap> bitmapList, float width, float height) {
		super(bitmapList, width, height);
	}

	public boolean isMirror() {
		return isMirror;
	}

	public void setMirror(boolean mirror) {
		isMirror = mirror;
	}

	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean run) {
		isRun = run;
	}

	public boolean isJump() {
		return isJump;
	}

	public void setJump(boolean jump) {
		isJump = jump;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean dead) {
		isDead = dead;
	}

	@Override
	public void nextFrame() {
		setFrameSequanceIndex((getFrameSequanceIndex() + 1) % 2);
	}

	@Override
	public void doLogic() {
		frameCount++;
		if (isDead) {
			setFrameSequanceIndex(3);
		} else if (isJump) {
			setFrameSequanceIndex(2);
		} else if (isRun) {
			if (frameCount % (GameView.FPS / 10) == 0) {
				Log.d("", "doLogic: frameCount % GameView.FPS / 10 == 0");
				nextFrame();
			}
		} else {
			setFrameSequanceIndex(0);
		}
	}

	@Override
	public void doDraw(Canvas canvas) {
		// 判断是否需要翻转绘制图片
		if (isMirror) {
			canvas.save();
			canvas.scale(-1, 1, getX() + getWidth() / 2, 0); // 通过水平翻转画布
			super.doDraw(canvas);
			canvas.restore();
		} else {
			super.doDraw(canvas);
		}
	}

	@Override
	public void outOfBounds() {
		if (getX() < 0) {
			setX(0);
		} else if (getX() > ScreenUtil.INSTANCE.getScreenWidth() - getWidth()) {
			setX(ScreenUtil.INSTANCE.getScreenWidth() - getWidth());
		}
		if (getY() < 0) {
			setY(0);
		} else if (getY() > ScreenUtil.INSTANCE.getScreenHeight() - getHeight()) {
			setY(ScreenUtil.INSTANCE.getScreenHeight() - getHeight());
		}
	}
}

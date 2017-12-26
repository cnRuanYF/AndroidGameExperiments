package com.ruanyf.androidgame10;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Feng on 2017/9/30.
 */
public class Bullet {

	private Bitmap bitmap;
	private float x, y, width, height, speedX, speedY; // 坐标,宽高,速度
	private boolean isVisible; // 可见性

	public Bullet(Bitmap bitmap) {
		this.bitmap = bitmap;
		width = bitmap.getWidth();
		height = bitmap.getHeight();
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setSpeed(float speedX, float speedY) {
		this.speedX = speedX;
		this.speedY = speedY;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean visible) {
		isVisible = visible;
	}

	public void move(float distanceX, float distanceY) {
		x += distanceX;
		y += distanceY;
	}

	/**
	 * 逻辑操作
	 */
	public void doLogic() {
		move(speedX, speedY);
		// 越界处理
		if (x + width < 0 || x > 480 || y + height < 0 || y > 800) {
			isVisible = false;
		}
	}

	/**
	 * 绘制操作
	 */
	public void doDraw(Canvas canvas) {
		if (isVisible) {
			canvas.drawBitmap(bitmap, x, y, null);
		}
	}
}

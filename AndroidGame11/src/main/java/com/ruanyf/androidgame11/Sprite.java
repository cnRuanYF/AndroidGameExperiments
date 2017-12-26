package com.ruanyf.androidgame11;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Feng on 2017/10/12.
 */
public class Sprite {

	private Bitmap bitmap;
	private float x, y, width, height, speedX, speedY; // 坐标,宽高,速度
	private boolean isVisible; // 可见性

	Sprite(Bitmap bitmap) {
		this.bitmap = bitmap;
		width = bitmap.getWidth();
		height = bitmap.getHeight();
	}


	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}


	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}


	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}


	public float getSpeedX() {
		return speedX;
	}

	public float getSpeedY() {
		return speedY;
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


	/**
	 * 移动
	 */
	public void move(float distanceX, float distanceY) {
		x += distanceX;
		y += distanceY;
	}

	/**
	 * 越界判断处理
	 */
	public void outOfBounds() {
		if (x + width < 0 || x > 480 || y + height < 0 || y > 800) {
			isVisible = false;
		}
	}

	/**
	 * 逻辑操作
	 */
	public void doLogic() {
		move(speedX, speedY);
		outOfBounds();
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
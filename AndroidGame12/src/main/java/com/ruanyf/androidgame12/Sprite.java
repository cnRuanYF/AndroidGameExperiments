package com.ruanyf.androidgame12;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by Feng on 2017/10/12.
 */
public class Sprite {

	private Bitmap bitmap;
	private float x, y, width, height, speedX, speedY; // 坐标,宽高,速度
	private boolean isVisible; // 可见性

	private Rect src, dst; // 用于多帧裁剪的矩形
	private int[] frameX, frameY; // 存放每帧坐标
	private int frameIndex, frameNumber; // 帧索引,总帧数

	/**
	 * 单帧图片的构造
	 */
	Sprite(Bitmap bitmap) {
		this(bitmap, bitmap.getWidth(), bitmap.getHeight());
	}

	/**
	 * 多帧图片的构造
	 */
	Sprite(Bitmap bitmap, int width, int height) {
		this.bitmap = bitmap;
		this.width = width;
		this.height = height;

		// 帧位的获取
		int totalColumns = bitmap.getWidth() / width; // 图片宽度/帧宽度=横向帧数
		int totalRows = bitmap.getHeight() / height; // 同上
		frameNumber = totalColumns * totalRows;
		frameX = new int[frameNumber];
		frameY = new int[frameNumber];
		for (int row = 0; row < totalRows; row++) {
			for (int col = 0; col < totalColumns; col++) {
				frameX[totalColumns * row + col] = col * width;
				frameY[totalColumns * row + col] = row * height;
			}
		}
		src = new Rect();
		dst = new Rect();
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
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


	public int getFrameIndex() {
		return frameIndex;
	}

	/**
	 * (多帧图片)下一帧
	 */
	public void nextFrame() {
		frameIndex = (frameIndex + 1) % frameNumber; // 到达最大帧后回到第0帧
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
	 * (后续扩展可能有从各种方向进入的敌机/子弹,在子类中一一重写不现实,
	 * 所以改为:根据移动速度判断方向,如从右侧向左移动,只在左侧越界,以此类推)
	 */
	public void outOfBounds() {
		if ((speedX < 0 && x + width < 0)
				|| (speedX > 0 && x > 480)
				|| (speedY < 0 && y + height < 0)
				|| (speedY > 0 && y > 800)) {
			isVisible = false;
		}
	}

	/**
	 * 碰撞检测(排除法)
	 */
	public boolean collisionWith(Sprite anotherSpr) {
		if (!isVisible || !anotherSpr.isVisible         // 排除Sprite隐藏的情形
				|| x + width < anotherSpr.x             // ..anotherSpr在右侧..
				|| anotherSpr.x + anotherSpr.width < x  // ..anotherSpr在左侧..
				|| y + height < anotherSpr.y            // ..anotherSpr在下方..
				|| anotherSpr.y + anotherSpr.height < y // ..anotherSpr在上方..
				) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 逻辑操作
	 */
	public void doLogic() {
		if (isVisible) {
//			move(speedX, speedY); // 默认速度是以60FPS为准的每帧移动距离
			move(speedX * 60 / GameView.FPS, speedY * 60 / GameView.FPS); // 根据实际FPS自适应
			outOfBounds();
		}
	}

	/**
	 * 绘制操作
	 */
	public void doDraw(Canvas canvas) {
		if (isVisible) {
			src.set(frameX[frameIndex], frameY[frameIndex],
					frameX[frameIndex] + (int) width, frameY[frameIndex] + (int) height);
			dst.set((int) x, (int) y, (int) (x + width), (int) (y + height));
			canvas.drawBitmap(bitmap, src, dst, null);
		}
	}
}
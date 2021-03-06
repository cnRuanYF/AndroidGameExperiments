package com.ruanyf.androidgame15;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by Feng on 2017/10/12.
 * Update on 2017/10/18.
 */
public class Sprite {

	private Bitmap bitmap;
	private float x, y, width, height; // 坐标,宽高

	private Rect src, dst; // 用于多帧裁剪的矩形
	private int[] frameX, frameY; // 存放每帧坐标
	private int frameTotal; // 总帧数
	private int[] frameSequance; // 帧序列
	private int frameSequanceIndex; // 帧序列索引

	Sprite(Bitmap bitmap, int width, int height) {
		this.bitmap = bitmap;
		this.width = width;
		this.height = height;

		// 帧位的获取
		int totalColumns = bitmap.getWidth() / width; // 图片宽度/帧宽度=横向帧数
		int totalRows = bitmap.getHeight() / height; // 同上
		frameTotal = totalColumns * totalRows;
		frameX = new int[frameTotal];
		frameY = new int[frameTotal];
		for (int row = 0; row < totalRows; row++) {
			for (int col = 0; col < totalColumns; col++) {
				frameX[totalColumns * row + col] = col * width;
				frameY[totalColumns * row + col] = row * height;
			}
		}
		src = new Rect();
		dst = new Rect();
		frameSequance = new int[frameTotal]; // 默认帧序列初始化
		for (int i = 0; i < frameSequance.length; i++) {
			frameSequance[i] = i; // 按顺序给帧序列赋值
		}
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


	public int[] getFrameSequance() {
		return frameSequance;
	}

	public void setFrameSequance(int[] frameSequance) {
		this.frameSequance = frameSequance;
	}

	public int getFrameSequanceIndex() {
		return frameSequanceIndex;
	}

	public void setFrameSequanceIndex(int frameSequanceIndex) {
		this.frameSequanceIndex = frameSequanceIndex;
	}


	/**
	 * 移动
	 */
	public void move(float distanceX, float distanceY) {
		x += distanceX;
		y += distanceY;
	}

	/**
	 * 绘制操作
	 */
	public void doDraw(Canvas canvas) {
		// 根据帧序列的索引值绘制
		src.set(frameX[frameSequance[frameSequanceIndex]],
				frameY[frameSequance[frameSequanceIndex]],
				frameX[frameSequance[frameSequanceIndex]] + (int) width,
				frameY[frameSequance[frameSequanceIndex]] + (int) height);
		dst.set((int) x, (int) y, (int) (x + width), (int) (y + height));
		canvas.drawBitmap(bitmap, src, dst, null);
	}

	/**
	 * 下一帧(到达最大帧后回到第0帧)
	 */
	public void nextFrame() {
		frameSequanceIndex = (frameSequanceIndex + 1) % frameSequance.length;
	}

}
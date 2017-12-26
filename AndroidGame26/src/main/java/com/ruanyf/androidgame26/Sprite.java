package com.ruanyf.androidgame26;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.List;

/**
 * Created by Feng on 2017/10/12.
 * Update on 2017/11/09.
 */
public class Sprite {

	private Bitmap bitmap; // 使用单张(多帧)图片构建Sprite
	private List<Bitmap> bitmapList; // 使用多张(单帧)图片构建Sprite
	private float x, y, width, height; // 坐标,宽高
	private boolean isVisible; // 是否可见
	private Rect src, dst; // 用于多帧裁剪的矩形
	private int[] frameX, frameY; // 存放每帧坐标
	private int frameTotal; // 总帧数
	private int[] frameSequance; // 帧序列
	private int frameSequanceIndex; // 帧序列索引

	/**
	 * 使用单张图片构建Sprite
	 */
	public Sprite(Bitmap bitmap) {
		this(bitmap, bitmap.getWidth(), bitmap.getHeight());
	}

	/**
	 * 使用单张(多帧)图片构建Sprite
	 */
	public Sprite(Bitmap bitmap, int width, int height) {
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

	/**
	 * 使用多张(单帧)图片构建Sprite
	 */
	public Sprite(List<Bitmap> bitmapList, float width, float height) {
		this.bitmapList = bitmapList;
		this.width = width;
		this.height = height;
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

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean visible) {
		isVisible = visible;
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
	 * 碰撞检测(排除法)
	 */
	public boolean collisionWith(Sprite anotherSpr) {
		if (!isVisible || !anotherSpr.isVisible
				|| x + width < anotherSpr.x             // 排除anotherSpr在右侧的情形
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
	 * 移动
	 */
	public void move(float distanceX, float distanceY) {
		x += distanceX;
		y += distanceY;
		outOfBounds();
	}

	/**
	 * 越界处理 (在子类重写此方法以实现)
	 */
	public void outOfBounds() {
	}

	/**
	 * 下一帧(到达最大帧后回到第0帧)
	 */
	public void nextFrame() {
		frameSequanceIndex = (frameSequanceIndex + 1) % frameSequance.length;
	}

	/**
	 * 逻辑操作 (在子类重写此方法以实现)
	 */
	public void doLogic() {
	}

	/**
	 * 绘制操作
	 */
	public void doDraw(Canvas canvas) {
		if (isVisible) {
			if (bitmap != null) { // 若单张图片不为空则按照多帧图片的方式绘制
				// 根据帧序列的索引值绘制
				src.set(frameX[frameSequance[frameSequanceIndex]],
						frameY[frameSequance[frameSequanceIndex]],
						frameX[frameSequance[frameSequanceIndex]] + (int) width,
						frameY[frameSequance[frameSequanceIndex]] + (int) height);
				dst.set((int) x, (int) y, (int) (x + width), (int) (y + height));
				canvas.drawBitmap(bitmap, src, dst, null);
			} else if (bitmapList != null) { // 若图片数组不为空则按照多张图片绘制
				canvas.drawBitmap(bitmapList.get(frameSequanceIndex), x, y, null);
			}
		}
	}

}
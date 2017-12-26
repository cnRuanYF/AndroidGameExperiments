package com.ruanyf.androidgame24;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by Feng on 2017/10/18.
 * Update on 2017/11/9.
 */
public class TiledLayer {

	private Bitmap bitmap;
	private float x, y, width, height; // 坐标,宽高
	private int tiledCols, tiledRows; // 行列数
	private int[][] tiledCell; // 存放各行列图块的索引
	private int[] tiledX, tiledY; // 存放每个图块坐标
	private Rect src, dst; // 用于裁剪的矩形

	TiledLayer(Bitmap bitmap, int width, int height, int tiledCols, int tiledRows) {
		this.bitmap = bitmap;
		this.width = width;
		this.height = height;
		this.tiledCols = tiledCols;
		this.tiledRows = tiledRows;

		tiledCell = new int[tiledRows][tiledCols]; // 默认索引初始化

		// 素材图片帧位的获取
		int bmpCols = bitmap.getWidth() / width; // 图片宽度/帧宽度=横向帧数
		int bmpRows = bitmap.getHeight() / height; // 同上
		tiledX = new int[bmpCols * bmpRows + 1]; // 地图编辑器生成的索引会在最前面加入索引为0的透明图块,原图块索引往后顺延(+1)
		tiledY = new int[bmpCols * bmpRows + 1];
		for (int row = 0; row < bmpRows; row++) {
			for (int col = 0; col < bmpCols; col++) {
				tiledX[bmpCols * row + col + 1] = col * width; // 同上,索引+1
				tiledY[bmpCols * row + col + 1] = row * height;
			}
		}
		src = new Rect();
		dst = new Rect();
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

	public int getTiledCols() {
		return tiledCols;
	}

	public void setTiledCols(int tiledCols) {
		this.tiledCols = tiledCols;
	}

	public int getTiledRows() {
		return tiledRows;
	}

	public void setTiledRows(int tiledRows) {
		this.tiledRows = tiledRows;
	}

	public int[][] getTiledCell() {
		return tiledCell;
	}

	public void setTiledCell(int[][] tiledCell) {
		this.tiledCell = tiledCell;
	}

	public int getTiledCellIndex(int tiledRow, int tiledCol){
		return tiledCell[tiledRow][tiledCol];
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
	 * 越界判断处理
	 */
	public void outOfBounds() {
		if (x > 0) {
			x = 0;
		} else if (x < ScreenUtil.INSTANCE.getScreenWidth() - tiledCols * width) {
			x = ScreenUtil.INSTANCE.getScreenWidth() - tiledCols * width;
		}
		if (y > 0) {
			y = 0;
		} else if (y < ScreenUtil.INSTANCE.getScreenHeight() - tiledRows * height) {
			y = ScreenUtil.INSTANCE.getScreenHeight() - tiledRows * height;
		}
	}


	/**
	 * 逻辑操作
	 */
	public void doLogic() {
	}

	/**
	 * 绘制操作
	 */
	public void doDraw(Canvas canvas) {
		for (int row = 0; row < tiledRows; row++) {
			for (int col = 0; col < tiledCols; col++) {
				int tiledCellIndex = tiledCell[row][col];
				if (tiledCellIndex == 0) {
					continue; // 透明图块无需绘制
				}
				src.set(tiledX[tiledCellIndex], tiledY[tiledCellIndex],
						tiledX[tiledCellIndex] + (int) width, tiledY[tiledCellIndex] + (int) height);
				dst.set((int) (x + col * width), (int) (y + row * height),
						(int) (x + col * width + width), (int) (y + row * height + height));
				canvas.drawBitmap(bitmap, src, dst, null);
			}
		}
	}

}
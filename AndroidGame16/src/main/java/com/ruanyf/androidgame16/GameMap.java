package com.ruanyf.androidgame16;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.List;

/**
 * Created by Feng on 2017/10/19.
 */

public class GameMap extends TiledLayer {

	GameMap(Bitmap bitmap, int width, int height, int tiledCols, int tiledRows) {
		super(bitmap, width, height, tiledCols, tiledRows);
	}

	/**
	 * 移动(重写,加入NPC跟随地图移动)
	 */
	@Override
	public void move(float distanceX, float distanceY, List<NPC> npcList) {
		super.move(distanceX, distanceY, npcList);
		for (NPC npc : npcList) { // NPC跟随地图移动
			npc.move(distanceX, distanceY);
		}
	}

	/**
	 * 绘制操作(重写,绘制大图无需再进行切割分块)
	 */
	@Override
	public void doDraw(Canvas canvas) {
		canvas.drawBitmap(getBitmap(), getX(), getY(), null);
		if (GameView.DEBUG_MODE) {
			doDebugDraw(canvas);
		}
	}

	/**
	 * (调试用)绘制每个格子是否可行走
	 */
	private void doDebugDraw(Canvas canvas) {
		int padding = 2; // 要绘制的矩形之间的间距
		Paint mPaint = new Paint();
		Rect mRect = new Rect();
		for (int row = 0; row < getTiledRows(); row++) {
			for (int col = 0; col < getTiledCols(); col++) {
				mRect.set((int) (getX() + col * getWidth() + padding),
						(int) (getY() + row * getHeight() + padding),
						(int) (getX() + col * getWidth() + getWidth() - padding),
						(int) (getY() + row * getHeight() + getHeight() - padding));
				switch (getTiledCell()[row][col]) {
					case 0:
						mPaint.setColor(Color.RED); // 不可行走的格子绘制红色矩形
						break;
					case 1:
						mPaint.setColor(Color.GREEN); // 可行走的格子绘制绿色矩形
						break;
				}
				mPaint.setStyle(Paint.Style.STROKE);
				mPaint.setAlpha(128);
				canvas.drawRect(mRect, mPaint);
				mPaint.setStyle(Paint.Style.FILL);
				mPaint.setAlpha(32);
				canvas.drawRect(mRect, mPaint);
			}
		}
	}

}
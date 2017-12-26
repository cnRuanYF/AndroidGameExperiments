package com.ruanyf.androidgame09;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Feng on 2017/9/30.
 */
public class Food {

	private int x, y, dir, speed = 20; // x,y坐标,方向,速度
	private int count; // (测试用)食物计数

	private static final int WIDTH = SnakeBlock.WIDTH;

	public Food() {
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void reset() {
		x = (int) (Math.random() * (MySnakeView.STAGE_RIGHT - MySnakeView.STAGE_LEFT) / WIDTH) * WIDTH + MySnakeView.STAGE_LEFT;
		y = (int) (Math.random() * (MySnakeView.STAGE_BOTTOM - MySnakeView.STAGE_TOP) / WIDTH) * WIDTH + MySnakeView.STAGE_TOP;
	}

	public void doDraw(Canvas canvas, Paint paint) {
		// 画食物
		paint.setColor(Color.RED);
		canvas.drawCircle(x + WIDTH / 2, y + WIDTH / 2, WIDTH / 2, paint);

		// (测试用)画序号
		if (MySnakeView.IS_DEBUG_MODE) {
			paint.setColor(Color.DKGRAY);
			paint.setTextSize(WIDTH / ("" + count).length());
			canvas.drawText("" + count, x + WIDTH / 2, y + WIDTH - WIDTH * ("" + count).length() / 10, paint);
		}
	}
}
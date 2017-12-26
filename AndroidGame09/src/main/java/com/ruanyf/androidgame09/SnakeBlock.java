package com.ruanyf.androidgame09;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;

/**
 * Created by Feng on 2017/9/28.
 * Update by Feng on 2017/9/30.
 */
public class SnakeBlock {

	private int x, y, dir, speed = 20; // x,y坐标,方向,速度
	private int order; // (测试用)身体序号

	public static final int DIR_LT = 0;
	public static final int DIR_RT = 1;
	public static final int DIR_UP = 2;
	public static final int DIR_DN = 3;
	public static final int WIDTH = 20;

	public SnakeBlock(int x, int y, int dir) {
		this.x = x;
		this.y = y;
		this.dir = dir;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getDir() {
		return dir;
	}

	public void setDir(int dir) {
		if ((dir == DIR_LT && this.dir != DIR_RT) ||
				(dir == DIR_RT && this.dir != DIR_LT) ||
				(dir == DIR_UP && this.dir != DIR_DN) ||
				(dir == DIR_DN && this.dir != DIR_UP)) {
			this.dir = dir; // 不能设置为当前方向相反
		}
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public void doLogic() {
		switch (dir) {
			case DIR_LT:
				x -= speed;
				break;
			case DIR_RT:
				x += speed;
				break;
			case DIR_UP:
				y -= speed;
				break;
			case DIR_DN:
				y += speed;
		}
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public void doDraw(Canvas canvas, Paint paint) {
		// 画简单方块
		paint.setColor(Color.CYAN);
		canvas.drawRect(x, y, x + WIDTH, y + WIDTH, paint);

		// 画箭头
		paint.setColor(Color.YELLOW);
		Path path = new Path();
		switch (dir) {
			case DIR_LT:
				path.moveTo(x, y + WIDTH / 2);
				path.lineTo(x + WIDTH, y);
				path.lineTo(x + WIDTH, y + WIDTH);
				break;
			case DIR_RT:
				path.moveTo(x + WIDTH, y + WIDTH / 2);
				path.lineTo(x, y);
				path.lineTo(x, y + WIDTH);
				break;
			case DIR_UP:
				path.moveTo(x + WIDTH / 2, y);
				path.lineTo(x, y + WIDTH);
				path.lineTo(x + WIDTH, y + WIDTH);
				break;
			case DIR_DN:
				path.moveTo(x + WIDTH / 2, y + WIDTH);
				path.lineTo(x, y);
				path.lineTo(x + WIDTH, y);
				break;
		}
		canvas.drawPath(path, paint);

		// (测试用)画序号
		if (MySnakeView.IS_DEBUG_MODE) {
			paint.setColor(Color.DKGRAY);
			paint.setTextSize(WIDTH / ("" + order).length());
			canvas.drawText("" + order, x + WIDTH / 2, y + WIDTH - WIDTH * ("" + order).length() / 10, paint);
		}
	}
}
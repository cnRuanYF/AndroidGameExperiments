package com.ruanyf.androidgame08;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;

/**
 * Created by Feng on 2017/9/28.
 */
public class SnakeBlock {

	private int x, y, dir, speed = 20; // x,y坐标,方向,速度

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
		paint.setColor(Color.CYAN);
//		canvas.drawRoundRect(x, y, x + WIDTH, y + WIDTH, WIDTH / 4, WIDTH / 4, paint);
		// 画箭头
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
	}
}
package com.ruanyf.androidgame07;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 水波纹类
 * Created by Feng on 2017/9/28.
 */
public class Ripple {

	private float x, y, radius;// 圆心x,坐标,半径
	private int alpha; // 透明度

	Ripple(float x, float y) {
		this.x = x;
		this.y = y;
		this.radius = 0;
		this.alpha = 128;
	}

	// 逻辑部分的操作
	public void doLogic() {
		radius++;
		if (alpha > 0) {
			alpha -= 1;
		}
	}

	// 绘图部分的操作
	public void doDraw(Canvas canvas, Paint paint) {
		paint.setAlpha(alpha);
		canvas.drawCircle(x, y, radius, paint);
	}

	public boolean isInvisible() {
		return alpha == 0; // 透明度为0即为不可见
	}
}

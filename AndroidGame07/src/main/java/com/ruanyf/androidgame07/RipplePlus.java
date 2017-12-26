package com.ruanyf.androidgame07;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;

/**
 * 水波纹类的增强版
 * Created by Feng on 2017/9/28.
 */
public class RipplePlus {

	private float sDensity;
	private float x, y, radius, rippleWidth, offsetY;// 圆心x,坐标,半径,波浪宽度,垂直偏移
	private Path clipPath;
	private RectF bgDst;

	RipplePlus(float sDensity, float x, float y) {
		this.sDensity = sDensity; // 以下所有计算都已此为系数实现不同分辨率屏幕的适配
		this.x = x;
		this.y = y;
		radius = 0;
		rippleWidth = sDensity * 5;
		offsetY = sDensity * 5;
		clipPath = new Path();
		bgDst = new RectF();
	}

	// 逻辑部分的操作
	public void doLogic() {
		radius += sDensity;
		if (offsetY > 0) {
			offsetY -= sDensity * 0.05;
		}
	}

	// 绘图部分的操作
	@TargetApi(Build.VERSION_CODES.LOLLIPOP) // Path.arcTo()方法需要API21以上才有效
	public void doDraw(Canvas canvas, Bitmap bgBitmap) {
		canvas.save();
		clipPath.reset();
		clipPath.arcTo(x - radius * 1.5f - rippleWidth, y - radius + rippleWidth * 0 / 4, x + radius * 1.5f + rippleWidth, y + radius - rippleWidth * 0, -85, 350, true); // 外圈
		clipPath.arcTo(x - radius * 1.5f + rippleWidth, y - radius + rippleWidth * 4 / 4, x + radius * 1.5f - rippleWidth, y + radius - rippleWidth * 3, -95, -350, true); // 内圈
		canvas.clipPath(clipPath); // 通过上面方法可实现裁剪出圆环
		bgDst.set(0, -offsetY / 2, canvas.getWidth(), canvas.getHeight() + sDensity * 5 - offsetY / 2); // 背景定位比原图向上偏移
//		canvas.drawColor(Color.CYAN); // 测试用
		canvas.drawBitmap(bgBitmap, null, bgDst, null); // 在圆环中填充比原图向上偏移的背景实现波浪效果
		canvas.restore();

		// 使用双波浪增强真实感
		canvas.save();
		clipPath.reset();
		clipPath.arcTo(x - radius * 1.5f - rippleWidth / 2, y - radius + rippleWidth * 1 / 4, x + radius * 1.5f + rippleWidth / 2, y + radius - rippleWidth * 1, -85, 350, true);
		clipPath.arcTo(x - radius * 1.5f + rippleWidth / 2, y - radius + rippleWidth * 3 / 4, x + radius * 1.5f - rippleWidth / 2, y + radius - rippleWidth * 2, -95, -350, true);
		canvas.clipPath(clipPath);
		bgDst.set(0, -offsetY, canvas.getWidth(), canvas.getHeight() + sDensity * 5 - offsetY);
//		canvas.drawColor(Color.RED); // 测试用
		canvas.drawBitmap(bgBitmap, null, bgDst, null);
		canvas.restore();
	}

	public boolean isInvisible() {
		return offsetY <= 0;
	}
}

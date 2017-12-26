package com.ruanyf.androidgame02;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Feng on 2017/9/14.
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(new MyView(this));
		Toast.makeText(this, "随机生成颜色", Toast.LENGTH_SHORT).show();

	}

	/**
	 * 自定义画笔类
	 */
	private class MyPaint extends Paint {

		// 随机生成颜色
		public void randomColor() {
			this.setColor(0xFF000000 + (int) (0xFFFFFF * Math.random())); // 颜色值为16进制AARRGGBB
		}

	}

	/**
	 * 自定义视图
	 */
	private class MyView extends View {

		private MyPaint mPaint;
		private Path mPath;
		private RectF mRectF; // 矩形对象RectF比Rect的精度更高

		public MyView(Context context) {
			super(context);

			mPaint = new MyPaint();
			mPath = new Path();
			mRectF = new RectF();

			mPaint.setAntiAlias(true);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			canvas.drawColor(0xFF000000);
			mPaint.setStrokeWidth(2);

			// 绘制圆形
			mPaint.randomColor();
			canvas.drawCircle(canvas.getWidth() / 2 + 100, canvas.getHeight() / 3, 50, mPaint); // 通过画布对象的尺寸相对定位
			mPaint.randomColor();
			mPaint.setStyle(Paint.Style.STROKE);
			canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 3, 100, mPaint);

			// 绘制矩形
			mPaint.randomColor();
			canvas.drawRect(100, 50, 250, 250, mPaint); // 直接通过坐标绘制
			mPaint.randomColor();
			mRectF.set(162, 114, 452, 229); // 设置矩形对象的坐标
			canvas.drawRect(mRectF, mPaint); // 通过矩形对象绘制

			// 绘制圆角矩形
			mPaint.randomColor();
			mPaint.setStyle(Paint.Style.FILL); // 仅填充
			mRectF.set(50, 300, 200, 600);
			canvas.drawRoundRect(mRectF, 30, 15, mPaint); // 通过矩形对象绘制(矩形对象,横向半径,纵向半径,笔刷对象)

			// 绘制椭圆
			mPaint.randomColor();
			canvas.drawOval(mRectF, mPaint); // 绘制矩形对象的内切圆

			// 绘制扇形
			mPaint.randomColor();
			canvas.drawArc(mRectF, 0, 90, true, mPaint); // 时钟3点钟方向为0度,参数3为经过的角度(顺时针增加,逆时针为负数)

			// 绘制楔形
			mPaint.randomColor();
			canvas.drawArc(mRectF, 0, 90, false, mPaint); // 其实就是未连接圆心的扇形

			// 绘制扇形(无填充)
			mPaint.randomColor();
			mPaint.setStyle(Paint.Style.STROKE);
			mRectF.set(canvas.getWidth() * 4 / 5 - 50, canvas.getHeight() * 4 / 5 - 100, canvas.getWidth() * 4 / 5 + 50, canvas.getHeight() * 4 / 5 + 100);
			canvas.drawArc(mRectF, -20, 90, true, mPaint);

			// 绘制圆弧
			mPaint.randomColor();
			canvas.drawArc(mRectF, 270, -90 - 45, false, mPaint);

			// 绘制五角星
			int[][] pos = new int[10][2]; // 用于存储10个点的坐标
			int r1 = 100; // 外圆半径
			int r2 = (int) (Math.sin(18 * Math.PI / 180) / Math.sin(126 * Math.PI / 180) * r1); // 内圆半径
			for (int i = 0; i < 5; i++) {
				pos[i * 2][0] = canvas.getWidth() * 2 / 3 + (int) (Math.cos((18 + 72 * i) * Math.PI / 180) * r2);
				pos[i * 2][1] = canvas.getHeight() * 3 / 5 + (int) (Math.sin((18 + 72 * i) * Math.PI / 180) * r2);
				pos[i * 2 + 1][0] = canvas.getWidth() * 2 / 3 + (int) (Math.cos((54 + 72 * i) * Math.PI / 180) * r1);
				pos[i * 2 + 1][1] = canvas.getHeight() * 3 / 5 + (int) (Math.sin((54 + 72 * i) * Math.PI / 180) * r1);
			}
			mPath.moveTo(pos[0][0], pos[0][1]);
			for (int i = pos.length - 1; i >= 0; i--) { // 连接每个点
				mPath.lineTo(pos[i][0], pos[i][1]);
			}
			mPaint.randomColor();
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE); // 样式为填充与描边
			canvas.drawPath(mPath, mPaint);

		}
	}
}

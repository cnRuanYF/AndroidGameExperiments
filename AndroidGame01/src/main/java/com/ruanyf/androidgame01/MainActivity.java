package com.ruanyf.androidgame01;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Feng on 2017/9/14.
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(new MyView(this)); // 设置包含的视图为自定义视图

	}

	/**
	 * 自定义视图
	 */
	private class MyView extends View {

		private Paint mPaint; // 画笔对象
		private Path mPath; // 路径对象

		public MyView(Context context) {
			super(context);

			mPaint = new Paint();
			mPath = new Path();

			mPaint.setAntiAlias(true); // 打开抗锯齿
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			canvas.drawColor(Color.BLACK); // 填充背景色(使用android.graphic.Color类预定义的颜色)

			// 绘制单个点
			mPaint.setColor(Color.BLUE); // 画笔颜色
			mPaint.setStrokeCap(Paint.Cap.ROUND); // 线条端点为圆形
			mPaint.setStrokeWidth(20); // 线条宽度
			canvas.drawPoint(100, 100, mPaint);

			// 绘制多个点
			mPaint.setColor(Color.LTGRAY);
			mPaint.setStrokeWidth(5);
			float[] pts = {50, 50, 100, 50, 150, 50, 200, 50, 250, 50};
			canvas.drawPoints(pts, mPaint);

			// 绘制直线
			mPaint.setColor(Color.CYAN);
			canvas.drawLine(20, 400, 120, 400, mPaint);
			canvas.drawLine(160, 420, 260, 420, mPaint);

			mPaint.setColor(Color.MAGENTA);
			float[] pts2 = {20, 400, 160, 220, 160, 420, 300, 240, 123, 456};
			canvas.drawLines(pts2, mPaint); // 在数组中依次取4个值(点1x,点1y,点2x,点2y)画线，不足4个忽略
			mPaint.setColor(Color.DKGRAY);
			canvas.drawLines(pts2, 1, 8, mPaint); // 在数组中按指定偏移量取值(从第1个值开始取8个)

			// 绘制路径
			mPaint.setColor(Color.GREEN);
			mPaint.setStyle(Paint.Style.STROKE); // 设置样式为仅绘制边框
			mPath.moveTo(300, 440); // 移动到起点
			mPath.lineTo(400, 440); // 画线到
			mPath.lineTo(440, 240);
			mPath.lineTo(340, 240);
			mPath.lineTo(320, 340);
			mPath.lineTo(390, 340);
			canvas.drawPath(mPath, mPaint);

			// 绘制贝塞尔曲线
			mPaint.setColor(Color.YELLOW);
			mPath.reset(); // 初始化路径
			mPath.moveTo(60, 220);
			mPath.quadTo(110, 140, 160, 220); // 贝塞尔曲线(控制点x,y,终点x,y)
			canvas.drawPath(mPath, mPaint);

			// 绘制贝塞尔曲线(使用相对位置)
			mPaint.setColor(Color.RED);
			mPath.reset();
			mPath.rMoveTo(200, 240); // 初始化后相对0,0点的位置
			mPath.rQuadTo(50, -80, 100, 0); // 贝塞尔曲线(控制点x,y,终点x,y)
			canvas.drawPath(mPath, mPaint);

			// 绘制二阶贝塞尔曲线
			mPaint.setColor(Color.WHITE);
			mPath.reset();
			mPath.moveTo(50, 500);
			mPath.cubicTo(150, 400, 250, 700, 350, 400); // 二阶贝塞尔曲线(控制点1x,y,控制点2x,y,终点x,y)
			canvas.drawPath(mPath, mPaint);

			// 绘制二阶贝塞尔曲线(使用相对位置)
			mPaint.setColor(Color.rgb(162, 114, 229)); // 使用rgb()方法设置颜色(取值0~255)
			mPath.reset();
			mPath.rMoveTo(100, 600);
			mPath.rCubicTo(100, -200, 200, 200, 300, 0); // 二阶贝塞尔曲线(控制点1x,y,控制点2x,y,终点x,y)
			canvas.drawPath(mPath, mPaint);

			// 贝塞尔曲线控制点标记
			mPaint.setStrokeWidth(2);
			mPaint.setColor(Color.DKGRAY);
			mPath.reset();
			mPath.moveTo(50, 500);
			mPath.lineTo(150, 400);
			mPath.lineTo(250, 700);
			mPath.lineTo(350, 400);
			mPath.lineTo(50, 500);
			canvas.drawPath(mPath, mPaint);

		}
	}
}

package com.ruanyf.androidgame07;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 实现触摸画线的简易画图工具视图
 * Created by Feng on 2017/9/28.
 */
public class MyPaintView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	private static final int FPS = 60;

	private boolean isRunning;
	private SurfaceHolder holder;
	private Canvas canvas;
	private Paint mPaint;
	private Path mPath;
	private RectF btnResetRect;
	private float tmpX = 0, tmpY = 0; // 用于记录按下时的坐标

	public MyPaintView(Context ctx) {
		super(ctx);
		holder = getHolder();
		holder.addCallback(this);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPath = new Path();
		btnResetRect = new RectF();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		isRunning = true;
		new Thread(this).start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isRunning = false;
	}

	@Override
	public void run() {
		while (isRunning) {
			long startTime = System.currentTimeMillis();
			doDraw();
			long drawTime = System.currentTimeMillis() - startTime;
			if (drawTime < 1000 / FPS) {
				try {
					Thread.sleep(1000 / FPS - drawTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void doDraw() {
		canvas = holder.lockCanvas();
		if (canvas != null) {
			float cW = canvas.getWidth();
			float cH = canvas.getHeight();
			mPaint.setStrokeWidth(cW / 480 * 2); // 尝试以480p屏幕为标准进行自适应适配
			canvas.drawColor(Color.BLACK);

			// 绘制清屏按钮
			btnResetRect.set(cW / 10, cH / 30 * 27, cW / 10 * 9, cH / 30 * 29); // 设置清空画布按钮的位置
			mPaint.setColor(Color.WHITE);
			mPaint.setStyle(Paint.Style.STROKE);
			canvas.drawRoundRect(btnResetRect, 20, 20, mPaint);
			mPaint.setTextSize(cW / 480 * 32);
			mPaint.setTextAlign(Paint.Align.CENTER);
			mPaint.setStyle(Paint.Style.FILL);
			canvas.drawText("CLEAR CANVAS", cW / 2, cH / 20 * 19, mPaint);

			// 绘制画图路径
			canvas.save();
			canvas.clipRect(0, 0, cW, cH / 30 * 26); // 使用剪切区以防止绘制到按钮上
			mPaint.setColor(Color.RED);
			mPaint.setStyle(Paint.Style.STROKE);
			canvas.drawPath(mPath, mPaint);
			canvas.restore();

			holder.unlockCanvasAndPost(canvas);
		}
	}

	/**
	 * 监听触摸事件
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN: // 按下事件
				tmpX = ev.getX();
				tmpY = ev.getY();
				mPath.moveTo(tmpX, tmpY); // 路径起点移动到按下处
				break;
			case MotionEvent.ACTION_MOVE: // 移动事件
				mPath.lineTo(ev.getX(), ev.getY()); // 手指移动到哪里路径就连接到哪里
				break;
			case MotionEvent.ACTION_UP: // 抬起事件
				if (btnResetRect.contains(tmpX, tmpY) && btnResetRect.contains(ev.getX(), ev.getY())) { // 若按下和抬起的触摸点坐标都在矩形范围内
					mPath.reset(); // 重置路径，相当于清空画布
				}
				break;
		}
//		return super.onTouchEventent(ev);
		return true; // 这里西药返回true来强制截断事件的传递
	}
}

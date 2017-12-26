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

import java.util.ArrayList;

/**
 * 实现触摸画线的简易画图工具视图增强版
 * Created by Feng on 2017/9/28.
 */
public class MyColorfulPaintView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	private static final int FPS = 60;

	private boolean isRunning;
	private SurfaceHolder holder;
	private Canvas canvas;
	private Paint mPaint;
	private Path tmpPath;
	private RectF btnResetRect;
	private boolean isBtnFocus = false;
	private ArrayList<Path> paths; // 用于存储每次下笔的路径的数组
	private ArrayList<Integer> colors; // 用于存储颜色的数组

	float tmpX = 0, tmpY = 0; // 用于记录按下时的坐标

	public MyColorfulPaintView(Context ctx) {
		super(ctx);
		holder = getHolder();
		holder.addCallback(this);
		mPaint = new Paint();
		tmpPath = new Path();
		paths = new ArrayList<Path>();
		colors = new ArrayList<Integer>();
		btnResetRect = new RectF();
		mPaint.setAntiAlias(true);
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
			mPaint.setStrokeWidth(cW / 480 * 2);
			canvas.drawColor(Color.BLACK);

			// 遍历路径列表逐一绘制
			if (!colors.isEmpty()) { // 若颜色列表为空则无需绘制
				mPaint.setStyle(Paint.Style.STROKE);
				for (int i = 0; i < paths.size(); i++) {
					mPaint.setColor(colors.get(i));
					canvas.drawPath(paths.get(i), mPaint);
				}
				// 绘制正在画的路径
				mPaint.setColor(colors.get(colors.size() - 1));
				canvas.drawPath(tmpPath, mPaint);
			}

			// 绘制清屏按钮
			btnResetRect.set(cW / 10 - 10, cH / 30 * 27 - 10, cW / 10 * 9 + 10, cH / 30 * 29 + 10);
			mPaint.setColor(Color.BLACK);
			mPaint.setStyle(Paint.Style.FILL);
			canvas.drawRoundRect(btnResetRect, 30, 30, mPaint); // 绘制底色

			btnResetRect.set(cW / 10, cH / 30 * 27, cW / 10 * 9, cH / 30 * 29);
			mPaint.setColor(Color.WHITE);
			mPaint.setStyle(isBtnFocus ? Paint.Style.FILL : Paint.Style.STROKE); // 根据按钮状态设置填充样式
			canvas.drawRoundRect(btnResetRect, 20, 20, mPaint);

			mPaint.setTextSize(cW / 480 * 32);
			mPaint.setTextAlign(Paint.Align.CENTER);
			mPaint.setColor(isBtnFocus ? Color.BLACK : Color.WHITE); // 根据按钮状态设置文字颜色
			mPaint.setStyle(Paint.Style.FILL);
			canvas.drawText("CLEAR CANVAS", cW / 2, cH / 20 * 19, mPaint);

			holder.unlockCanvasAndPost(canvas);
		}
	}

	/**
	 * 监听触摸事件
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				tmpX = ev.getX();
				tmpY = ev.getY();
				if (!btnResetRect.contains(tmpX, tmpY)) { // 如果触摸点不在按钮范围内
					tmpPath = new Path();
					tmpPath.moveTo(tmpX, tmpY);
					colors.add(0xFF000000 + (int) (0xFFFFFF * Math.random())); // 创建随机颜色加入列表
				} else {
					isBtnFocus = true;
				}
				break;

			case MotionEvent.ACTION_MOVE:
				if (!btnResetRect.contains(tmpX, tmpY)) {
					tmpPath.lineTo(ev.getX(), ev.getY());
				} else {
					isBtnFocus = btnResetRect.contains(ev.getX(), ev.getY());
				}
				break;

			case MotionEvent.ACTION_UP:
				if (!btnResetRect.contains(tmpX, tmpY)) {
					paths.add(tmpPath); // 将本次触摸绘制的路径存入路径列表
				} else if (btnResetRect.contains(ev.getX(), ev.getY())) {
					colors.clear(); // 清空颜色列表
					paths.clear(); // 清空路径列表
					isBtnFocus = false;
				}
				break;
		}

		return true;
	}
}

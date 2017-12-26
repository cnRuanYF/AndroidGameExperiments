package com.ruanyf.androidgame07;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * 利用面向对象思想实现的水波纹视图类
 * Created by Feng on 2017/9/28.
 */
public class MyRippleView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	private static final int FPS = 60;
	private boolean isRunning;

	private SurfaceHolder holder;
	private Canvas canvas;
	private Paint mPaint;
	private ArrayList<Ripple> rippleList; // 用于存放水波纹对象的数组

	public MyRippleView(Context ctx) {
		super(ctx);
		holder = getHolder();
		holder.addCallback(this);
		mPaint = new Paint();
		mPaint.setAntiAlias(true); // 切记设置,否则波纹在透明度变化时会出现偏色
		rippleList = new ArrayList<Ripple>();
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
			doLogic(); // 将逻辑与绘制分离
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

	// 运行逻辑部分
	private void doLogic() {
		for (int i = 0; i < rippleList.size(); i++) {
			if (rippleList.get(i).isInvisible()) {
				rippleList.remove(i); // 若水波纹对象已完全透明,则移出数组不再绘制
			} else {
				rippleList.get(i).doLogic(); // 调用水波纹对象自己的逻辑处理方法
			}
		}
	}

	// 运行绘制部分
	private void doDraw() {
		canvas = holder.lockCanvas();
		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(10);
		if (canvas != null) {
			canvas.drawColor(0xFF4080FF);
			for (int i = 0; i < rippleList.size(); i++) { // 遍历水波纹对象数组
				rippleList.get(i).doDraw(canvas, mPaint); // 传递画布和画笔,调用水波纹对象自己的绘制方法
			}
			holder.unlockCanvasAndPost(canvas);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				rippleList.add(new Ripple(ev.getX(), ev.getY())); // 以按下位置为中心创建一个波纹加入到数组中
				break;
			// 这个场景只需要监听按下操作
		}
		return true;
	}
}

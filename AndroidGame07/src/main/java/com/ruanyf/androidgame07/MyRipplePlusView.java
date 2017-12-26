package com.ruanyf.androidgame07;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 水波纹视图类的增强版
 * Created by Feng on 2017/9/28.
 */
public class MyRipplePlusView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	private static final int FPS = 60;
	private boolean isRunning;

	private SurfaceHolder holder;
	private Canvas canvas;
	private ArrayList<RipplePlus> rippleList;
	private Bitmap bgBitmap; // 用于背景图片
	private RectF bgDst; // 用于背景图片的定位
	private float sDensity; // 像素密度(用于屏幕适配)

	public MyRipplePlusView(Context ctx) {
		super(ctx);

		sDensity = ctx.getResources().getDisplayMetrics().density; // 获取像素密度

		holder = getHolder();
		holder.addCallback(this);
		rippleList = new ArrayList<RipplePlus>();

		bgBitmap = null;
		try {
			bgBitmap = BitmapFactory.decodeStream(ctx.getAssets().open("bg2x.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		bgDst = new RectF();

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
				rippleList.remove(i);
			} else {
				rippleList.get(i).doLogic();
			}
		}
	}

	// 运行绘制部分
	private void doDraw() {
		canvas = holder.lockCanvas();
		if (canvas != null) {
			bgDst.set(0, 0, canvas.getWidth(), canvas.getHeight() + sDensity * 5);
//			canvas.drawColor(Color.BLACK); // 测试用
			canvas.drawBitmap(bgBitmap, null, bgDst, null);
			for (int i = 0; i < rippleList.size(); i++) {
				rippleList.get(i).doDraw(canvas, bgBitmap);
			}
			holder.unlockCanvasAndPost(canvas);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				rippleList.add(new RipplePlus(sDensity, ev.getX(), ev.getY()));
				break;
		}
		return true;
	}
}

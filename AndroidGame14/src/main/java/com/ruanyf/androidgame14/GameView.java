package com.ruanyf.androidgame14;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Feng on 2017/10/18.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	public static final int FPS = 60;
	public static final int designScreenWidth = 800;
	public static final int designScreenHeight = 480;

	private final GestureDetector mGestureDetector;

	private boolean isRunning;

	private SurfaceHolder holder;
	private Canvas canvas;
	private Paint mPaint;

	private float screenScaleX, screenScaleY;

	private TiledLayer tiledLayer;

	public GameView(Context ctx) {
		super(ctx);

		holder = getHolder();
		holder.addCallback(this);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);

		screenScaleX = (float) getResources().getDisplayMetrics().widthPixels / designScreenWidth;
		screenScaleY = (float) getResources().getDisplayMetrics().heightPixels / designScreenHeight;

		init(); // 初始化游戏并开局

		setLongClickable(true); // 使手势监听生效

		mGestureDetector = new GestureDetector(ctx, new GestureDetector.SimpleOnGestureListener() {

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				tiledLayer.move(-distanceX, -distanceY); // 拖动地图
				return super.onScroll(e1, e2, distanceX, distanceY);
			}

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				init(); // 双击初始化状态
				return super.onDoubleTap(e);
			}
		});
	}


	/**
	 * 从assets中按文件名获取位图对象
	 *
	 * @param filePath 图片文件路径
	 * @return 位图对象
	 */
	public Bitmap getBitmap(String filePath) {
		try {
			return BitmapFactory.decodeStream(getContext().getAssets().open(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 初始化游戏(重新开局)
	 */
	public void init() {

		Bitmap mapBitmap = getBitmap("map1.png");
		tiledLayer = new TiledLayer(mapBitmap, 40, 40, 64, 12);
		tiledLayer.setTiledCell(new int[][]{
				{31, 31, 0, 0, 31, 31, 0, 0, 31, 31, 31, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 31, 0,
						0, 0, 0, 0, 0, 0, 0, 39, 40, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
				},
				{0, 0, 31, 0, 0, 0, 31, 0, 31, 0, 31, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 31, 0,
						0, 0, 0, 0, 0, 0, 0, 39, 40, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
				},
				{31, 31, 31, 0, 31, 31, 31, 0, 31, 31, 31, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 31, 31, 31, 31, 0, 31, 0,
						56, 31, 0, 0, 0, 0, 0, 39, 40, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
				},
				{31, 0, 0, 0, 31, 0, 0, 0, 0, 0, 31, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 31, 0, 0, 0, 0, 31, 0,
						0, 31, 0, 0, 0, 0, 0, 34, 35, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 27, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
				},
				{31, 31, 31, 0, 31, 31, 31, 0, 31, 31, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 31, 56, 0, 0, 31, 0, 0, 0, 0, 31, 0,
						0, 31, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 27, 27, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
				},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 31,
						56, 0, 0, 0, 0, 0, 0, 0, 0, 31, 0, 31, 56, 31, 31, 31,
						0, 31, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						27, 28, 29, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
				},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 31, 31, 56, 0, 0, 0,
						0, 0, 0, 0, 17, 18, 19, 0, 0, 31, 0, 0, 0, 0, 0, 0,
						0, 31, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 27,
						28, 29, 27, 0, 0, 0, 0, 6, 0, 41, 42, 43, 44, 0, 0, 0
				},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 26, 26, 26, 0, 0, 31, 0, 0, 0, 0, 0, 0,
						0, 31, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 27, 28,
						29, 28, 29, 0, 0, 0, 0, 11, 0, 46, 47, 48, 49, 0, 16, 0
				},
				{0, 0, 0, 0, 1, 4, 0, 1, 2, 3, 4, 0, 0, 0, 0, 17,
						19, 0, 26, 0, 30, 30, 30, 0, 0, 31, 31, 31, 31, 31, 31, 31,
						31, 31, 0, 0, 0, 0, 7, 8, 9, 0, 0, 0, 0, 27, 28, 29,
						28, 29, 27, 0, 0, 0, 0, 21, 0, 51, 52, 53, 54, 0, 21, 0
				},
				{26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 0, 26,
						26, 0, 30, 0, 30, 30, 30, 0, 0, 0, 0, 0, 0, 34, 35, 0,
						0, 0, 0, 0, 0, 0, 12, 13, 14, 0, 0, 0, 27, 28, 29, 28,
						29, 28, 29, 0, 0, 0, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26
				},
				{30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 0, 30,
						30, 0, 30, 0, 30, 30, 30, 0, 0, 0, 0, 0, 0, 39, 40, 0,
						0, 0, 0, 0, 0, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
						26, 26, 26, 0, 0, 0, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30
				},
				{30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 0, 30,
						30, 0, 30, 0, 30, 30, 30, 0, 0, 0, 0, 0, 0, 39, 40, 0,
						0, 0, 0, 0, 0, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
						30, 30, 30, 0, 0, 0, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30
				}
		});

	}


	/**
	 * 游戏的逻辑操作
	 */
	private void doLogic() {
	}

	/**
	 * 游戏的绘制操作
	 */
	private void doDraw() {
		canvas = holder.lockCanvas();
		if (canvas != null) {
			canvas.save();
			canvas.scale(screenScaleX, screenScaleY); // 根据开始获得的缩放比例改变画布坐标系

			canvas.drawColor(Color.LTGRAY);
			mPaint.setColor(Color.LTGRAY);
			mPaint.setShadowLayer(50, 0, 0, Color.WHITE);
			mPaint.setTextSize(200);
			mPaint.setTextSkewX(-0.5f);
			canvas.drawText("229RYF", 50, designScreenHeight - 100, mPaint);

			tiledLayer.doDraw(canvas);

			canvas.restore();
			holder.unlockCanvasAndPost(canvas);
		}
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event); // 把触摸事件传递给手势识别器
		return super.onTouchEvent(event);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		isRunning = true;
		new Thread(this).start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// 这里什么也没有嘻嘻嘻
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isRunning = false;
	}

	@Override
	public void run() {
		while (isRunning) {
			long startTime = System.currentTimeMillis();
			doLogic();
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
}
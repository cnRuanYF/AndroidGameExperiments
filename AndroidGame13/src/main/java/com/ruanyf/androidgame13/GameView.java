package com.ruanyf.androidgame13;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Feng on 2017/10/18.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	public static final int FPS = 10;
	public static final int designScreenWidth = 800;
	public static final int designScreenHeight = 480;

	private final GestureDetector mGestureDetector;
	private final Bitmap playerBitmap;

	private boolean isRunning;

	private SurfaceHolder holder;
	private Canvas canvas;
	private Paint mPaint;

	private float screenScaleX, screenScaleY;
	private Player player;

	public GameView(Context ctx) {
		super(ctx);

		holder = getHolder();
		holder.addCallback(this);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);

		screenScaleX = (float) getResources().getDisplayMetrics().widthPixels / designScreenWidth;
		screenScaleY = (float) getResources().getDisplayMetrics().heightPixels / designScreenHeight;

		playerBitmap = getBitmap("zhaolinger1.png");

		init(); // 初始化游戏并开局

		setFocusable(true); // 设置为可获取焦点,使按键监听生效
		setLongClickable(true); // 使手势监听生效
		mGestureDetector = new GestureDetector(ctx, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				float moveX = e2.getX() - e1.getX();
				float moveY = e2.getY() - e1.getY();
				if (Math.abs(moveX) > Math.abs(moveY)) {
					if (moveX < 0) {
						detectDir(Dir.LEFT);
					} else {
						detectDir(Dir.RIGHT);
					}
				} else {
					if (moveY < 0) {
						detectDir(Dir.UP);
					} else {
						detectDir(Dir.DOWN);
					}
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				init(); // 双击初始化状态
				return super.onDoubleTap(e);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
			case KeyEvent.KEYCODE_A:
				detectDir(Dir.LEFT);
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
			case KeyEvent.KEYCODE_D:
				detectDir(Dir.RIGHT);
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
			case KeyEvent.KEYCODE_W:
				detectDir(Dir.UP);
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
			case KeyEvent.KEYCODE_S:
				detectDir(Dir.DOWN);
				break;
			case KeyEvent.KEYCODE_ENTER:
			case KeyEvent.KEYCODE_SPACE:
				init();
				break;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 识别到方向的操作
	 */
	private void detectDir(Dir dir) {
		if (player.getDir() != dir) { // 若当前方向和检查测到的不同
			switch (dir) {
				case LEFT:
					player.setFrameSequance(player.getLeftFrameSequance());
					break;
				case RIGHT:
					player.setFrameSequance(player.getRightFrameSequance());
					break;
				case UP:
					player.setFrameSequance(player.getUpFrameSequance());
					break;
				case DOWN:
					player.setFrameSequance(player.getDownFrameSequance());
					break;
			}
			player.setDir(dir);
			player.setFrameSequanceIndex(0);
		} else { // 否则继续行走(下一帧)
			switch (dir) {
				case LEFT:
					player.move(-20, 0);
					break;
				case RIGHT:
					player.move(20, 0);
					break;
				case UP:
					player.move(0, -20);
					break;
				case DOWN:
					player.move(0, 20);
					break;
			}
			player.nextFrame();
		}
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

		player = new Player(playerBitmap, playerBitmap.getWidth() / 4, playerBitmap.getHeight() / 4);
		player.setDownFrameSequance(new int[]{0, 1, 2, 3});
		player.setLeftFrameSequance(new int[]{4, 5, 6, 7});
		player.setRightFrameSequance(new int[]{8, 9, 10, 11});
		player.setUpFrameSequance(new int[]{12, 13, 14, 15});
		player.setFrameSequance(player.getLeftFrameSequance());
		player.setPosition(designScreenWidth / 2 - player.getWidth() / 2,
				designScreenHeight - player.getHeight() * 3);
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

			player.doDraw(canvas);

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
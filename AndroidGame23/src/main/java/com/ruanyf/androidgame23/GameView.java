package com.ruanyf.androidgame23;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Feng on 2017/10/18.
 * Update on 2017/11/9.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	public static boolean DEBUG_MODE = true; // 是否开启调试模式
	private boolean isRunning;

	public static final int FPS = 60;
	public static final int FRAME_PERIOD = 1000 / FPS;
	private int frameCount, frameSkipped;

	private float screenScale;
	private int screenWidth, screenHeight;

	private SurfaceHolder holder;
	private Canvas canvas;
	private Paint mPaint;


	private Bitmap btnLeftBitmap, btnRightBitmap, btnDownBitmap, btnABitmap, btnBBitmap;
	private RectF btnLeftRect, btnRightRect, btnDownRect, btnARect, btnBRect;

	private Player player;
	private int playerSpeedY; // 用于计算角色跳跃

	public GameView(Context ctx) {
		super(ctx);

		holder = getHolder();
		holder.addCallback(this);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);

		// 确定屏幕大小
		DisplayMetrics dm = getResources().getDisplayMetrics();
		ScreenUtil.INSTANCE.setActualScreenSize(dm.widthPixels, dm.heightPixels);
		screenScale = ScreenUtil.INSTANCE.getScreenScale();
		screenWidth = ScreenUtil.INSTANCE.getScreenWidth();
		screenHeight = ScreenUtil.INSTANCE.getScreenHeight();

		setFocusable(true); // 设置为可获取焦点,使按键监听生效
		setLongClickable(true); // 使手势监听生效

		btnLeftBitmap = getBitmap("button/left.png");
		btnRightBitmap = getBitmap("button/right.png");
		btnDownBitmap = getBitmap("button/down.png");
		btnABitmap = getBitmap("button/a.png");
		btnBBitmap = getBitmap("button/b.png");

		btnLeftRect = new RectF(20, 340, 20 + btnLeftBitmap.getWidth(), 340 + btnLeftBitmap.getHeight());
		btnRightRect = new RectF(120, 340, 120 + btnRightBitmap.getWidth(), 340 + btnRightBitmap.getHeight());
		btnDownRect = new RectF(70, 400, 70 + btnDownBitmap.getWidth(), 400 + btnDownBitmap.getHeight());
		btnBRect = new RectF(screenWidth - 70 - btnABitmap.getWidth(), 400, screenWidth - 70, 400 + btnABitmap.getHeight());
		btnARect = new RectF(screenWidth - 20 - btnBBitmap.getWidth(), 340, screenWidth - 20, 340 + btnBBitmap.getHeight());

		init(); // 初始化游戏并开局

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
	 * 识别到按钮的操作
	 */
	private void detectInput(JoyPad button) {
		switch (button) {
			case LEFT:
				player.setMirror(true);
				player.setRun(true);
				break;
			case RIGHT:
				player.setMirror(false);
				player.setRun(true);
				break;
			case DOWN:
				break;
			case A:
				if (!player.isJump()) {
					player.setJump(true);
					playerSpeedY = -16;
				}
				break;
			case B:
				break;
		}
	}

	/**
	 * 按键抬起的操作
	 */
	private void cancelInput() {
		player.setRun(false);
	}


	/**
	 * 初始化游戏
	 */
	public void init() {
		// 生成玩家
		List<Bitmap> playerBitmapList = new ArrayList<Bitmap>();
		for (int i = 0; i < 4; i++) {
			playerBitmapList.add(getBitmap("mario/mario" + i + ".png"));
		}
		player = new Player(playerBitmapList, playerBitmapList.get(0).getWidth(), playerBitmapList.get(0).getHeight());
		player.setPosition(100, 300);
		player.setVisible(true);
	}

	/**
	 * 游戏的逻辑操作
	 */
	private void doLogic() {
		frameCount++;
		player.doLogic();
		doMove();
	}

	/**
	 * 处理角色移动
	 */
	private void doMove() {
		if (player.isRun()) {
			player.move(player.isMirror() ? -4 : 4, 0);
		}
		if (player.isJump()) {
			player.move(0, playerSpeedY);
			if (playerSpeedY < 16) {
				playerSpeedY++;
			} else {
				playerSpeedY = 0;
				player.setJump(false);
			}
		}
	}

	/**
	 * 游戏的绘制操作
	 */
	private void doDraw() {
		canvas = holder.lockCanvas();
		if (canvas != null) {
			canvas.save();
			canvas.scale(screenScale, screenScale); // 根据开始获得的缩放比例改变画布坐标系

			canvas.drawColor(Color.DKGRAY);

			// 按顺序绘制背景层,碰撞层,玩家,前景层
			player.doDraw(canvas);

			// 绘制按钮
			canvas.drawBitmap(btnLeftBitmap, null, btnLeftRect, null);
			canvas.drawBitmap(btnRightBitmap, null, btnRightRect, null);
			canvas.drawBitmap(btnDownBitmap, null, btnDownRect, null);
			canvas.drawBitmap(btnABitmap, null, btnARect, null);
			canvas.drawBitmap(btnBBitmap, null, btnBRect, null);

			if (DEBUG_MODE) {
				mPaint.setColor(Color.YELLOW);
				mPaint.setShadowLayer(2, 0, 0, Color.BLACK);
				mPaint.setTextSize(16);
				canvas.drawText("DEBUG MODE", 20, screenHeight - 10, mPaint);
				canvas.drawText("FrameCount: " + frameCount, 20, 30, mPaint);
				canvas.drawText("FrameSkipped: " + frameSkipped, 20, 50, mPaint);
				canvas.drawText("Time: " + frameCount * 1000 / FPS + "ms", 20, 70, mPaint);
				canvas.drawText("Ave.FPS: " + FPS * (frameCount - frameSkipped) / frameCount, 20, 90, mPaint);
			}

			canvas.restore();
			holder.unlockCanvasAndPost(canvas);
		}
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int pointerCount = event.getPointerCount();
		for (int i = 0; i < pointerCount; i++) {
			float x = event.getX(i) / screenScale;
			float y = event.getY(i) / screenScale;
			if (btnLeftRect.contains(x, y)) {
				detectInput(JoyPad.LEFT);
			} else if (btnRightRect.contains(x, y)) {
				detectInput(JoyPad.RIGHT);
			} else if (btnDownRect.contains(x, y)) {
				detectInput(JoyPad.DOWN);
			} else if (btnARect.contains(x, y)) {
				detectInput(JoyPad.A);
			} else if (btnBRect.contains(x, y)) {
				detectInput(JoyPad.B);
			}
		}
		// 抬起手指则停止移动
		if (event.getAction() == MotionEvent.ACTION_UP) {
			cancelInput();
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
			case KeyEvent.KEYCODE_A:
				detectInput(JoyPad.LEFT);
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
			case KeyEvent.KEYCODE_D:
				detectInput(JoyPad.RIGHT);
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
			case KeyEvent.KEYCODE_S:
				detectInput(JoyPad.DOWN);
				break;
			case KeyEvent.KEYCODE_J:
				detectInput(JoyPad.B);
				break;
			case KeyEvent.KEYCODE_K:
				detectInput(JoyPad.A);
				break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
			case KeyEvent.KEYCODE_A:
				cancelInput();
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
			case KeyEvent.KEYCODE_D:
				cancelInput();
				break;
			case KeyEvent.KEYCODE_E:
			case KeyEvent.KEYCODE_VOLUME_UP:
				DEBUG_MODE = !DEBUG_MODE; // 切换调试状态
				break;
			case KeyEvent.KEYCODE_R:
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				init(); // 初始化状态
				break;
			case KeyEvent.KEYCODE_Q:
				((MainActivity) getContext()).finish(); // 退出游戏
				break;
		}
		return super.onKeyUp(keyCode, event);
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
			// 获取每一轮开始时间
			long startTime = System.currentTimeMillis();
			doLogic();
			doDraw();
			// 根据结束时间以及帧周期计算休眠时间
			int sleepTime = FRAME_PERIOD - (int) (System.currentTimeMillis() - startTime);
			// 若休眠时间<0(绘制时间过长)，则进行跳帧，直到需要休眠
			while (sleepTime < 0) {
				doLogic(); // 仅更新逻辑不进行绘制
				frameSkipped++;
				sleepTime += FRAME_PERIOD;
			}
			// 若休眠时间>0，则线程根据休眠时间正常休眠
			if (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
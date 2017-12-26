package com.ruanyf.androidgame26;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Feng on 2017/11/15.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	public static boolean DEBUG_MODE = false; // 是否开启调试模式

	public static final int FPS = 60;
	public static final int designScreenWidth = 800;
	public static final int designScreenHeight = 480;

	private Context ctx;

	private GestureDetector mGestureDetector;
	private final SensorEventListener sensorEventListener;

	private float screenScaleX, screenScaleY;

	private boolean isRunning;

	private SurfaceHolder holder;
	private Canvas canvas;
	private Paint mPaint;

	private int frameCount;

	private Sprite ballSprite;
	private Sprite finishSprite;
	private Sprite animationSprite;
	private List<Sprite> holes;
	private boolean isWin;
	private boolean isDrawWin;
	private Bitmap floorBitmap;
	private Bitmap holeBitmap;
	private Bitmap ballBitmap;
	private Bitmap animationBitmap;
	private Bitmap finishBitmap;
	private Bitmap winBitmap;
	private SensorManager sensorManager;
	private Sensor accelerometerSensor;


	public GameView(Context ctx) {
		super(ctx);
		this.ctx = ctx;

		holder = getHolder();
		holder.addCallback(this);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);

		screenScaleX = (float) getResources().getDisplayMetrics().widthPixels / designScreenWidth;
		screenScaleY = (float) getResources().getDisplayMetrics().heightPixels / designScreenHeight;

		init(); // 初始化游戏并开局

		setFocusable(true); // 设置为可获取焦点,使按键监听生效
		setLongClickable(true); // 使手势监听生效

		// 手势探测器
		mGestureDetector = new GestureDetector(ctx, new GestureDetector.SimpleOnGestureListener() {

			// 为方便测试，可直接滑动屏幕控制球的走向
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				ballSprite.move(-distanceX, -distanceY);
				return super.onScroll(e1, e2, distanceX, distanceY);
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				return super.onSingleTapConfirmed(e);
			}

			// 双击屏幕直接初始化
			@Override
			public boolean onDoubleTap(MotionEvent e) {
				init();
				return super.onDoubleTap(e);
			}
		});

		// 传感器事件监听器
		sensorEventListener = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent event) {
				// 传感器的数值不随屏幕方向变化
				float x = event.values[0]; // 正对着竖屏状态的手机，将屏幕往左翻转至垂直，值为10，反之往右为-10
				float y = event.values[1]; // 条件同上，将屏幕往下翻转至垂直，值为10，反之往上为-10
//				float z = event.values[2]; // 屏幕朝上为10，反之朝下为-10，该游戏用不到此参数
				ballSprite.move(y * 2, x * 2);
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {

			}
		};
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_ENTER:
			case KeyEvent.KEYCODE_SPACE:
				init();
				break;
			case KeyEvent.KEYCODE_Q:
				((MainActivity) getContext()).finish(); // 退出游戏
				break;
		}

		return super.onKeyDown(keyCode, event);
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
	 * 初始化游戏
	 */
	public void init() {

		floorBitmap = getBitmap("floor.png");
		holeBitmap = getBitmap("hole.png");
		finishBitmap = getBitmap("finish.png");
		ballBitmap = getBitmap("ball.png");
		animationBitmap = getBitmap("animation.png");
		winBitmap = getBitmap("win.png");


		ballSprite = new Ball(ballBitmap) {
			// 根据游戏需要，球需要有一半和陷阱重叠才判定为落洞，故重写碰撞检测
			@Override
			public boolean collisionWith(Sprite anotherSpr) {
				if (!isVisible() || !anotherSpr.isVisible()
						|| getX() + getWidth() / 2 < anotherSpr.getX()                                  // 排除anotherSpr在右侧的情形
						|| anotherSpr.getX() + anotherSpr.getWidth() < getX() + getWidth() / 2   // ..anotherSpr在左侧..
						|| getY() + getHeight() / 2 < anotherSpr.getY()                          // ..anotherSpr在下方..
						|| anotherSpr.getY() + anotherSpr.getHeight() < getY() + getHeight() / 2 // ..anotherSpr在上方..
						) {
					return false;
				} else {
					return true;
				}
			}
		};
		ballSprite.setPosition(50, 50);
		ballSprite.setVisible(true);

		finishSprite = new Sprite(finishBitmap);
		finishSprite.setPosition(designScreenWidth - 100, designScreenHeight - 100);
		finishSprite.setVisible(true);

		holes = new ArrayList<Sprite>();
		for (int row = 0; row < 5; row++) {
			for (int col = 0; col < 7; col++) {
				Sprite holeSprite = new Sprite(holeBitmap);
				holeSprite.setPosition(100 * (col + 1) - 50 * row + 80, (designScreenHeight - ballBitmap.getHeight()) / 4 * row - col * 5 + 10);
				holeSprite.setVisible(true);
				holes.add(holeSprite);
			}
		}

		animationSprite = new Sprite(animationBitmap, animationBitmap.getWidth(), animationBitmap.getHeight() / 15);


		// 状态初始化
		isWin = false;
		isDrawWin = false;
		frameCount = 0;

	}


	/**
	 * 游戏的逻辑操作
	 */
	private void doLogic() {
		// 和陷阱的碰撞检测
		for (Sprite holeSpr : holes) {
			if (ballSprite.collisionWith(holeSpr)) {
//				animationSprite.setPosition(holeSpr.getX() + holeSpr.getWidth() / 2 - animationSprite.getWidth() / 2,
//						holeSpr.getY() + holeSpr.getHeight() / 2 - animationSprite.getHeight() / 2); // 理论上这是正确的位置计算方式，但是美工给的图尺寸有偏差
				animationSprite.setPosition(holeSpr.getX() + holeSpr.getWidth() - animationSprite.getWidth(),
						holeSpr.getY() + holeSpr.getHeight() - animationSprite.getHeight()); // 只好凭感觉瞎算了
				animationSprite.setVisible(true);
				ballSprite.setVisible(false);
			}
		}
		// 和终点的碰撞检测
		if (ballSprite.collisionWith(finishSprite)) {
			animationSprite.setPosition(finishSprite.getX() + finishSprite.getWidth() - animationSprite.getWidth(),
					finishSprite.getY() + finishSprite.getHeight() - animationSprite.getHeight()); // 同上。。。
			animationSprite.setVisible(true);
			ballSprite.setVisible(false);
			isWin = true;
		}
		// 落洞动画的处理
		if (animationSprite.isVisible()) {
			animationSprite.nextFrame();
			if (animationSprite.getFrameSequanceIndex() == 0) {
				animationSprite.setVisible(false);
				if (isWin) {
					isDrawWin = true;
				} else {
					init();
				}
			}
		}

		frameCount++;
	}

	/**
	 * 游戏的绘制操作
	 */
	private void doDraw() {
		canvas = holder.lockCanvas();
		if (canvas != null) {
			canvas.save();
			canvas.scale(screenScaleX, screenScaleY); // 根据开始获得的缩放比例改变画布坐标系
			// 按顺序先绘制地板
			canvas.drawBitmap(floorBitmap, 0, 0, null);
			// 绘制终点
			finishSprite.doDraw(canvas);
			// 绘制陷阱
			for (Sprite holeSpr : holes) {
				holeSpr.doDraw(canvas);
			}
			// 绘制球
			ballSprite.doDraw(canvas);
			// 绘制落洞动画
			animationSprite.doDraw(canvas);
			// 绘制胜利画面
			if (isDrawWin) {
				canvas.drawBitmap(winBitmap, designScreenWidth / 2 - winBitmap.getWidth() / 2,
						designScreenHeight / 2 - winBitmap.getHeight() / 2, null);
			}

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

		sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE); // 获取系统传感器管理器
		accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // 获取加速度传感器
		sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME); // 注册传感器监听，精确度为游戏模式
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// 这里什么也没有嘻嘻嘻
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isRunning = false;

		sensorManager.unregisterListener(sensorEventListener); // 在退出时反注册传感器监听以节约电量
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
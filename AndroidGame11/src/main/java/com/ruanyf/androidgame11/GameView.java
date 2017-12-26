package com.ruanyf.androidgame11;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Feng on 2017/9/30.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	public static final boolean DEBUG_MODE = true;

	public static final int FPS = 60;
	public static final int BULLETS_SIZE = 60; // 弹夹大小

	private final GestureDetector mGestureDetector;
	private final Bitmap backgroundBitmap1;
	private final Bitmap playerBitmap1;
	private final Bitmap enemyBitmap1;
	private final Bitmap bulletBitmap1;
	private final Bitmap bulletBitmap2;

	private boolean isRunning;
	private boolean isGameover;
	private int frameCount;
	private float playTimeSec;

	private SurfaceHolder holder;
	private Canvas canvas;
	private Paint mPaint;

	private float screenScaleX, screenScaleY;
	private Background background;
	private Player player;
	private List<Enemy> enemies;

	public GameView(Context ctx) {
		super(ctx);

		holder = getHolder();
		holder.addCallback(this);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(16);
		mPaint.setStrokeWidth(4);

		screenScaleX = (float) getResources().getDisplayMetrics().widthPixels / 480;
		screenScaleY = (float) getResources().getDisplayMetrics().heightPixels / 800;

		backgroundBitmap1 = getBitmap("background1.png");
		playerBitmap1 = getBitmap("player1.png");
		enemyBitmap1 = getBitmap("enemy1.png");
		bulletBitmap1 = getBitmap("bullet1.png");
		bulletBitmap2 = getBitmap("bullet2.png");

		init(); // 初始化游戏并开局

		setLongClickable(true); // 使手势监听生效
		mGestureDetector = new GestureDetector(ctx, new GestureDetector.SimpleOnGestureListener() {

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				if (!isGameover) {
					// distanceX/Y的值为上次onScroll触发时的坐标-本次触发时的坐标,所以应取负值
					player.move(-distanceX / screenScaleX, -distanceY / screenScaleY); // 要注意这里也要做屏幕适配
				}
				return super.onScroll(e1, e2, distanceX, distanceY);
			}

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				// 双击重新开始游戏的处理
				if (isGameover) {
					init(); // 在GameOver状态才重新开始
				}
				if (DEBUG_MODE) {
					if (player.getWeapon() == Player.WEAPON_NORMAL) {
						player.setWeapon(Player.WEAPON_SHOTGUN);
					} else {
						init();
					}
				}
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

		isGameover = false;// 状态初始化
		frameCount = 0;

		// 生成背景
		background = new Background(backgroundBitmap1);

		// 生成玩家
		player = new Player(playerBitmap1);
		player.setVisible(true);
		player.setPosition(480 / 2 - player.getWidth() / 2, 800 - player.getHeight() * 3);

		// 装填弹夹
		List<Bullet> bullets = new ArrayList<>();
		for (int i = 0; i < BULLETS_SIZE; i++) {
			bullets.add(new Bullet(bulletBitmap1));
		}
		player.setBullets(bullets);

		// 生成敌人
		enemies = new ArrayList<>();
	}

	/**
	 * 关卡进度控制
	 */
	private void doProgress() {
		/*
		 * 第3秒时出现第1波敌人(不会射击)
		 *   E E E E E E E E E
		 * i   E E E E E E E
		 * ↑     E E E E E
		 * │       E E E
		 * ┼──→j     E
		 */
		if (playTimeSec == 3) {
			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < i * 2 + 1; j++) {
					Enemy e = new Enemy(enemyBitmap1, Enemy.WEAPON_NORMAL);
					e.setPosition(240 - e.getWidth() / 2 - 50 * (i - j), -e.getHeight() - 50 * i);
					e.setSpeed(0, 3);
					e.setVisible(true);
					enemies.add(e);
				}
			}
		}

		// 5秒后每0.5秒随机产生敌人
		if (playTimeSec > 5 && playTimeSec % 0.5 == 0) {
			// 复用已经隐藏的敌机
			for (Enemy e : enemies) {
				if (!e.isVisible()) {
					e.setPosition((float) ((480 - e.getWidth()) * Math.random()), -e.getHeight());
					// 随机出现不同武器的敌机
					int weaponRate = (int) (Math.random() * 100);
					if (weaponRate > 90) { // 敌机10%几率携带霰弹枪
						List<Bullet> bullets = new ArrayList<>();
						for (int i = 0; i < 9; i++) {
							bullets.add(new Bullet(bulletBitmap2));
						}
						e.setBullets(bullets);
						e.setWeapon(Enemy.WEAPON_SHOTGUN);
					} else if (weaponRate > 50) { // 40%几率携带普通子弹
						List<Bullet> bullets = new ArrayList<>();
						for (int i = 0; i < 3; i++) {
							bullets.add(new Bullet(bulletBitmap2));
						}
						e.setBullets(bullets);
						e.setWeapon(Enemy.WEAPON_NORMAL);
					}
					e.setVisible(true);
					break;
				}
			}
		}
	}

	/**
	 * 游戏的逻辑操作
	 */
	private void doLogic() {
		if (!isGameover) {
			frameCount++;
			playTimeSec = (float) frameCount / FPS;

			background.doLogic();
			player.doLogic();
			if (enemies != null) {
				for (Enemy e : enemies) {
					e.doLogic();
				}
			}
			doProgress();
		}
	}

	/**
	 * 游戏的绘制操作
	 */
	private void doDraw() {
		canvas = holder.lockCanvas();
		if (canvas != null) {
			canvas.save();
			canvas.scale(screenScaleX, screenScaleY); // 根据开始获得的缩放比例改变画布坐标系

			background.doDraw(canvas);
			player.doDraw(canvas);
			if (enemies != null) {
				for (Enemy e : enemies) {
					e.doDraw(canvas);
				}
			}

			drawDebugInfo();

			canvas.restore();
			holder.unlockCanvasAndPost(canvas);
		}
	}

	/**
	 * 调试信息的的绘制操作
	 */
	private void drawDebugInfo() {
		if (DEBUG_MODE) {
			String debugStr1 = "---- DEBUG MODE ----";
			String debugStr2 = player.getWeapon() == Player.WEAPON_SHOTGUN ?
					"散弹发射器 (双击屏幕初始化)" : "普通子弹 (双击屏幕激活散弹)";
			String debugStr3 = "frameCount: " + frameCount;
			String debugStr4 = "playTimeSec: " + playTimeSec;
			mPaint.setColor(Color.BLACK);
			mPaint.setAlpha(128);
			mPaint.setStyle(Paint.Style.STROKE);
			canvas.drawText(debugStr1, 20, 20 + mPaint.getTextSize(), mPaint);
			canvas.drawText(debugStr2, 20, 40 + mPaint.getTextSize(), mPaint);
			canvas.drawText(debugStr3, 20, 60 + mPaint.getTextSize(), mPaint);
			canvas.drawText(debugStr4, 20, 80 + mPaint.getTextSize(), mPaint);
			mPaint.setColor(Color.YELLOW);
			mPaint.setStyle(Paint.Style.FILL);
			canvas.drawText(debugStr1, 20, 20 + mPaint.getTextSize(), mPaint);
			canvas.drawText(debugStr2, 20, 40 + mPaint.getTextSize(), mPaint);
			canvas.drawText(debugStr3, 20, 60 + mPaint.getTextSize(), mPaint);
			canvas.drawText(debugStr4, 20, 80 + mPaint.getTextSize(), mPaint);
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

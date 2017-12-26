package com.ruanyf.androidgame12;

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
	private final Bitmap bulletBitmap1, bulletBitmap2;
	private final Bitmap BlastBitmap1;

	private boolean isRunning;
	private boolean isGameover;
	private int score;
	private float frameCount;
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

		screenScaleX = (float) getResources().getDisplayMetrics().widthPixels / 480;
		screenScaleY = (float) getResources().getDisplayMetrics().heightPixels / 800;

		backgroundBitmap1 = getBitmap("background1.png");
		playerBitmap1 = getBitmap("player1.png");
		enemyBitmap1 = getBitmap("enemy1.png");
		bulletBitmap1 = getBitmap("bullet1.png");
		bulletBitmap2 = getBitmap("bullet2.png");
		BlastBitmap1 = getBitmap("blast1.png");

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
				} else {
					if (DEBUG_MODE) {
						if (player.getWeapon() == Player.WEAPON_NORMAL) {
							player.setWeapon(Player.WEAPON_SHOTGUN);
						} else {
							player.setWeapon(Player.WEAPON_NORMAL);
						}
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
		score = 0;

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
		 * 第3秒时出现第1波敌人(作为敌机初始化)
		 *  E E E E E E E E E
		 * i  E E E E E E E
		 * ↑    E E E E E
		 * │      E E E
		 * ┼──→j    E
		 */
		if (playTimeSec == 3) {
			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < i * 2 + 1; j++) {
					Enemy enemy = new Enemy(enemyBitmap1, Enemy.WEAPON_NORMAL);
					enemy.setPosition(240 - enemy.getWidth() / 2 - 50 * (i - j), -enemy.getHeight() - 50 * i);
					enemy.setSpeed(0, 3);
					enemy.setVisible(true);
					enemy.setBlast(new Blast(BlastBitmap1, BlastBitmap1.getWidth() / 15, BlastBitmap1.getHeight())); // 设置爆炸效果
					enemies.add(enemy);
				}
			}
		}

		// 5秒后每0.5秒随机产生敌人
		if (playTimeSec > 5 && playTimeSec % 0.5 == 0) {
			// 复用已经隐藏的敌机
			for (Enemy e : enemies) {
				if (e.isReuseable()) {
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
	 * 碰撞检测
	 */
	private void checkCollition() {
		// 检测敌机是否与玩家碰撞
		for (Enemy enemy : enemies) {
			if (enemy.collisionWith(player)) {
//				enemy.setVisible(false);
//				player.setVisible(false);
				isGameover = true;
				return;
			}
			// 检测敌机子弹是否与玩家碰撞
			if (enemy.getBullets() != null) {
				for (Bullet bullet : enemy.getBullets()) {
					if (bullet.collisionWith(player)) {
//						bullet.setVisible(false);
//						player.setVisible(false);
						isGameover = true;
						return;
					}
				}
			}
			// 检测玩家子弹是否打到敌机
			if (player.getBullets() != null) {
				for (Bullet bullet : player.getBullets()) {
					if (bullet.collisionWith(enemy)) {
						bullet.setVisible(false);
						enemy.setVisible(false);
						Blast blast = enemy.getBlast();
						blast.setPosition(enemy.getX() + enemy.getWidth() / 2 - blast.getWidth() / 2,
								enemy.getY() + enemy.getHeight() / 2 - blast.getHeight() / 2);
						blast.setVisible(true);
						score++; // 得分
					}
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
			playTimeSec = frameCount / FPS;

			background.doLogic();
			player.doLogic();
			if (enemies != null) {
				for (Enemy e : enemies) {
					e.doLogic();
				}
			}
			doProgress();
			checkCollition();
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
			if (isGameover) {
				String str1 = "你挂了!";
				String str2 = "双击屏幕再来";
				mPaint.setTextAlign(Paint.Align.CENTER);
				mPaint.setColor(Color.WHITE);
				mPaint.setShadowLayer(5, 0, 0, Color.RED);
				mPaint.setTextSize(48);
				canvas.drawText(str1, 240, 400, mPaint);
				mPaint.setTextSize(32);
				canvas.drawText(str2, 240, 450, mPaint);
			}
			// 绘制分数
			String scoreStr = "Score: " + score;
			mPaint.setTextAlign(Paint.Align.RIGHT);
			mPaint.setColor(Color.WHITE);
			mPaint.setShadowLayer(2, 0, 0, Color.BLUE);
			mPaint.setTextSize(30);
			canvas.drawText(scoreStr, 460, 40, mPaint);

			drawDebugInfo(); // 绘制调试信息
			canvas.restore();
			holder.unlockCanvasAndPost(canvas);
		}
	}

	/**
	 * 调试信息的的绘制操作
	 */
	private void drawDebugInfo() {
		if (DEBUG_MODE) {
			String debugLine1 = "---- DEBUG MODE ----";
			String debugLine2 = "双击屏幕切换子弹, 当前: "
					+ (player.getWeapon() == Player.WEAPON_SHOTGUN ?
					"散弹发射器" : "普通子弹");
			String debugLine3 = "frameCount: " + frameCount;
			String debugLine4 = "playTimeSec: " + playTimeSec;
			String debugLine5 = "FPS: " + FPS;
			mPaint.setTextSize(16);
			mPaint.setTextAlign(Paint.Align.LEFT);
			mPaint.setColor(Color.YELLOW);
			mPaint.setShadowLayer(1, 0, 0, Color.BLACK);
			canvas.drawText(debugLine1, 20, 40, mPaint);
			canvas.drawText(debugLine2, 20, 60, mPaint);
			canvas.drawText(debugLine3, 20, 80, mPaint);
			canvas.drawText(debugLine4, 20, 100, mPaint);
			canvas.drawText(debugLine5, 20, 120, mPaint);
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
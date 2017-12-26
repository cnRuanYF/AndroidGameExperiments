package com.ruanyf.androidgame16;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Feng on 2017/10/18.
 * Update on 2017/10/19
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	public static boolean DEBUG_MODE = false; // 是否开启调试模式

	public static final int FPS = 30;
	public static final int designScreenWidth = 800;
	public static final int designScreenHeight = 480;

	private GestureDetector mGestureDetector;

	private float screenScaleX, screenScaleY;

	private boolean isRunning;

	private SurfaceHolder holder;
	private Canvas canvas;
	private Paint mPaint;

	private GameMap gameMap;
	private Player player;
	private List<NPC> npcList;

	private Bitmap gameMapBitmap;
	private Bitmap playerBitmap;
	private Bitmap npcDajiaBitmap;
	private Bitmap npcLixiaoyaoBitmap;
	private Bitmap npcSuanmingBitmap;
	private Bitmap npcTiaoshengBitmap;
	private Bitmap npcXiaogouBitmap;

	private int scrollCount; // 用于滑动计数,实现延时效果

	public GameView(Context ctx) {
		super(ctx);

		holder = getHolder();
		holder.addCallback(this);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);

		screenScaleX = (float) getResources().getDisplayMetrics().widthPixels / designScreenWidth;
		screenScaleY = (float) getResources().getDisplayMetrics().heightPixels / designScreenHeight;

		gameMapBitmap = getBitmap("cunzhuang1.png");
		playerBitmap = getBitmap("zhaolinger1.png");
		npcDajiaBitmap = getBitmap("dajia.png");
		npcLixiaoyaoBitmap = getBitmap("lixiaoyao.png");
		npcSuanmingBitmap = getBitmap("suanming.png");
		npcTiaoshengBitmap = getBitmap("tiaosheng.png");
		npcXiaogouBitmap = getBitmap("xiaogou.png");

		init(); // 初始化游戏并开局

		setFocusable(true); // 设置为可获取焦点,使按键监听生效
		setLongClickable(true); // 使手势监听生效
		mGestureDetector = new GestureDetector(ctx, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				if (scrollCount++ % 2 == 0) { // 每检测到2次滑动再触发
					if (Math.abs(distanceX) > Math.abs(distanceY)) {
						if (distanceX > 0) {
							detectDir(Dir.LEFT);
						} else {
							detectDir(Dir.RIGHT);
						}
					} else {
						if (distanceY > 0) {
							detectDir(Dir.UP);
						} else {
							detectDir(Dir.DOWN);
						}
					}
				}
				return super.onScroll(e1, e2, distanceX, distanceY);
			}

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				init(); // 双击初始化状态
				DEBUG_MODE = !DEBUG_MODE; // 切换调试状态
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
					if (!player.footCollisionWith(gameMap, -20, 0)) { // 若未和不可行走区域碰撞
						if (player.getX() > designScreenWidth / 2) { // 若玩家位置位于屏幕右半部分的情形
							player.move(-20, 0); // 按指定方向移动玩家
						} else if (gameMap.getX() < 0) { // 玩家已达屏幕中心,显示区域左侧还有未显示的地图的情形
							gameMap.move(20, 0, npcList); // 反向卷动地图(在地图类中实现NPC跟随移动)
						} else { // 地图已卷动至最左侧的情形
							player.move(-20, 0); // 玩家继续移动
						}
					}
					break;
				case RIGHT:
					if (!player.footCollisionWith(gameMap, 20, 0)) {
						if (player.getX() < designScreenWidth / 2) {
							player.move(20, 0);
						} else if (gameMap.getX() + gameMap.getTiledCols() * gameMap.getWidth() > designScreenWidth) {
							gameMap.move(-20, 0, npcList);
						} else {
							player.move(20, 0);
						}
					}
					break;
				case UP:
					if (!player.footCollisionWith(gameMap, 0, -20)) {
						if (player.getY() > designScreenHeight / 2) {
							player.move(0, -20);
						} else if (gameMap.getY() < 0) {
							gameMap.move(0, 20, npcList);
						} else {
							player.move(0, -20);
						}
					}
					break;
				case DOWN:
					if (!player.footCollisionWith(gameMap, 0, 20)) {
						if (player.getY() > designScreenHeight / 2) {
							player.move(0, 20);
						} else if (gameMap.getY() + gameMap.getTiledRows() * gameMap.getHeight() > designScreenHeight) {
							gameMap.move(0, -20, npcList);
						} else {
							player.move(0, 20);
						}
					}
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
	 * 初始化游戏
	 */
	public void init() {

		int mapOffsetY = -100; // 地图初始偏移(用于NPC相对定位)

		// 生成地图
		gameMap = new GameMap(gameMapBitmap, 40, 40, 44, 16);
		gameMap.setPosition(0, mapOffsetY);
		gameMap.setTiledCell(new int[][]{
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0,
						0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1},
				{0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
		});

		// 生成玩家
		player = new Player(playerBitmap, playerBitmap.getWidth() / 4, playerBitmap.getHeight() / 4);
		player.setDownFrameSequance(new int[]{0, 1, 2, 3});
		player.setLeftFrameSequance(new int[]{4, 5, 6, 7});
		player.setRightFrameSequance(new int[]{8, 9, 10, 11});
		player.setUpFrameSequance(new int[]{12, 13, 14, 15});
		player.setFrameSequance(player.getRightFrameSequance());
		player.setPosition(designScreenWidth / 3 - player.getWidth() / 2,
				designScreenHeight / 2);

		// 生成NPC
		npcList = new ArrayList<>();

		NPC npcDajia = new NPC(npcDajiaBitmap, npcDajiaBitmap.getWidth() / 4, npcDajiaBitmap.getHeight());
		npcDajia.setPosition(1100, 90 + mapOffsetY);
		npcList.add(npcDajia);

		NPC npcLixiaoyao = new NPC(npcLixiaoyaoBitmap, npcLixiaoyaoBitmap.getWidth() / 4, npcLixiaoyaoBitmap.getHeight() / 4);
		npcLixiaoyao.setPosition(450, 20 + mapOffsetY);
		npcLixiaoyao.setMove(new int[]{8, 9, 10, 11}, new int[]{4, 5, 6, 7}, 5, 0);
		npcList.add(npcLixiaoyao);

		NPC npcSuanming = new NPC(npcSuanmingBitmap, npcSuanmingBitmap.getWidth() / 4, npcSuanmingBitmap.getHeight() / 4);
		npcSuanming.setPosition(1200, 400 + mapOffsetY);
		npcSuanming.setMove(new int[]{12, 13, 14, 15}, new int[]{0, 1, 2, 3}, 10, -5);
		npcList.add(npcSuanming);

		NPC npcTiaosheng = new NPC(npcTiaoshengBitmap, npcTiaoshengBitmap.getWidth() / 4, npcTiaoshengBitmap.getHeight());
		npcTiaosheng.setPosition(300, 400 + mapOffsetY);
		npcList.add(npcTiaosheng);

		NPC npcXiaogou = new NPC(npcXiaogouBitmap, npcXiaogouBitmap.getWidth() / 4, npcXiaogouBitmap.getHeight() / 4);
		npcXiaogou.setPosition(750, 400 + mapOffsetY);
		npcXiaogou.setMove(new int[]{0, 1, 2, 3}, new int[]{12, 13, 14, 15}, 0, 8);
		npcList.add(npcXiaogou);

	}


	/**
	 * 游戏的逻辑操作
	 */
	private void doLogic() {
		for (NPC npc : npcList) {
			npc.doLogic();
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

			gameMap.doDraw(canvas);
			for (NPC npc : npcList) {
				if (npc.getY() + npc.getHeight() <= player.getY() + player.getHeight()) {
					npc.doDraw(canvas); // 先绘制脚部在玩家脚部“后面”的NPC
				}
			}
			player.doDraw(canvas);
			for (NPC npc : npcList) {
				if (npc.getY() + npc.getHeight() > player.getY() + player.getHeight()) {
					npc.doDraw(canvas);
				}
			}

			if (DEBUG_MODE) {
				mPaint.setColor(Color.YELLOW);
				mPaint.setShadowLayer(5, 0, 0, Color.BLACK);
				mPaint.setTextSize(16);
				canvas.drawText("DEBUG MODE", 20, 30, mPaint);
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
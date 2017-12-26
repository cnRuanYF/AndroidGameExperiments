package com.ruanyf.androidgame08;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by Feng on 2017/9/28.
 */
public class MySnakeView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	private static final int FPS = 15;
	private static final int STAGE_LEFT = 20; // 舞台尺寸相关(测试)
	private static final int STAGE_TOP = 20;
	private static final int STAGE_RIGHT = 460;
	private static final int STAGE_BOTTOM = 640;
	private static final int SNAKE_LENGTH = 20; // 初始的蛇长度
	private static final int SNAKE_DEF_DIR = SnakeBlock.DIR_UP; // 初始的蛇长度
	private int snakeDir = SNAKE_DEF_DIR; // 蛇的移动方向

	private boolean isRunning;
	private boolean isGameover; // 标记是否游戏结束

	private GestureDetector mGestureDetector;

	private SurfaceHolder holder;
	private Canvas canvas;
	private Paint mPaint;

	private SnakeBlock snakeHead;
	private ArrayList<SnakeBlock> snakeBlocks;

	public MySnakeView(Context ctx) {
		super(ctx);

		holder = getHolder();
		holder.addCallback(this);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextAlign(Paint.Align.CENTER); // 文字居中便于显示提示
		snakeBlocks = new ArrayList<SnakeBlock>(); // 用来存放蛇的每一节身子

		init(); // 初始化游戏并开局

		setFocusable(true); // 设置为可获取焦点,才可以监听到按键
		setLongClickable(true); // 若未设置可长按,整个手势监听都不生效!
		mGestureDetector = new GestureDetector(ctx, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				float moveX = e2.getX() - e1.getX();
				float moveY = e2.getY() - e1.getY();
				if (Math.abs(moveX) > Math.abs(moveY)) {
					if (moveX < 0) { // Left
//                snakeHead.setDir(SnakeBlock.DIR_LT); // 禁止调头的判断已经在setDir方法中实现
						snakeDir = SnakeBlock.DIR_LT;
					} else { // Right
//                snakeHead.setDir(SnakeBlock.DIR_RT);
						snakeDir = SnakeBlock.DIR_RT;
					}
				} else {
					if (moveY < 0) { // Up
//                snakeHead.setDir(SnakeBlock.DIR_UP);
						snakeDir = SnakeBlock.DIR_UP;
					} else { // Down
//                snakeHead.setDir(SnakeBlock.DIR_DN);
						snakeDir = SnakeBlock.DIR_DN;
					}
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				// 双击重新开始游戏的处理
				if (isGameover) {
					init(); // 在GameOver状态才重新开始
				}
				return super.onDoubleTap(e);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_UP:
			case KeyEvent.KEYCODE_W:
//          snakeHead.setDir(SnakeBlock.DIR_UP);
				snakeDir = SnakeBlock.DIR_UP;
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
			case KeyEvent.KEYCODE_S:
//          snakeHead.setDir(SnakeBlock.DIR_DN);
				snakeDir = SnakeBlock.DIR_DN;
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
			case KeyEvent.KEYCODE_A:
//          snakeHead.setDir(SnakeBlock.DIR_LT);
				snakeDir = SnakeBlock.DIR_LT;
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
			case KeyEvent.KEYCODE_D:
//          snakeHead.setDir(SnakeBlock.DIR_RT);
				snakeDir = SnakeBlock.DIR_RT;
				break;
			case KeyEvent.KEYCODE_ENTER:
			case KeyEvent.KEYCODE_SPACE:
				if (isGameover) {
					init();
				}
				break;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 初始化游戏(重新开局)
	 */
	public void init() {
		isGameover = false;
		snakeDir = SNAKE_DEF_DIR; // 方向还原为默认
		snakeBlocks.clear(); // 清除原来的蛇体数据
		int bornX = 100 + (int) (200 / SnakeBlock.WIDTH * Math.random()) * SnakeBlock.WIDTH; // 生成随机出生点
		int bornY = 100 + (int) (400 / SnakeBlock.WIDTH * Math.random()) * SnakeBlock.WIDTH;
		for (int i = 0; i < SNAKE_LENGTH; i++) { // 按初始蛇长添加若干节蛇身
			snakeBlocks.add(new SnakeBlock(bornX, bornY + SnakeBlock.WIDTH * i, SNAKE_DEF_DIR));
		}
		snakeHead = snakeBlocks.get(0); // 蛇身数组的第一个作为蛇头
	}

	/**
	 * 游戏的逻辑操作
	 */
	private void doLogic() {
		if (!isGameover) {
			snakeHead.setDir(snakeDir); // 在新的一帧绘制前再将蛇转向,避免一帧之内多次转向导致调头
			for (int i = 0; i < snakeBlocks.size(); i++) { // 遍历蛇身数组
				snakeBlocks.get(i).doLogic(); // 由每一节蛇身执行自身的逻辑操作
			}
			for (int i = snakeBlocks.size() - 1; i > 0; i--) { // 修改方向需从蛇尾到蛇头遍历
				snakeBlocks.get(i).setDir(snakeBlocks.get(i - 1).getDir()); // 蛇尾获取前1节身体的方向...第2节获取蛇头的方向(由此,每次操作只需改变蛇头方向)
			}
			// 越界判断,撞墙则游戏结束(蛇身走的路线都是蛇头走过的,因此只需判断蛇头,蛇身的定位点为左上角,所以右侧与下侧需要加上蛇身宽度)
			if (snakeHead.getX() < STAGE_LEFT || snakeHead.getX() + SnakeBlock.WIDTH > STAGE_RIGHT || snakeHead.getY() < STAGE_TOP || snakeHead.getY() + SnakeBlock.WIDTH > STAGE_BOTTOM) {
				isGameover = true;
			}
		}
	}

	/**
	 * 游戏的绘制操作
	 */
	private void doDraw() {
		canvas = holder.lockCanvas();
		if (canvas != null) {
			canvas.drawColor(Color.DKGRAY);

			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setColor(Color.CYAN);
			canvas.drawRect(STAGE_LEFT, STAGE_TOP, STAGE_RIGHT, STAGE_BOTTOM, mPaint); // 画舞台边框

			mPaint.setStyle(Paint.Style.FILL);
			for (int i = 0; i < snakeBlocks.size(); i++) {
				snakeBlocks.get(i).doDraw(canvas, mPaint); // 由每一节蛇身自身执行绘制操作
			}
			if (isGameover) {
				mPaint.setColor(Color.WHITE);
				mPaint.setTextScaleX(0.25f);
				mPaint.setTextSize(220);
				canvas.drawText("GAME OVER", canvas.getWidth() / 2, canvas.getHeight() / 2, mPaint);
				mPaint.setTextScaleX(1);
				mPaint.setTextSize(30);
				canvas.drawText("你居然把自己给撞屎了", canvas.getWidth() / 2, canvas.getHeight() / 2 + 50, mPaint);
				mPaint.setTextSize(20);
				canvas.drawText("Space/Enter/双击屏幕开始新游戏", canvas.getWidth() / 2, canvas.getHeight() / 2 + 100, mPaint);
			}
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

package com.ruanyf.androidgame17;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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

	private int scrollCount; // 用于滑动计数,实现延时效果

	private int collisionNpcId; // 碰撞的NPCID(避免重复对话)
	private int talkIndex; // NPC对话索引

	public GameView(Context ctx) {
		super(ctx);

		holder = getHolder();
		holder.addCallback(this);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);

		screenScaleX = (float) getResources().getDisplayMetrics().widthPixels / designScreenWidth;
		screenScaleY = (float) getResources().getDisplayMetrics().heightPixels / designScreenHeight;

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
			public boolean onSingleTapConfirmed(MotionEvent e) {
				detectSingleTap();
				return super.onSingleTapConfirmed(e);
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
	 * 识别到单击事件的操作
	 */
	private void detectSingleTap() {
		if (talkIndex != -1) {
			talkIndex++; // 若索引不为-1,则对话索引自增(下次将绘制下一条对话)
			if (talkIndex == npcList.get(collisionNpcId).getTalks().size()) {
				talkIndex = -1; // 若对话结束(索引达到对话中NPC的对话长度),则重置索引
			}
		}
	}

	/**
	 * 识别到方向的操作
	 */
	private void detectDir(Dir dir) {
		if (talkIndex == -1) { // 未对话时人物才可以移动
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
	 * 从xml文件中获取对话(通过DOM方式解析)
	 */
	public List<Talk> getTalksFromXML(String filePath, String talksId) {
		List<Talk> talks = new ArrayList<>();
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(getContext().getAssets().open(filePath));
			Element documentElement = document.getDocumentElement(); // 获取文档根元素节点
			NodeList talksNodeList = documentElement.getElementsByTagName("talks"); // 根据标签名获得节点列表
			for (int i = 0; i < talksNodeList.getLength(); i++) {
				Element talksElement = (Element) talksNodeList.item(i); // 遍历节点列表取出元素节点
				if (talksId.equals(talksElement.getAttribute("id"))) { // 判断标签中属性id的值
					NodeList talkNodeList = talksElement.getElementsByTagName("talk"); // 继续遍历对话组中的所有对话节点
					for (int j = 0; j < talkNodeList.getLength(); j++) {
						Element talkElement = (Element) talkNodeList.item(j);
						String talkName = talkElement.getAttribute("name");
						String talkText = talkElement.getTextContent();
						talks.add(new Talk(talkName, talkText));
					}
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return talks;
	}

	/**
	 * 初始化游戏
	 */
	public void init() {

		Bitmap gameMapBitmap = getBitmap("cunzhuang1.png");
		Bitmap playerBitmap = getBitmap("zhaolinger1.png");
		Bitmap npcDajiaBitmap = getBitmap("dajia.png");
		Bitmap npcLixiaoyaoBitmap = getBitmap("lixiaoyao.png");
		Bitmap npcSuanmingBitmap = getBitmap("suanming.png");
		Bitmap npcTiaoshengBitmap = getBitmap("tiaosheng.png");
		Bitmap npcXiaogouBitmap = getBitmap("xiaogou.png");

		int mapOffsetY = -100; // 地图初始偏移(用于NPC相对定位)

		collisionNpcId = -1;
		talkIndex = -1;

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
		npcDajia.setTalks(getTalksFromXML("story.xml", "t004"));
		npcList.add(npcDajia);

		NPC npcLixiaoyao = new NPC(npcLixiaoyaoBitmap, npcLixiaoyaoBitmap.getWidth() / 4, npcLixiaoyaoBitmap.getHeight() / 4) {
			@Override // NPC在屋顶,重写碰撞检测
			public boolean collisionWith(Sprite anotherSpr) {
				if (getX() + getWidth() < anotherSpr.getX()                   // 排除anotherSpr在右侧的情形
						|| anotherSpr.getX() + anotherSpr.getWidth() < getX() // ..anotherSpr在左侧..
						|| getY() + getHeight() + 75 < anotherSpr.getY()      // ..anotherSpr在下方..(NPC在屋顶,特殊判断)
						|| anotherSpr.getY() + anotherSpr.getHeight() < getY() + anotherSpr.getHeight() / 2 // ..anotherSpr在上方..
						) {
					return false;
				} else {
					return true;
				}
			}
		};
		npcLixiaoyao.setPosition(450, 20 + mapOffsetY);
		npcLixiaoyao.setMove(new int[]{8, 9, 10, 11}, new int[]{4, 5, 6, 7}, 5, 0);
		npcLixiaoyao.setTalks(getTalksFromXML("story.xml", "t003"));
		npcList.add(npcLixiaoyao);

		NPC npcSuanming = new NPC(npcSuanmingBitmap, npcSuanmingBitmap.getWidth() / 4, npcSuanmingBitmap.getHeight() / 4);
		npcSuanming.setPosition(1200, 400 + mapOffsetY);
		npcSuanming.setMove(new int[]{12, 13, 14, 15}, new int[]{0, 1, 2, 3}, 10, -5);
		npcSuanming.setTalks(getTalksFromXML("story.xml", "t002"));
		npcList.add(npcSuanming);

		NPC npcTiaosheng = new NPC(npcTiaoshengBitmap, npcTiaoshengBitmap.getWidth() / 4, npcTiaoshengBitmap.getHeight());
		npcTiaosheng.setPosition(300, 400 + mapOffsetY);
		npcTiaosheng.setTalks(getTalksFromXML("story.xml", "t000"));
		npcList.add(npcTiaosheng);

		NPC npcXiaogou = new NPC(npcXiaogouBitmap, npcXiaogouBitmap.getWidth() / 4, npcXiaogouBitmap.getHeight() / 4);
		npcXiaogou.setPosition(750, 400 + mapOffsetY);
		npcXiaogou.setMove(new int[]{0, 1, 2, 3}, new int[]{12, 13, 14, 15}, 0, 8);
		npcXiaogou.setTalks(getTalksFromXML("story.xml", "t001"));
		npcList.add(npcXiaogou);

		for (NPC npc : npcList) {
			for (Talk talk : npc.getTalks()) {
				Log.d(talk.getTalkName(), talk.getTalkText());
			}
		}

	}

	/**
	 * 玩家与NPC的碰撞检测
	 */
	private boolean collisionWithNpc() {
		if (npcList != null) {
			for (int i = 0; i < npcList.size(); i++) {
				if (collisionNpcId == i) {
					continue; // 若是上次碰撞到的NPC则跳过判定
				}
				if (npcList.get(i).collisionWith(player)) {
					collisionNpcId = i; // 记录碰撞的NPCID
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 游戏的逻辑操作
	 */
	private void doLogic() {
		if (collisionWithNpc()) {
			talkIndex = 0; // 与NPC碰撞开始对话
		}
		if (talkIndex == -1) {
			// 不在对话状态NPC才可移动
			for (NPC npc : npcList) {
				npc.doLogic();
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
			canvas.scale(screenScaleX, screenScaleY); // 根据开始获得的缩放比例改变画布坐标系

			gameMap.doDraw(canvas); // 绘制地图

			for (NPC npc : npcList) { // 先绘制脚部在玩家脚部“后面”的NPC
				if (npc.getY() + npc.getHeight() <= player.getY() + player.getHeight()) {
					npc.doDraw(canvas);
				}
			}

			player.doDraw(canvas); // 绘制玩家

			for (NPC npc : npcList) {  // 再绘制在玩家“前面”的NPC
				if (npc.getY() + npc.getHeight() > player.getY() + player.getHeight()) {
					npc.doDraw(canvas);
				}
			}

			if (talkIndex != -1) { // 绘制对话
				npcList.get(collisionNpcId).getTalks().get(talkIndex).doDraw(canvas);
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
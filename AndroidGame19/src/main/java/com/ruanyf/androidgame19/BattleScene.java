package com.ruanyf.androidgame19;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import java.io.IOException;

/**
 * 战斗场景类
 * Created by Feng on 2017/11/01.
 */
public class BattleScene {

	private Context ctx;

	private Paint mPaint;
	private RectF rectArea; // 用于点击定位
	private RectF btnAttackArea, btnDefenceArea, btnItemArea, btnSkillArea;

	private BattleMenuState menuState;
	private int frameCount;

	private Bitmap battleBGBitmap;
	private Bitmap btnAttackBitmap, btnDefenceBitmap, btnItemBitmap, btnSkillBitmap;
	private Bitmap choicePanelBitmap;
	private Bitmap playerBitmap1, playerBitmap2;
	private Bitmap zymw1Bitmap, zymw2Bitmap;
	private Bitmap fireSkillBitmap, iceSkillBitmap;

	private Sprite playerSpr1, playerSpr2;
	private Sprite enemySpr, enemyAttackSpr;
	private Sprite fireSkillSpr, iceSkillSpr;

	public BattleScene(Context ctx) {
		super();
		this.ctx = ctx;

		mPaint = new Paint();
		mPaint.setAntiAlias(true);

		rectArea = new RectF();

		init(); // 初始化游戏并开局
	}

	/**
	 * 识别到单击事件的操作
	 */
	public void detectSingleTap(float tapX, float tapY) {
		switch (menuState) {
			case NULL:
				if (btnSkillArea.contains(tapX, tapY)) {
					menuState = BattleMenuState.SKILL;
				}
				break;
			case SKILL:
				if (btnSkillArea.contains(tapX, tapY)) {
					menuState = BattleMenuState.NULL;
					break;
				}
				rectArea.set(420, 180, 570, 200);
				if (rectArea.contains(tapX, tapY)) {
					fireSkillSpr.setVisible(true);
					menuState = BattleMenuState.NULL;
					break;
				}
				rectArea.set(420, 200, 570, 220);
				if (rectArea.contains(tapX, tapY)) {
					iceSkillSpr.setVisible(true);
					menuState = BattleMenuState.NULL;
					break;
				}
				break;
			default:
				break;
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
			return BitmapFactory.decodeStream(ctx.getAssets().open(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 初始化场景
	 */
	public void init() {
		// 获取图片
		battleBGBitmap = getBitmap("battle/bg_battle.png");
		choicePanelBitmap = getBitmap("battle/bg_choice_panel.png");
		playerBitmap1 = getBitmap("battle/player_zhaolinger.png");
		playerBitmap2 = getBitmap("battle/player_lixiaoyao.png");
		zymw1Bitmap = getBitmap("battle/enemy_zymw_1.png");
		zymw2Bitmap = getBitmap("battle/enemy_zymw_2.png");
		fireSkillBitmap = getBitmap("battle/skill_fire.png");
		iceSkillBitmap = getBitmap("battle/skill_ice.png");
		btnAttackBitmap = getBitmap("battle/btn_attack.png");
		btnDefenceBitmap = getBitmap("battle/btn_defence.png");
		btnItemBitmap = getBitmap("battle/btn_item.png");
		btnSkillBitmap = getBitmap("battle/btn_skill.png");

		// 生成玩家,敌人,攻击精灵
		playerSpr1 = new Sprite(playerBitmap1);
		playerSpr2 = new Sprite(playerBitmap2);
		enemySpr = new Sprite(zymw1Bitmap, zymw1Bitmap.getWidth() / 4, zymw1Bitmap.getHeight() / 4);
		enemyAttackSpr = new Sprite(zymw2Bitmap, zymw2Bitmap.getWidth(), zymw2Bitmap.getHeight() / 4);
		fireSkillSpr = new Sprite(fireSkillBitmap, fireSkillBitmap.getWidth() / 4, fireSkillBitmap.getHeight());
		iceSkillSpr = new Sprite(iceSkillBitmap, iceSkillBitmap.getWidth() / 4, iceSkillBitmap.getHeight());

		playerSpr1.setPosition(600, 350);
		playerSpr2.setPosition(680, 330);
		enemySpr.setPosition(100, 100);
		enemyAttackSpr.setPosition(600, 300);
		fireSkillSpr.setPosition(100, 100);
		iceSkillSpr.setPosition(100, 100);

		playerSpr1.setVisible(true);
		playerSpr2.setVisible(true);
		enemySpr.setVisible(true);

		// 按钮区域
		btnAttackArea = new RectF(650, 150, 650 + btnAttackBitmap.getWidth(), 150 + btnAttackBitmap.getHeight());
		btnDefenceArea = new RectF(700, 200, 700 + btnDefenceBitmap.getWidth(), 200 + btnDefenceBitmap.getHeight());
		btnItemArea = new RectF(650, 250, 650 + btnItemBitmap.getWidth(), 250 + btnItemBitmap.getHeight());
		btnSkillArea = new RectF(600, 200, 600 + btnSkillBitmap.getWidth(), 200 + btnSkillBitmap.getHeight());

		// 重置状态
		menuState = BattleMenuState.NULL;
		frameCount = 0;

	}

	/**
	 * 游戏的逻辑操作
	 */
	public void doLogic() {
		frameCount++;
		if (frameCount % 5 == 0 && enemySpr.isVisible()) { // 若敌人可见,每5帧执行一次
			enemySpr.nextFrame();
		}

		if (frameCount % (GameView.FPS * 3) == 0) { // 敌人每3秒攻击一次
			enemySpr.setVisible(false);
			enemyAttackSpr.setVisible(true);
		}
		if (frameCount % 5 == 0) { // 同上延迟处理,每5帧执行一次
			if (enemyAttackSpr.isVisible()) {
				enemyAttackSpr.nextFrame();
				if (enemyAttackSpr.getFrameSequanceIndex() == 0) { // 若回到第0帧则代表攻击动作结束
					enemyAttackSpr.setVisible(false);
					enemySpr.setVisible(true);
				}
			}
			if (fireSkillSpr.isVisible()) {
				fireSkillSpr.nextFrame();
				if (fireSkillSpr.getFrameSequanceIndex() == 0) { // 同上,判断攻击是否结束
					fireSkillSpr.setVisible(false);
				}
			}
			if (iceSkillSpr.isVisible()) {
				iceSkillSpr.nextFrame();
				if (iceSkillSpr.getFrameSequanceIndex() == 0) { // 同上,判断攻击是否结束
					iceSkillSpr.setVisible(false);
				}
			}
		}
	}

	/**
	 * 游戏的绘制操作
	 */
	public void doDraw(Canvas canvas) {
		// 按顺序绘制视觉上的从后往前
		canvas.drawBitmap(battleBGBitmap, 0, 0, null);

		enemySpr.doDraw(canvas);
		fireSkillSpr.doDraw(canvas);
		iceSkillSpr.doDraw(canvas);
		enemyAttackSpr.doDraw(canvas);
		playerSpr1.doDraw(canvas);
		playerSpr2.doDraw(canvas);

		canvas.drawBitmap(btnAttackBitmap, null, btnAttackArea, null);
		canvas.drawBitmap(btnDefenceBitmap, null, btnDefenceArea, null);
		canvas.drawBitmap(btnItemBitmap, null, btnItemArea, null);
		canvas.drawBitmap(btnSkillBitmap, null, btnSkillArea, null);

		switch (menuState) {
			case SKILL:
				canvas.drawBitmap(choicePanelBitmap, 400, 150, null);
				mPaint.setTextSize(16);
				mPaint.setColor(Color.RED);
				canvas.drawText("火·", 420, 200, mPaint);
				mPaint.setColor(Color.BLUE);
				canvas.drawText("水·", 420, 220, mPaint);
				mPaint.setColor(Color.BLACK);
				canvas.drawText("三味真火　　12SP", 440, 200, mPaint);
				canvas.drawText("玄冰诀　　　10SP", 440, 220, mPaint);
				break;
			default:
				break;
		}
	}
}

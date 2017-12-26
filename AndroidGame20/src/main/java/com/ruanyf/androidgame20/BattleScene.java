package com.ruanyf.androidgame20;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

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
	private Bitmap playerDeadBitmap1, playerDeadBitmap2;
	private Bitmap playerStatBitmap1, playerStatBitmap2;
	private Bitmap zymw1Bitmap, zymw2Bitmap;
	private Bitmap fireSkillBitmap, iceSkillBitmap;

	private Sprite playerSpr1, playerSpr2;
	private Sprite enemySpr, enemyAttackSpr;
	private Sprite fireSkillSpr, iceSkillSpr;
	private Sprite playerDeadSpr1, playerDeadSpr2;


	private int player1HPCurrent, player1HPTotal, player1SPCurrent, player1SPTotal;
	private int player2HPCurrent, player2HPTotal, player2SPCurrent, player2SPTotal;
	private int enemyHPCurrent, enemyHPTotal, enemySPCurrent, enemySPTotal;
	private boolean isHitPlayer1, isHitPlayer2, isFireHitEnemy, isIceHitEnemy;
	private String player1HurtString, player2HurtString, enemyHurtString;
	private int player1HurtDelay, player2HurtDelay, enemyHurtDelay;

	private Shader hpBarShader, spBarShader, barStrokeShader;


	public BattleScene(Context ctx) {
		super();
		this.ctx = ctx;

		mPaint = new Paint();
		mPaint.setAntiAlias(true);

		rectArea = new RectF();

		hpBarShader = new LinearGradient(0, 415, 0, 425, Color.rgb(255, 128, 128), Color.rgb(240, 0, 0), Shader.TileMode.REPEAT);
		spBarShader = new LinearGradient(0, 435, 0, 445, Color.rgb(128, 128, 255), Color.rgb(0, 0, 240), Shader.TileMode.REPEAT);
		barStrokeShader = new LinearGradient(0, 410, 0, 430, Color.BLACK, Color.GRAY, Shader.TileMode.REPEAT);


		init(); // 初始化游戏并开局
	}

	/**
	 * 设置参战角色初始状态
	 */
	public void setPlayerState1(int currentHP, int totalHP, int currentSP, int totalSP) {
		this.player1HPCurrent = currentHP;
		this.player1HPTotal = totalHP;
		this.player1SPCurrent = currentSP;
		this.player1SPTotal = totalSP;
	}

	public void setPlayerState2(int currentHP, int totalHP, int currentSP, int totalSP) {
		this.player2HPCurrent = currentHP;
		this.player2HPTotal = totalHP;
		this.player2SPCurrent = currentSP;
		this.player2SPTotal = totalSP;
	}

	public void setEnemyState(int currentHP, int totalHP, int currentSP, int totalSP) {
		this.enemyHPCurrent = currentHP;
		this.enemyHPTotal = totalHP;
		this.enemySPCurrent = currentSP;
		this.enemySPTotal = totalSP;
	}

	/**
	 * 识别到单击事件的操作
	 */
	public void detectSingleTap(float tapX, float tapY) {
		// 玩家或敌人死亡时不可操作
		if (player1HPCurrent == 0 && player2HPCurrent == 0 || enemyHPCurrent == 0) {
			menuState = BattleMenuState.NULL;
			return;
		}
		switch (menuState) {
			case NULL:
				if (btnSkillArea.contains(tapX, tapY)) {
					menuState = BattleMenuState.SKILL;
				}
				break;
			case SKILL:
				// 关闭菜单
				if (btnSkillArea.contains(tapX, tapY)) {
					menuState = BattleMenuState.NULL;
					break;
				}
				// 三味真火(P1)
				rectArea.set(420, 180, 570, 200);
				if (rectArea.contains(tapX, tapY)
						&& player1HPCurrent > 0 && player1SPCurrent >= 12) { // 不死亡且SP足够才可使用技能
					player1SPCurrent -= 12; // 扣除对应SP
					fireSkillSpr.setVisible(true);
					menuState = BattleMenuState.NULL;
					break;
				}
				// 玄冰诀(P2)
				rectArea.set(420, 200, 570, 220);
				if (rectArea.contains(tapX, tapY)
						&& player2HPCurrent > 0 && player2SPCurrent >= 10) { // 不死亡且SP足够才可使用技能
					player2SPCurrent -= 12; // 扣除对应SP
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
		playerDeadBitmap1 = getBitmap("battle/player_zhaolinger_dead.png");
		playerDeadBitmap2 = getBitmap("battle/player_lixiaoyao_dead.png");
		playerStatBitmap1 = getBitmap("battle/stat_zhaolinger.png");
		playerStatBitmap2 = getBitmap("battle/stat_lixiaoyao.png");
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
		playerDeadSpr1 = new Sprite(playerDeadBitmap1);
		playerDeadSpr2 = new Sprite(playerDeadBitmap2);
		enemySpr = new Sprite(zymw1Bitmap, zymw1Bitmap.getWidth() / 4, zymw1Bitmap.getHeight() / 4);
		enemyAttackSpr = new Sprite(zymw2Bitmap, zymw2Bitmap.getWidth(), zymw2Bitmap.getHeight() / 4);
		fireSkillSpr = new Sprite(fireSkillBitmap, fireSkillBitmap.getWidth() / 4, fireSkillBitmap.getHeight());
		iceSkillSpr = new Sprite(iceSkillBitmap, iceSkillBitmap.getWidth() / 4, iceSkillBitmap.getHeight());

		playerSpr1.setPosition(600, 350);
		playerSpr2.setPosition(680, 330);
		playerDeadSpr1.setPosition(650, 400);
		playerDeadSpr2.setPosition(680, 380);
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

		if ((player1HPCurrent > 0 || player2HPCurrent > 0) && enemyHPCurrent > 0 // 双方都存活时敌人才攻击
				&& frameCount % (GameView.FPS * 3) == 0) { // 敌人每3秒攻击一次
			enemySpr.setVisible(false);
			enemyAttackSpr.setVisible(true);
		}
		if (frameCount % 5 == 0) { // 同上延迟处理,每5帧执行一次
			// 敌人攻击的处理
			if (enemyAttackSpr.isVisible()) {
				enemyAttackSpr.nextFrame();
				if (enemyAttackSpr.getFrameSequanceIndex() == 0) { // 若回到第0帧则代表攻击动作结束
					enemyAttackSpr.setVisible(false);
					enemySpr.setVisible(true);
					if (isHitPlayer1) {
						if (Math.random() > 0.9) { // 命中率为0.9
							player1HurtString = "Miss";
						} else {
							int hurt = 10 + (int) (Math.random() * 5); // 10~14随机伤害
							player1HurtString = "-" + hurt;
							player1HPCurrent -= hurt;
							if (player1HPCurrent < 0) { // 防止生命值为负
								player1HPCurrent = 0;
							}
						}
						isHitPlayer1 = false; // 触发一次不再触发
					}
					if (isHitPlayer2) {
						if (Math.random() > 0.9) { // 命中率为0.9
							player2HurtString = "Miss";
						} else {
							int hurt = 10 + (int) (Math.random() * 5); // 10~14随机伤害
							player2HurtString = "-" + hurt;
							player2HPCurrent -= hurt;
							if (player2HPCurrent < 0) { // 防止生命值为负
								player2HPCurrent = 0;
							}
						}
						isHitPlayer2 = false;
					}
				}
			}
			if (fireSkillSpr.isVisible()) {
				fireSkillSpr.nextFrame();
				if (fireSkillSpr.getFrameSequanceIndex() == 0) { // 同上,判断攻击是否结束
					fireSkillSpr.setVisible(false);
					if (isFireHitEnemy) {
						if (Math.random() > 0.9) { // 命中率为0.9
							enemyHurtString = "Miss";
						} else {
							int hurt = 20 + (int) (Math.random() * 5); // 20~29随机伤害
							enemyHurtString = "-" + hurt;
							enemyHPCurrent -= hurt;
							if (enemyHPCurrent < 0) { // 防止生命值为负
								enemyHPCurrent = 0;
							}
						}
						isFireHitEnemy = false;
					}
				}
			}
			if (iceSkillSpr.isVisible()) {
				iceSkillSpr.nextFrame();
				if (iceSkillSpr.getFrameSequanceIndex() == 0) { // 同上,判断攻击是否结束
					iceSkillSpr.setVisible(false);
					if (isIceHitEnemy) {
						if (Math.random() > 0.9) { // 命中率为0.9
							enemyHurtString = "Miss";
						} else {
							int hurt = 10 + (int) (Math.random() * 10); // 10~19随机伤害
							enemyHurtString = "-" + hurt;
							enemyHPCurrent -= hurt;
							if (enemyHPCurrent < 0) { // 防止生命值为负
								enemyHPCurrent = 0;
							}
						}
						isIceHitEnemy = false;
					}
				}
			}
		}
		checkCollision(); // 碰撞检测
		checkDead(); // 死亡检测
	}

	/**
	 * 死亡检测
	 */
	private void checkDead() {
		if (player1HPCurrent == 0) {
			playerSpr1.setVisible(false);
			playerDeadSpr1.setVisible(true);
		}
		if (player2HPCurrent == 0) {
			playerSpr2.setVisible(false);
			playerDeadSpr2.setVisible(true);
		}
		if (enemyHPCurrent == 0) {
			enemySpr.setVisible(false);
		}
	}

	/**
	 * 碰撞检测
	 */
	private void checkCollision() {
		if (!isHitPlayer1) {
			if (enemyAttackSpr.collisionWith(playerSpr1)) {
				isHitPlayer1 = true;
			}
		}
		if (!isHitPlayer2) {
			if (enemyAttackSpr.collisionWith(playerSpr2)) {
				isHitPlayer2 = true;
			}
		}
		if (!isFireHitEnemy) {
			if (fireSkillSpr.collisionWith(enemySpr)) {
				isFireHitEnemy = true;
			}
		}
		if (!isIceHitEnemy) {
			if (iceSkillSpr.collisionWith(enemySpr)) {
				isIceHitEnemy = true;
			}
		}
	}

	/**
	 * 游戏的绘制操作
	 */
	public void doDraw(Canvas canvas) {
		// 按顺序绘制视觉上的从后往前
		canvas.drawBitmap(battleBGBitmap, 0, 0, null);
		// 人物与攻击技能
		enemySpr.doDraw(canvas);
		fireSkillSpr.doDraw(canvas);
		iceSkillSpr.doDraw(canvas);
		enemyAttackSpr.doDraw(canvas);
		playerSpr1.doDraw(canvas);
		playerSpr2.doDraw(canvas);
		playerDeadSpr1.doDraw(canvas);
		playerDeadSpr2.doDraw(canvas);
		// 菜单按钮
		canvas.drawBitmap(btnAttackBitmap, null, btnAttackArea, null);
		canvas.drawBitmap(btnDefenceBitmap, null, btnDefenceArea, null);
		canvas.drawBitmap(btnItemBitmap, null, btnItemArea, null);
		canvas.drawBitmap(btnSkillBitmap, null, btnSkillArea, null);
		// 菜单
		switch (menuState) {
			case SKILL:
				canvas.drawBitmap(choicePanelBitmap, 400, 150, null);
				mPaint.setTextSize(16);
				mPaint.setColor(player1HPCurrent > 0 && player1SPCurrent >= 12 ? Color.RED : Color.LTGRAY); // 技能不可用显示灰色
				canvas.drawText("火·", 420, 200, mPaint);
				mPaint.setColor(player1HPCurrent > 0 && player1SPCurrent >= 12 ? Color.BLACK : Color.LTGRAY);
				canvas.drawText("三味真火　　12SP", 440, 200, mPaint);
				mPaint.setColor(player2HPCurrent > 0 && player2SPCurrent >= 10 ? Color.BLUE : Color.LTGRAY);
				canvas.drawText("水·", 420, 220, mPaint);
				mPaint.setColor(player2HPCurrent > 0 && player2SPCurrent >= 10 ? Color.BLACK : Color.LTGRAY);
				canvas.drawText("玄冰诀　　　10SP", 440, 220, mPaint);
				break;
			default:
				break;
		}
		// 角色状态
		canvas.drawBitmap(playerStatBitmap1, 50, 350, null);
		canvas.drawBitmap(playerStatBitmap2, 250, 350, null);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(2);
		mPaint.setShader(barStrokeShader);
		canvas.drawRect(135, 415, 135 + player1HPTotal, 425, mPaint);
		canvas.drawRect(340, 415, 340 + player2HPTotal, 425, mPaint);
		canvas.drawRect(130, 435, 130 + player1SPTotal, 445, mPaint);
		canvas.drawRect(330, 435, 330 + player2SPTotal, 445, mPaint);
		if (enemyHPCurrent > 0) {
			canvas.drawRect(120, 80, 120 + enemyHPTotal, 85, mPaint);
		}
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setShader(hpBarShader);
		canvas.drawRect(135, 415, 135 + player1HPCurrent, 425, mPaint);
		canvas.drawRect(340, 415, 340 + player2HPCurrent, 425, mPaint);
		if (enemyHPCurrent > 0) {
			canvas.drawRect(120, 80, 120 + enemyHPCurrent, 85, mPaint);
		}
		mPaint.setShader(spBarShader);
		canvas.drawRect(130, 435, 130 + player1SPCurrent, 445, mPaint);
		canvas.drawRect(330, 435, 330 + player2SPCurrent, 445, mPaint);
		mPaint.setShader(null);
		// 伤害浮字
		mPaint.setTextSize(24);
		mPaint.setColor(Color.RED);
		if (enemyHurtString != null) {
			canvas.drawText(enemyHurtString, 200, 200, mPaint);
			if (enemyHurtDelay++ == GameView.FPS) { // 伤害显示1秒
				enemyHurtDelay = 0;
				enemyHurtString = null;
			}
		}
		if (player1HurtString != null) {
			canvas.drawText(player1HurtString, 620, 340, mPaint);
			if (player1HurtDelay++ == GameView.FPS) { // 伤害显示1秒
				player1HurtDelay = 0;
				player1HurtString = null;
			}
		}
		if (player2HurtString != null) {
			canvas.drawText(player2HurtString, 720, 340, mPaint);
			if (player2HurtDelay++ == GameView.FPS) { // 伤害显示1秒
				player2HurtDelay = 0;
				player2HurtString = null;
			}
		}
	}
}

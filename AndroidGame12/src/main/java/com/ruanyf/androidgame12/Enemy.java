package com.ruanyf.androidgame12;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

/**
 * Created by Feng on 2017/9/30.
 */
public class Enemy extends Sprite {

	public static final int WEAPON_NORMAL = 0; // (拓展)普通武器
	public static final int WEAPON_SHOTGUN = 1; // (拓展)散弹

	private float frameCount; // 帧数计数
	private float fireRate = 1; // 每秒发射子弹数

	private List<Bullet> bullets; // 弹夹(用于复用子弹对象,节省资源)
	private Blast blast; // 爆炸效果

	private int weapon; // (拓展)武器种类

	public Enemy(Bitmap bitmap, int weapon) {
		super(bitmap);
		this.weapon = weapon;
	}

	public List<Bullet> getBullets() {
		return bullets;
	}

	public void setBullets(List<Bullet> bullets) {
		this.bullets = bullets;
	}

	public void setWeapon(int weapon) {
		this.weapon = weapon;
	}

	public Blast getBlast() {
		return blast;
	}

	public void setBlast(Blast blast) {
		this.blast = blast;
	}

	public void move(float distanceX, float distanceY) {
		super.move(distanceX, distanceY);
	}

	/**
	 * 发射普通子弹
	 */
	public void fireNormal() {
		for (Bullet bullet : bullets) {
			if (!bullet.isVisible()) { // 复用已经隐藏的子弹
				bullet.setSpeed(0, 5);
				bullet.setPosition(getX() + getWidth() / 2 - bullet.getWidth() / 2, getY() + getHeight());
				bullet.setVisible(true);
				break; // 一次只发射一颗,即可结束循环
			}
		}
	}

	/**
	 * 发射散弹
	 */
	public void fireShotgun() {
		int fireCount = 0;
		for (Bullet bullet : bullets) {
			if (!bullet.isVisible()) { // 复用已经隐藏的子弹
				fireCount++;
				switch (fireCount) {
					case 1:
						bullet.setSpeed(-1, 4.5f);
						break;
					case 2:
						bullet.setSpeed(1, 4.5f);
						break;
					case 3:
						bullet.setSpeed(0, 5);
						break;
				}
				bullet.setPosition(getX() + getWidth() / 2 - bullet.getWidth() / 2, getY() + getHeight());
				bullet.setVisible(true);
				if (fireCount == 3) {
					break;
				} // 一次累计发射3颗,结束循环
			}
		}
	}

	/**
	 * 检测是否可复用
	 */
	public boolean isReuseable() {
		// 自身显示时不可复用
		if (isVisible()) {
			return false;
		}
		// 还有子弹显示时不可复用
		if (bullets != null) {
			for (Bullet b : bullets) {
				if (b.isVisible()) {
					return false;
				}
			}
		}
		// 爆炸效果显示时不可复用
		if (blast != null && blast.isVisible()) {
			return false;
		}
		return true;
	}

	/**
	 * 逻辑操作
	 */
	public void doLogic() {
		super.doLogic();
		// 若敌机被消灭则不可继续开火
		if (isVisible()) {
			frameCount++;
			if (frameCount > GameView.FPS / fireRate) { // 通过FPS与每秒子弹数计算每次开火间隔帧数
				frameCount = 0;
				// 开火的处理
				if (isVisible() && bullets != null) {
					switch (weapon) { // 根据武器的不同选择不同的开火方式
						case WEAPON_NORMAL:
							fireNormal();
							break;
						case WEAPON_SHOTGUN:
							fireShotgun();
							break;
					}
				}
			}
		}
		if (bullets != null) { // 子弹执行各自的逻辑操作
			for (Bullet bullet : bullets) {
				bullet.doLogic();
			}
		}
		if (blast != null) { // 爆炸特效逻辑
			blast.doLogic();
		}
	}

	/**
	 * 绘制操作
	 */
	public void doDraw(Canvas canvas) {
		super.doDraw(canvas);
		if (bullets != null) { // 子弹执行各自的绘制操作
			for (Bullet bullet : bullets) {
				bullet.doDraw(canvas);
			}
		}
		if (blast != null) { // 爆炸特效绘制
			blast.doDraw(canvas);
		}
	}

}
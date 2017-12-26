package com.ruanyf.androidgame11;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

/**
 * Created by Feng on 2017/9/30.
 */
public class Enemy extends Sprite {

	public static final int WEAPON_NORMAL = 0; // (拓展)普通武器
	public static final int WEAPON_SHOTGUN = 1; // (拓展)散弹

	private int frameCount; // 帧数计数
	private float fireRate = 1; // 每秒发射子弹数

	private List<Bullet> bullets; // 弹夹(用于复用子弹对象,节省资源)

	private int weapon; // (拓展)武器种类

	public Enemy(Bitmap bitmap, int weapon) {
		super(bitmap);
		this.weapon = weapon;
	}

	public void setBullets(List<Bullet> bullets) {
		this.bullets = bullets;
	}

	public void setWeapon(int weapon) {
		this.weapon = weapon;
	}

	public int getWeapon() {
		return weapon;
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
	 * 逻辑操作
	 */
	public void doLogic() {
		super.doLogic();
		frameCount++;
		if (frameCount > GameView.FPS / fireRate) { // 通过FPS与每秒子弹数计算每次开火间隔帧数
			frameCount = 0;
			// 开火的处理
			if (bullets != null) {
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
		if (bullets != null) { // 子弹执行各自的逻辑操作
			for (Bullet bullet : bullets) {
				bullet.doLogic();
			}
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
	}

	/**
	 * 越界判断处理
	 * (敌机从上方进入，所以重写方法，取消上方越界判断)
	 * (后续扩展可能有从两侧进入的敌机，所以只保留下方越界判断)
	 */
	@Override
	public void outOfBounds() {
		if (getY() > 800) {
			setVisible(false);
		}
	}
}

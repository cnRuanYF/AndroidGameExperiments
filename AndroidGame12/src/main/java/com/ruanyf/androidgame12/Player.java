package com.ruanyf.androidgame12;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

/**
 * Created by Feng on 2017/9/30.
 */
public class Player extends Sprite {

	public static final int WEAPON_NORMAL = 0; // (拓展)普通武器
	public static final int WEAPON_SHOTGUN = 1; // (拓展)散弹

	private float frameCount; // 帧数计数
	private float fireRate = 3; // 每秒发射子弹数

	private List<Bullet> bullets; // 弹夹(用于复用子弹对象,节省资源)

	private int weapon; // (拓展)武器种类

	public Player(Bitmap bitmap) {
		super(bitmap);
		weapon = WEAPON_NORMAL;
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

	public int getWeapon() {
		return weapon;
	}

	/**
	 * 移动
	 */
	public void move(float distanceX, float distanceY) {
		super.move(distanceX, distanceY);
		outOfBounds(getX(), getY(), getWidth(), getHeight()); // 应该在每次移动后进行判断
	}

	/**
	 * 玩家的越界判断处理
	 * (若放在doLogic方法中,若玩家在doLogic和doDraw之间调用了move方法,依然会造成短暂越界)
	 */
	private void outOfBounds(float x, float y, float width, float height) {
		if (x < 0) {
			x = 0;
		} else if (x + width > 480) {
			x = 480 - width;
		}
		if (y < 0) { // 这里如果继续使用else,若玩家在四个边角越界,当处理完横向越界,就不会再处理纵向越界
			y = 0;
		} else if (y + height > 800) {
			y = 800 - height;
		}
		setPosition(x, y);
	}

	/**
	 * 发射普通子弹
	 */
	public void fireNormal() {
		for (Bullet bullet : bullets) {
			if (!bullet.isVisible()) { // 复用已经隐藏的子弹
				bullet.setSpeed(0, -5);
				bullet.setPosition(getX() + getWidth() / 2 - bullet.getWidth() / 2, getY());
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
						bullet.setSpeed(-1, -4.5f);
						break;
					case 2:
						bullet.setSpeed(1, -4.5f);
						break;
					case 3:
						bullet.setSpeed(0, -5);
						break;
				}
				bullet.setPosition(getX() + getWidth() / 2 - bullet.getWidth() / 2, getY());
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
		// 玩家的移动依靠玩家输入，越界判断也不一样，所以不执行父类逻辑
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

}

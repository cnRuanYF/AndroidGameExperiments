package com.ruanyf.androidgame10;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;

/**
 * Created by Feng on 2017/9/30.
 */
public class Player {

	public static final int WEAPON_NORMAL = 0; // (拓展)普通武器
	public static final int WEAPON_SHOTGUN = 1; // (拓展)散弹

	private Bitmap bitmap;
	private float x, y, width, height; // 坐标,宽高

	private int frameCount; // 帧数计数
	private float fireRate = 5; // 每秒发射子弹数

	private ArrayList<Bullet> bullets; // 弹夹(用于复用子弹对象,节省资源)

	private int weapon; // (拓展)武器种类

	public Player(Bitmap bitmap) {
		this.bitmap = bitmap;
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		weapon = WEAPON_NORMAL;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setBullets(ArrayList<Bullet> bullets) {
		this.bullets = bullets;
	}

	public void setWeapon(int weapon) {
		this.weapon = weapon;
	}

	public int getWeapon() {
		return weapon;
	}

	public void move(float distanceX, float distanceY) {
		x += distanceX;
		y += distanceY;
		// 越界处理 (若放在doLogic方法中,若玩家在doLogic和doDraw之间调用了move方法,依然会造成短暂越界)
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
	}

	/**
	 * 逻辑操作
	 */
	public void doLogic() {
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
		canvas.drawBitmap(bitmap, x, y, null);
		if (bullets != null) { // 子弹执行各自的绘制操作
			for (Bullet bullet : bullets) {
				bullet.doDraw(canvas);
			}
		}
	}

	/**
	 * 发射普通子弹
	 */
	public void fireNormal() {
		for (Bullet bullet : bullets) {
			if (!bullet.isVisible()) { // 复用已经隐藏的子弹
				bullet.setSpeed(0, -5);
				bullet.setPosition(x + width / 2 - bullet.getWidth() / 2, y);
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
						bullet.setSpeed(0, -5);
						break;
					case 3:
						bullet.setSpeed(1, -4.5f);
						break;
				}
				bullet.setPosition(x + width / 2 - bullet.getWidth() / 2, y);
				bullet.setVisible(true);
				if (fireCount == 3) {
					break;
				} // 一次累计发射3颗,结束循环
			}
		}
	}

}

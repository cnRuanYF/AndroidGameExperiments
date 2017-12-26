package com.ruanyf.androidgame36.plants;

import com.ruanyf.androidgame36.bullets.Bullet;

import org.cocos2d.actions.CCScheduler;

import java.util.ArrayList;

/**
 * Created by Feng on 2017/12/13.
 */
public abstract class ShooterPlant extends AttackPlant {

	private ArrayList<Bullet> bullets;
	private boolean isAttack;

	public ShooterPlant(String format, int number) {
		super(format, number);
		bullets = new ArrayList<Bullet>();
	}

	public ArrayList<Bullet> getBullets() {
		return bullets;
	}

	public void setBullets(ArrayList<Bullet> bullets) {
		this.bullets = bullets;
	}

	public abstract void creatBullet(float t);

	public void attackZombie() {
		if (!isAttack) {
			creatBullet(0);
			CCScheduler.sharedScheduler().schedule("creatBullet", this, 5, false);
			isAttack = true;
		}
	}

	public void stopAttackZombie() {
		if (isAttack) {
			CCScheduler.sharedScheduler().unschedule("creatBullet", this);
			isAttack = false;
		}
	}
}

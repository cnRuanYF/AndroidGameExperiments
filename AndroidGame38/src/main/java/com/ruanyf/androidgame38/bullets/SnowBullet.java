package com.ruanyf.androidgame38.bullets;

import com.ruanyf.androidgame38.Zombie;

/**
 * Created by Feng on 2017/12/13.
 * Update on 2017/12/13.
 */
public class SnowBullet extends Bullet {

	public SnowBullet() {
		super("bullet/bullet2.png");
		setAttack(10);
	}

	@Override
	public void showBlast(Zombie zombie) {
		zombie.slow();
	}
}

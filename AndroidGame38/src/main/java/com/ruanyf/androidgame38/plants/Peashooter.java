package com.ruanyf.androidgame38.plants;

import com.ruanyf.androidgame38.OnDestroyListener;
import com.ruanyf.androidgame38.bullets.PeaBullet;

/**
 * Created by Feng on 2017/11/29.
 * Update on 2017/12/14.
 */
public class Peashooter extends ShooterPlant {

	public Peashooter() {
		super("plant/Peashooter/Frame%02d.png", 13);
		setPrice(100);
	}

	@Override
	public void creatBullet(float t) {
		final PeaBullet aPeaBullet = new PeaBullet();
		aPeaBullet.setPosition(getPosition().x + 20, getPosition().y + 50);
		getParent().addChild(aPeaBullet, 6);
		getBullets().add(aPeaBullet);
		aPeaBullet.setOnDestroyListener(new OnDestroyListener() {

			@Override
			public void onDestroy() {
				aPeaBullet.removeSelf();
				getBullets().remove(aPeaBullet);
			}
		});
		aPeaBullet.move();
	}

}

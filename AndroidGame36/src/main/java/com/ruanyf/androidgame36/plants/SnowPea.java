package com.ruanyf.androidgame36.plants;

import com.ruanyf.androidgame36.OnDestroyListener;
import com.ruanyf.androidgame36.bullets.SnowBullet;

/**
 * Created by Feng on 2017/11/29.
 * Update on 2017/12/13.
 */
public class SnowPea extends ShooterPlant {

	public SnowPea() {
		super("plant/SnowPea/Frame%02d.png", 15);
	}

	@Override
	public void creatBullet(float t) {
		final SnowBullet aSnowBullet = new SnowBullet();
		aSnowBullet.setPosition(getPosition().x + 20, getPosition().y + 50);
		getParent().addChild(aSnowBullet, 6);
		getBullets().add(aSnowBullet);
		aSnowBullet.setOnDestroyListener(new OnDestroyListener() {

			@Override
			public void onDestroy() {
				aSnowBullet.removeSelf();
				getBullets().remove(aSnowBullet);
			}
		});
		aSnowBullet.move();
	}

}

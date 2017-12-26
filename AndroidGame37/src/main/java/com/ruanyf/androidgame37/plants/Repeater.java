package com.ruanyf.androidgame37.plants;

import com.ruanyf.androidgame37.OnDestroyListener;
import com.ruanyf.androidgame37.bullets.PeaBullet;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCSequence;

/**
 * Created by Feng on 2017/11/29.
 * Update on 2017/12/13.
 */
public class Repeater extends ShooterPlant {

	public Repeater() {
		super("plant/Repeater/Frame%02d.png", 15);
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
		CCDelayTime aCcDelayTime = CCDelayTime.action(0.5f);
		CCCallFunc aCcCallFunc = CCCallFunc.action(this, "creatBulletTwo");
		CCSequence aCcSequence = CCSequence.actions(aCcDelayTime, aCcCallFunc);
		aPeaBullet.runAction(aCcSequence);
	}

	public void creatBulletTwo() {
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

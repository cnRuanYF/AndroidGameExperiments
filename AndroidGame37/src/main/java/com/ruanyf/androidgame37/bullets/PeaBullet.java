package com.ruanyf.androidgame37.bullets;

import com.ruanyf.androidgame37.Zombie;

import org.cocos2d.actions.instant.CCHide;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCSprite;

/**
 * Created by Feng on 2017/12/13.
 * Update on 2017/12/13.
 */
public class PeaBullet extends Bullet {

	public PeaBullet() {
		super("bullet/bullet1.png");
	}

	@Override
	public void showBlast(Zombie zombie) {
		CCSprite bulletBlastSprite = CCSprite.sprite("bullet/bulletBlast1.png");
		bulletBlastSprite.setPosition(zombie.getPosition().x, zombie.getPosition().y + 60);
		getParent().addChild(bulletBlastSprite, 6);
		CCDelayTime aCcDelayTime = CCDelayTime.action(0.1f);
		CCHide aCcHide = CCHide.action();
		CCSequence aCcSequence = CCSequence.actions(aCcDelayTime, aCcHide);
		bulletBlastSprite.runAction(aCcSequence);
	}
}

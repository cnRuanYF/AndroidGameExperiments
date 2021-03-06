package com.ruanyf.androidgame37.plants;

import com.ruanyf.androidgame37.FightLayer;
import com.ruanyf.androidgame37.Sun;

import org.cocos2d.actions.CCScheduler;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCJumpTo;
import org.cocos2d.actions.interval.CCSequence;

/**
 * Created by Feng on 2017/11/29.
 * Update on 2017/12/13.
 */
public class SunFlower extends ProductPlant {

	private Sun aSun;

	public SunFlower() {
		super("plant/SunFlower/Frame%02d.png", 18);
		CCScheduler.sharedScheduler().schedule("creatSun", this, 10, false);
	}

	public void creatSun(float t) {
		aSun = new Sun();
		aSun.setPosition(getPosition().x - 200, getPosition().y + 40);
		getParent().getParent().addChild(aSun);
		CCJumpTo mCcJumpTo = CCJumpTo.action(0.5f, ccp(getPosition().x - 200, getPosition().y), 40, 1);
		CCCallFunc mCcCallFunc1 = CCCallFunc.action(this, "addSun");
		CCDelayTime mCcDelayTime = CCDelayTime.action(5);
		CCCallFunc mCcCallFunc2 = CCCallFunc.action(this, "destroySun");
		CCSequence mCcSequence = CCSequence.actions(mCcJumpTo, mCcCallFunc1, mCcDelayTime, mCcCallFunc2);
		aSun.runAction(mCcSequence);
	}

	public void addSun() {
		((FightLayer) (getParent().getParent())).addSun(aSun);
	}

	public void destroySun() {
		((FightLayer) (getParent().getParent())).remove(aSun);
		aSun.removeSelf();
	}
}

package com.ruanyf.androidgame37;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.util.CGPointUtil;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Feng on 2017/12/14.
 */
public class Sun extends CCSprite {

	public Sun() {
		super("sun/Frame00.png");
		ArrayList<CCSpriteFrame> mCcSpriteFrames = new ArrayList<CCSpriteFrame>();
		for (int i = 0; i < 22; i++) {
			CCSpriteFrame mCcSpriteFrame = CCSprite.sprite(String.format(Locale.CHINA,
					"sun/Frame%02d.png", i)).displayedFrame();
			mCcSpriteFrames.add(mCcSpriteFrame);
		}
		CCAnimation mCcAnimation = CCAnimation.animation("base", 0.2f, mCcSpriteFrames);
		CCAnimate mCcAnimate = CCAnimate.action(mCcAnimation);
		CCRepeatForever mCcRepeatForever = CCRepeatForever.action(mCcAnimate);
		runAction(mCcRepeatForever);
	}

	public void collect() {
		CGPoint endPoint = ccp(25, 455);
		float t = CGPointUtil.distance(getPosition(), endPoint) / 500;
		CCMoveTo mCcMoveTo = CCMoveTo.action(t, endPoint);
		CCCallFunc mCcCallFunc = CCCallFunc.action(this, "addSunNumber");
		CCSequence mCcSequence = CCSequence.actions(mCcMoveTo, mCcCallFunc);
		runAction(mCcSequence);
	}

	public void addSunNumber() {
		((FightLayer) getParent()).addSunNumber();
		removeSelf();
	}

}

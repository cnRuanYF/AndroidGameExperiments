package com.ruanyf.androidgame33;

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
 * Created by Feng on 2017/12/07.
 */
public class Zombie extends CCSprite {

	private float speed = 20;

	public Zombie(FightLayer fightLayer, CGPoint startPoint, CGPoint endPoint) {
		super("zombies/zombies_1/walk/z_1_00.png");
		setAnchorPoint(0.5f, 0);
		setPosition(startPoint);
		float t = CGPointUtil.distance(startPoint, endPoint) / speed;
		CCMoveTo mCcMoveTo = CCMoveTo.action(t, endPoint);
		CCCallFunc mCcCallFunc = CCCallFunc.action(fightLayer, "end");
		CCSequence mCcSequence = CCSequence.actions(mCcMoveTo, mCcCallFunc);
		runAction(mCcSequence);
		ArrayList<CCSpriteFrame> mCcSpriteFrames = new ArrayList<CCSpriteFrame>();
		for (int i = 0; i < 7; i++) {
			CCSpriteFrame mCcSpriteFrame = CCSprite.sprite(String.format(Locale.CHINA,
					"zombies/zombies_1/walk/z_1_%02d.png", i)).displayedFrame();
			mCcSpriteFrames.add(mCcSpriteFrame);
		}
		CCAnimation mCcAnimation = CCAnimation.animation("walk", 0.2f, mCcSpriteFrames);
		CCAnimate mCcAnimate = CCAnimate.action(mCcAnimation, true);
		CCRepeatForever mCcRepeatForever = CCRepeatForever.action(mCcAnimate);
		runAction(mCcRepeatForever);

	}

}

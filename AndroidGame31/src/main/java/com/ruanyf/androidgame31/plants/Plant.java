package com.ruanyf.androidgame31.plants;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Feng on 2017/11/29.
 */
public abstract class Plant extends CCSprite {

	public Plant(String format, int number) {
		super(String.format(Locale.CHINA, format, 0));
		setAnchorPoint(0.5f, 0);
		ArrayList<CCSpriteFrame> mCcSpriteFrames = new ArrayList<CCSpriteFrame>();
		for (int i = 0; i < number; i++) {
			CCSpriteFrame mCcSpriteFrame = CCSprite.sprite(String.format(Locale.CHINA,
					format, i)).displayedFrame();
			mCcSpriteFrames.add(mCcSpriteFrame);
		}
		CCAnimation mCcAnimation = CCAnimation.animation("base", 0.2f, mCcSpriteFrames);
		CCAnimate mCcAnimate = CCAnimate.action(mCcAnimation);
		CCRepeatForever mCcRepeatForever = CCRepeatForever.action(mCcAnimate);
		runAction(mCcRepeatForever);
	}

}

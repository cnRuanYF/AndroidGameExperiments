package com.ruanyf.androidgame35.bullets;

import com.ruanyf.androidgame35.OnDestroyListener;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.util.CGPointUtil;

/**
 * Created by Feng on 2017/12/13.
 */
public abstract class Bullet extends CCSprite {

	private int speed = 100;
	private OnDestroyListener onDestroyListener;

	public OnDestroyListener getOnDestroyListener() {
		return onDestroyListener;
	}

	public void setOnDestroyListener(OnDestroyListener onDestroyListener) {
		this.onDestroyListener = onDestroyListener;
	}

	public Bullet(String filepath) {
		super(filepath);
	}

	public void move() {
		CGSize winSize = CCDirector.sharedDirector().getWinSize();
		CGPoint endPoint = ccp(winSize.width + 240, getPosition().y);
		float t = CGPointUtil.distance(getPosition(), endPoint) / speed;
		CCMoveTo mCcMoveTo = CCMoveTo.action(t, endPoint);
		CCCallFunc mCcCallFunc = CCCallFunc.action(onDestroyListener, "onDestroy");
		CCSequence mCcSequence = CCSequence.actions(mCcMoveTo, mCcCallFunc);
		runAction(mCcSequence);
	}
}

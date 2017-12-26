package com.ruanyf.androidgame36;

import com.ruanyf.androidgame36.plants.Plant;

import org.cocos2d.actions.CCScheduler;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.base.CCSpeed;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCTintTo;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.util.CGPointUtil;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Feng on 2017/12/07.
 * Update on 2017/12/13.
 */
public class Zombie extends CCSprite {
	private float speed = 20;
	private int attack = 10;
	private int life = 100;
	private FightLayer fightLayer;
	private CGPoint endPoint;
	private Plant targetPlant;


	public Zombie(FightLayer fightLayer, CGPoint startPoint, CGPoint endPoint) {
		super("zombies/zombies_1/walk/z_1_00.png");
		setAnchorPoint(0.5f, 0);
		setPosition(startPoint);
		this.fightLayer = fightLayer;
		this.endPoint = endPoint;
		move();
	}

	private void move() {
		float t = CGPointUtil.distance(getPosition(), endPoint) / speed;
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

	public void attackPlant(Plant aPlant) {
		if (targetPlant == null) {
			targetPlant = aPlant;
			stopAllActions();
			ArrayList<CCSpriteFrame> mCcSpriteFrames = new ArrayList<CCSpriteFrame>();
			for (int i = 0; i < 10; i++) {
				CCSpriteFrame mCcSpriteFrame = CCSprite.sprite(String.format(Locale.CHINA,
						"zombies/zombies_1/attack/z_1_attack_%02d.png", i)).displayedFrame();
				mCcSpriteFrames.add(mCcSpriteFrame);
			}
			CCAnimation mCcAnimation = CCAnimation.animation("attack", 0.2f, mCcSpriteFrames);
			CCAnimate mCcAnimate = CCAnimate.action(mCcAnimation, true);
			CCRepeatForever mCcRepeatForever = CCRepeatForever.action(mCcAnimate);
			runAction(mCcRepeatForever);
			CCScheduler.sharedScheduler().schedule("attackOne", this, 1, false);
		}
	}

	public void attackOne(float t) {
		if (targetPlant != null) {
			targetPlant.attacked(attack);
			if (targetPlant.getLife() == 0) {
				targetPlant.removeSelf();
				targetPlant = null;
				CCScheduler.sharedScheduler().unschedule("attackOne", this);
				stopAllActions();
				move();
			}
		}
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public void attack(int attack) {
		life -= attack;
		if (life < 0) {
			life = 0;
		}
	}

	public void slow() {
		stopAllActions();
		if (targetPlant == null) {
			float t = CGPointUtil.distance(getPosition(), endPoint) / speed;
			CCMoveTo mCcMoveTo = CCMoveTo.action(t, endPoint);
			CCCallFunc mCcCallFunc = CCCallFunc.action(fightLayer, "end");
			CCSequence mCcSequence = CCSequence.actions(mCcMoveTo, mCcCallFunc);
			CCSpeed mCcSpeed1 = CCSpeed.action(mCcSequence, 0.2f);
			runAction(mCcSpeed1);
			ArrayList<CCSpriteFrame> mCcSpriteFrames = new ArrayList<CCSpriteFrame>();
			for (int i = 0; i < 7; i++) {
				CCSpriteFrame mCcSpriteFrame = CCSprite.sprite(String.format(Locale.CHINA, "zombies/zombies_1/walk/z_1_%02d.png", i)).displayedFrame();
				mCcSpriteFrames.add(mCcSpriteFrame);
			}
			CCAnimation mCcAnimation = CCAnimation.animation("walk", 0.2f, mCcSpriteFrames);
			CCAnimate mCcAnimate = CCAnimate.action(mCcAnimation, true);
			CCRepeatForever mCcRepeatForever = CCRepeatForever.action(mCcAnimate);
			CCSpeed mCcSpeed2 = CCSpeed.action(mCcRepeatForever, 0.2f);
			runAction(mCcSpeed2);
		} else {
			ArrayList<CCSpriteFrame> mCcSpriteFrames = new ArrayList<CCSpriteFrame>();
			for (int i = 0; i < 10; i++) {
				CCSpriteFrame mCcSpriteFrame = CCSprite.sprite(String.format(Locale.CHINA, "zombies/zombies_1/attack/z_1_attack_%02d.png", i)).displayedFrame();
				mCcSpriteFrames.add(mCcSpriteFrame);
			}
			CCAnimation mCcAnimation = CCAnimation.animation("attack", 0.2f, mCcSpriteFrames);
			CCAnimate mCcAnimate = CCAnimate.action(mCcAnimation, true);
			CCRepeatForever mCcRepeatForever = CCRepeatForever.action(mCcAnimate);
			CCSpeed mCcSpeed3 = CCSpeed.action(mCcRepeatForever, 0.2f);
			runAction(mCcSpeed3);
			attack = 2;
		}
		CCTintTo mCcTintTo1 = CCTintTo.action(0.1f, ccc3(150, 150, 255));
		CCDelayTime mCcDelayTime = CCDelayTime.action(3);
		CCTintTo mCcTintTo2 = CCTintTo.action(0.1f, ccc3(255, 255, 255));
		CCCallFunc mCcCallFunc = CCCallFunc.action(this, "normal");
		CCSequence mCcSequence = CCSequence.actions(mCcTintTo1, mCcDelayTime, mCcTintTo2, mCcCallFunc);
		runAction(mCcSequence);
	}

	public void normal() {
		stopAllActions();
		attack = 10;
		if (targetPlant == null) {
			move();
		} else {
			ArrayList<CCSpriteFrame> mCcSpriteFrames = new ArrayList<CCSpriteFrame>();
			for (int i = 0; i < 10; i++) {
				CCSpriteFrame mCcSpriteFrame = CCSprite.sprite(String.format(Locale.CHINA, "zombies/zombies_1/attack/z_1_attack_%02d.png", i)).displayedFrame();
				mCcSpriteFrames.add(mCcSpriteFrame);
			}
			CCAnimation mCcAnimation = CCAnimation.animation("attack", 0.2f, mCcSpriteFrames);
			CCAnimate mCcAnimate = CCAnimate.action(mCcAnimation, true);
			CCRepeatForever mCcRepeatForever = CCRepeatForever.action(mCcAnimate);
			runAction(mCcRepeatForever);
		}
	}
}

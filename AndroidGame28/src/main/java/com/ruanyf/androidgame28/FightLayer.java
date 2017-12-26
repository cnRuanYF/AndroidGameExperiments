package com.ruanyf.androidgame28;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCTMXObjectGroup;
import org.cocos2d.layers.CCTMXTiledMap;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.types.CGSize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Feng on 2017/11/16.
 */
class FightLayer extends CCLayer {

	private CGSize winSize;

	public FightLayer() {
		super();
		loadMap();
	}

	private void loadMap() {
		CCTMXTiledMap mCctmxTiledMap = CCTMXTiledMap.tiledMap("fight/map1.tmx");
		addChild(mCctmxTiledMap);
		CCTMXObjectGroup objectGroup_show = mCctmxTiledMap.objectGroupNamed("show");
		ArrayList<HashMap<String, String>> objects = objectGroup_show.objects;
		for (HashMap<String, String> hashMap : objects) {
			int x = Integer.parseInt(hashMap.get("x"));
			int y = Integer.parseInt(hashMap.get("y"));
			CCSprite zombieCcSprite = CCSprite.sprite("zombies/zombies_1/shake/z_1_00.png");
			zombieCcSprite.setPosition(x, y);
			mCctmxTiledMap.addChild(zombieCcSprite);
			ArrayList<CCSpriteFrame> mCcSpriteFrames = new ArrayList<CCSpriteFrame>();
			for (int i = 0; i < 2; i++) {
				CCSpriteFrame mCcSpriteFrame = CCSprite.sprite(String.format(Locale.CHINA,
						"zombies/zombies_1/shake/z_1_%02d.png", i))
						.displayedFrame();
				mCcSpriteFrames.add(mCcSpriteFrame);
			}
			CCAnimation mCcAnimation = CCAnimation.animation("show", 0.2f, mCcSpriteFrames);
			CCAnimate mCcAnimate = CCAnimate.action(mCcAnimation, true);
			CCRepeatForever mCcRepeatForever = CCRepeatForever.action(mCcAnimate);
			zombieCcSprite.runAction(mCcRepeatForever);
		}
		winSize = CCDirector.sharedDirector().getWinSize();
		CCDelayTime mCcDelayTime = CCDelayTime.action(2);
		CCMoveBy mCcMoveBy = CCMoveBy.action(2, ccp(winSize.width - mCctmxTiledMap.getContentSize().width, 0));
		CCSequence mCcSequence = CCSequence.actions(mCcDelayTime, mCcMoveBy);
		mCctmxTiledMap.runAction(mCcSequence);
	}
}

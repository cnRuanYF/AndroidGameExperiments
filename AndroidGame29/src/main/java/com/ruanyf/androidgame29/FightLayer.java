package com.ruanyf.androidgame29;

import android.view.MotionEvent;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCTMXObjectGroup;
import org.cocos2d.layers.CCTMXTiledMap;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Feng on 2017/11/16.
 * Update on 2017/11/23.
 */
public class FightLayer extends CCLayer {

	private CGSize winSize;
	private CCSprite aSeedBankSprite;
	private CCSprite aSeedChooserSprite;
	private ArrayList<PlantCard> aPlantCards;
	private ArrayList<PlantCard> aSelectPlantCards;

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
		CCMoveBy mCcMoveBy = CCMoveBy.action(2,
				ccp(winSize.width - mCctmxTiledMap.getContentSize().width, 0));
		CCCallFunc mCcCallFunc = CCCallFunc.action(this, "loadChoose");
		CCSequence mCcSequence = CCSequence.actions(mCcDelayTime,
				mCcMoveBy, mCcCallFunc);
		mCctmxTiledMap.runAction(mCcSequence);
	}

	public void loadChoose() {
		aSeedBankSprite = CCSprite.sprite("choose/SeedBank.png");
		aSeedBankSprite.setAnchorPoint(0, 1);
		aSeedBankSprite.setPosition(0, winSize.height);
		addChild(aSeedBankSprite);

		aSeedChooserSprite = CCSprite.sprite("choose/SeedChooser.png");
		aSeedChooserSprite.setAnchorPoint(0, 0);
		addChild(aSeedChooserSprite);

		CCLabel mCcLabel = CCLabel.makeLabel("50", "", 16);
		mCcLabel.setColor(ccColor3B.ccBLACK);
		mCcLabel.setPosition(30, 420);
		addChild(mCcLabel);


		aPlantCards = new ArrayList<PlantCard>();
		for (int i = 0; i < 8; i++) {
			PlantCard aPlantCard = new PlantCard(i);
			aPlantCards.add(aPlantCard);
			aPlantCard.getLightCardSprite().setPosition(50 + 55 * (i % 6),
					330 - 80 * (i / 6));
			aSeedChooserSprite.addChild(aPlantCard.getLightCardSprite());
			aPlantCard.getDarkCardSprite().setPosition(50 + 55 * (i % 6),
					330 - 80 * (i / 6));
			aSeedChooserSprite.addChild(aPlantCard.getDarkCardSprite());

		}
		aSelectPlantCards = new ArrayList<PlantCard>();
		setIsTouchEnabled(true);
	}

	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		CGPoint aCgPoint = convertTouchToNodeSpace(event);
		if (CGRect.containsPoint(aSeedChooserSprite.getBoundingBox(), aCgPoint)) {
			if (aSelectPlantCards.size() < 5) {
				for (PlantCard aPlantCard : aPlantCards) {
					if (CGRect.containsPoint(aPlantCard.getLightCardSprite()
							.getBoundingBox(), aCgPoint)) {
						if (!aSelectPlantCards.contains(aPlantCard)) {
							aSelectPlantCards.add(aPlantCard);
							CCMoveTo mCcMoveTo = CCMoveTo.action(0.1f,
									ccp(45 + 55 * aSelectPlantCards.size(), 450));
							aPlantCard.getLightCardSprite().runAction(mCcMoveTo);
						}
					}
				}
			}
		}
		if (CGRect.containsPoint(aSeedBankSprite.getBoundingBox(), aCgPoint)) {
			boolean isRemove = false;
			for (int i = 0; i < aSelectPlantCards.size(); i++) {
				PlantCard aPlantCard = aSelectPlantCards.get(i);
				if (CGRect.containsPoint(aPlantCard.getLightCardSprite()
						.getBoundingBox(), aCgPoint)) {
					CCMoveTo mCcMoveTo = CCMoveTo.action(0.1f,
							aPlantCard.getDarkCardSprite().getPosition());
					aPlantCard.getLightCardSprite().runAction(mCcMoveTo);
					aSelectPlantCards.remove(aPlantCard);
					isRemove = true;
					break;
				}

			}
			if (isRemove) {
				for (int i = 0; i < aSelectPlantCards.size(); i++) {
					PlantCard aPlantCard = aSelectPlantCards.get(i);
					CCMoveTo mCcMoveTo = CCMoveTo.action(0.1f, ccp(100 + 55 * i, 450));
					aPlantCard.getLightCardSprite().runAction(mCcMoveTo);
				}
			}
		}
		return super.ccTouchesBegan(event);
	}
}

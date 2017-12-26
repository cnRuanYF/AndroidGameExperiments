package com.ruanyf.androidgame35;

import android.view.MotionEvent;

import com.ruanyf.androidgame35.plants.*;

import org.cocos2d.actions.CCScheduler;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.layers.CCTMXObjectGroup;
import org.cocos2d.layers.CCTMXTiledMap;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.transitions.CCFlipXTransition;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Feng on 2017/11/16.
 * Update on 2017/12/07.
 */
public class FightLayer extends CCLayer {

	private CGSize winSize;
	private CCSprite aSeedBankSprite;
	private CCSprite aSeedChooserSprite;
	private ArrayList<PlantCard> aPlantCards;
	private ArrayList<PlantCard> aSelectPlantCards;
	private CCTMXTiledMap mCctmxTiledMap;
	private CCSprite aSeedChooser_Button_DisabledSprite;
	private CCSprite aSeedChooser_ButtonSprite;
	private CCSprite startReadySprite;
	private boolean isStart;
	private CCSprite selectCardSprite;
	private Plant selectPlant;
	private ArrayList<FightLine> aFightLines;
	private ArrayList<ArrayList<CGPoint>> towerPoints;
	private ArrayList<CGPoint> path;
	private Random aRandom;

	public FightLayer() {
		super();
		loadMap();
	}

	private void loadMap() {
		mCctmxTiledMap = CCTMXTiledMap.tiledMap("fight/map1.tmx");
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
				CCSpriteFrame mCcSpriteFrame = CCSprite
						.sprite(String.format(Locale.CHINA, "zombies/zombies_1/shake/z_1_%02d.png", i))
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
		CCCallFunc mCcCallFunc = CCCallFunc.action(this, "loadChoose");
		CCSequence mCcSequence = CCSequence.actions(mCcDelayTime, mCcMoveBy, mCcCallFunc);
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

		aSeedChooser_Button_DisabledSprite = CCSprite.sprite("choose/SeedChooser_Button_Disabled.png");
		aSeedChooser_Button_DisabledSprite.setPosition(aSeedChooserSprite.getContentSize().width / 2, 50);
		aSeedChooserSprite.addChild(aSeedChooser_Button_DisabledSprite);

		aSeedChooser_ButtonSprite = CCSprite.sprite("choose/SeedChooser_Button.png");
		aSeedChooser_ButtonSprite.setPosition(aSeedChooserSprite.getContentSize().width / 2, 50);
		aSeedChooserSprite.addChild(aSeedChooser_ButtonSprite);
		aSeedChooser_ButtonSprite.setVisible(false);

		aPlantCards = new ArrayList<PlantCard>();
		for (int i = 0; i < 8; i++) {
			PlantCard aPlantCard = new PlantCard(i);
			aPlantCards.add(aPlantCard);
			aPlantCard.getLightCardSprite().setPosition(50 + 55 * (i % 6), 330 - 80 * (i / 6));
			aSeedChooserSprite.addChild(aPlantCard.getLightCardSprite());
			aPlantCard.getDarkCardSprite().setPosition(50 + 55 * (i % 6), 330 - 80 * (i / 6));
			aSeedChooserSprite.addChild(aPlantCard.getDarkCardSprite());

		}
		aSelectPlantCards = new ArrayList<PlantCard>();
		setIsTouchEnabled(true);
	}

	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		CGPoint aCgPoint = convertTouchToNodeSpace(event);
		if (isStart) {
			if (CGRect.containsPoint(aSeedBankSprite.getBoundingBox(), aCgPoint)) {
				if (selectCardSprite != null) {
					selectCardSprite.setOpacity(255);
					selectCardSprite = null;
				}
				for (PlantCard aPlantCard : aSelectPlantCards) {
					if (CGRect.containsPoint(aPlantCard.getLightCardSprite().getBoundingBox(), aCgPoint)) {
						selectCardSprite = aPlantCard.getLightCardSprite();
						selectCardSprite.setOpacity(100);
						switch (aPlantCard.getId()) {
							case 0:
								selectPlant = new Peashooter();
								break;
							case 1:
								selectPlant = new SunFlower();
								break;
							case 2:
								selectPlant = new CherryBomb();
								break;
							case 3:
								selectPlant = new WallNut();
								break;
							case 4:
								selectPlant = new PotatoMine();
								break;
							case 5:
								selectPlant = new SnowPea();
								break;
							case 6:
								selectPlant = new Chomper();
								break;
							case 7:
								selectPlant = new Repeater();
								break;
							default:
								break;
						}
					}
				}
			} else if (selectPlant != null && selectCardSprite != null) {
				int row = ((int) aCgPoint.y - 20) / 80;
				int col = ((int) aCgPoint.x - 40) / 80;
				if (row >= 0 && row < 5 && col >= 0 && col < 9) {
					FightLine aFightLine = aFightLines.get(row);
					if (!aFightLine.isContainPlant(col)) {
						selectPlant.setPosition(towerPoints.get(row).get(col));
						mCctmxTiledMap.addChild(selectPlant);
						aFightLine.addPlant(col, selectPlant);
					}
				}
//				selectPlant.setPosition(aCgPoint);
//				addChild(selectPlant);
				selectPlant = null;
				selectCardSprite.setOpacity(255);
				selectCardSprite = null;
			}
		} else {
			if (CGRect.containsPoint(aSeedChooserSprite.getBoundingBox(), aCgPoint)) {
				if (aSelectPlantCards.size() < 5) {
					for (PlantCard aPlantCard : aPlantCards) {
						if (CGRect.containsPoint(aPlantCard.getLightCardSprite().getBoundingBox(), aCgPoint)) {
							if (!aSelectPlantCards.contains(aPlantCard)) {
								aSelectPlantCards.add(aPlantCard);
								if (aSelectPlantCards.size() == 5) {
									aSeedChooser_ButtonSprite.setVisible(true);
								}
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
					if (CGRect.containsPoint(aPlantCard.getLightCardSprite().getBoundingBox(), aCgPoint)) {
						CCMoveTo mCcMoveTo = CCMoveTo.action(0.1f, aPlantCard.getDarkCardSprite().getPosition());
						aPlantCard.getLightCardSprite().runAction(mCcMoveTo);
						aSelectPlantCards.remove(aPlantCard);
						aSeedChooser_ButtonSprite.setVisible(false);
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
			if (aSeedChooser_ButtonSprite.getVisible()) {
				if (CGRect.containsPoint(aSeedChooser_ButtonSprite.getBoundingBox(), aCgPoint)) {
					for (PlantCard aPlantCard : aSelectPlantCards) {
						addChild(aPlantCard.getLightCardSprite());
					}
					aSeedChooserSprite.removeSelf();

					CCMoveBy mCcMoveBy = CCMoveBy.action(2,
							ccp(mCctmxTiledMap.getContentSize().width - winSize.width - 200, 0));
					CCCallFunc mCcCallFunc = CCCallFunc.action(this, "startReady");
					CCSequence mCcSequence = CCSequence.actions(mCcMoveBy, mCcCallFunc);
					mCctmxTiledMap.runAction(mCcSequence);
				}
			}
		}
		return super.ccTouchesBegan(event);
	}

	public void startReady() {
		setIsTouchEnabled(false);
		startReadySprite = CCSprite.sprite("startready/startReady_00.png");
		startReadySprite.setPosition(winSize.width / 2, winSize.height / 2);
		addChild(startReadySprite);
		ArrayList<CCSpriteFrame> mCcSpriteFrames = new ArrayList<CCSpriteFrame>();
		for (int i = 0; i < 3; i++) {
			CCSpriteFrame mCcSpriteFrame = CCSprite
					.sprite(String.format(Locale.CHINA, "startready/startReady_%02d.png", i)).displayedFrame();
			mCcSpriteFrames.add(mCcSpriteFrame);
		}
		CCAnimation mCcAnimation = CCAnimation.animation("startReady", 0.2f, mCcSpriteFrames);
		CCAnimate mCcAnimate = CCAnimate.action(mCcAnimation, false);
		CCCallFunc mCcCallFunc = CCCallFunc.action(this, "start");
		CCSequence mCcSequence = CCSequence.actions(mCcAnimate, mCcCallFunc);
		startReadySprite.runAction(mCcSequence);
	}

	public void start() {
		startReadySprite.removeSelf();
		isStart = true;
		setIsTouchEnabled(true);
		towerPoints = new ArrayList<ArrayList<CGPoint>>();
		for (int i = 0; i < 5; i++) {
			ArrayList<CGPoint> aPoints = new ArrayList<CGPoint>();
			CCTMXObjectGroup objectGroup_tower = mCctmxTiledMap.objectGroupNamed("tower" + i);
			ArrayList<HashMap<String, String>> objects = objectGroup_tower.objects;
			for (HashMap<String, String> hashMap : objects) {
				int x = Integer.parseInt(hashMap.get("x"));
				int y = Integer.parseInt(hashMap.get("y"));
				aPoints.add(ccp(x, y));
			}
			towerPoints.add(aPoints);
		}
		aFightLines = new ArrayList<FightLine>();
		for (int i = 0; i < 5; i++) {
			aFightLines.add(new FightLine());
		}
		path = new ArrayList<CGPoint>();
		CCTMXObjectGroup objectGroup_path = mCctmxTiledMap.objectGroupNamed("path");
		ArrayList<HashMap<String, String>> objects = objectGroup_path.objects;
		for (HashMap<String, String> hashMap : objects) {
			int x = Integer.parseInt(hashMap.get("x"));
			int y = Integer.parseInt(hashMap.get("y"));
			path.add(ccp(x, y));
		}
		aRandom = new Random();
		CCScheduler.sharedScheduler().schedule("addZombie", this, 10, false);

	}

	public void addZombie(float t) {
		int i = aRandom.nextInt(5);
		Zombie aZombie = new Zombie(this, path.get(2 * i), path.get(2 * i + 1));
		mCctmxTiledMap.addChild(aZombie, 5 - i);
		aFightLines.get(i).addZombie(aZombie);
	}

	public void end() {
		setIsTouchEnabled(false);
		for (CCNode ccNode : mCctmxTiledMap.getChildren()) {
			ccNode.stopAllActions();
			ccNode.unscheduleAllSelectors();
		}
		CCSprite aZombiesWonSprite = CCSprite.sprite("zombieswon/ZombiesWon.png");
		aZombiesWonSprite.setPosition(winSize.width / 2, winSize.height / 2);
		addChild(aZombiesWonSprite);
		CCScheduler.sharedScheduler().unschedule("addZombie", this);
		CCDelayTime mCcDelayTime = CCDelayTime.action(2);
		CCCallFunc mCcCallFunc = CCCallFunc.action(this, "restart");
		CCSequence mCcSequence = CCSequence.actions(mCcDelayTime, mCcCallFunc);
		aZombiesWonSprite.runAction(mCcSequence);
	}

	public void restart() {
		CCScene mCcScene = CCScene.node();
		mCcScene.addChild(new MenuLayer());
		CCFlipXTransition mCcFlipXTransition = CCFlipXTransition.transition(2, mCcScene, 1);
		CCDirector.sharedDirector().replaceScene(mCcFlipXTransition);
	}
}

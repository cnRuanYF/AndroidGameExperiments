package com.ruanyf.androidgame39;

import android.view.MotionEvent;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.transitions.CCSlideInRTransition;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

/**
 * 菜单图层
 * Created by Feng on 2017/11/16.
 * Update on 2017/12/21.
 */
public class MenuLayer extends CCLayer {

	private int menuOffsetY;

	public MenuLayer() {
		super();

		// 屏幕适配
		CGSize winSize = CCDirector.sharedDirector().getWinSize();
		menuOffsetY = ((int) winSize.height - 800) / 2;

		// 菜单背景
		CCSprite menuSpr = CCSprite.sprite("menu.png");
		menuSpr.setPosition(winSize.width / 2, winSize.height / 2);
		addChild(menuSpr);

		setIsTouchEnabled(true);
	}

	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		CGPoint touchPoint = convertTouchToNodeSpace(event);

		// 人人对战按钮
		CGRect h2hButtonRect = CGRect.make(160, 145 + menuOffsetY, 175, 45);
		if (CGRect.containsPoint(h2hButtonRect, touchPoint)) {
			CCScene ccScene = CCScene.node();
			ccScene.addChild(new ChessLayer());
			CCSlideInRTransition ccSlideInRTransition = CCSlideInRTransition.transition(2, ccScene);
			CCDirector.sharedDirector().replaceScene(ccSlideInRTransition);
		}

		return super.ccTouchesBegan(event);
	}
}

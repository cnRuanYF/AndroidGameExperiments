package com.ruanyf.androidgame35;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.MotionEvent;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.instant.CCHide;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.transitions.CCJumpZoomTransition;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Feng on 2017/11/16.
 */
public class LogoLayer extends CCLayer {
	public LogoLayer() {
		logo1();
	}

	private void logo1() {
		CCSprite logoSprite1 = CCSprite.sprite("logo/logo1.png");
		logoSprite1.setAnchorPoint(0, 0); // Sprite默认锚点为中心点
		addChild(logoSprite1);
		CCDelayTime mCcDelayTime = CCDelayTime.action(2);
		CCHide mCcHide = CCHide.action();
		CCCallFunc mCcCallFunc = CCCallFunc.action(this, "logo2");
		CCSequence mCcSequence = CCSequence.actions(mCcDelayTime, mCcHide, mCcCallFunc);
		logoSprite1.runAction(mCcSequence);
	}

	public void logo2() {
		CCSprite logoSprite2 = CCSprite.sprite("logo/logo2.png");
		CGSize WinSize = CCDirector.sharedDirector().getWinSize();
		logoSprite2.setPosition(WinSize.getWidth() / 2, WinSize.getHeight() / 2);
		addChild(logoSprite2);
		CCDelayTime mCcDelayTime = CCDelayTime.action(2);
		CCHide mCcHide = CCHide.action();
		CCCallFunc mCcCallFunc = CCCallFunc.action(this, "cg");
		CCSequence mCcSequence = CCSequence.actions(mCcDelayTime, mCcHide, mCcCallFunc);
		logoSprite2.runAction(mCcSequence);
	}

	public void cg() {
		CCSprite cgSprite = CCSprite.sprite("cg/cg00.png");
		cgSprite.setAnchorPoint(0, 0);
		addChild(cgSprite);
		ArrayList<CCSpriteFrame> mCcSpriteFrames = new ArrayList<CCSpriteFrame>();
		for (int i = 0; i < 19; i++) {
			CCSpriteFrame mCcSpriteFrame = CCSprite.sprite(String.format(Locale.CHINA, "cg/cg%02d.png", i))
					.displayedFrame();
			mCcSpriteFrames.add(mCcSpriteFrame);
		}
		CCAnimation mCcAnimation = CCAnimation.animation("cg", 0.2f, mCcSpriteFrames);
		CCAnimate mCcAnimate = CCAnimate.action(mCcAnimation, false);
		cgSprite.runAction(mCcAnimate);
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				SystemClock.sleep(4000);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				setIsTouchEnabled(true);
				super.onPostExecute(result);
			}
		}.execute();
	}

	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		CGPoint aCgPoint = convertTouchToNodeSpace(event);
		CGRect aCgRect = CGRect.make(245, 15, 305, 40);//(x,y,w,h)
		if (CGRect.containsPoint(aCgRect, aCgPoint)) {
			CCScene mCcScene = CCScene.node();
			mCcScene.addChild(new MenuLayer());
			CCJumpZoomTransition mCcJumpZoomTransition =
					CCJumpZoomTransition.transition(2, mCcScene);
			CCDirector.sharedDirector().runWithScene(mCcJumpZoomTransition);
		}
		return super.ccTouchesBegan(event);

	}
}

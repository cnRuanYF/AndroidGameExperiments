package com.ruanyf.androidgame36;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.transitions.CCFadeTransition;

/**
 * Created by Feng on 2017/11/16.
 */
public class MenuLayer extends CCLayer {
	public MenuLayer() {
		super();
		CCSprite main_menu_bgSprite = CCSprite.sprite("menu/main_menu_bg.png");
		main_menu_bgSprite.setAnchorPoint(0, 0);
		addChild(main_menu_bgSprite);
		
		CCMenu mCcMenu = CCMenu.menu();
		CCSprite start_adventure_defaultSprite = CCSprite.sprite("menu/start_adventure_default.png");
		CCSprite start_adventure_pressSprite = CCSprite.sprite("menu/start_adventure_press.png");
		CCMenuItemSprite mCcMenuItemSprite = CCMenuItemSprite.item(start_adventure_defaultSprite,
				start_adventure_pressSprite,this,"start");
		mCcMenuItemSprite.setPosition(170,100);
		mCcMenu.addChild(mCcMenuItemSprite);
		addChild(mCcMenu);
	}
	public void start(Object item){
		CCScene mCcScene =CCScene.node();
		mCcScene.addChild(new FightLayer());
		CCFadeTransition mCcFadeTransition = CCFadeTransition.transition(2,mCcScene);
		CCDirector.sharedDirector().runWithScene(mCcFadeTransition);
	}

}

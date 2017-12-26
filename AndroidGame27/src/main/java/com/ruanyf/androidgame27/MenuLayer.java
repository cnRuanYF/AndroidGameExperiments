package com.ruanyf.androidgame27;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCSprite;

/**
 * Created by Feng on 2017/11/16.
 */
public class MenuLayer extends CCLayer {
	public MenuLayer() {
		super();
		CCSprite main_menu_bgSprite = CCSprite.sprite("menu/main_menu_bg.png");
		main_menu_bgSprite.setAnchorPoint(0, 0);
		addChild(main_menu_bgSprite);
	}
}

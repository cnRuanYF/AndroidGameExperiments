package com.ruanyf.androidgame33;

import org.cocos2d.nodes.CCSprite;

import java.util.Locale;

/**
 * Created by Feng on 2017/11/23.
 */
public class PlantCard {
	private int id;
	private CCSprite lightCardSprite;
	private CCSprite darkCardSprite;

	public PlantCard(int id) {
		super();
		this.id = id;
		lightCardSprite = CCSprite.sprite(String.format(Locale.CHINA, "choose/p%02d.png", id));
		darkCardSprite = CCSprite.sprite(String.format(Locale.CHINA, "choose/p%02d.png", id));
		darkCardSprite.setOpacity(100);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CCSprite getLightCardSprite() {
		return lightCardSprite;
	}

	public void setLightCardSprite(CCSprite lightCardSprite) {
		this.lightCardSprite = lightCardSprite;
	}

	public CCSprite getDarkCardSprite() {
		return darkCardSprite;
	}

	public void setDarkCardSprite(CCSprite darkCardSprite) {
		this.darkCardSprite = darkCardSprite;
	}

}

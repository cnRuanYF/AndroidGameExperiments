package com.ruanyf.androidgame26;

import android.graphics.Bitmap;

/**
 * Created by Feng on 2017/11/15.
 */
public class Ball extends Sprite {

	public Ball(Bitmap bitmap) {
		super(bitmap);
	}

	@Override
	public void outOfBounds() {
		if (getX() < 0) {
			setX(0);
		} else if (getX() > GameView.designScreenWidth - getWidth()) {
			setX(GameView.designScreenWidth - getWidth());
		}
		if (getY() < 0) {
			setY(0);
		} else if (getY() > GameView.designScreenHeight - getHeight()) {
			setY(GameView.designScreenHeight - getHeight());
		}
	}
}

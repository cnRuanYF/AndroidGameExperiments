package com.ruanyf.androidgame25;


import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

/**
 * Created by Feng on 2017/11/9.
 */

public class Player extends Sprite {

	private boolean isMirror, isRun, isJump, isDead;
	private long frameCount;

	/**
	 * 使用多张(单帧)图片构建Sprite
	 */
	public Player(List<Bitmap> bitmapList, float width, float height) {
		super(bitmapList, width, height);
	}

	public boolean isMirror() {
		return isMirror;
	}

	public void setMirror(boolean mirror) {
		isMirror = mirror;
	}

	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean run) {
		isRun = run;
	}

	public boolean isJump() {
		return isJump;
	}

	public void setJump(boolean jump) {
		isJump = jump;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean dead) {
		isDead = dead;
	}

	@Override
	public void nextFrame() {
		setFrameSequanceIndex((getFrameSequanceIndex() + 1) % 2);
	}

	@Override
	public void doLogic() {
		frameCount++;
		if (isDead) {
			setFrameSequanceIndex(3);
		} else if (isJump) {
			setFrameSequanceIndex(2);
		} else if (isRun) {
			if (frameCount % (GameView.FPS / 10) == 0) {
				nextFrame();
			}
		} else {
			setFrameSequanceIndex(0);
		}
	}

	@Override
	public void doDraw(Canvas canvas) {
		// 判断是否需要翻转绘制图片
		if (isMirror) {
			canvas.save();
			canvas.scale(-1, 1, getX() + getWidth() / 2, 0); // 通过水平翻转画布
			super.doDraw(canvas);
			canvas.restore();
		} else {
			super.doDraw(canvas);
		}
	}

	@Override
	public void outOfBounds() {
		if (getX() < 0) {
			setX(0);
		} else if (getX() > ScreenUtil.INSTANCE.getScreenWidth() - getWidth()) {
			setX(ScreenUtil.INSTANCE.getScreenWidth() - getWidth());
		}
//		if (getY() < 0) {
//			setY(0);
		/* 为了实现掉下悬崖，此处判定不再需要
		} else if (getY() > ScreenUtil.INSTANCE.getScreenHeight() - getHeight()) {
			setY(ScreenUtil.INSTANCE.getScreenHeight() - getHeight());
		*/
//		}
	}

	public boolean siteCollisionWith(TiledLayer tiledLayer, CollisionSite collisionSite) {
		// 碰撞点坐标
		float siteX = 0, siteY = 0;
		// 确定碰撞点X坐标
		switch (collisionSite) {
			case LEFT_TOP:
			case LEFT_CENTER:
			case LEFT_BOTTOM:
				siteX = getX();
				break;
			case RIGHT_TOP:
			case RIGHT_CENTER:
			case RIGHT_BOTTOM:
				siteX = getX() + getWidth();
				break;
			case TOP_LEFT:
			case BOTTOM_LEFT:
				siteX = getX() + getWidth() * 1 / 4;
				break;
			case TOP_CENTER:
			case BOTTOM_CENTER:
				siteX = getX() + getWidth() * 2 / 4;
				break;
			case TOP_RIGHT:
			case BOTTOM_RIGHT:
				siteX = getX() + getWidth() * 3 / 4;
				break;
		}
		// 确定碰撞点Y坐标
		switch (collisionSite) {
			case LEFT_TOP:
			case RIGHT_TOP:
				siteY = getY() + getHeight() * 1 / 4;
				break;
			case LEFT_CENTER:
			case RIGHT_CENTER:
				siteY = getY() + getHeight() * 2 / 4;
				break;
			case LEFT_BOTTOM:
			case RIGHT_BOTTOM:
				siteY = getY() + getHeight() * 3 / 4;
				break;
			case TOP_LEFT:
			case TOP_CENTER:
			case TOP_RIGHT:
				siteY = getY();
				break;
			case BOTTOM_LEFT:
			case BOTTOM_CENTER:
			case BOTTOM_RIGHT:
				siteY = getY() + getHeight();
				break;
		}
		// 屏幕坐标 - 地图偏移 = 相对于地图的坐标
		float siteMapX = siteX - tiledLayer.getX();
		float siteMapY = siteY - tiledLayer.getY();
		// 相对于地图的坐标 / 图块大小 = 第几个图块
		int siteCol = (int) (siteMapX / tiledLayer.getWidth());
		int siteRow = (int) (siteMapY / tiledLayer.getHeight());
		// 是否超出地图范围
		if (siteCol > tiledLayer.getTiledCols() - 1 || siteRow > tiledLayer.getTiledRows() - 1
				|| siteCol < 0 || siteRow < 0) {
//			return true;
			return false;
		}
		// 是否与实体砖块碰撞
		if (tiledLayer.getTiledCellIndex(siteRow, siteCol) != 0) {
			return true;
		}
		return false;
	}
}

package com.ruanyf.androidgame20;

import android.graphics.Bitmap;

/**
 * Created by Feng on 2017/10/18.
 * Update on 2017/10/19
 */
public class Player extends Sprite {

	private int[] leftFrameSequance, rightFrameSequance, upFrameSequance, downFrameSequance; // 四方形走帧序列
	private Dir dir; // 当前方向

	Player(Bitmap bitmap, int width, int height) {
		super(bitmap, width, height);
	}

	public int[] getLeftFrameSequance() {
		return leftFrameSequance;
	}

	public void setLeftFrameSequance(int[] leftFrameSequance) {
		this.leftFrameSequance = leftFrameSequance;
	}

	public int[] getRightFrameSequance() {
		return rightFrameSequance;
	}

	public void setRightFrameSequance(int[] rightFrameSequance) {
		this.rightFrameSequance = rightFrameSequance;
	}

	public int[] getUpFrameSequance() {
		return upFrameSequance;
	}

	public void setUpFrameSequance(int[] upFrameSequance) {
		this.upFrameSequance = upFrameSequance;
	}

	public int[] getDownFrameSequance() {
		return downFrameSequance;
	}

	public void setDownFrameSequance(int[] downFrameSequance) {
		this.downFrameSequance = downFrameSequance;
	}

	public Dir getDir() {
		return dir;
	}

	public void setDir(Dir dir) {
		this.dir = dir;
	}

	/**
	 * 移动(重写,移动后立即处理越界)
	 */
	@Override
	public void move(float distanceX, float distanceY) {
		super.move(distanceX, distanceY);
		playerOutOfBounds(getX(), getY(), getWidth(), getHeight()); // 越界处理须在改变位置后立即判断
	}

	/**
	 * 玩家的越界判断处理
	 */
	private void playerOutOfBounds(float x, float y, float width, float height) {
		if (x < 0) {
			x = 0;
		} else if (x + width > GameView.designScreenWidth) {
			x = GameView.designScreenWidth - width;
		}
		if (y < 0) { // 这里如果继续使用else,若玩家在四个边角越界,当处理完横向越界,就不会再处理纵向越界
			y = 0;
		} else if (y + height > GameView.designScreenHeight) {
			y = GameView.designScreenHeight - height;
		}
		setPosition(x, y);
	}

	/**
	 * 玩家的脚与地图不可行走区域的碰撞判定(通过将要移动的距离预判)
	 */
	public boolean footCollisionWith(GameMap map, float distanceX, float distanceY) {
		float footX = getX() + getWidth() / 2 + distanceX;
		float footY = getY() + getHeight() - 20 + distanceY;
		float footMapX = footX - map.getX();
		float footMapY = footY - map.getY();
		int footTiledCol = (int) (footMapX / map.getWidth());
		int footTiledRow = (int) (footMapY / map.getHeight());
		// 行走的越界检测
		if (footTiledCol > map.getTiledCols() - 1
				|| footTiledRow > map.getTiledRows() - 1) {
			return true;
		}
		if (map.getTiledCell()[footTiledRow][footTiledCol] != 1) {
			return true;
		}
		return false;
	}

}

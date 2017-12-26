package com.ruanyf.androidgame17;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Feng on 2017/10/19.
 */
public class NPC extends Sprite {

	private int[] frameSequance1, frameSequance2;
	private int state;
	private int speedX, speedY;
	private int delay, count;
	private boolean isMove;

	private List<Talk> talks = new ArrayList<>(); // NPC对话

	NPC(Bitmap bitmap, int width, int height) {
		super(bitmap, width, height);
	}

	/**
	 * 设置NPC的移动方式
	 *
	 * @param frameSequance1 正向移动的帧序列
	 * @param frameSequance2 反向移动的帧序列
	 * @param speedX         X轴移动速度
	 * @param speedY         Y轴移动速度
	 */
	public void setMove(int[] frameSequance1, int[] frameSequance2, int speedX, int speedY) {
		this.frameSequance1 = frameSequance1;
		this.frameSequance2 = frameSequance2;
		this.speedX = speedX;
		this.speedY = speedY;
		this.isMove = true;
	}

	public List<Talk> getTalks() {
		return talks;
	}

	public void setTalks(List<Talk> talks) {
		this.talks = talks;
	}

	/**
	 * NPC碰撞检测(以脚为准)
	 */
	@Override
	public boolean collisionWith(Sprite anotherSpr) {
		if (getX() + getWidth() < anotherSpr.getX()                   // 排除anotherSpr在右侧的情形
				|| anotherSpr.getX() + anotherSpr.getWidth() < getX() // ..anotherSpr在左侧..
				|| getY() + getHeight() < anotherSpr.getY() + anotherSpr.getHeight() / 2            // ..anotherSpr在下方..
				|| anotherSpr.getY() + anotherSpr.getHeight() < getY() + anotherSpr.getHeight() / 2 // ..anotherSpr在上方..
				) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * NPC的逻辑处理
	 */
	public void doLogic() {
		if (delay++ % 5 == 0) {
			if (isMove) {
				if (count++ % 40 < 20) {
					if (state != 1) {
						setFrameSequance(frameSequance1);
						state = 1;
					} else {
						move(speedX, speedY);
						nextFrame();
					}
				} else {
					if (state != 2) {
						setFrameSequance(frameSequance2);
						state = 2;
					} else {
						move(-speedX, -speedY);
						nextFrame();
					}
				}
			} else {
				nextFrame();
			}
		}
	}
}

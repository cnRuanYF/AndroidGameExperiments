package com.ruanyf.androidgame16;

import android.graphics.Bitmap;

/**
 * Created by Feng on 2017/10/19.
 */
public class NPC extends Sprite {

	private int[] frameSequance1, frameSequance2;
	private int state;
	private int speedX, speedY;
	private int delay, count;
	private boolean isMove;

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

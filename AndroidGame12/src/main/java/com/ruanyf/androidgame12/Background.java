package com.ruanyf.androidgame12;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Feng on 2017/9/30.
 */
public class Background {

	private Bitmap bitmap;
	private float y1, y2;

	public Background(Bitmap bitmap) {
		this.bitmap = bitmap;
		y1 = 0 - bitmap.getHeight();
		y2 = 0;
	}

	/**
	 * 逻辑操作
	 */
	public void doLogic() {
//		y1 += 2; // 默认速度是以60FPS为准的每帧移动距离
//		y2 += 2;
		y1 += 2 * 60 / GameView.FPS; // 根据实际FPS自适应
		y2 += 2 * 60 / GameView.FPS;
		if (y1 >= 0) {
			y1 = 0 - bitmap.getHeight();
			y2 = 0;
		}
	}

	/**
	 * 绘制操作
	 */
	public void doDraw(Canvas canvas) {
		canvas.drawBitmap(bitmap, 0, y1, null);
		canvas.drawBitmap(bitmap, 0, y2, null);
	}
}

package com.ruanyf.androidgame11;

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
		y1 += 2;
		y2 += 2;
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

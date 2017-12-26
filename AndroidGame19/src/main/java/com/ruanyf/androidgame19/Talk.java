package com.ruanyf.androidgame19;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

/**
 * Created by Feng on 2017/10/26.
 */

public class Talk {

	private String talkName, talkText; // 对话角色名,对话内容
	private Paint paint;
	private RectF windowRect;
	private Shader windowShader, windowBorderShader, splitShader, nameShader, textShader;

	public Talk(String talkName, String talkText) {
		this.talkName = talkName;
		this.talkText = talkText;
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(2);
		windowRect = new RectF(100, 300, 700, 450);
		windowShader = new LinearGradient(0, 300, 0, 450, Color.argb(128, 0, 0, 128), Color.argb(192, 0, 0, 64), Shader.TileMode.REPEAT);
		windowBorderShader = new LinearGradient(0, 300, 0, 450, Color.argb(255, 128, 255, 255), Color.argb(255, 0, 192, 192), Shader.TileMode.CLAMP);
		splitShader = new LinearGradient(100, 0, 700, 0, Color.argb(255, 0, 255, 255), Color.argb(0, 0, 255, 255), Shader.TileMode.REPEAT);
		nameShader = new LinearGradient(0, 310, 0, 340, Color.argb(255, 255, 255, 0), Color.argb(255, 192, 128, 0), Shader.TileMode.REPEAT);
		textShader = new LinearGradient(0, 355, 0, 385, Color.argb(255, 255, 255, 255), Color.argb(255, 192, 192, 192), Shader.TileMode.REPEAT);
	}

	public String getTalkName() {
		return talkName;
	}

	public void setTalkName(String talkName) {
		this.talkName = talkName;
	}

	public String getTalkText() {
		return talkText;
	}

	public void setTalkText(String talkText) {
		this.talkText = talkText;
	}

	public void doDraw(Canvas canvas) {
		// 窗口
		paint.setShader(windowShader);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRoundRect(windowRect, 20, 20, paint);
		paint.setShader(windowBorderShader);
//		paint.setShader(null);
//		paint.setColor(Color.CYAN);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRoundRect(windowRect, 20, 20, paint);
		// 分割线
		paint.setShader(splitShader);
		canvas.drawLine(110, 350, 690, 350, paint);
		// 角色名
		paint.setStyle(Paint.Style.FILL);
		paint.setTextSize(24);
		paint.setShader(nameShader);
		canvas.drawText(talkName, 120, 335, paint);
		// 对话内容
		paint.setTextSize(20);
		paint.setShader(textShader);
		for (int i = 0; i <= talkText.length() / 28; i++) { // 每行显示28个字
			canvas.drawText(talkText, 28 * i, Math.min(28 * (i + 1), talkText.length()), // 截取每行第1个字~可显示长度或结尾(已更短的为准)
					120, 380 + 30 * i, paint);
		}
		// 点击继续
		paint.setShader(null);
		paint.setColor(Color.CYAN);
		paint.setTextSize(16);
		canvas.drawText("（点击继续）", 600, 435, paint);

	}
}

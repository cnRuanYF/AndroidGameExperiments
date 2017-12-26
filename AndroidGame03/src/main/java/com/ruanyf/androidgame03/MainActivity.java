package com.ruanyf.androidgame03;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Feng on 2017/9/20.
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(new MyView(this));

	}

	private class MyView extends View {

		private Paint mPaint;
		private Path mPath;

		public MyView(Context context) {
			super(context);

			mPaint = new Paint();
			mPath = new Path();
		}

		// 绘制十字标记的方法
		private void drawMark(Canvas canvas, int x, int y) {
			int origColor = mPaint.getColor(); // 读取原来的画笔颜色
			mPaint.setColor(Color.RED);
			canvas.drawLine(x - 5, y, x + 5, y, mPaint); // 横线
			canvas.drawLine(x, y - 5, x, y + 5, mPaint); // 竖线
			mPaint.setColor(origColor); // 恢复原来的颜色
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			canvas.drawColor(Color.LTGRAY);

			mPaint.setAntiAlias(true);
			mPaint.setStrokeWidth(2);

			// 基本的绘制与文本截取方式
			mPaint.setColor(Color.BLACK);
			mPaint.setTextSize(16); // 文字大小
			canvas.drawText("1621145229", 50, 50, mPaint); // 绘制文本(字符串,x,y,画笔对象),文字锚点默认位于左下角
			drawMark(canvas, 50, 50); // 标记锚点位置
			canvas.drawText("1621145229", 2, 8, 150, 50, mPaint); // 字符串,起始索引,结束索引(同substring的计算方式)
			canvas.drawText("1621145229".substring(2, 8), 250, 50, mPaint);
			char[] text = {'1', '6', '2', '1', '1', '4', '5', '2', '2', '9'};
			canvas.drawText(text, 2, 8, 350, 50, mPaint); // 字符数组,起始索引,长度

			// 绘制下划线文字
			mPaint.setTextSize(24);
			mPaint.setColor(Color.YELLOW);
			mPaint.setUnderlineText(true); // 设置下划线
			canvas.drawText("UnderlineText", 50, 100, mPaint);
			mPaint.setUnderlineText(false); // 取消下划线

			// 绘制删除线文字
			mPaint.setStrikeThruText(true); // 设置删除线
			canvas.drawText("StrikeThruText", 250, 100, mPaint);
			mPaint.setStrikeThruText(false); // 取消删除线

			// 绘制特定的字体(系统内置)
			mPaint.setColor(Color.MAGENTA);
			mPaint.setTextSize(16);
			mPaint.setTypeface(Typeface.DEFAULT); // 默认字体
			canvas.drawText("默认 / DEFAULT", 50, 150, mPaint);
			mPaint.setTypeface(Typeface.DEFAULT_BOLD); // 默认加粗字体
			canvas.drawText("默认加粗 / DEFAULT_BOLD", 250, 150, mPaint);
			mPaint.setTypeface(Typeface.SANS_SERIF); // 无衬线字体
			canvas.drawText("无衬线 / SANS_SERIF", 50, 175, mPaint);
			mPaint.setTypeface(Typeface.SERIF); // 有衬线字体
			canvas.drawText("有衬线 / SERIF", 250, 175, mPaint);
			mPaint.setTypeface(Typeface.MONOSPACE); // 等宽字体
			canvas.drawText("等宽 / MONOSPACE", 50, 200, mPaint);

			// 绘制特定的字体(自定义)
			mPaint.setColor(Color.BLUE);
			mPaint.setTypeface(Typeface.create("宋体", Typeface.BOLD_ITALIC)); // 查找系统中指定名称的字体(不存在则使用默认)，并加粗倾斜
			canvas.drawText("宋体 (BOLD_ITALIC)", 50, 250, mPaint);
			mPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "STXINGKA.TTF")); // 从原生资源文件中获取(获取上下文.获取原生资源管理器,字体文件路径)
			canvas.drawText("STXINGKA.TTF / 华文行楷", 250, 250, mPaint);
			mPaint.setTypeface(Typeface.DEFAULT); // 还原默认字体

			// 设置字体效果
			mPaint.setColor(Color.RED);
			mPaint.setTextScaleX(3); // 文本在x轴缩放3倍
			canvas.drawText("setTextScaleX(3)", 50, 300, mPaint);
			mPaint.setTextScaleX(1); // 恢复默认缩放

			mPaint.setTextSkewX(0.5f); // 水平方向错切(参数为斜率,正数左斜，负数右斜)
			canvas.drawText("setTextSkewX(0.5f)", 50, 325, mPaint);
			mPaint.setTextSkewX(-0.5f);
			canvas.drawText("setTextSkewX(-0.5f)", 250, 325, mPaint);
			mPaint.setTextSkewX(0); //

			// 设置字体对齐(其实是设置锚点的位置,然后以锚点定位)
			mPaint.setColor(Color.DKGRAY);
			mPaint.setTextAlign(Paint.Align.LEFT); // 水平左对齐(默认对齐方式)
			canvas.drawText("Align.LEFT(锚点在左下角)", canvas.getWidth() / 2, 375, mPaint);
			mPaint.setTextAlign(Paint.Align.CENTER); // 水平居中对齐
			canvas.drawText("Align.CENTER(锚点在中下方)", canvas.getWidth() / 2, 400, mPaint);
			mPaint.setTextAlign(Paint.Align.RIGHT); // 水平右对齐
			canvas.drawText("Align.RIGHT(锚点在右下角)", canvas.getWidth() / 2, 425, mPaint);
			drawMark(canvas, canvas.getWidth() / 2, 375);
			drawMark(canvas, canvas.getWidth() / 2, 400);
			drawMark(canvas, canvas.getWidth() / 2, 425);

			// 沿指定路径绘制文本
			mPaint.setColor(0xFFFF8020);
			mPaint.setTextAlign(Paint.Align.CENTER);
			mPath.addCircle(125, 525, 75, Path.Direction.CW); // 顺时针
			canvas.drawTextOnPath("1621145229 RuanYaofeng", mPath, 0, 0, mPaint); // 沿路径绘制(字符串,路径对象,水平偏移,垂直偏移,画笔对象)
			canvas.drawTextOnPath("阮耀锋", mPath, 150, 20, mPaint);
			mPath.moveTo(190, 515); // 箭头注释
			mPath.lineTo(200, 525);
			mPath.lineTo(210, 515);
			mPaint.setStyle(Paint.Style.STROKE);
			canvas.drawPath(mPath, mPaint);
			mPaint.setStyle(Paint.Style.FILL);

			mPath.reset(); // 重设路径
			mPath.addCircle(325, 525, 75, Path.Direction.CCW); // 反时针
			canvas.drawTextOnPath("1621145229 RuanYaofeng", mPath, 0, 0, mPaint);
			canvas.drawTextOnPath("阮耀锋", mPath, 150, 20, mPaint);
			mPath.moveTo(390, 535); // 箭头注释
			mPath.lineTo(400, 525);
			mPath.lineTo(410, 535);
			mPaint.setStyle(Paint.Style.STROKE);
			canvas.drawPath(mPath, mPaint);
			mPaint.setStyle(Paint.Style.FILL);

			mPaint.setColor(0xFF00A000);
			mPath.reset();
			mPath.moveTo(50,700);
			mPath.cubicTo(175,500,325,900,450,700);
			canvas.drawTextOnPath("1621145229 / RuanYaofeng / 16软件工程专升本2班 / 阮耀锋", mPath, 0, 0, mPaint);
			mPaint.setStyle(Paint.Style.STROKE);
			canvas.drawPath(mPath, mPaint);

		}
	}
}

package com.ruanyf.androidgame04;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;

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

		private Bitmap bitmap; // 位图对象
		private Paint mPaint;
		private Rect src, dst;
		private Matrix matrix; // 矩阵对象

		public MyView(Context context) {
			super(context);

			try {
				bitmap = BitmapFactory.decodeStream(getContext().getAssets().open("avatar.png")); // 通过位图工厂解析通过原生资源管理器获取到的图片的输入流
			} catch (IOException e) {
				e.printStackTrace();
			}
			mPaint = new Paint();
			src = new Rect();
			dst = new Rect();
			matrix = new Matrix();
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			mPaint.setAntiAlias(true);

			// 基本的绘制图片方式
			canvas.drawBitmap(bitmap, 100, 100, null); // 图片的锚点位于左上角,无需特殊效果时可以不传入画笔对象

			mPaint.setAlpha(128); // 取值0(透明)~255(不透明)，大于255的情况截取16进制最后两位
			canvas.drawBitmap(bitmap, 200, 200, mPaint); // 通过画笔绘制半透明图片

			src.set(120, 60, 240, 180); // 用于截取的矩形对象,根据参数决定截取范围(左上角x,y,右下角x,y坐标)
			dst.set(200, 600, 400, 700); // 由于显示截取后图片的矩形对象,根据参数决定显示位置(左上角x,y,右下角x,y坐标)
			canvas.drawBitmap(bitmap, src, dst, null); // 根据矩形对象截取图片并显示

			// 通过颜色数组绘制图像
			int[] colors = new int[150 * 150];
			for (int i = 0; i < colors.length; i++) {
				colors[i] = (int) (0x80 * Math.random() + 0x80) * 0x1000000 + (int) (0xFFFFFF * Math.random()); // 生成透明度在127~255之间的随机颜色
			}
			canvas.drawBitmap(colors, 0, 150, 320, 575, 150, 150, true, null); // 通过颜色数组绘制(颜色数组,偏移量,每行像素数,x,y,宽,高,是否有透明色,画笔);

			// 根据矩阵变换图片
			float[] valuesOrig = { // 原始状态
					1, 0, 0,
					0, 1, 0,
					0, 0, 1};
			matrix.setValues(valuesOrig);
			canvas.drawBitmap(bitmap, matrix, null);

//			float[] valuesTranslate = { // 平移(50,100)
//					1, 0, 50,
//					0, 1, 100,
//					0, 0, 1};
//			matrix.setValues(valuesTranslate);
			matrix.setTranslate(50, 100); // 等同于以上效果
			canvas.drawBitmap(bitmap, matrix, null);

//			float[] valuesScale = { // 缩放(横向*2,纵向*0.5)
//					2, 0, 0,
//					0, 0.5f, 0,
//					0, 0, 1};
//			matrix.setValues(valuesScale);
			matrix.setScale(2, 0.5f);
			canvas.drawBitmap(bitmap, matrix, null);

//			float[] valuesSkew = { // 错切/倾斜(横向左斜0.25,纵向下斜0.75)
//					1, -0.25, 0,
//					0.75f, 1, 0,
//					0, 0, 1};
//			matrix.setValues(valuesSkew);
			matrix.setSkew(-0.25f, 0.75f);
			canvas.drawBitmap(bitmap, matrix, null);

//			float cos=(float)Math.cos(Math.PI /180*-30);
//			float sin=(float)Math.sin(Math.PI /180*-30);
//			float[] valuesRotate = { // 旋转(-30度)
//					cos, -sin, 0,
//					sin, cos, 0,
//					0, 0, 1};
//			matrix.setValues(valuesRotate);
			matrix.setRotate(-30); // 默认以左上角为锚点旋转
			canvas.drawBitmap(bitmap, matrix, null);
			matrix.setRotate(-72, 480, 150); // 指定旋转的中心点坐标(可以超过图片尺寸)
			canvas.drawBitmap(bitmap, matrix, null);

			// 矩阵叠加(不同先后顺序影响最终结果)
			matrix.reset(); // 重置矩阵
			matrix.preTranslate(200, 200); // 矩阵前乘(正序相乘)
			matrix.preRotate(30, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
			matrix.preScale(0.75f, 0.75f);
			matrix.preSkew(0.5f, 0.5f);
			canvas.drawBitmap(bitmap, matrix, null);

			matrix.reset();
			matrix.postTranslate(200, 200); // 矩阵后乘(倒序相乘)
			matrix.postRotate(30, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
			matrix.postScale(0.75f, 0.75f);
			matrix.postSkew(0.5f, 0.5f);
			canvas.drawBitmap(bitmap, matrix, null);

			// 按网格切割后形变
			float[] verts = new float[5 * 5 * 2]; // 用于切割网格的顶点坐标数组
			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {
					verts[(5 * i + j) * 2] = bitmap.getWidth() / 4 * j + 29; // 获取图片宽度,计算每个顶点x坐标,+整体x轴位移
					verts[(5 * i + j) * 2 + 1] = bitmap.getHeight() / 4 * i + 369; // 获取图片高度,计算每个顶点y坐标,+整体y轴位移
				}
			}
//			verts[14] = verts[14] + 10; // 第1行第2个点的x坐标(索引从0起)
//			verts[15] = verts[15] + 10; // 同上,的y坐标
			verts = vertsTranslate(verts, 5, 1, 2, 15, 15); //变换顶点坐标(顶点坐标数组,每行顶点数,行,列,x坐标偏移,y坐标偏移)
			verts = vertsTranslate(verts, 5, 2, 1, -15, -15);
			verts = vertsTranslate(verts, 5, 3, 3, 15, -15);
			verts = vertsTranslate(verts, 5, 4, 0, -15, 15);
			canvas.drawBitmapMesh(bitmap, 4, 4, verts, 0, null, 0, null); // 按网格切割图片后根据坐标数组绘制(位图,网格横向数量,纵向数量,顶点坐标数组,顶点坐标数组偏移,顶点颜色数组,顶点颜色数组偏移,画笔)

			// 再做一个随机形变
			float[] vertsRandom = new float[5 * 5 * 2];
			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {
					vertsRandom[(5 * i + j) * 2] = (bitmap.getWidth() / 4 * j * 0.5f + 340) + (int) (Math.random() * 10); // 计算顶点坐标后整体缩放0.5x,再进行0~10范围内的随机位移
					vertsRandom[(5 * i + j) * 2 + 1] = (bitmap.getHeight() / 4 * i * 0.5f + 29) + (int) (Math.random() * 10);
				}
			}
			canvas.drawBitmapMesh(bitmap, 4, 4, vertsRandom, 0, null, 0, null);

		}

		// 变换顶点坐标的方法 by Feng
		private float[] vertsTranslate(float[] verts, int width, int row, int col, int rx, int ry) {
			verts[(width * row + col) * 2] += rx;
			verts[(width * row + col) * 2 + 1] += ry;
			return verts;
		}
	}
}

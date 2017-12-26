package com.ruanyf.androidgame05;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

/**
 * Created by Feng on 2017/9/21.
 */
public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new MyView(this));

		setTitle("Android " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK + ")"); // 在标题栏显示Android版本号
	}

	private class MyView extends View {

		private Paint mPaint, mPaintStroke;
		private Rect mRect, mRectFull;
		private Matrix mMatrix;
		private Region mRegion;
		private Path mPath;

		public MyView(Context context) {
			super(context);

			mPaint = new Paint();
			mPaintStroke = new Paint();
			mRect = new Rect();
			mRectFull = new Rect();
			mMatrix = new Matrix();
			mRegion = new Region();
			mPath = new Path();

			mPaint.setAntiAlias(true);
		}

		// 生成随机颜色
		private int randomColor() {
			return (int) (0x80000000 + 0xFF * Math.random() * 0x10000 + 0xFF * Math.random() * 0x100 + 0xFF * Math.random());
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			mPaintStroke.setStyle(Paint.Style.STROKE);
			mRectFull.set(0, 0, canvas.getWidth(), canvas.getHeight());

			mRect.set(100, 100, 229, 229);

			// 坐标系的变换 (不会对已绘制图形产生影响,只作为接下来绘制的参考,且每次改变都与之前叠加)
			canvas.save(); // 保存坐标系状态

			canvas.translate(123, 123); // 坐标系平移(100,100)
			mPaint.setColor(randomColor());
			mPaintStroke.setColor(mPaint.getColor());
			canvas.drawRect(mRect, mPaint);
			canvas.drawRect(mRectFull, mPaintStroke); // 绘制当前画布范围

			canvas.rotate(29); // 坐标系旋转(29度)
			mPaint.setColor(randomColor());
			mPaintStroke.setColor(mPaint.getColor());
			canvas.drawRect(mRect, mPaint);
			canvas.drawRect(mRectFull, mPaintStroke);

			canvas.scale(0.75f, 0.5f); // 坐标系缩放(横向0.75x,纵向0.5x)
			mPaint.setColor(randomColor());
			mPaintStroke.setColor(mPaint.getColor());
			canvas.drawRect(mRect, mPaint);
			canvas.drawRect(mRectFull, mPaintStroke);

			canvas.skew(0.29f, 0.5f); // 坐标系错切(横向0.29,纵向0.5)
			mPaint.setColor(randomColor());
			mPaintStroke.setColor(mPaint.getColor());
			canvas.drawRect(mRect, mPaint);
			canvas.drawRect(mRectFull, mPaintStroke);

			canvas.restore(); // 还原到上次保存的坐标系状态

			// 使用矩阵变换坐标系 (不完全等价于上面的效果,matrix参照的坐标系为全屏,上面参照的是标题栏下方的区域)
			// 经测试,早期版本中matrix参照的坐标系为全屏,从API24(Android 7.0)开始,参照标题栏下方区域
			canvas.save();
			mMatrix.preTranslate(123, 123);
			mMatrix.preRotate(29);
			mMatrix.preScale(0.75f, 0.5f);
			mMatrix.preSkew(0.29f, 0.5f);
			canvas.setMatrix(mMatrix);
			mPaint.setColor(Color.RED);
			mPaintStroke.setColor(mPaint.getColor());
			canvas.drawRect(mRect, mPaint);
			canvas.drawRect(mRectFull, mPaintStroke);
			canvas.restore();

			// 坐标系状态的多次保存与恢复测试
			// 测试结果:画布的saveCount默认值为1,保存后+1,恢复后-1
			Log.i("canvas.saveCount", "" + canvas.getSaveCount()); // 测试保存后canvas的saveCount计数(1)
			int saveCount = canvas.save(); // 保存坐标系(返回值为保存之前的层数:1)
			Log.i("canvas.saveCount", "" + canvas.getSaveCount()); // (2)
			canvas.translate(123, 123);
			canvas.save(); // 保存画布坐标系状态
			Log.i("canvas.saveCount", "" + canvas.getSaveCount()); // (3)
			canvas.rotate(29);
			canvas.save();
			Log.i("canvas.saveCount", "" + canvas.getSaveCount()); // (4)
			canvas.scale(0.75f, 0.5f);
			canvas.save();
			Log.i("canvas.saveCount", "" + canvas.getSaveCount()); // (5)
			canvas.skew(0.29f, 0.5f);
			canvas.restore(); // 恢复到上一次保存的坐标系状态(无返回值)
			Log.i("canvas.saveCount", "" + canvas.getSaveCount()); // (4)

			mPaint.setColor(randomColor());
			mPaintStroke.setColor(mPaint.getColor());
			canvas.drawRect(mRect, mPaint);
			canvas.drawRect(mRectFull, mPaintStroke);

//			canvas.restore();
//			canvas.restore();
//			canvas.restore(); // 连续恢复多次,可直接恢复到之前保存的保存点
			canvas.restoreToCount(saveCount); // 恢复到指定保存状态
			Log.i("canvas.saveCount", "" + canvas.getSaveCount()); // (1)

			mPaint.setColor(randomColor());
			mPaintStroke.setColor(mPaint.getColor());
			canvas.drawRect(mRect, mPaint);
			canvas.drawRect(mRectFull, mPaintStroke);

			// 剪切区的使用
			int tColor = randomColor();
			mPaintStroke.setColor(tColor);

			canvas.save();
			canvas.clipRect(29, 29, 50, 100); // 设置剪切区为矩形
			canvas.drawColor(randomColor()); // 绘制仅对剪切区生效
			canvas.clipRect(29, 29, 100, 50); // 再次设置仅能让剪切区变小,不能变大
			canvas.drawColor(tColor);
			canvas.restore(); // 恢复画布也可以恢复剪切区

			canvas.drawRect(29, 29, 100, 50, mPaintStroke); // 标出第二次设置的剪切区区域
			canvas.drawLine(0, canvas.getHeight(), canvas.getWidth(), 0, mPaintStroke); // 画一条线确认剪切区是否恢复

			// 使用Path设置剪切区
			canvas.save();
			mPath.moveTo(29,600);
			mPath.cubicTo(123,450,229,750,321,600);
			canvas.clipPath(mPath); // 使用Path设置剪切区
			canvas.drawColor(randomColor());
			canvas.restore();

			// 测试坐标系变化对剪切区的影响
			canvas.save();
			canvas.rotate(-29);
			canvas.clipRect(29,567,123,678); // 坐标系的变化会影响到clipRect
			canvas.drawColor(randomColor());
			canvas.restore();

			// 使用Region对象设置剪切区
			// 经测试,在早期版本中参照的坐标系为全屏(且不受画布变换影响),从API24(Android 7.0)开始参照的坐标系标题栏下方区域,
			// 且clipRegion方法已被废弃,从API26(Android 8.0)开始,该方法失效
			canvas.save();
			canvas.rotate(29);
			mRegion.set(29,567,480,654); // 设置Region的范围
			mRect.set(350,29,400,229);
			mRegion.union(mRect); // 可以且仅可以使用Rect联合多个区域
			mRect.set(400,229,450,720);
			mRegion.union(mRect);
			canvas.clipRegion(mRegion); // 使用clipRegion不会受到坐标系影响
			canvas.drawColor(randomColor());
			canvas.restore();

		}
	}
}

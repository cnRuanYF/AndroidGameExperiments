package com.ruanyf.androidgame06;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by Feng on 2017/9/21.
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(new MyView(this));
//		setContentView(new MyBufferView(this));
//		setContentView(new MyThreadView(this));
		setContentView(new MySurfaceView(this));
	}

	/**
	 * 自定义视图类(使用正常的绘制方式)
	 * 此方式按照代码顺序依次绘制,清空画布,再依次绘制,清空画布,再依次绘制...在图形复杂时会出现闪屏现象
	 */
	private class MyView extends View {

		private Paint mPaint;
		private int radius; // 作为圆的半径

		public MyView(Context context) {
			super(context);
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			radius = radius > 200 ? 0 : radius; // 若半径超过200,重设为0
			mPaint.setColor(Color.RED);
			canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, radius++, mPaint); // 每次绘制半径+1

			invalidate(); // 使View失效，系统会再次执行onDraw()方法
		}
	}

	/**
	 * 自定义视图类(自制缓冲绘制避免闪屏)
	 * 此方式按照代码顺序依次绘制图形在缓冲区,再把缓冲区绘制到屏幕,每次刷新都是完整的画面,可避免闪屏
	 */
	private class MyBufferView extends View {

		private Paint mPaint;
		private int radius; // 作为圆的半径

		public MyBufferView(Context context) {
			super(context);
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			Bitmap bufferBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888); // 创建一个用于缓冲绘制的Bitmap对象
			Canvas bufferCanvas = new Canvas(bufferBitmap); // 自建一个用于缓冲的画布对象(对该画布进行绘制会绘制到bufferBitmap)
			bufferDraw(bufferCanvas); // 调用自定义类似onDraw的方法
			canvas.drawBitmap(bufferBitmap, 0, 0, null);

			invalidate();
		}

		// 此方法中可以像onDraw方法一样进行绘制操作
		private void bufferDraw(Canvas canvas) {
			radius = radius > 200 ? 0 : radius;
			mPaint.setColor(Color.YELLOW);
			canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, radius++, mPaint);
		}
	}

	/**
	 * 自定义视图类(使用线程控制刷新速度)
	 * 之前的方法在绘制完毕立即调用invalidate()方法进行重绘,根据不同设备的处理能力,以及绘制的复杂度,可能造成帧率不稳定
	 * 而使用线程,以固定的频率重绘,可以解决这个问题
	 */
	private class MyThreadView extends View implements Runnable {

		private Paint mPaint;
		private int radius;

		public MyThreadView(Context context) {
			super(context);
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			new Thread(this).start(); // 执行这个线程(这个类实现了Runnable接口,可直接使用this)
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			Bitmap bufferBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas bufferCanvas = new Canvas(bufferBitmap);

			radius = radius > 200 ? 0 : radius;
			mPaint.setColor(Color.CYAN);
			bufferCanvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, radius++, mPaint);

			canvas.drawBitmap(bufferBitmap, 0, 0, null);
		}

		/**
		 * 当一个实现了Runnable接口的类创建并启动线程时,调用这个方法
		 */
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(50); // 线程等待50毫秒(根据实际需要的fps更改)
					postInvalidate(); // 线程不能直接更新UI,只能通过post一个请求给UI执行
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 自定义SurfaceView类(该类继承于View,是一个基于游戏优化的双缓冲视图)
	 */
	private class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

		private SurfaceHolder holder; // Surface持有者对象
		private boolean isRunning; // 是否在运行的标记
		private int fps = 60; // 设定帧率
		private int frame; // 记录帧数
		private Canvas canvas;
		private int circleRadius = 1;
		private int circleRadiusChange = 1; // 决定半径递增还是递减(1或-1)
		private Path circlePath;

		public MySurfaceView(Context context) {
			super(context);
			holder = getHolder();
			holder.addCallback(this); // 加入回调监听(实现了SurfaceHolder.Callback接口)
			circlePath = new Path();
		}

		/**
		 * This is called immediately after the surface is first created.
		 * 这个方法在Surface第一次被创建时调用
		 * Implementations of this should start up whatever rendering code
		 * they desire.  Note that only one thread can ever draw into
		 * a {@link Surface}, so you should not draw into the Surface here
		 * if your normal rendering will be in another thread.
		 * (不会英语,这一段无法翻译,又吃了没文化的亏)
		 *
		 * @param holder The SurfaceHolder whose surface is being created.
		 *               正在创建的surface的持有者对象
		 */
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			Log.i("MySurfaceView", "surfaceCreated");
			isRunning = true;
			new Thread(this).start();
		}

		/**
		 * This is called immediately after any structural changes (format or
		 * size) have been made to the surface.  You should at this point update
		 * the imagery in the surface.  This method is always called at least
		 * once, after {@link #surfaceCreated}.
		 * 这个方法会在对surface做出任何结构变化(格式或尺寸)之后调用
		 * 你应该在此更新surface中的图像
		 * 这个方法在surfaceCreated之后,至少被调用一次
		 *
		 * @param holder The SurfaceHolder whose surface has changed.
		 *               发生改变的surface的持有者对象
		 * @param format The new PixelFormat of the surface.
		 * @param width  The new width of the surface.
		 * @param height The new height of the surface.
		 */
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			Log.i("MySurfaceView", "surfaceChanged");
		}

		/**
		 * This is called immediately before a surface is being destroyed. After
		 * returning from this call, you should no longer try to access this
		 * surface.  If you have a rendering thread that directly accesses
		 * the surface, you must ensure that thread is no longer touching the
		 * Surface before returning from this function.
		 * 这个方法在surface被销毁之前调用
		 * 执行完这个方法后你不应该再继续操作此surface
		 * 如果你有一个线程直接访问surface,则必须确保执行完这个方法后不再访问
		 *
		 * @param holder The SurfaceHolder whose surface is being destroyed.
		 *               正在销毁的surface的持有者对象
		 */
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.i("MySurfaceView", "surfaceDestroyed");
			isRunning = false;
		}

		@Override
		public void run() {
			while (isRunning) {
				long startTime = System.currentTimeMillis(); // 记录执行前的系统时间(毫秒)
				doDraw(); // 把绘制相关代码写入单独的方法提高代码整洁度
				long drawTime = System.currentTimeMillis() - startTime; // 计算时间差得到绘制耗时
				if (drawTime < 1000 / fps) { // 如果绘制时间小于每帧时间,则需要等待
					Log.i("Frame " + frame++, "onDraw() took " + drawTime + "ms."); // Log输出每帧绘制时间
					try {
						Thread.sleep(1000 / fps - drawTime); // 使休眠时间+绘制花费时间=每帧时间
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					Log.w("Frame " + frame++, "onDraw() took " + drawTime + "ms that is too long!!!"); // 绘制时间过久的警告
				}
			}
		}

		private void doDraw() {
			canvas = holder.lockCanvas(); // 获得可用于绘制的Canvas对象,若surface未创建或不可编辑,则返回null(必须加入回调监听以保证可用)
			if (canvas != null) {

				circleRadiusChange = circleRadius >= 150 ? -1 : circleRadiusChange;
				circleRadiusChange = circleRadius <= 5 ? 1 : circleRadiusChange;
				circleRadius += circleRadiusChange; // 半径递增至150开始递减,递减至5则开始递增...
				int[] colors = new int[(circleRadius * 2) * (circleRadius * 2)];
				for (int i = 0; i < colors.length; i++) {
					colors[i] = (int) (0xFFFFFF * Math.random());
				}
				canvas.drawColor(Color.BLACK); // 使用surfaceView进行绘制时必须先清空画布,否则可能造成残影
				circlePath.reset();
				circlePath.addCircle(canvas.getWidth()/2, canvas.getHeight()/2, circleRadius, Path.Direction.CW);
				canvas.clipPath(circlePath);
				canvas.drawBitmap(colors, 0, circleRadius * 2, canvas.getWidth() / 2 - circleRadius, canvas.getHeight() / 2 - circleRadius, circleRadius * 2, circleRadius * 2, false, null);

				holder.unlockCanvasAndPost(canvas); // 将surface中的像素绘制到屏幕,同时丢失surface中的内容,不保证再次调用lockCanvas方法获取到的残留物还是与之前一致的)
			}
		}

	}

}

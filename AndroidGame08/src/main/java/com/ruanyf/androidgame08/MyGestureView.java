package com.ruanyf.androidgame08;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Feng on 2017/9/28.
 */
public class MyGestureView extends View {

	private static final String TAG = "手势";
	private GestureDetector mGestureDetector;

	public MyGestureView(Context ctx) {
		super(ctx);
		setLongClickable(true); // 设置为可长按才能监听到长按事件
		mGestureDetector = new GestureDetector(ctx, new GestureDetector.SimpleOnGestureListener() { // 创建手势探测器,绑定简单手势监听器
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				Log.d(TAG, "onSingleTapUp\t\t单击抬起时触发");
				return super.onSingleTapUp(e);
			}

			@Override
			public void onLongPress(MotionEvent e) {
				Log.d(TAG, "onLongPress\t\t按下并持续一段时间后触发(长按,之后不会再触发其他单击相关事件)");
				super.onLongPress(e);
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				Log.d(TAG, "onScroll\t\t\t按下并移动时触发,一次按下可多次触发");
				return super.onScroll(e1, e2, distanceX, distanceY);
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				float moveX = e2.getX() - e1.getX(); // 计算滑动触发时手指的相对移动距离(像素)
				float moveY = e2.getY() - e1.getY();
				Log.d(TAG, "onFling\t\t\t快速滑动并抬起时触发(moveX = " + moveX + ", moveY = " + moveY + ")");
				if (Math.abs(moveX) > Math.abs(moveY)) { // 若x轴移动大于y轴,则判定为向左或向右
					if (moveX < 0) {
						Log.d(TAG, "onFling\t\t\t← ← ← ← ← 向左滑动 ← ← ← ← ←");
					} else {
						Log.d(TAG, "onFling\t\t\t→ → → → → 向右滑动 → → → → →");
					}
				} else {
					if (moveY < 0) {
						Log.d(TAG, "onFling\t\t\t↑ ↑ ↑ ↑ ↑ 向上滑动 ↑ ↑ ↑ ↑ ↑");
					} else {
						Log.d(TAG, "onFling\t\t\t↓ ↓ ↓ ↓ ↓ 向下滑动 ↓ ↓ ↓ ↓ ↓");
					}
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}

			@Override
			public void onShowPress(MotionEvent e) {
				Log.d(TAG, "onShowPress\t\t按下并持续一段时间后触发(短按)");
				super.onShowPress(e);
			}

			@Override
			public boolean onDown(MotionEvent e) {
				Log.d(TAG, "onDown\t\t\t\t按下时立刻触发");
				return super.onDown(e);
			}

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				Log.d(TAG, "onDoubleTap\t\t第二次按下时立刻触发,优先级高于onDoubleTapEvent");
				return super.onDoubleTap(e);
			}

			@Override
			public boolean onDoubleTapEvent(MotionEvent e) {
				Log.d(TAG, "onDoubleTapEvent\t第二次按下的一系列动作(按下移动抬起)都会触发,本次e.getAction()=" + e.getAction());
				return super.onDoubleTapEvent(e);
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				Log.d(TAG, "onSingleTapConfirmed\t单击被确认时触发(双击有效时间内没有点击第二次)");
				return super.onSingleTapConfirmed(e);
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event); // 把触摸事件传递给手势识别器
		return super.onTouchEvent(event);
	}
}

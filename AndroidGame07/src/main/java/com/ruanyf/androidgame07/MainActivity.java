package com.ruanyf.androidgame07;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

/**
 * Created by Feng on 2017/9/28.
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 创建对话框用于选择要显示的视图
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("选择要显示的视图：");

		// 添加内容&设置列表项点击监听
		String[] views = new String[]{
				"MyPaintView\n画图 (简陋版)",
				"MyColorfulPaintView\n画图 (彩色版)",
				"MyRippleView\n水波纹 (简陋版)",
				"MyRipplePlusView\n水波纹 (逼真版)"};
		builder.setItems(views, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case 0:
						setContentView(new MyPaintView(getApplication())); // 画图视图
						break;
					case 1:
						setContentView(new MyColorfulPaintView(getApplication())); // Colorful画图视图
						break;
					case 2:
						setContentView(new MyRippleView(getApplication())); // 水波纹视图
						break;
					case 3:
						setContentView(new MyRipplePlusView(getApplication())); // 水波纹Plus视图
						break;
				}
			}
		});

		// 设置取消直接结束App
		builder.setOnCancelListener(
				new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						finish();
					}
				}
		);
		builder.show(); // 显示对话框

	}
}

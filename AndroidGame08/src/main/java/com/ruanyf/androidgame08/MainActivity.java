package com.ruanyf.androidgame08;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Feng on 2017/9/28.
 */
public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final String[] titles = new String[]{"MyGestureView\n手势测试",
				"MySnakeView\n贪吃蛇没食物饿不死"};
		new AlertDialog.Builder(this)
				.setTitle("选择要显示的视图")
				.setItems(titles, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								setContentView(new MyGestureView(getApplication()));
								break;
							case 1:
								setContentView(new MySnakeView(getApplication()));
								break;
						}
						setTitle(titles[which]);
					}
				})
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						finish();
					}
				})
				.show();
	}
}

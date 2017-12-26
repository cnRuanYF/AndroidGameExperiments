package com.ruanyf.androidgame22;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * 游戏音乐类
 * Created by Feng on 2017/11/02.
 */
public class GameMusic {
	private Context ctx;
	private MediaPlayer mediaPlayer;
	private String fileName;

	// 音乐文件
	public static final String BGM_CG = "sounds/bgm_cg.mid",
			BGM_TITLE = "sounds/bgm_title.mp3",
			BGM_STORY = "sounds/bgm_story.mp3",
			BGM_BATTLE = "sounds/bgm_battle.mp3",
			BGM_WIN = "sounds/bgm_win.wav",
			BGM_GAMEOVER = "sounds/bgm_gameover.wav",
			BGM_STAFF = "sounds/bgm_staff.mp3",
			SFX_FLIGHT = "sounds/fx_flight.wav";

	public GameMusic(Context context) {
		super();
		this.ctx = context;
		mediaPlayer = new MediaPlayer();
	}

	/**
	 * 根据文件名播放音乐
	 *
	 * @param fileName 文件路径
	 */
	public void play(String fileName) {
		if (this.fileName == fileName) {
			return; // 正在播放相同的文件则返回
		}
		this.fileName = fileName;
		mediaPlayer.reset();
		try {
			AssetFileDescriptor assetFileDescriptor = ctx.getAssets().openFd(fileName);
			mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
					assetFileDescriptor.getStartOffset(),
					assetFileDescriptor.getLength()); // 设置数据源()
			mediaPlayer.setLooping(true); // 设置循环播放
			mediaPlayer.prepare(); // 预加载媒体播放器
			mediaPlayer.start(); // 播放
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 停止音乐播放
	 */
	public void stop() {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
	}
}

package com.ruanyf.androidgame22;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.io.IOException;

/**
 * 游戏音效池
 * Created by Feng on 2017/11/02.
 */
public class GameSoundPool {
	private Context ctx;
	private SoundPool soundPool;
	private String fileName;
	private int enemyDieSound, enemyAttackSound;
	private int fireSkillSound, iceSkillSound;
	private int zhaolingerHurtSound, lixiaoyaoHurtSound;

	public GameSoundPool(Context context) {
		super();
		this.ctx = context;
		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0); // 最多同时播放5个,播放类型,质量(0为默认)
		enemyDieSound = getSoundID("sounds/enemy_die.wav");
		enemyAttackSound = getSoundID("sounds/enemy_attack.wav");
		fireSkillSound = getSoundID("sounds/skill_fire.ogg");
		iceSkillSound = getSoundID("sounds/skill_ice.wav");
		zhaolingerHurtSound = getSoundID("sounds/hurt_zhaolinger.wav");
		lixiaoyaoHurtSound = getSoundID("sounds/hurt_lixiaoyao.wav");
	}

	/**
	 * 根据文件名获取音效ID
	 *
	 * @param fileName 文件路径
	 */
	public int getSoundID(String fileName) {
		int soundID = 0;
		try {
			soundID = soundPool.load(ctx.getAssets().openFd(fileName), 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return soundID;
	}

	/**
	 * 播放指定音效
	 */
	public void play(int soundID){
		soundPool.play(soundID,1,1,1,0,1);
	}

	/*
	 * 获取各种音效的Getter
	 */
	public int getEnemyDieSound() {
		return enemyDieSound;
	}

	public int getEnemyAttackSound() {
		return enemyAttackSound;
	}

	public int getFireSkillSound() {
		return fireSkillSound;
	}

	public int getIceSkillSound() {
		return iceSkillSound;
	}

	public int getZhaolingerHurtSound() {
		return zhaolingerHurtSound;
	}

	public int getLixiaoyaoHurtSound() {
		return lixiaoyaoHurtSound;
	}

}

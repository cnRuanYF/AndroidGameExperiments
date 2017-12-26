package com.ruanyf.androidgame34;

import android.util.SparseArray;

import com.ruanyf.androidgame34.plants.Plant;

import org.cocos2d.actions.CCScheduler;

import java.util.ArrayList;

/**
 * Created by Feng on 2017/11/23.
 */
public class FightLine {
	private SparseArray<Plant> plants;
	private ArrayList<Zombie> zombies;

	public FightLine() {
		super();
		plants = new SparseArray<Plant>();
		zombies = new ArrayList<Zombie>();
		CCScheduler.sharedScheduler().schedule("attackPlant", this, 0.5f, false);
	}

	public void addPlant(int col, Plant plant) {
		plants.put(col, plant);
	}

	public boolean isContainPlant(int col) {
		if (plants.get(col) != null) {
			return true;
		}
		return false;
	}

	public void addZombie(Zombie aZombie) {
		zombies.add(aZombie);
	}

	public void attackPlant(float t) {
		if (plants.size() != 0 && !zombies.isEmpty()) {
			for (Zombie zombie : zombies) {
				int col = ((int) zombie.getPosition().x - 240) / 80;
				Plant aPlant = plants.get(col);
				if (aPlant != null) {
					if (aPlant.getLife() == 0) {
						plants.remove(col);
					} else {
						zombie.attackPlant(aPlant);
					}
				}

			}
		}
	}
}

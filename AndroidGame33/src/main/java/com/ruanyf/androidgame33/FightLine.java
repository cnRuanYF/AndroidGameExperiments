package com.ruanyf.androidgame33;

import android.util.SparseArray;

import com.ruanyf.androidgame33.plants.Plant;

/**
 * Created by Feng on 2017/11/23.
 */
public class FightLine {
	private SparseArray<Plant> plants;

	public FightLine() {
		super();
		plants = new SparseArray<Plant>();
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
}

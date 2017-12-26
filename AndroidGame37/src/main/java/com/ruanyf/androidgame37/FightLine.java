package com.ruanyf.androidgame37;

import android.util.SparseArray;

import com.ruanyf.androidgame37.bullets.Bullet;
import com.ruanyf.androidgame37.plants.Plant;
import com.ruanyf.androidgame37.plants.ShooterPlant;

import org.cocos2d.actions.CCScheduler;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Feng on 2017/11/23.
 * Update on 2017/12/13.
 */
public class FightLine {
	private SparseArray<Plant> plants;
	private ArrayList<Zombie> zombies;
	private ArrayList<ShooterPlant> shooterPlants;

	public FightLine() {
		super();
		plants = new SparseArray<Plant>();
		zombies = new ArrayList<Zombie>();
		shooterPlants = new ArrayList<ShooterPlant>();
		CCScheduler.sharedScheduler().schedule("attackPlant", this, 0.5f, false);
		CCScheduler.sharedScheduler().schedule("attackZombie", this, 0.5f, false);
		CCScheduler.sharedScheduler().schedule("bulletDamage", this, 0.2f, false);
	}

	public void addPlant(int col, Plant aPlant) {
		plants.put(col, aPlant);
		if (aPlant instanceof ShooterPlant) {
			shooterPlants.add((ShooterPlant) aPlant);
		}
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
						if (aPlant instanceof ShooterPlant) {
							shooterPlants.remove(aPlant);
						}
					} else {
						zombie.attackPlant(aPlant);
					}
				}

			}
		}
	}

	public void attackZombie(float t) {
		if (!shooterPlants.isEmpty()) {
			for (ShooterPlant shooterPlant : shooterPlants) {
				if (zombies.isEmpty()) {
					shooterPlant.stopAttackZombie();
				} else {
					shooterPlant.attackZombie();
				}
			}
		}
	}

	public void bulletDamage(float t) {
		if (!zombies.isEmpty() && !shooterPlants.isEmpty()) {
			Iterator<Zombie> iterator = zombies.iterator();
			while (iterator.hasNext()) {
				Zombie zombie = iterator.next();
				for (ShooterPlant shooterPlant : shooterPlants) {
					for (Bullet bullet : shooterPlant.getBullets()) {
						if (bullet.getVisible()) {
							if (bullet.getPosition().x > zombie.getPosition().x - 20 && bullet.getPosition().x < zombie.getPosition().x + 20) {
								bullet.showBlast(zombie);
								bullet.setVisible(false);
								zombie.attack(bullet.getAttack());
								if (zombie.getLife() == 0) {
									zombie.removeSelf();
									iterator.remove();
								}
							}
						}
					}
				}
			}
		}
	}
}

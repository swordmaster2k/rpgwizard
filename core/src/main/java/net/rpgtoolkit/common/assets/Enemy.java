/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.common.assets;

public class Enemy extends AbstractSprite {

	private int level;
	private double health;
	private double attack;
	private double defence;
	private double magic;
	private double experienceReward;
	private double goldReward;

	public Enemy(AssetDescriptor descriptor) {
		super(descriptor);
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public double getHealth() {
		return health;
	}

	public void setHealth(double health) {
		this.health = health;
	}

	public double getAttack() {
		return attack;
	}

	public void setAttack(double attack) {
		this.attack = attack;
	}

	public double getDefence() {
		return defence;
	}

	public void setDefence(double defence) {
		this.defence = defence;
	}

	public double getMagic() {
		return magic;
	}

	public void setMagic(double magic) {
		this.magic = magic;
	}

	public double getExperienceReward() {
		return experienceReward;
	}

	public void setExperienceReward(double experienceReward) {
		this.experienceReward = experienceReward;
	}

	public double getGoldReward() {
		return goldReward;
	}

	public void setGoldReward(double goldReward) {
		this.goldReward = goldReward;
	}

}

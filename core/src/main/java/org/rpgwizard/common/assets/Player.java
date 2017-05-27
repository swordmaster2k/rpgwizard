/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

public class Player extends AbstractSprite {

	private int level;
	private int maxLevel;
	private double experience;
	private double maxExperience;
	private double health;
	private double maxHealth;
	private double attack;
	private double maxAttack;
	private double defence;
	private double maxDefence;
	private double magic;
	private double maxMagic;

	/**
	 * Opens a player from an existing file
	 *
	 * @param descriptor
	 *            Character (.tem) file to open
	 */
	public Player(AssetDescriptor descriptor) {
		super(descriptor);
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public double getExperience() {
		return experience;
	}

	public void setExperience(double experience) {
		this.experience = experience;
	}

	public double getMaxExperience() {
		return maxExperience;
	}

	public void setMaxExperience(double maxExperience) {
		this.maxExperience = maxExperience;
	}

	public double getHealth() {
		return health;
	}

	public void setHealth(double health) {
		this.health = health;
	}

	public double getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(double maxHealth) {
		this.maxHealth = maxHealth;
	}

	public double getAttack() {
		return attack;
	}

	public void setAttack(double attack) {
		this.attack = attack;
	}

	public double getMaxAttack() {
		return maxAttack;
	}

	public void setMaxAttack(double maxAttack) {
		this.maxAttack = maxAttack;
	}

	public double getDefence() {
		return defence;
	}

	public void setDefence(double defence) {
		this.defence = defence;
	}

	public double getMaxDefence() {
		return maxDefence;
	}

	public void setMaxDefence(double maxDefence) {
		this.maxDefence = maxDefence;
	}

	public double getMagic() {
		return magic;
	}

	public void setMagic(double magic) {
		this.magic = magic;
	}

	public double getMaxMagic() {
		return maxMagic;
	}

	public void setMaxMagic(double maxMagic) {
		this.maxMagic = maxMagic;
	}

	@Override
	public void reset() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}

/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

public class SpecialMove extends AbstractAsset {

	private String name;
	private String description;
	private AssetDescriptor program;
	private AssetDescriptor statusEffect;
	private AssetDescriptor animation;
	private int fightPower;
	private int movePowerCost;
	private int movePowerDrainedFromTarget;
	private boolean usableInBattle;
	private boolean usableInMenu;

	public SpecialMove(AssetDescriptor descriptor) {
		super(descriptor);
		reset();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getFightPower() {
		return fightPower;
	}

	public void setFightPower(int fightPower) {
		this.fightPower = fightPower;
	}

	public int getMovePowerCost() {
		return movePowerCost;
	}

	public void setMovePowerCost(int cost) {
		this.movePowerCost = cost;
	}

	public AssetDescriptor getProgram() {
		return program;
	}

	public void setProgram(AssetDescriptor program) {
		this.program = program;
	}

	public int getMovePowerDrainedFromTarget() {
		return movePowerDrainedFromTarget;
	}

	public void setMovePowerDrainedFromTarget(int amount) {
		this.movePowerDrainedFromTarget = amount;
	}

	public boolean isUsableInBattle() {
		return usableInBattle;
	}

	public void isUsableInBattle(boolean value) {
		this.usableInBattle = value;
	}

	public boolean isUsableInMenu() {
		return usableInMenu;
	}

	public void isUsableInMenu(boolean value) {
		this.usableInMenu = value;
	}

	public AssetDescriptor getStatusEffect() {
		return statusEffect;
	}

	public void setStatusEffect(AssetDescriptor asset) {
		this.statusEffect = asset;
	}

	public AssetDescriptor getAnimation() {
		return animation;
	}

	public void setAnimation(AssetDescriptor asset) {
		this.animation = asset;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void reset() {
		this.name = "";
		this.description = "";
		this.movePowerCost = 0;
		this.fightPower = 0;
		this.program = null;
		this.movePowerDrainedFromTarget = 0;
		this.statusEffect = null;
		this.animation = null;
		this.usableInBattle = false;
		this.usableInMenu = false;
	}
}

/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import java.util.HashMap;
import java.util.Map;

public class Character extends AbstractSprite {

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
    private int gold;
    private Map<String, String> equipment;
    private Map<String, String> inventory;

    /**
     * Opens a player from an existing file
     *
     * @param descriptor
     *            Character (.character) file to open
     */
    public Character(AssetDescriptor descriptor) {
        super(descriptor);

        equipment = new HashMap<>();
        equipment.put(Equipment.HEAD.toString(), "");
        equipment.put(Equipment.CHEST.toString(), "");
        equipment.put(Equipment.RIGHT_HAND.toString(), "");
        equipment.put(Equipment.LEFT_HAND.toString(), "");
        equipment.put(Equipment.BOOTS.toString(), "");
        equipment.put(Equipment.GLOVES.toString(), "");
        equipment.put(Equipment.ACCESSORY_1.toString(), "");
        equipment.put(Equipment.ACCESSORY_2.toString(), "");

        inventory = new HashMap<>();
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

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public Map<String, String> getEquipment() {
        return equipment;
    }

    public void setEquipment(Map<String, String> equipment) {
        this.equipment = equipment;
    }

    public Map<String, String> getInventory() {
        return inventory;
    }

    public void setInventory(Map<String, String> inventory) {
        this.inventory = inventory;
    }

    @Override
    public void reset() {

    }

}

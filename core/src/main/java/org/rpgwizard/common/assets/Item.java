/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

/**
 *
 * @author Joshua Michael Daly
 */
public class Item extends AbstractAsset {

    private String name;
    private String icon;
    private String description;
    private String type;
    private int price;

    private double healthEffect;
    private double attackEffect;
    private double defenceEffect;
    private double magicEffect;

    public Item(AssetDescriptor descriptor) {
        super(descriptor);
        init();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public double getHealthEffect() {
        return healthEffect;
    }

    public void setHealthEffect(double healthEffect) {
        this.healthEffect = healthEffect;
    }

    public double getAttackEffect() {
        return attackEffect;
    }

    public void setAttackEffect(double attackEffect) {
        this.attackEffect = attackEffect;
    }

    public double getDefenceEffect() {
        return defenceEffect;
    }

    public void setDefenceEffect(double defenceEffect) {
        this.defenceEffect = defenceEffect;
    }

    public double getMagicEffect() {
        return magicEffect;
    }

    public void setMagicEffect(double magicEffect) {
        this.magicEffect = magicEffect;
    }

    private void init() {
        name = "";
        icon = "";
        description = "";
        type = "";
        price = 0;

        healthEffect = 0.0;
        attackEffect = 0.0;
        defenceEffect = 0.0;
        magicEffect = 0.0;
    }

}

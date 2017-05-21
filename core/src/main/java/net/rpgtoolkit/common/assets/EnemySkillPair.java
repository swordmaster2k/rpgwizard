/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.common.assets;

/**
 * 
 * @author Geoff Wilson
 * @author Joshua Michael Daly
 */
public class EnemySkillPair
{
    private String enemy; // Could in the future be changed to the actual enemey file
    private int skill;

    public EnemySkillPair(String enemy, int skill)
    {
        this.enemy = enemy;
        this.skill = skill;
    }

    public String getEnemy()
    {
        return this.enemy;
    }

    public int getSkill()
    {
        return this.skill;
    }

    public void setEnemy(String enemy)
    {
        this.enemy = enemy;
    }

    public void setSkill(int skill)
    {
        this.skill = skill;
    }
}

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
 * @author Joel Moore
 */
public class PlayerSpecialMove
{
    private String name;
    private long minExperience;
    private long minLevel;
    private String conditionVariable;
    private String conditionVariableTest;

    public PlayerSpecialMove(String name, long minExperience, long minLevel, String cVar, String cVarTest) {
        this.name = name;
        this.minExperience = minExperience;
        this.minLevel = minLevel;
        this.conditionVariable = cVar;
        this.conditionVariableTest = cVarTest;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMinExperience() {
        return this.minExperience;
    }

    public void setMinExperience(long minExperience) {
        this.minExperience = minExperience;
    }

    public long getMinLevel() {
        return this.minLevel;
    }

    public void setMinLevel(long minLevel) {
        this.minLevel = minLevel;
    }

    public String getConditionVariable() {
        return this.conditionVariable;
    }

    public void setConditionVariable(String conditionVariable) {
        this.conditionVariable = conditionVariable;
    }

    public String getConditionVariableTest() {
        return this.conditionVariableTest;
    }

    public void setConditionVariableTest(String conditionVariableTest) {
        this.conditionVariableTest = conditionVariableTest;
    }
}

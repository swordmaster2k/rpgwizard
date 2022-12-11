/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.animation;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Joshua Michael Daly
 */
public enum AnimationEnum {
    NORTH("north"), SOUTH("south"), EAST("east"), WEST("west"), IDLE("idle");

    private final String value;

    private static final Map<String, AnimationEnum> lookup = new HashMap<>();

    static {
        for (AnimationEnum e : EnumSet.allOf(AnimationEnum.class)) {
            lookup.put(e.getValue(), e);
        }
    }

    AnimationEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AnimationEnum fromValue(String value) {
        return lookup.get(value);
    }

}

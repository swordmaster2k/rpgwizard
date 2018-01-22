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
 * @author Chris Hutchinson <chris@cshutchinson.com>
 */
public enum NPCWalkDirection {

    NORTH(0), SOUTH(1), EAST(2), WEST(3), NORTH_WEST(4), NORTH_EAST(5), SOUTH_WEST(6), SOUTH_EAST(7);

    private final int value;

    private NPCWalkDirection(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

}

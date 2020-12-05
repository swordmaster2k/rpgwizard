/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.map;

import java.util.stream.Stream;

/**
 * Basic event to be used in assets that can trigger events.
 * 
 * @author Joshua Michael Daly
 */
public enum EventType {

    OVERLAP("overlap"), KEYPRESS("keypress");

    private final String value;

    private EventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static String[] toStringArray() {
        return Stream.of(EventType.values()).map(EventType::getValue).toArray(String[]::new);
    }

    @Override
    public String toString() {
        return value;
    }

}

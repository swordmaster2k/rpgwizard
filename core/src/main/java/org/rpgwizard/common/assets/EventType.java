/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import java.util.stream.Stream;

/**
 * Basic event to be used in assets that can trigger events.
 * 
 * @author Joshua Michael Daly
 */
public enum EventType {

    OVERLAP, KEYPRESS;

    public static String[] toStringArray() {
        return Stream.of(EventType.values()).map(EventType::name).toArray(String[]::new);
    }
}

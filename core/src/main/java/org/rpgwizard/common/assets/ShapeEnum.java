/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import java.util.stream.Stream;

public enum ShapeEnum {

    // @formatter:off
    CIRCLE("Circle"),
    POLYGON("Polygon");
    // @formatter:on

    private final String value;

    private ShapeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String[] toStringArray() {
        return Stream.of(ShapeEnum.values()).map(ShapeEnum::getValue).toArray(String[]::new);
    }
}

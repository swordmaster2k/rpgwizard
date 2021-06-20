/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.map.generation;

/**
 *
 * @author Joshua Michael Daly
 */
public enum ScriptType {

    CUSTOM, MAP_LINK;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

}

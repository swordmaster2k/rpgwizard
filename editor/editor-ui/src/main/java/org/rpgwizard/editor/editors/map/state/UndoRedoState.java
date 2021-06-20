/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.map.state;

import lombok.Getter;
import org.rpgwizard.common.assets.map.Map;

/**
 *
 * @author Joshua Michael Daly
 */
@Getter
public class UndoRedoState {

    private final Map map;
    private final UndoRedoType type;

    public UndoRedoState(Map map, UndoRedoType type) {
        this.map = map;
        this.type = type;
    }

    /**
     * Copy constructor.
     * 
     * @param state
     */
    public UndoRedoState(UndoRedoState state) {
        this.map = new Map(state.getMap());
        this.type = state.type;
    }

}

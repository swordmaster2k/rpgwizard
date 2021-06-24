/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.tileset;

import java.util.EventObject;
import lombok.Getter;
import org.rpgwizard.common.assets.tileset.Tile;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public class TileSelectionEvent extends EventObject {

    @Getter
    private final Tile tile;

    public TileSelectionEvent(Object source, Tile tile) {
        super(source);
        this.tile = tile;
    }

}

/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.tileset;

import java.util.EventObject;
import org.rpgwizard.common.assets.Tile;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public class TileSelectionEvent extends EventObject {

	private final Tile tile;

	public TileSelectionEvent(Object source, Tile tile) {
		super(source);
		this.tile = tile;
	}

	public Tile getTile() {
		return tile;
	}

}

/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.events;

import java.util.EventObject;
import org.rpgwizard.common.assets.Board;
import org.rpgwizard.common.assets.BoardLayer;
import org.rpgwizard.common.assets.BoardSprite;

/**
 * An <code>EventObject</code> used to contain information of a change that has
 * happened on a board.
 *
 * @author Joshua Michael Daly
 */
public class BoardChangedEvent extends EventObject {

	private BoardLayer layer;
	private BoardSprite boardSprite;

	/**
	 * Creates a new event.
	 *
	 * @param board
	 *            board the event happened on
	 */
	public BoardChangedEvent(Board board) {
		super(board);
	}

	/**
	 * Gets the layer that was effected.
	 *
	 * @return effected layer
	 */
	public BoardLayer getLayer() {
		return layer;
	}

	/**
	 * Sets the effected layer.
	 *
	 * @param layer
	 *            effected layer
	 */
	public void setLayer(BoardLayer layer) {
		this.layer = layer;
	}

	/**
	 * Gets the BoardSprite that was added, if any.
	 * 
	 * @return
	 */
	public BoardSprite getBoardSprite() {
		return boardSprite;
	}

	/**
	 * Sets the BoardSprite that was added.
	 * 
	 * @param boardSprite
	 */
	public void setBoardSprite(BoardSprite boardSprite) {
		this.boardSprite = boardSprite;
	}
}

/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import java.awt.Color;

/**
 * A layer shade.
 *
 * @author Joshua Michael Daly
 */
public class BoardLayerShade {

	private Color colour;
	private long layer;

	/**
	 * Creates a blank layer shade.
	 */
	public BoardLayerShade() {

	}

	/**
	 * Creates a layer shade with a colour and associated layer.
	 *
	 * @param colour
	 *            colour to use
	 * @param layer
	 *            associated layer
	 */
	public BoardLayerShade(Color colour, long layer) {
		this.colour = colour;
		this.layer = layer;
	}

	/**
	 * Creates a layer shade from an r,g,b value and a associated layer.
	 *
	 * @param r
	 * @param g
	 * @param b
	 * @param layer
	 *            associated layer
	 */
	public BoardLayerShade(int r, int g, int b, long layer) {
		colour = new Color(r, g, b);
		this.layer = layer;
	}

	/**
	 * Gets the layer shade colour.
	 *
	 * @return current colour
	 */
	public Color getColour() {
		return colour;
	}

	/**
	 * Sets the layer shade colour.
	 *
	 * @param colour
	 *            new colour
	 */
	public void setColour(Color colour) {
		this.colour = colour;
	}

	/**
	 * Gets the layer shade layer.
	 *
	 * @return layer index
	 */
	public long getLayer() {
		return layer;
	}

	/**
	 * Sets the layer shade layer.
	 *
	 * @param layer
	 *            new layer index
	 */
	public void setLayer(long layer) {
		this.layer = layer;
	}

}
